//
// KISSmetricsSDK
//
// Copyright 2014 KISSmetrics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kissmetrics.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.os.Build;
import android.util.Log;

/**
 * VerificationImpl
 * <p/>
 * Handles HttpURLConnections for KISSmetrics verification queries.
 */
class VerificationImpl {
  private static final Integer CONNECTION_TIMEOUT = 20;
  private final String TRK_URL = "https://et.kissmetrics.io/m/trk";

  private HttpURLConnection connection;

  /**
   * Opens a connection from a URL.
   * Allows for injection of mock HttpURLConnection via method override.
   *
   * @param url Verification query URL
   * @return HttpURLConnection  from the URL
   * @throws IOException
   */
  protected HttpURLConnection createHttpURLConnection(URL url) throws IOException {
    return (HttpURLConnection) url.openConnection();
  }

  /**
   * Makes a request to the provided verification urlString.
   * Handles the response and notifies the provided VerificationDelgate on completion.
   *
   * @param productKey  KISSmetrics product key
   * @param installUuid Random identifier for this app install
   * @param delegate    VerificationDelegate instance for completion callbacks.
   */
  public void verifyTracking(String productKey, String installUuid, VerificationDelegate delegate) {
    synchronized (this) {

      Log.v(KISSmetricsAPI.TAG, "verifyTracking");

      URL url = null;
      connection = null;

      long currentTime = System.currentTimeMillis();

      boolean success = false;
      long expirationDate = 0;
      boolean doTrack = true; // We should always track by default. But we may not always send.
      String baseUrl = "";

      String urlString = String.format("%s?product_key=%s&install_uuid=%s", TRK_URL, productKey, installUuid);

      try {
        url = new URL(urlString);
        connection = createHttpURLConnection(url);
        connection.setUseCaches(true); // !!!: Set cache location. ???:Override expiration
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECTION_TIMEOUT * 1000);
        connection.setRequestProperty("User-Agent", "KISSmetrics-Android/2.0.3");

        // addressing java.io.EOFException
        if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
          connection.setRequestProperty("Connection", "close");
        }

        // TODO: Apply any easily obtainable device/OS info
        //setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        connection.connect();

        int responseCode = connection.getResponseCode();
        expirationDate = connection.getHeaderFieldDate("Expires", currentTime);

        // Parse response for success
        if (responseCode == 200 || responseCode == 304) {
          success = true;

          BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
          }
          br.close();

          // Expected JSON payload = { "reason": "PRODUCT_SAMPLING", "tracking": false, "tracking_endpoint": "trc.kissmetrics.io"}
          String jsonString = sb.toString();
          JSONObject jsonObject = new JSONObject(jsonString);
          doTrack = jsonObject.getBoolean("tracking");
          baseUrl = "https://" + jsonObject.getString("tracking_endpoint");
        }

        // ELSE

        // We're getting an unexpected response. KM may be down. Since
        // we can't be sure, we send the default success of false and
        // doTrack of true.

        // We want to continue to track as we do not want to lose data
        // over this. It will however not be sent or deleted until we
        // have a confirmed successful instruction to track or not.
      } catch (Exception e) {
        // The KISSmetrics SDK should not cause a customer's app to crash.
        // Log a warning and continue.
        Log.w(KISSmetricsAPI.TAG, "Verification experienced an Exception.", e);
      } finally {

        if (connection != null) {
          connection.disconnect();
          connection = null;
        }

        // Callback to delegate
        if (delegate != null) {
          delegate.verificationComplete(success, doTrack, baseUrl, expirationDate);
        } else {
          Log.w(KISSmetricsAPI.TAG, "Verification delegate not available");
        }
      }
    }
  }
}
