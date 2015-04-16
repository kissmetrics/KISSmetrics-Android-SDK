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


// Full integration tests of KISSmetricsAPI

package com.kissmetrics.sdk;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cactus.mock.MockHttpURLConnection;
import org.junit.After;
import org.junit.Before;

import com.kissmetrics.sdk.ArchiverImpl;
import com.kissmetrics.sdk.KISSmetricsAPI;

import android.app.Activity;
import android.content.SharedPreferences;
import android.test.ActivityTestCase;

public class KISSmetricsAPITest extends ActivityTestCase {
  String key = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

  int urlSuccessCount;
  int urlErrorCount;
  int urlBadStatusCount;

  public void uth_resetKISSmetricsAPISingleton() {
    Field instance;
    try {
      instance = KISSmetricsAPI.class.getDeclaredField("sharedAPI");
      instance.setAccessible(true);
      instance.set(null, null);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void uth_resetArchiverSingleton() {
    Field instance;
    try {
      instance = ArchiverImpl.class.getDeclaredField("sharedArchiver");
      instance.setAccessible(true);
      instance.set(null, null);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void uth_cleanSlate() {
    SharedPreferences pref = getInstrumentation().getTargetContext().getSharedPreferences("KISSmetricsIdentity", Activity.MODE_PRIVATE);
    pref.edit().clear().commit();

    getInstrumentation().getTargetContext().deleteFile("KISSmetricsSettings");
    getInstrumentation().getTargetContext().deleteFile("KISSmetricsActions");
    getInstrumentation().getTargetContext().deleteFile("KISSmetricsSavedEvents");
    getInstrumentation().getTargetContext().deleteFile("KISSmetricsSavedProperties");
  }

  private Date dateForNow() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    Date mockDate = calendar.getTime();

    return mockDate;
  }

  private MockHttpURLConnection mockConnection(String urlString, String inputStream, String header, long headerDate) {
    URL url = null;

    try {
      url = new URL("http://www.kissmetrics.com/"); // <- No specific URL required
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    MockHttpURLConnection mockConnection = null;
    mockConnection = new MockHttpURLConnection(url);
    mockConnection.setExpectedGetInputStream(new ByteArrayInputStream(inputStream.getBytes()));
    mockConnection.setExpectedGetHeaderField(header);
    mockConnection.setExpectedGetHeaderFieldDate(headerDate);

    return mockConnection;
  }

  private void mockResponse(String header) {
    MockHttpURLConnection mockConnection = mockConnection("http://www.kissmetrics.com/", "", header, 0);

    TestableConnectionImpl testableConnectionImpl = new TestableConnectionImpl();
    testableConnectionImpl.setHttpURLConnection(mockConnection);

    // We need to mock this Sender so that it uses our testableConnectionImpl
    Sender sender = new Sender(false);
    sender.injectedConnection = testableConnectionImpl;
    KISSmetricsAPI.sender = sender;
  }

  private void mockVerificationResponse(String tracking) {
    // Expected JSON payload = { "reason": "PRODUCT_SAMPLING", "tracking": false, "tracking_endpoint": "trk.kissmetrics.com"}
    String mockJson = "{\"tracking\": " + tracking + ", \"tracking_endpoint\": \"trk.kissmetrics.com\" }";
    long expDate = dateForNow().getTime() + 86400000;
    MockHttpURLConnection mockConnection = mockConnection("http://www.kissmetrics.com/", mockJson, "HTTP/1.1 200 OK", expDate);

    TestableVerificationImpl testableVerificationImpl = new TestableVerificationImpl();
    testableVerificationImpl.setHttpURLConnection(mockConnection);

    // Inject our VerificationImpl instance with mocked HttpURLConnection
    KISSmetricsAPI.setVerificationImpl(testableVerificationImpl);
  }

  private void startApiWithTrackingAndResponseHeader(String tracking, String header) {
    mockVerificationResponse(tracking);
    mockResponse(header);
    KISSmetricsAPI.sharedAPI(key, this.getInstrumentation().getTargetContext());

    // Allow time in background to read and write archived files
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void enforceEmptyQueue() {
    if (ArchiverImpl.sharedArchiver().getQueueCount() != 0) {
      throw new RuntimeException("Archiver sendQueue must be empty before starting this test but contained " + ArchiverImpl.sharedArchiver().getQueueCount() + " records");
    }
  }

  public void _requestSuccessful() {
    urlSuccessCount++;
  }

  public void _requestReceivedError() {
    urlErrorCount++;
  }

  public void _requestReceivedUnexpectedStatusCode() {
    urlBadStatusCount++;
  }

  @Before
  protected void setUp() throws Exception {
    super.setUp();

    uth_cleanSlate();
    uth_resetArchiverSingleton();
    uth_resetKISSmetricsAPISingleton();
  }

  @After
  protected void tearDown() throws Exception {
    uth_cleanSlate();
    uth_resetKISSmetricsAPISingleton();
    uth_resetArchiverSingleton();

    super.tearDown();
  }

  public final void testSuccessfulConnectionToKM() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    KISSmetricsAPI.sharedAPI().record("liveServerTest");

    Thread.sleep(1000);
    assertEquals("Does not retain successfully uploaded records", 0, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testStressfulRecordsToKM() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    Runnable runnable1 = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 100; i++) {
          KISSmetricsAPI.sharedAPI().record("liveServerTest_thread1_" + i);
        }
      }
    };
    ExecutorService service1 = Executors.newFixedThreadPool(1);
    service1.execute(runnable1);

    Runnable runnable2 = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 100; i++) {
          KISSmetricsAPI.sharedAPI().record("liveServerTest_thread2_" + i);
        }
      }
    };
    ExecutorService service2 = Executors.newFixedThreadPool(1);
    service2.execute(runnable2);

