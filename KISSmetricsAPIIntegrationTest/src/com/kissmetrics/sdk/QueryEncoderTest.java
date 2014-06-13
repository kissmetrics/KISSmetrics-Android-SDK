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

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;

import com.kissmetrics.sdk.QueryEncoder;

import android.test.AndroidTestCase;


public class QueryEncoderTest extends AndroidTestCase {

	static QueryEncoder cut;
	
	static String key = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	static String clientType = "mobile_app";
	static String userAgent = "android+2.0.5";
	static String baseUrl = "https://trk.kissmetrics.com";
	
	static String reservedString = "!*'();:@&=+$,/?#[]";
	static String encodedReservedString = "%21%2A%27%28%29%3B%3A%40%26%3D%2B%24%2C%2F%3F%23%5B%5D";
	static String unsafeString = "<>#%{}|\\^~` []";
	static String encodedUnsafeString = "%3C%3E%23%25%7B%7D%7C%5C%5E~%60%20%5B%5D";
	static String unreservedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
	static String encodedUnreservedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
 
    
    @Before
	protected void setUp() throws Exception{
		super.setUp();
		// Setup code here
		
		// Initialize QueryEncoder
		cut = new QueryEncoder(key, clientType, userAgent);
	}
	
    @After
	protected void tearDown() throws Exception{
		// Tear down code here
    	
    	cut = null;
    	
		super.tearDown();
	}
	

	public final void test_encodeOfReservedCharacters() {

		String result = cut.encode(reservedString);
		
		assertEquals("urlEncoding of reserved URL characters yeilds expected URL", encodedReservedString, result);
	}
	

	public final void test_encodeOfUnsafeCharacters() {

		String result = cut.encode(unsafeString);
		
		assertEquals("urlEncoding of unsafe URL characters yeilds expected URL", encodedUnsafeString, result);
	}
	

	public final void test_urlEncodeOfUnreservedCharacters() {

		String result = cut.encode(unreservedString);
		
		assertEquals("urlEncoding of unreserved URL characters yeilds expected URL", encodedUnreservedString, result);
	}
	

	public final void test_urlEncodeIdentity() {
		
	    String testIdentityString = "testuser@example.com";
	    
	    // For now we expect identities to have basic url encoding, 
	    // so we only test for a match from _urlEncode.
	    String expectedEncodedIdentity = cut.encode(testIdentityString);
	    
	    String encodedIdentity = cut.encodeIdentity(testIdentityString);
	    
	    assertEquals("urlEncodeIdentity yields expected URL", expectedEncodedIdentity, encodedIdentity);
	}
	

	public final void test_urlEncodeEvent() {
		
	    String testEventString = "KISSmetrics urlEncodeEvent";
	    
	    // For now we expect events to have basic url encoding, 
	    // so we only test for a match from _urlEncode.
	    String expectedEncodedEvent = cut.encode(testEventString);
	    
	    String encodedEvent = cut.encodeEvent(testEventString);
	    
	    assertEquals("urlEncodeEvent yields exected URL", expectedEncodedEvent, encodedEvent);
	}
	
	
	public final void test_urlEncodeProperties() {
		
	    HashMap<String, String> testPropertyHashMap = new HashMap<String, String>();
	    testPropertyHashMap.put("Reserved", reservedString);
	    testPropertyHashMap.put("Unsafe", unsafeString);
	    testPropertyHashMap.put("Unreserved", unreservedString);     
	                                             
	    String encodedProperties = cut.encodeProperties(testPropertyHashMap);
	    
	    // We check that our encoded string contains the 3 expected properties.
	    String reserved = "&Reserved="+encodedReservedString;
	    String unsafe =  "&Unsafe="+encodedUnsafeString;
	    String unreserved = "&Unreserved="+encodedUnreservedString;
	    
	    encodedProperties = encodedProperties.replace(reserved, "");
	    encodedProperties = encodedProperties.replace(unsafe, "");
	    encodedProperties = encodedProperties.replace(unreserved, "");
	    
	    String expectedEncodedProperties = "";
	    
	    assertEquals("urlEncodeProperties yields expected URL", expectedEncodedProperties, encodedProperties);
	}
	
	
	public final void test_urlEncodePropertiesDropsNullValues() {
		
	    HashMap<String, String> testPropertyHashMap = new HashMap<String, String>();
	    testPropertyHashMap.put("Reserved", null);    
	                                             
	    String encodedProperties = cut.encodeProperties(testPropertyHashMap);
	    
	    String expectedEncodedProperties = "";
	    
	    assertEquals("urlEncodeProperties drops null property values", expectedEncodedProperties, encodedProperties);
	}
	

	public final void test_createEventQueryWithProperties() {
		
		String event = "testEvent";
	    
	    // Testing with a LinkedHashMap to maintain expected order
	    LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
	    properties.put("propertyOne", "testPropertyOne");
	    properties.put("propertyTwo", "testPropertyTwo");
	    
	    String identity = "testuser@example.com";
	    
	    long timestamp = System.currentTimeMillis()/1000;

	    String createdQuery = cut.createEventQuery(event, properties, identity, timestamp);
	    
	    String expectedQuery = "/e?_k=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&_c=mobile_app&_u=android+2.0.5&_p=testuser%40example.com";

	    expectedQuery += "&_n=testEvent";
	    expectedQuery += ("&_d=1&_t=" + String.valueOf(timestamp));  
	    expectedQuery += "&propertyOne=testPropertyOne&propertyTwo=testPropertyTwo";
	        
	    assertEquals("URL incorrect", expectedQuery, createdQuery);
	}

	
	public final void test_createPropertiesQuery() {

		// Testing with a LinkedHashMap to maintain expected order
	    LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
	    properties.put("propertyOne", "testPropertyOne");
	    properties.put("propertyTwo", "testPropertyTwo");

	    String identity = "testuser@example.com";

	    long timestamp = System.currentTimeMillis()/1000;

	    String createdQuery = cut.createPropertiesQuery(properties, identity, timestamp);

	    String expectedQuery = "/s?_k=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&_c=mobile_app&_u=android+2.0.5&_p=testuser%40example.com&_d=1&_t=";

	    expectedQuery = expectedQuery + String.valueOf(timestamp);
	    expectedQuery = expectedQuery + "&propertyOne=testPropertyOne&propertyTwo=testPropertyTwo";
	    
	    assertEquals("URL incorrect", expectedQuery, createdQuery);
	}
	
	
	// TODO: Write tests for properties methods using null properties
	
	public final void test_createAliasQuery() {

	    String newIdentity = "testnewuser@example.com";
	    String oldIdentity = "testolduser@example.com";
	    
	    String createdQuery = cut.createAliasQuery(oldIdentity, newIdentity);

	    String expectedQuery = "/a?_k=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx&_c=mobile_app&_u=android+2.0.5&_p=testolduser%40example.com&_n=testnewuser%40example.com";
	    
	    assertEquals("URL incorrect", expectedQuery, createdQuery);
	}
	
}
