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

import android.test.ActivityTestCase;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.cactus.mock.MockHttpURLConnection;

public class VerificationImplTest extends ActivityTestCase implements VerificationDelegate {
	URL testUrl = null;
	MockHttpURLConnection mockConnection = null;
	
	boolean resultSuccess = false;
	boolean resultDoTrack = false;
	String resultBaseUrl = null;
	long resultExpirationDate = 0L;

	protected void setUp() throws Exception {
		try {
			testUrl = new URL("http://www.google.com/"); // <- No special url required, just needs to be valid.
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		
		mockConnection = new MockHttpURLConnection(testUrl);
		mockConnection.setExpectedGetInputStream(new ByteArrayInputStream("".getBytes()));//<- Not always used but required for mock, so empty 
	}
	
	/*
	 * 	Expected Headers
	 * 
	  	HTTP/1.1 200 OK
  	 	Cache-Control: max-age=86400
  		Content-Type: application/json
  		Date: Wed, 13 Nov 2013 00:23:46 GMT
  		Expires: Thu, 14 Nov 2013 00:23:46 GMT
  		Server: nginx/1.4.2
  		Content-Length: 56
  		Connection: keep-alive
	 */
	
	private Date dateForNow() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date mockDate = calendar.getTime();
		
		return mockDate;
	}

	public final void testVerificationResponseFail() {
		// Since we're testing local values set them to the 
		// incorrect value before we test.
		resultSuccess = false;
		resultDoTrack = true;
		resultBaseUrl = null;

		String mockJson = "{\"reason\": \"PRODUCT_DISABLED\", \"tracking\": false, \"tracking_endpoint\": \"trk.kissmetrics.com\"}";
		mockConnection.setExpectedGetInputStream(new ByteArrayInputStream(mockJson.getBytes()));
		
		mockConnection.setExpectedGetHeaderField("HTTP/1.1 404 Not Found");
		
		mockConnection.setExpectedGetHeaderFieldDate(0L);
		
		// Inject the mock connection via setter in our class override
		TestableVerificationImpl verificationImpl = new TestableVerificationImpl();
		verificationImpl.setHttpURLConnection(mockConnection);

		// Method under test
		verificationImpl.verifyTracking("PRODUCTKEYSHOULDNTMATTER", "INSTALLUUIDSHOULDNTMATTER", this);
		
		// Assert values returned via callback
		assertEquals("A 404 response is reported as unsuccessful", false, resultSuccess);
		assertEquals("A failure to connect sets doTrack to true to prevent data loss", true, resultDoTrack);
		assertEquals("A successful response provides a baseUrl tracking endpoint", "", resultBaseUrl);
		assertEquals("A successful response sets an expiration at or less than 30 days", 0L, resultExpirationDate);
	}

	public final void testVerificationResponseTrackingFalse() {
		// Since we're testing local values set them to the 
		// incorrect value before we test.
		resultSuccess = false;
		resultDoTrack = true;
		resultBaseUrl = "";
		resultExpirationDate = 0L;
		
		String mockJson = "{\"reason\": \"PRODUCT_DISABLED\", \"tracking\": false, \"tracking_endpoint\": \"trk.kissmetrics.com\"}";
		mockConnection.setExpectedGetInputStream(new ByteArrayInputStream(mockJson.getBytes()));
		
		mockConnection.setExpectedGetHeaderField("HTTP/1.1 200 OK");
		
		long expectedExpirationDate = dateForNow().getTime();
		
		mockConnection.setExpectedGetHeaderFieldDate(expectedExpirationDate);
		
		// Inject the mock connection via setter in our class override
		TestableVerificationImpl verificationImpl = new TestableVerificationImpl();
		verificationImpl.setHttpURLConnection(mockConnection);

		// Method under test
		verificationImpl.verifyTracking("PRODUCTKEYSHOULDNTMATTER", "INSTALLUUIDSHOULDNTMATTER", this);
		
		// Assert values returned via callback
		assertEquals("A 200 response is reported as successful", true, resultSuccess);
		assertEquals("A tracking:false JSON body set doTrack to false", false, resultDoTrack);
		assertEquals("A successful response provides a baseUrl tracking endpoint", "https://trk.kissmetrics.com", resultBaseUrl);
		assertEquals("A successful response sets an expiration at or less than 30 days", expectedExpirationDate, resultExpirationDate);
	}

	public final void testVerificationResponseTrackingTrue() {
		// Since we're testing local values set them to the 
		// incorrect value before we test.
		resultSuccess = false;
		resultDoTrack = false;
		resultBaseUrl = "";
		resultExpirationDate = 0L;
		
		String mockJson = "{\"tracking\": true, \"tracking_endpoint\": \"trk.testing.kissmetrics.com\"}";
		mockConnection.setExpectedGetInputStream(new ByteArrayInputStream(mockJson.getBytes()));
		
		mockConnection.setExpectedGetHeaderField("HTTP/1.1 200 OK");

		long expectedExpirationDate = dateForNow().getTime();
	
		mockConnection.setExpectedGetHeaderFieldDate(expectedExpirationDate);
		
		// Inject the mock connection via setter in our class override
		TestableVerificationImpl verificationImpl = new TestableVerificationImpl();
		verificationImpl.setHttpURLConnection(mockConnection);

		// Method under test
		verificationImpl.verifyTracking("PRODUCTKEYSHOULDNTMATTER", "INSTALLUUIDSHOULDNTMATTER", this);
		
		// Assert values returned via callback
		assertEquals("A 200 response is reported as successful", true, resultSuccess);
		assertEquals("A tracking:false JSON body set doTrack to true", true, resultDoTrack);
		assertEquals("A successful response provides a baseUrl tracking endpoint", "https://trk.testing.kissmetrics.com", resultBaseUrl);
		assertEquals("Receives expected expiration from headers", expectedExpirationDate, resultExpirationDate);
	}

	public final void testMalformedURL() {
		resultSuccess = true;
		resultDoTrack = false;
		resultBaseUrl = "";
		resultExpirationDate = 999999999L;
		
		// No need to mock for malformedURL
		VerificationImpl verificationImpl = new VerificationImpl();
		
		// Method under test
		verificationImpl.verifyTracking("PRODUCTKEYSHOULDNTMATTER", "INSTALLUUIDSHOULDNTMATTER", this);
				
		// Assert values returned via callback
		assertEquals("A malformedURL is not successful", false, resultSuccess);
		assertEquals("A malformedURL does allow tracking", true, resultDoTrack);
		assertEquals("A malformedURL sets expiration date to 0", 0L, resultExpirationDate);
	}

	// VerificationImpl uses a callback to return results.
	// Pickup the completion callback here and record the results.
	// We'll check these values as part of each test.
	@Override
	public void verificationComplete(boolean success, boolean doTrack, String baseUrl, long expirationDate) {
		resultSuccess = success;
		resultDoTrack = doTrack;
		resultBaseUrl = baseUrl;
		resultExpirationDate = expirationDate;
	}
}