    Thread.sleep(20000);
    assertEquals("Does not retain successfully uploaded records", 0, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testEmptyQueuePlacesSenderInReadyState() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    // Send 10 records
    for (int i = 0; i < 10; i++) {
      KISSmetricsAPI.sharedAPI().record("senderStateTest_batch_1");
    }

    // Allow the queue to empty and the executor to shutdown
    Thread.sleep(1000);

    // Send another 10 records
    // If Sender is not in the ready state, these will not be sent
    for (int i = 0; i < 10; i++) {
      KISSmetricsAPI.sharedAPI().record("senderStateTest_batch_2");
    }

    Thread.sleep(1000);
    assertEquals("Does not retain successfully uploaded records", 0, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testIdentifySetsIdentity() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    String expectedIdentity = "KISSmetricsAPI@example.com";

    KISSmetricsAPI.sharedAPI().identify(expectedIdentity);

    Thread.sleep(1000);
    assertEquals("Identifying a user retains the set identity string", expectedIdentity, KISSmetricsAPI.sharedAPI().identity());
  }

  public final void testIdentityCannotBeNull() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    String currentIdentity = KISSmetricsAPI.sharedAPI().identity();
    String newIdentity = null;

    KISSmetricsAPI.sharedAPI().identify(newIdentity);

    Thread.sleep(1000);
    assertEquals("Identifying a user as null retains the last set identity string", currentIdentity, KISSmetricsAPI.sharedAPI().identity());
  }

  public final void testIdentiyCannotBeEmptyString() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    String currentIdentity = KISSmetricsAPI.sharedAPI().identity();
    String newIdentity = "";

    KISSmetricsAPI.sharedAPI().identify(newIdentity);

    Thread.sleep(1000);
    assertEquals("Identifying a user as an empty string retains the last set identity string", currentIdentity, KISSmetricsAPI.sharedAPI().identity());
  }

  public final void testIdentiyCannotBeResetToCurrentValue() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    String currentIdentity = KISSmetricsAPI.sharedAPI().identity();

    KISSmetricsAPI.sharedAPI().identify(currentIdentity);

    Thread.sleep(1000);
    assertEquals("Does not create and retain a new identity record", 0, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testAliasNotRetained() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");

    String currentIdentity = KISSmetricsAPI.sharedAPI().identity();
    String alias = "unitTestJhonny@example.com";

    KISSmetricsAPI.sharedAPI().alias(alias, currentIdentity);

    Thread.sleep(1000);
    assertNotSame("Aliasing an identity does not retain the alias as the identity", alias, currentIdentity);
  }

  public final void testRemovesSuccessfulRecordFromArchive() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");
    enforceEmptyQueue();

    KISSmetricsAPI.sharedAPI().record("passingURLResponseTest");

    Thread.sleep(1000);
    assertEquals("Removes successfully uploaded records from the archives sendQueue", 0, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testRemovesSuccessfulPropertiesFromArchive() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 200 OK");
    enforceEmptyQueue();

    HashMap<String, String> propertyHash = new HashMap<String, String>();
    propertyHash.put("passingURLResponseTest", "1");

    KISSmetricsAPI.sharedAPI().set(propertyHash);

    Thread.sleep(1000);
    assertEquals("Removes successfully uploaded properties from the archives sendQueue", 0, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testRetainsRecordsUnderServerOutage() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 503 SERVICE UNAVAILABLE");
    enforceEmptyQueue();

    KISSmetricsAPI.sharedAPI().record("testRetainsRecordsUnderServerOutage");

    Thread.sleep(1000);
    assertEquals("Retains records in archive during KM server outage", 1, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testRetainsPropertiesUnderServerOutage() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 503 SERVICE UNAVAILABLE");
    enforceEmptyQueue();

    HashMap<String, String> propertyHash = new HashMap<String, String>();
    propertyHash.put("testRetainsPropertiesUnderServerOutage", "1");

    KISSmetricsAPI.sharedAPI().set(propertyHash);

    Thread.sleep(1000);
    assertEquals("Retains properties in archive during KM server outage", 1, ArchiverImpl.sharedArchiver().getQueueCount());
  }

  public final void testSetDistinctProperty() throws InterruptedException {
    startApiWithTrackingAndResponseHeader("true", "HTTP/1.1 503 SERVICE UNAVAILABLE");

    ArchiverImpl.sharedArchiver().clearSavedProperties();

    KISSmetricsAPI.sharedAPI().setDistinct("testSetDistinctStringProperty", "testValue");

    KISSmetricsAPI.sharedAPI().setDistinct("testSetDistinctStringProperty", "testValue");

    Thread.sleep(1000);
    assertEquals("Ignores second call to set distinct property with the same value", 1, ArchiverImpl.sharedArchiver().getQueueCount());
  }
}
