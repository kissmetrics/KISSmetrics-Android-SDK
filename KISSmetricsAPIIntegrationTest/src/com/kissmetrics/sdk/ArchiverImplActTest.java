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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.kissmetrics.sdk.ArchiverImpl;
import com.kissmetrics.sdk.KISSmetricsAPI.RecordCondition;
import com.kissmetrics.sdk.QueryEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityTestCase;


/**
 * ArchiverImpl Integration tests
 * 
 */
public class ArchiverImplActTest extends ActivityTestCase {

	static QueryEncoder queryEncoder;
	
	static String key = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	static String userAgent = "kissmetrics-android/2.0.5";
	static String clientType = "mobile_app";
	

	
	/************************************************
     * Reflection Unit Test Helpers
     ************************************************/
	public <T extends Object> Object uth_getInternalStorageObjectOfType(Class<T> type, Context context, String path) {
		T r = null;
		
    	FileInputStream fis;
    	ObjectInputStream ois;
		try {
			fis = context.openFileInput(path);
			ois = new ObjectInputStream(fis);
			r = type.cast(ois.readObject());
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return r;
	}
	
	public void uth_writeObjectToInternalStorageFile(Object object, String path) {
		try {
    		FileOutputStream fos = getInstrumentation().getTargetContext().openFileOutput(path, Context.MODE_PRIVATE);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(object);
    		oos.flush();
    		oos.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	
    public void uth_resetSingleton() {
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

    
    @SuppressWarnings("unchecked")
	public HashMap<String, Object> uth_getSettings() {
		Field f;
		HashMap<String, Object> r = null;
		try {
			f = ArchiverImpl.class.getDeclaredField("settings");
			f.setAccessible(true);
			r = (HashMap<String, Object>) f.get(ArchiverImpl.sharedArchiver());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} //IllegalAccessException
		
		return r;
	}
    
    
	@SuppressWarnings("unchecked")
	public List<String> uth_getSendQueue() {
		Field f;
		List<String> r = null;
		try {
			f = ArchiverImpl.class.getDeclaredField("sendQueue");
			f.setAccessible(true);
			r = (List<String>) f.get(ArchiverImpl.sharedArchiver());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} //IllegalAccessException
		
		return r;
	}
	
	
	public void uth_unarchiveIdentity() {
		Method method = null;
		try {
			method = ArchiverImpl.sharedArchiver().getClass().getDeclaredMethod("unarchiveIdentity", new Class[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(ArchiverImpl.sharedArchiver(), new Object[]{});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void uth_archiveSavedIdEvents() {
		Method method = null;
		try {
			method = ArchiverImpl.sharedArchiver().getClass().getDeclaredMethod("archiveSavedIdEvents", new Class[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(ArchiverImpl.sharedArchiver(), new Object[]{});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void uth_unarchiveSavedIdEvents() {
		Method method = null;
		try {
			method = ArchiverImpl.sharedArchiver().getClass().getDeclaredMethod("unarchiveSavedIdEvents", new Class[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(ArchiverImpl.sharedArchiver(), new Object[]{});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	public List<String> uth_getSavedIdEvents() {
		Field f;
		List<String> r = null;
		try {
			f = ArchiverImpl.class.getDeclaredField("savedIdEvents");
			f.setAccessible(true);
			r = (List<String>) f.get(ArchiverImpl.sharedArchiver());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} //IllegalAccessException
		
		return r;
	}
	
	
	public void uth_archiveSavedProperties() {
		Method method = null;
		try {
			method = ArchiverImpl.sharedArchiver().getClass().getDeclaredMethod("archiveSavedProperties", new Class[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (method != null){
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(ArchiverImpl.sharedArchiver(), new Object[]{});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void uth_unarchiveSavedProperties() {
		Method method = null;
		try {
			method = ArchiverImpl.sharedArchiver().getClass().getDeclaredMethod("unarchiveSavedProperties", new Class[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (method != null) {
			if(!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(ArchiverImpl.sharedArchiver(), new Object[]{});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> uth_getSavedProperties() {
		Field f;
		HashMap<String, String> r = null;
		try {
			f = ArchiverImpl.class.getDeclaredField("savedProperties");
			f.setAccessible(true);
			r = (HashMap<String, String>) f.get(ArchiverImpl.sharedArchiver());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} //IllegalAccessException
		
		return r;
	}
	
	
	public void uth_archiveSendQueue() {
		Method method = null;
		try {
			method = ArchiverImpl.sharedArchiver().getClass().getDeclaredMethod("archiveSendQueue", new Class[]{});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		if (method != null) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			try {
				method.invoke(ArchiverImpl.sharedArchiver(), new Object[]{});
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
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
    
    
    
    /************************************************
     * Setup and tear down
     ************************************************/
	protected void setUp() throws Exception {
		super.setUp();
		ArchiverImpl.sharedArchiver(key, getInstrumentation().getTargetContext());
		
		// Initialize QueryEncoder
		queryEncoder = new QueryEncoder(key, clientType, userAgent);
	}
	
	protected void tearDown() throws Exception {
		uth_cleanSlate();
		uth_resetSingleton();
		
		super.tearDown();
	}
	

	
	/************************************************
     * Tests
     ************************************************/
	@SuppressWarnings("unchecked")
	public final void testDefaultSettings() {
		
		// Define the expected default map
		HashMap<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("baseUrl", "https://trk.kissmetrics.com");
		expectedMap.put("verification_exp_date", 0L);
		expectedMap.put("hasGenericIdentity", false);
		expectedMap.put("doSend", false);
		expectedMap.put("doTrack", true);
		
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");
		
		assertEquals("Correct default settings are applied on class init", expectedMap, archivedMap);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveInstallUuid() {
		
		String testInstallUuid = UUID.randomUUID().toString();
		ArchiverImpl.sharedArchiver().archiveInstallUuid(testInstallUuid);
			
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");

		assertEquals("archiveInstallUuid retains integrity upon archiving", testInstallUuid, archivedMap.get("installUuid"));
	}
	
	
	public final void testArchiveIntallUuidSetsInstallUuidSetting() {
		
		String expectedInstallUuid = UUID.randomUUID().toString();
		ArchiverImpl.sharedArchiver().archiveInstallUuid(expectedInstallUuid);
		
		String returnedInstallUuid = ArchiverImpl.sharedArchiver().getInstallUuid();
		
		assertEquals("archiveInstallUuid sets installUuid", expectedInstallUuid, returnedInstallUuid);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveDoTrack() {
		
		// Set the doTrack value to false. Not the default value!
		ArchiverImpl.sharedArchiver().archiveDoTrack(false);
	
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");
	
		assertEquals("archiveDoTrack retains integrity upon archiving", false, archivedMap.get("doTrack"));
	}
	
	
	public final void testArchiveDoTrackSetsDoTrack() {
		
		boolean expectedDoTrack = false;
		ArchiverImpl.sharedArchiver().archiveDoTrack(expectedDoTrack);
		
		boolean returnedDoTrack = ArchiverImpl.sharedArchiver().getDoTrack();
		
		assertEquals("archiveDoTrack sets doTrack", expectedDoTrack, returnedDoTrack);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveDoSend() {
		
		// Set the doSend value to true. Not the default value!
		ArchiverImpl.sharedArchiver().archiveDoSend(true);
		
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");

		assertEquals("archiveDoSend retains integrity upon archiving", true, archivedMap.get("doSend"));
	}
	
	
	public final void testArchiveDoSendSetsDoSend() {
		
		boolean expectedDoSend = true;
		
		ArchiverImpl.sharedArchiver().archiveDoSend(expectedDoSend);
		
		boolean returnedDoSend = ArchiverImpl.sharedArchiver().getDoSend();
		
		assertEquals("archiveDoSend sets doSend", expectedDoSend, returnedDoSend);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveBaseUrl() {
		
		String expectedBaseUrl = "http://some.test.example.com";
		
		// Set the doSend value to true. Not the default value!
		ArchiverImpl.sharedArchiver().archiveBaseUrl(expectedBaseUrl);
		
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");

		assertEquals("archiveBaseUrl retains integrity upon archiving", expectedBaseUrl, archivedMap.get("baseUrl"));
	}
	
	
	public final void testArchiveBaseUrlSetsBaseUrl() {
		
		String expectedBaseUrl = "http://some.othertest.example.com";
		
		ArchiverImpl.sharedArchiver().archiveBaseUrl(expectedBaseUrl);
		
		String returnedBaseUrl = ArchiverImpl.sharedArchiver().getBaseUrl();
		
		assertEquals("archiveDoSend sets doSend", expectedBaseUrl, returnedBaseUrl);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveVerificationExpDate() {
		
		long expectedDate = 8675309L;
		
		// Set the verificationExpDate to something other than the default 0.
		ArchiverImpl.sharedArchiver().archiveVerificationExpDate(expectedDate);
		
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");
		
		assertEquals("archiveVerificationExpDate retains integrity upon archiving", expectedDate, archivedMap.get("verification_exp_date"));
	}
	
	
	public final void testArchiveVerificationExpDateSetsVerificationExpDate() {
		
		long expectedDate = 8675309L;
		
		// Set the verificationExpDate to something other than the default 0.
		ArchiverImpl.sharedArchiver().archiveVerificationExpDate(expectedDate);
		
		long receivedDate = (Long)this.uth_getSettings().get("verification_exp_date");
		
		assertEquals("archiveVerificationExpDate sets verificationExpDate", expectedDate, receivedDate);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveHasGenerticIdentity() {

		ArchiverImpl.sharedArchiver().archiveHasGenericIdentity(false);
		
		// Read from the file system directly
		HashMap<String, Object> archivedMap = (HashMap<String, Object>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSettings");
		
		assertEquals("archiveHasGenericIdentity retains integrity upon archiving", false, archivedMap.get("hasGenericIdentity"));
	}
	
	
	public final void testArchiveHasGenericIdentitySetsHasGenericIdentity() {
		
		ArchiverImpl.sharedArchiver().archiveHasGenericIdentity(true);
		
		boolean hasGenericIdentity = (Boolean)this.uth_getSettings().get("hasGenericIdentity");
		
		assertEquals("archiveHasGenericIdentity sets hasGenericIdentity", true, hasGenericIdentity);
	}
	
	
	public final void testArchiveFirstIdentity() {
		
		String expectedIdentity = "someTestIdentity";
		
		ArchiverImpl.sharedArchiver().archiveFirstIdentity(expectedIdentity);
		 
		// Read from the file system directly
		SharedPreferences pref = getInstrumentation().getTargetContext().getSharedPreferences("KISSmetricsIdentity", Activity.MODE_PRIVATE);
    	String archivedIdentity = pref.getString("identity", "");

    	assertEquals("archiveFirstIdentity retains integrity upon archiving", expectedIdentity, archivedIdentity);
	}
	
	
	public final void testArchiveFirstIdentitySetsLastIdentity() {
		
		String expectedIdentity = "someSetTestIdentity";
		
		ArchiverImpl.sharedArchiver().archiveFirstIdentity(expectedIdentity);
		
		String returnedIdentity = ArchiverImpl.sharedArchiver().getIdentity();
		
		assertEquals("archiveFirstIdentity sets lastIdentity", expectedIdentity, returnedIdentity);
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveFirstIdentityDoesNotCreateRecord() {
		
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("someTestIdentity");
		
		// Read from the file system directly
		List<String> archivedActions = (List<String>) this.uth_getInternalStorageObjectOfType(ArrayList.class, getInstrumentation().getTargetContext(), "KISSmetricsActions");

		assertEquals("archiveFirst identity doesn't create an identify record", 0, archivedActions.size());
	}
	
	
	public final void testArchiveFirstIdentityIgnoresNullValue() {
		
		String expectedIdentity = "nonNullTestIdentity";
		ArchiverImpl.sharedArchiver().archiveFirstIdentity(expectedIdentity);
		ArchiverImpl.sharedArchiver().archiveFirstIdentity(null);
		
		// Read from the file system directly
		SharedPreferences pref = getInstrumentation().getTargetContext().getSharedPreferences("KISSmetricsIdentity", Activity.MODE_PRIVATE);
		String archivedIdentity = pref.getString("identity", "");
		
		assertEquals("archiveFirstIdentity ignores null values", expectedIdentity, archivedIdentity);
	}
	
	
	public final void testArchiveFirstIdentityIgnoresEmptyValue() {
		
		String expectedIdentity = "anIdentity";
		ArchiverImpl.sharedArchiver().archiveFirstIdentity(expectedIdentity);
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("");

		// Read from the file system directly
		SharedPreferences pref = getInstrumentation().getTargetContext().getSharedPreferences("KISSmetricsIdentity", Activity.MODE_PRIVATE);
		String archivedIdentity = pref.getString("identity", "");
		
		assertEquals("archiveFirstIdentity ignores empty values", expectedIdentity, archivedIdentity);
	}
	
	
	public final void testArchiveIdentityIgnoresNullIdentity() {
		
		ArchiverImpl.sharedArchiver().archiveIdentity(null);
		
		assertEquals("archiveIdentity ignores null identities", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveIdentityIgnoresEmptyIdentity() {
		
		ArchiverImpl.sharedArchiver().archiveIdentity("");
		
		assertEquals("archiveIdentity ignores empty identities", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testUnarchiveIdentitySetsLastIdentity() {
		
		String identity = "testIdentity";
		
		ArchiverImpl.sharedArchiver().archiveIdentity(identity);
		
		uth_unarchiveIdentity();
		
		assertEquals("unarchiveIdentiy sets lastIdentity value", identity, ArchiverImpl.sharedArchiver().getIdentity());
	}
	
	
	public final void testArchiveIdentityCreatesAliasQueryWhenLastIdentityWasGeneric()
	{
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("oldTestUser@example.com");
		
		String identityString = "testNewUser@example.com";
		
		String expectedRecord = queryEncoder.createAliasQuery(identityString, ArchiverImpl.sharedArchiver().getIdentity());
		
		ArchiverImpl.sharedArchiver().archiveIdentity(identityString);
		
		assertEquals("An alias query is created for the archived identity", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveIdentityDoesNotCreateAliasQueryWhenLastIdentityWasNotGeneric()
	{
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("someUnknownGenericIdentity");
		
		ArchiverImpl.sharedArchiver().archiveHasGenericIdentity(false);
		
		ArchiverImpl.sharedArchiver().archiveIdentity("someKnownIdentity@example.com");
		
		// Read from the file system directly
		List<String> archivedActions = (List<String>) this.uth_getInternalStorageObjectOfType(ArrayList.class, getInstrumentation().getTargetContext(), "KISSmetricsActions");
				
		assertEquals("No query is created for the archived identity", 0, archivedActions.size());
	}
	
	
	public final void testArchiveIdentityClearsSavedIdEventsWhenLastIdentityWasNotGeneric()
	{
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("someUnknownGenericIdentity");
		ArchiverImpl.sharedArchiver().archiveIdentity("someKnownIdentity@example.com");
		
		// Populate and directly archive the test events
		List<String> expectedEvents = new ArrayList<String>();
		expectedEvents.add("testEvent");
	
		// Write to the file system directly
		uth_writeObjectToInternalStorageFile(expectedEvents, "KISSmetricsSavedEvents");
		
		// Unarchive saved events
		uth_unarchiveSavedIdEvents();

		ArchiverImpl.sharedArchiver().archiveIdentity("aDifferentUser@example.com");
		
		assertEquals("No query is created for the archived identity", 0, this.uth_getSavedIdEvents().size());
	}
	
	
	public final void testArahiveIdentityClearsSavedPropertiesWhenLastIdentityWasNotGeneric()
	{
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("someUnknownGenericIdentity");
		ArchiverImpl.sharedArchiver().archiveIdentity("someKnownIdentity@example.com");
		
		// Populate and directly archive the test events
		HashMap<String, String> expectedProperties = new HashMap<String, String>();
		expectedProperties.put("testProperty", "testValue");
		
		// Write to the file system directly
		uth_writeObjectToInternalStorageFile(expectedProperties, "KISSmetricsSavedProperties");
		
		uth_unarchiveSavedProperties();
		
		ArchiverImpl.sharedArchiver().archiveIdentity("aDifferentUser@example.com");
		
		assertEquals("No query is created for the archived identity", 0, this.uth_getSavedProperties().size());
	}
	
	
	
	public final void testArchiveAlias() {

		String identity = "aliasTestIdentity";
		ArchiverImpl.sharedArchiver().archiveFirstIdentity(identity);

		String aliasString = "testAlias@example.com";
		String expectedRecord = queryEncoder.createAliasQuery(identity, aliasString);
		ArchiverImpl.sharedArchiver().archiveAlias(identity, aliasString);
		
		assertEquals("archiveAlias creates an expected alias record", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveAliasIgnoresNullString() {
		
		ArchiverImpl.sharedArchiver().archiveFirstIdentity("testUser@example.com");
		ArchiverImpl.sharedArchiver().archiveAlias(null, null);

		assertEquals("archiveFirstIdentity does not apply an identitfy action from a null identity", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveAliasIgnoresEmptyStrings() {

		ArchiverImpl.sharedArchiver().archiveFirstIdentity("testUser@example.com");
		ArchiverImpl.sharedArchiver().archiveAlias("", "");

		assertEquals("archiveFirstIdentity does not apply an identify action from an empty identity", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testGetFirstRecordReturnsRecord() {
		
		long timestamp = System.currentTimeMillis()/1000;
		String expectedUrlString = queryEncoder.createEventQuery("testGetFirstRecordReturnsRecord", null, "testuser@example.com", timestamp);
		
		List<String> sendQueue = this.uth_getSendQueue();
		sendQueue.add(expectedUrlString);
		
		assertEquals("getFirstRecord returns the record at the top of the stack", expectedUrlString, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testGetFirstRecordReturnsNullFromEmptySendQueue() {
		assertEquals("getFirstRecord returns null when empty", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testRemoveFirstRecord() {
		List<String> sendQueue = this.uth_getSendQueue();
		
		// First add 1 new record
		long timestamp1 = System.currentTimeMillis()/1000;
		String testRecord1 = queryEncoder.createEventQuery("firstRecord", null, "testuser@example.com", timestamp1);
		sendQueue.add(testRecord1);
		
		// And add 2nd new record
		long timestamp2 = System.currentTimeMillis()/1000;
		String testRecord2 = queryEncoder.createEventQuery("secondRecord", null, "testuser@example.com", timestamp2);
		sendQueue.add(testRecord2);
		
		ArchiverImpl.sharedArchiver().removeQueryString(0);
		
		assertEquals("removeFirstRecord removes the first record from the top of the stack", testRecord2, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testGetQueueCountWhenEmpty() {
		assertEquals("getQueueCount returns 0 when empty", 0, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	public final void testGetQueueCountForOne() {
		List<String> sendQueue = this.uth_getSendQueue();
		sendQueue.add("AnyTestStringWorks");
		
		assertEquals("getQueueCount returns correct count", 1, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	public final void testArchiveRecordWithNullProperties() {
		String eventNameString = "testArchiveRecord";
		String identity = ArchiverImpl.sharedArchiver().getIdentity();

		long timestamp = System.currentTimeMillis()/1000;
		String expectedRecord = queryEncoder.createEventQuery(eventNameString, null, identity, timestamp);
		
		// Use archiveRecord to create a similar record. (timestamp may not match expected record)
		ArchiverImpl.sharedArchiver().archiveEvent(eventNameString, null, RecordCondition.RECORD_ALWAYS);
		
		// !!!: The timestamp of the archiveRecord and the expectedRecord may not match.
		// If this is a frequent result we should remove the &t=xxxxxxxxx from
		// both the archiveRecord and expectedRecord prior to comparison.
		assertEquals("archiveRecord without properties creates an expected event record", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveRecordWithProperties() {
		
		String eventNameString = "testArchiveRecord";
		
		String identityString = ArchiverImpl.sharedArchiver().getIdentity();
		
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		propertiesMap.put("propertyOne", "testPropertyOne");
		propertiesMap.put("propertyTwo", "testPropertyTwo");
		
		// Create an expected record with null properties
		long timestamp = System.currentTimeMillis()/1000;
		String expectedRecord = queryEncoder.createEventQuery(eventNameString, propertiesMap, identityString, timestamp);
		
		// Use archiveRecord to create a similar record. (timestamp may not match expected record)
		ArchiverImpl.sharedArchiver().archiveEvent(eventNameString, propertiesMap, RecordCondition.RECORD_ALWAYS);
		
		// !!!: The timestamp of the archiveRecord and the expectedRecord may not match.
		// If this is a frequent result we should remove the &t=xxxxxxxxx from
		// both the archiveRecord and expectedRecord prior to comparison.
		assertEquals("archiveRecord with properties creates an expected event record", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}

	
	public final void testArchiveRecordIgnoresNull() {
		ArchiverImpl.sharedArchiver().archiveEvent(null, null, RecordCondition.RECORD_ALWAYS);
		assertEquals("archiveRecord ignores null events", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}

	
	public final void testArchiveRecordIgnoresEmptyString() {
		ArchiverImpl.sharedArchiver().archiveEvent("", null, RecordCondition.RECORD_ALWAYS);
		assertEquals("archiveRecord ignores empty events", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveProperties() {
		String identityString = ArchiverImpl.sharedArchiver().getIdentity();
		
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		propertiesMap.put("propertyOne", "testPropertyOne");
		propertiesMap.put("propertyTwo", "testPropertyTwo");
		
		// Create an expected record with our test properties
		long timestamp = System.currentTimeMillis()/1000;
		String expectedRecord = queryEncoder.createPropertiesQuery(propertiesMap, identityString, timestamp);
		
		ArchiverImpl.sharedArchiver().archiveProperties(propertiesMap);
		
		assertEquals("archiveProperty creates an expected property record", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchivePropertiesIgnoresNullProperties() {
		
		ArchiverImpl.sharedArchiver().archiveProperties(null);
		
		assertEquals("archiveProperties ignores null properties", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchivePropertiesIgnoresEmptyProperties() {
		
		HashMap<String, String> propertiesMap = new HashMap<String, String>();
		ArchiverImpl.sharedArchiver().archiveProperties(propertiesMap);
		
		assertEquals("archiveProperties ignores empty properties", null, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveRecordOnce() {
		
		String identity = ArchiverImpl.sharedArchiver().getIdentity();
		
		String eventName = "uniqueRecord";

		long timestamp = System.currentTimeMillis()/1000;
		String expectedRecord = queryEncoder.createEventQuery(eventName, null, identity, timestamp);
		
		ArchiverImpl.sharedArchiver().archiveEvent(eventName, null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
		
		assertEquals("archiveRecordOnce adds an event to the sendQueue when the event is unique", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveRecordOnceIgnoresSubsequentCalls() {
		
		String eventNameString = "uniqueRecord";
	
		ArchiverImpl.sharedArchiver().archiveEvent(eventNameString, null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
		ArchiverImpl.sharedArchiver().archiveEvent(eventNameString, null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
		ArchiverImpl.sharedArchiver().archiveEvent(eventNameString, null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
		
		assertEquals("archiveRecordOnce must not archive subsequent calls to record the same event name", 1, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	public final void testArchiveRecordOnceAllowedAfterClearingSavedEvents() {
		ArchiverImpl.sharedArchiver().archiveEvent("eventName", null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
		ArchiverImpl.sharedArchiver().clearSavedIdEvents();
		ArchiverImpl.sharedArchiver().archiveEvent("eventName", null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
		
		assertEquals("archiveRecordOnce allows for recording unique events of the same name after clearing saved events", 2, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	public final void testArchiveDistinctPropertyValue() {
		
		String firstIdentity = ArchiverImpl.sharedArchiver().getIdentity();
		
		String propertyName = "distinctProperty";
		String propertyValue = "testDistinctValue";
		
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put(propertyName, propertyValue);
		
		// Create an expected record with nil properties
		long timestamp = System.currentTimeMillis()/1000;
		String expectedRecord = queryEncoder.createPropertiesQuery(propertyMap, firstIdentity, timestamp);
		
		ArchiverImpl.sharedArchiver().archiveDistinctProperty(propertyName, propertyValue);
	
		assertEquals("archiveDistinctProperty adds the expected property to the sendQueue when the value is distinct", expectedRecord, ArchiverImpl.sharedArchiver().getQueryString(0));
	}
	
	
	public final void testArchiveDistinctPropertyAcceptsNewValues() {
		
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testDistinctValue");
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testNewDistinctValue");
		
		assertEquals("archiveDistinctProperty allows for updates to properties when the value is distinct", 2, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	public final void testArchiveDistinctPropertyIgnoresOldValue() {
		
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testDistinctValue");
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testDistinctValue");
		
		assertEquals("archiveDistinctProperty ignores attemps to set properties with the same/current value", 1, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	public final void testArchiveDistinctPropertyAllowsValueToggling() {
		
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testDistinctValue");
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testnewDistinctValue");
		ArchiverImpl.sharedArchiver().archiveDistinctProperty("distinctProperty", "testDistinctValue");
		
		assertEquals("archiveDistinctProperty allows for toggling of property values", 3, ArchiverImpl.sharedArchiver().getQueueCount());
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveSavedEvents() {

		List<String> savedEvents = this.uth_getSavedIdEvents();
		savedEvents.add("testEvent");
		
		this.uth_archiveSavedIdEvents();
		
		// Read from the file system directly
		List<String> archivedEvents = (List<String>) this.uth_getInternalStorageObjectOfType(ArrayList.class, getInstrumentation().getTargetContext(), "KISSmetricsSavedEvents");

		assertEquals("archiveSavedEvents must retain integrity upon archiving", savedEvents, archivedEvents);
	}
	
	
	public final void testUnarchiveSavedEvents() {
		
		// Populate and directly archive the test events
		List<String> expectedEvents = new ArrayList<String>();
		expectedEvents.add("testEvent");
	
		// Write to the file system directly
		uth_writeObjectToInternalStorageFile(expectedEvents, "KISSmetricsSavedEvents");
		
		// Unarchive saved events
		uth_unarchiveSavedIdEvents();
		List<String> savedEvents = uth_getSavedIdEvents();
		
		assertEquals("archiveSavedEvents must retain integrity upon unarchiving", expectedEvents, savedEvents);
	}
	
	
	public final void testClearSavedEvents() {
		
		List<String> savedEvents = this.uth_getSavedIdEvents();
		savedEvents.add("testEvent");
		
		this.uth_archiveSavedIdEvents();
		
		ArchiverImpl.sharedArchiver().clearSavedIdEvents();
		
		assertEquals("clearSavedEvents empties the savedEvents array", 0, this.uth_getSavedIdEvents().size());
	}
	
	
	@SuppressWarnings("unchecked")
	public final void testArchiveSavedProperties() {
		
		HashMap<String, String> expectedProperties = this.uth_getSavedProperties();
		expectedProperties.put("testProperty", "testValue");

		this.uth_archiveSavedProperties();
		
		// Read from the file system directly
		HashMap<String, String> archivedProperties = (HashMap<String, String>) this.uth_getInternalStorageObjectOfType(HashMap.class, getInstrumentation().getTargetContext(), "KISSmetricsSavedProperties");
		
		assertEquals("archiveSavedProperties must retain integrity upon archiving", expectedProperties, archivedProperties);
	}
	
	
	public final void testUnarchiveSavedProperties() {
		
		// Populate and directly archive the test properties
		HashMap<String, String> expectedProperties = new HashMap<String, String>();
		expectedProperties.put("testProperty", "testValue");
		
		// Write to the file system directly
		uth_writeObjectToInternalStorageFile(expectedProperties, "KISSmetricsSavedProperties");
		
		uth_unarchiveSavedProperties();
		HashMap<String, String> savedProperties = uth_getSavedProperties();
		
		assertEquals("archiveSavedProperties must retain integrity upon unarchiving", expectedProperties, savedProperties);
	}
	
	
	public final void testClearSavedProperties() {
		
		HashMap<String, String> savedProperties = uth_getSavedProperties();
		savedProperties.put("testProperty", "testValue");
		this.uth_archiveSavedProperties();
		
		ArchiverImpl.sharedArchiver().clearSavedProperties();
		
		assertEquals("clearSavedProperties empties the savedProperties HashMap", 0, uth_getSavedProperties().size());
	}

	
	@SuppressWarnings("unchecked")
	public final void testArchiveData() {

		List<String> expectedSendQueue = this.uth_getSendQueue();
		expectedSendQueue.add("https://trk.kissmetrics.com/a?_k=b8f68fe5004d29bcd21d3138b43ae755a16c12cf&_x=ios/2.0&_p=testnewuser%40example.com&_n=testolduser%40example.com");
		
		this.uth_archiveSendQueue();
		
		// Read from the file system directly
		List<String> returnedSendQueue = (List<String>) this.uth_getInternalStorageObjectOfType(ArrayList.class, getInstrumentation().getTargetContext(), "KISSmetricsActions");
		
		assertEquals("archiveData retains the sendQueue after archiving", expectedSendQueue, returnedSendQueue);
	}
}
