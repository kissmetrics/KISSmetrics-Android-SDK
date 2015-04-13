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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Build;
import android.util.Log;

/**
 * ConnectionImpl 
 * 
 * Handles HttpURLConnections for KISSmetrics API queries. 
 * 
 */
public class ConnectionImpl implements Connection {
	private static final Integer CONNECTION_TIMEOUT = 20;
	
	private HttpURLConnection connection;

	/**
	 * Opens a connection from a URL.
	 * Allows for injection of mock HttpURLConnection via method override.
	 * 
	 * @param url  API query URL
	 * @return HttpURLConnection from the URL
	 * @throws IOException
	 */
	protected HttpURLConnection createHttpURLConnection(URL url) throws IOException{
		return (HttpURLConnection)url.openConnection();
	}

	/**
	 * Makes a request to the provided API query urlString.
	 * Handles the response and notifies the provided ConnectionDelgate on completion.
	 * 
	 * @param urlString  URL encoded API query string
	 * @delegate delegate Object implementing the ConnectionDelegate interface
	 */
	public void sendRecord(final String urlString, final ConnectionDelegate delegate) {
		URL url = null;
		connection = null;
		
		boolean success = false;
		boolean malformed = false;
		int responseCode = -1;
		
		try {
			url = new URL(urlString);
				
			connection = createHttpURLConnection(url);
			connection.setUseCaches(false);
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECTION_TIMEOUT*1000);
			connection.setRequestProperty("User-Agent", USER_AGENT);
			// TODO: Apply any easily obtainable device/OS info to the user agent value 
			
			// addressing java.io.EOFException
			if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) { 
				connection.setRequestProperty("Connection", "close"); 
			}
			
			responseCode = connection.getResponseCode();
			connection.connect();
			
		} catch (MalformedURLException e) {
			Log.w("KISSmetricsAPI", "Connection URL was malformed: " + e);
	        malformed = true;
		} catch (Exception e) {
			Log.w("KISSmetricsAPI", "Connection experienced an Exception: " + e);
		} finally {
      if (connection != null) {
        connection.disconnect();
        connection = null;
      }

      if (malformed != true && (responseCode == 200 || responseCode == 304)) {
        success = true;
      } else {
        success = false;
      }

      // Callback to delegate
      if (delegate != null) {
        delegate.connectionComplete(urlString, success, malformed);
      } else {
        Log.w("KISSmetricsAPI", "Connection delegate not available");
      }
    }
  }
}
