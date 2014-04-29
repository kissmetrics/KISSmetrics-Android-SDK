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

import org.apache.cactus.mock.MockHttpURLConnection;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;

import android.test.ActivityTestCase;
import android.util.Log;



// Class under test

//Class test helpers
import com.kissmetrics.sdk.ConnectionDelegate;
import com.kissmetrics.sdk.ConnectionImpl;
import com.kissmetrics.sdk.TestableConnectionImpl;

// ConnectionImplTest
// implements ConnectionDelegate to test delegate callback results
public class ConnectionImplTest extends ActivityTestCase implements ConnectionDelegate {
	
	URL testUrl = null;
	MockHttpURLConnection mockConnection = null;
	
	String  resultUrl = null;
	boolean resultSuccess = false;
	boolean resultMalformed = false;
	

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		try {
			testUrl = new URL("http://www.kissmetrics.com/"); // <- Not special url required, just needs to be valid.
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		
		mockConnection = new MockHttpURLConnection(testUrl);
		mockConnection.setExpectedGetInputStream(new ByteArrayInputStream("".getBytes()));//<- Not used but required for mock, so empty 
	}
	
	
	@After
	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	
	public final void testConnectionResponseHeader200() {

		// Since we're testing the value of a local bool, 
		// set it to the incorrect value before we test.
		resultSuccess = false;
		resultMalformed = true;
		
		mockConnection.setExpectedGetHeaderField("HTTP/1.1 200 OK");

		// Inject the mock connection via setter in our class override
		TestableConnectionImpl connectionImpl = new TestableConnectionImpl();
		connectionImpl.setHttpURLConnection(mockConnection);

		// Method under test
		connectionImpl.sendRecord("http://www.kissmetrics.com", this);

		// Assert values returned via callback
		assertEquals("A 200 response is reported as successful", true, resultSuccess);
		assertEquals("A 200 response is not malformed", false, resultMalformed);
	}
	
	
	public final void testConnectionResponseHeader304() {

		resultSuccess = false;
		resultMalformed = true;

		mockConnection.setExpectedGetHeaderField("HTTP/1.1 304 Not Modified");

		TestableConnectionImpl connectionImpl = new TestableConnectionImpl();
		connectionImpl.setHttpURLConnection(mockConnection);

		// Method under test
		connectionImpl.sendRecord("http://www.kissmetrics.com", this);

		// Assert values returned via callback
		assertEquals("A 304 response is reported as successful", true, resultSuccess);
		assertEquals("A 304 response is not malformed", false, resultMalformed);
	}
	
	
	public final void testConnectionResponseHeader404() {

		resultSuccess = true;
		resultMalformed = true;

		mockConnection.setExpectedGetHeaderField("HTTP/1.1 404 Not Found");

		TestableConnectionImpl connectionImpl = new TestableConnectionImpl();
		connectionImpl.setHttpURLConnection(mockConnection);

		// Method under test
		connectionImpl.sendRecord("http://www.kissmetrics.com", this);

		// Assert values returned via callback
		assertEquals("A 404 response is reported as unsuccessful", false, resultSuccess);
		assertEquals("A 404 response is not malformed", false, resultMalformed);
	}
	
	
	public final void testConnectionResponseHeader503() {

		resultSuccess = true;
		resultMalformed = true;
		
		mockConnection.setExpectedGetHeaderField("HTTP/1.1 503 Service Unavailable");

		TestableConnectionImpl connectionImpl = new TestableConnectionImpl();
		connectionImpl.setHttpURLConnection(mockConnection);

		// Method under test
		connectionImpl.sendRecord("http://www.kissmetrics.com", this);

		// Assert values returned via callback
		assertEquals("A 503 response is reported as unsuccessful", false, resultSuccess);
		assertEquals("A 503 response is not malformed", false, resultMalformed);
	}

	
	public final void testMalformedURL() {

		resultSuccess = true;
		resultMalformed = false;
		
		// No need to mock for malformedURL
		ConnectionImpl connectionImpl = new ConnectionImpl();

		// Method under test
		connectionImpl.sendRecord("htt.p://www.kissmetrics.com", this);

		// Assert values returned via callback
		assertEquals("A malformedURL is not successful", false, resultSuccess);
		assertEquals("A malformedURL is malformed", true, resultMalformed);
	}

	
	// ConnectionImpl uses a callback to return results.
	// Pickup the completion callback here and record the results.
	// We'll check these values as part of each test.
	@Override
	public void connectionComplete(String urlString, boolean success, boolean malformed) {
		
		Log.d("KISSmetricsAPI", "ConnectionImplTest as ConnectionDelegate received connectionComplete callback");
		
		// Set our results for testing
		this.resultUrl = urlString;
		this.resultSuccess = success;
		this.resultMalformed = malformed;
	}
}
