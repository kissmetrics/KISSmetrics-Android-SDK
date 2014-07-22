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
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.kissmetrics.sdk.KISSmetricsAPI.RecordCondition;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * ArchiverImpl
 * 
 * Handles read/write of settings, identity, recorded events and properties.
 * Recorded events and properties are URL encoded before being saved to a send queue.
 * 
 */
public class ArchiverImpl implements Archiver {

	private static final String  CLIENT_TYPE = "mobile_app";
	private static final String  USER_AGENT = "kissmetrics-android/2.0.5";
	private static final String  IDENTITY_PREF = "KISSmetricsIdentity";
	private static final String  SETTINGS_FILE = "KISSmetricsSettings";
	private static final String  INSTALL_UUID_KEY = "installUuid";
	private static final String  HAS_GENERIC_IDENTITY_KEY = "hasGenericIdentity";
	private static final String  VERIFICATION_EXP_DATE_KEY = "verification_exp_date";
	private static final String  DO_TRACK_KEY = "doTrack";
	private static final String  DO_SEND_KEY = "doSend";
	private static final String  BASE_URL_KEY = "baseUrl";
	private static final String  APP_VERSION_KEY = "appVersionKey";
	private static final String  ACTIONS_FILE = "KISSmetricsActions";
	private static final String  SAVED_ID_EVENTS_FILE = "KISSmetricsSavedEvents";
	private static final String  SAVED_INSTALL_EVENTS_FILE = "KISSmetricsSavedInstallEvents";
	private static final String  SAVED_PROPERTIES_FILE = "KISSmetricsSavedProperties";
	private static final long    VERIFICATION_EXP_DATE_DEFAULT = 0L;
	private static final boolean HAS_GENERIC_IDENTITY_DEFAULT = false;
	private static final boolean DO_TRACK_DEFAULT = true;
	private static final boolean DO_SEND_DEFAULT = false;
	private static final String  BASE_URL_DEFAULT = "https://trk.kissmetrics.com";
	
	private static ArchiverImpl sharedArchiver = null;

	private String key;
	private Context context;
	private QueryEncoder queryEncoder;
	private HashMap<String, Object> settings;
	private String lastIdentity;
	private List<String> sendQueue;
	private List<String> savedIdEvents;
	private List<String> savedInstallEvents;
	private HashMap<String, String> savedProperties;
	

	/**
	 * Initializes the private singleton.
	 * 
	 * @param productKey  KISSmetrics product key
	 * @param applicationContext  Android application context
	 */
	private ArchiverImpl(final String key, final Context applicationContext) {
		
		this.key = key;
		this.context = applicationContext;
		this.queryEncoder = new QueryEncoder(this.key, CLIENT_TYPE, USER_AGENT);
		
		synchronized (this) {
			unarchiveSettings();
			unarchiveIdentity();
			unarchiveSendQueue();
			unarchiveSavedInstallEvents();
			unarchiveSavedIdEvents();
			unarchiveSavedProperties();
		}
	}

	
	/**
	 * Initializes and/or returns the ArchiverImpl singleton instance. This method must be called 
	 * before making any other calls.
	 * 
	 * @param productKey  KISSmetrics product key
	 * @param applicationContext  Android application context
	 * @return Archiver singleton instance
	 */
	public static synchronized ArchiverImpl sharedArchiver(final String productKey, 
														   final Context applicationContext) {
		if (sharedArchiver == null) {
			sharedArchiver = new ArchiverImpl(productKey, applicationContext);
		}
		return sharedArchiver;
	}
	
	
	/**
	 * @return ArchiverImpl singleton instance
	 */
	public static synchronized ArchiverImpl sharedArchiver() {
		
		if (sharedArchiver == null) {
			Log.w("KISSmetricsAPI", 
				  "KISSMetricsAPI: WARNING - Returning null object in sharedAPI as " +
				  "sharedArchiver(<API_KEY>, <Context>): has not been called.");
		}
		return sharedArchiver;
	}
	
	
    
    /************************************************
     * Private methods
     ************************************************/
	
	/**
	 * Unarchives settings from Internal Storage or establishes defaults. 
	 * Sets value of mSettings with unarchived settings.
	 * 
	 * Suppresses warnings for ObjectInputStream readObject cast to HashMap<String, Object>.
	 */
	@SuppressWarnings("unchecked")
	private void unarchiveSettings() {
	
		// Not synch'd as should always be called inside of a sync block !!
		try {
			FileInputStream fis = this.context.openFileInput(SETTINGS_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.settings = (HashMap<String, Object>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			// The KISSmetrics SDK should not cause a customer's app to crash.
			// If a FileNotFoundException or IOException arises we define default settings 
			// and carry on. 
			Log.w("KISSmetricsAPI", "Unable to unarchive saved settings");
		}
		
		// The file doesn't exist yet or there was an error in reading the file. 
		// Create the settings file with default values.
		if (this.settings == null) {
			archiveDefaultSettings();
		}
	}
	
	
	/**
	 * Defines mSettings with default values and archives to Internal Storage.
	 */
	private void archiveDefaultSettings() {
		
		this.settings = new HashMap<String, Object>();
		
		// Apply default settings
		this.settings.put(DO_TRACK_KEY, DO_TRACK_DEFAULT);
		this.settings.put(DO_SEND_KEY, DO_SEND_DEFAULT);
		this.settings.put(BASE_URL_KEY, BASE_URL_DEFAULT);
		this.settings.put(VERIFICATION_EXP_DATE_KEY, VERIFICATION_EXP_DATE_DEFAULT);
		this.settings.put(HAS_GENERIC_IDENTITY_KEY, HAS_GENERIC_IDENTITY_DEFAULT);

		this.archiveSettings();
	}
	
	
	/**
	 * Archives mSettings to Internal Storage
	 */
	private void archiveSettings() {

		try {
			FileOutputStream fos = this.context.openFileOutput(SETTINGS_FILE, 
																Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.settings);
            oos.flush();
            oos.close(); 
		} catch (Exception e) {
			// The KISSmetrics SDK should not cause a customer's app to crash.
			// If a FileNotFoundException or IOException arises we ignore it.
			Log.w("KISSmetricsAPI", "Unable to archive settings");
		}
	}
    
	
	/**
	 * Unarchive's identity from Shared Preferences.
	 * Sets value of mLastIdentity with unarchived identity.
	 */
    private void unarchiveIdentity() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	SharedPreferences pref = this.context.getSharedPreferences(IDENTITY_PREF, 
    																Context.MODE_PRIVATE);
    	
    	// We should not have to protect against a null identity here.
    	// If preferences are cleared by the user while an application is running we will continue 
    	// to use mLastIdentity.
    	// Upon the next true launch of an application a null unarchived identity will trigger the 
    	// creation of a new anon identity.
    	this.lastIdentity = pref.getString("identity", "");
    }

    
	/**
	 * Unarchives saved install events from Internal Storage or establishes a new List object. 
	 * Sets value of mSavedInstallEvents with unarchived saved events.
	 * 
	 * Suppresses warnings for ObjectInputStream readObject cast to List<String>.
	 */
    @SuppressWarnings("unchecked")
    private void unarchiveSavedInstallEvents() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	try {
        	FileInputStream fis = this.context.openFileInput(SAVED_INSTALL_EVENTS_FILE);
        	ObjectInputStream ois = new ObjectInputStream(fis);
        	this.savedInstallEvents = (List<String>) ois.readObject();
        	ois.close();
        } catch (Exception e) {
        	// The KISSmetrics SDK should not cause a customer's app to crash.
        	// If a FileNotFoundException or IOException arises we define mSavedInstallEvents 
        	// as a new ArrayList and carry on.
        	Log.w("KISSmetricsAPI", "Unable to unarchive saved install events");
        }
        	
        if (this.savedInstallEvents == null) {
        	// The file doesn't exist yet or there was an error in reading the file. 
    		// Create the savedInstallEvents file with a new Array List.
        	this.savedInstallEvents = new ArrayList<String>();
        	this.archiveSavedInstallEvents();
        }
    }
    
    
    /**
     * Archives mSavedInstallEvents from Internal Storage.
     */
    private void archiveSavedInstallEvents() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	try {
    		FileOutputStream fos = this.context.openFileOutput(SAVED_INSTALL_EVENTS_FILE, 
    															Context.MODE_PRIVATE);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(this.savedInstallEvents);
    		oos.flush();
    		oos.close();
    	} catch (Exception e) {
    		// The KISSmetrics SDK should not cause a customer's app to crash.
    		// If a FileNotFoundException or IOException arises we ignore it.
    		Log.w("KISSmetricsAPI", "Unable to archive saved install events");
    	}
    }
    
    
	/**
	 * Unarchives saved events from Internal Storage or establishes a new List object. 
	 * Sets value of mSavedIdEvents with unarchived saved events.
	 * 
	 * Suppresses warnings for ObjectInputStream readObject cast to List<String>.
	 */
    @SuppressWarnings("unchecked")
    private void unarchiveSavedIdEvents() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	try {
        	FileInputStream fis = this.context.openFileInput(SAVED_ID_EVENTS_FILE);
        	ObjectInputStream ois = new ObjectInputStream(fis);
        	this.savedIdEvents = (List<String>) ois.readObject();
        	ois.close();
        } catch (Exception e) {
        	// The KISSmetrics SDK should not cause a customer's app to crash.
        	// If a FileNotFoundException or IOException arises we define mSavedIdEvents as a new
        	// ArrayList and carry on.
        	Log.w("KISSmetricsAPI", "Unable to unarchive saved identity events");
        }
        	
        if (this.savedIdEvents == null) {
        	// The file doesn't exist yet or there was an error in reading the file. 
    		// Create the savedIdEvents file with a new Array List.
        	this.savedIdEvents = new ArrayList<String>();
        	this.archiveSavedIdEvents();
        }
    }
    
    
    /**
     * Archives mSavedIdEvents from Internal Storage.
     */
    private void archiveSavedIdEvents() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	try {
    		FileOutputStream fos = this.context.openFileOutput(SAVED_ID_EVENTS_FILE, 
    															Context.MODE_PRIVATE);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(this.savedIdEvents);
    		oos.flush();
    		oos.close();
    	} catch (Exception e) {
    		// The KISSmetrics SDK should not cause a customer's app to crash.
    		// If a FileNotFoundException or IOException arises we ignore it.
    		Log.w("KISSmetricsAPI", "Unable to archive saved identity events");
    	}
    }
    
    
	/**
	 * Unarchives saved properties from Internal Storage or establishes new a HashMap object. 
	 * Sets value of mSavedProperties with unarchived saved properties.
	 * 
	 * Suppresses warnings for ObjectInputStream readObject cast to HashMap<String, String>.
	 */
    @SuppressWarnings("unchecked")
	private void unarchiveSavedProperties() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	try {
    		FileInputStream fis = this.context.openFileInput(SAVED_PROPERTIES_FILE);
    		ObjectInputStream ois = new ObjectInputStream(fis);
    		this.savedProperties = (HashMap<String, String>) ois.readObject();
    		ois.close();
    	} catch (Exception e) {
    		// The KISSmetrics SDK should not cause a customer's app to crash.
        	// If a FileNotFoundException or IOException arises we define mSavedProperites as a new
        	// HashMap and carry on.
    		Log.w("KISSmetricsAPI", "Unable to unarchive saved properties");
    	}
    	
    	if (this.savedProperties == null) {
    		// The file doesn't exist yet or there was an error in reading the file. 
    		// Create the savedProperties file with a new HashMap.
    		this.savedProperties = new HashMap<String, String>();
    		this.archiveSavedProperties();
    	}
    }
    
    
    /**
     * Archives mSavedProperties to Internal Storage.
     */
    private void archiveSavedProperties() {
    	
    	// Not synch'd as should always be called inside of a sync block !!
    	try {
    		FileOutputStream fos = this.context.openFileOutput(SAVED_PROPERTIES_FILE, 
    															Context.MODE_PRIVATE);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
    		oos.writeObject(this.savedProperties);
    		oos.flush();
    		oos.close();
    	} catch (Exception e) {
    		// The KISSmetrics SDK should not cause a customer's app to crash.
    		// If a FileNotFoundException or IOException arises we ignore it.
    		Log.w("KISSmetricsAPI", "Unable to archive saved properties");
    	}
    }
    
    
	/**
	 * Unarchives sendQueue from Internal Storage or establishes a new List<String, String> object. 
	 * Sets value of mSendQueue with unarchived sendQueue.
	 * 
	 * Suppresses warnings for ObjectInputStream readObject cast to List<String, String>.
	 */
    @SuppressWarnings("unchecked")
	private void unarchiveSendQueue() {
    	
    	// Not synch'd as should always be called inside of a synch block !!
    	try {
    		FileInputStream fis = this.context.openFileInput(ACTIONS_FILE);
    		ObjectInputStream ois = new ObjectInputStream(fis);
    		this.sendQueue = (List<String>) ois.readObject();
    		ois.close();
    	} catch (Exception e) {
    		// The KISSmetrics SDK should not cause a customer's app to crash.
        	// If a FileNotFoundException or IOException arises we define mSavedProperites as a new
        	// List<String> and carry on.
    		Log.w("KISSmetricsAPI", "Unable to unarchive data");
    	}
    	
    	if (this.sendQueue == null) {
    		this.sendQueue = new ArrayList<String>();
    		this.archiveSendQueue();
    	}
    }
    
    
    /**
     * Archives mSendQueue to Internal Storage.
     */
    private void archiveSendQueue() {
    	
    	// Not synch'd as should always be called inside of a sync block !!	
    	try {
    		FileOutputStream fos = this.context.openFileOutput(ACTIONS_FILE, Context.MODE_PRIVATE);
    		ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.sendQueue);
            oos.flush();
            oos.close();
    	} catch (Exception e) {
    		// The KISSmetrics SDK should not cause a customer's app to crash.
    		// If a FileNotFoundException or IOException arises we ignore it.
    		Log.w("KISSmetricsAPI", "Unable to archive data");
    	}
    }
	
    
    /**
     * Helper method to get seconds from the system clock.
     * 
     * @return Current time in seconds
     */
    private long currentTimeSeconds() {
    	return System.currentTimeMillis() / 1000L;
    }

	
    
    /************************************************
     * Public methods
     ************************************************/
	
    /**
     * Archives install UUID to Settings > Internal Storage.
     * 
     * @param installUuid  Unique identifier of the application install which may only be 
     * 				 	   archived once.
     */
    public void archiveInstallUuid(final String installUuid) {
    	
    	// Protect from null or empty string, or overriding install UUID value
    	if (installUuid == null || 
    		installUuid.length() == 0 || 
    		this.settings.get(INSTALL_UUID_KEY) != null) {
    		
    		Log.w("KISSmetricsAPI", "installUuid not valid to save");
    		return;
    	}
    	
    	synchronized (this) {
    		this.settings.put(INSTALL_UUID_KEY, installUuid);
    		this.archiveSettings();
    	}
    }
    
    
    /**
     * Archives doTrack boolean to Settings > Internal Storage.
     * 
     * @param doTrack  Setting to control recording of aliases, events and properties.
     */
    public void archiveDoTrack(final boolean doTrack) {
    	
    	synchronized (this) {
    		this.settings.put(DO_TRACK_KEY, doTrack);
    		this.archiveSettings();
    	}
    }
    
    
    /**
     * Archives doSend boolean to Settings > Internal Storage.
     * 
     * @param doSend  Setting to control the uploading of aliases, events and properties.
     */
    public void archiveDoSend(final boolean doSend) {
    	
    	synchronized (this) {
    		this.settings.put(DO_SEND_KEY, doSend);
    		this.archiveSettings();
    	}
    }
    

    /**
     * Archives baseUrl String to Settings > Internal Storage.
     * 
     * @param baseUrl Setting for API base URL path
     */
    public void archiveBaseUrl(final String baseUrl) {
    	
    	// Protect from null or empty string
    	if (baseUrl == null || baseUrl.length() == 0) {
    		Log.w("KISSmetricsAPI", "baseUrl not valid to save");
    		return;
    	}
    	
    	synchronized (this) {
    		this.settings.put(BASE_URL_KEY, baseUrl);
    		this.archiveSettings();
    	}
    }
    
    
    /**
     * Archives doTrack verification expiration date to Settings > Internal Storage.
     * 
     * @param expDate  Milliseconds from unix epoch when verification expires.
     */
    public void archiveVerificationExpDate(final long expDate) {
    	synchronized (this) {
    		this.settings.put(VERIFICATION_EXP_DATE_KEY, expDate);
    		this.archiveSettings();
    	}
    }
    
    
    /**
     * Archives hasGenericIdentity boolean to Settings > Internal Storage.
     * 
     * @param hasGenericIdentity setting to indicate that a proper identity has been given.
     */
    public void archiveHasGenericIdentity(final boolean hasGenericIdentity) {
    	synchronized (this) {
    		this.settings.put(HAS_GENERIC_IDENTITY_KEY, hasGenericIdentity);
    	}
    }
    
    
    /**
     * Archives appVersion to Settings > Internal Storage.
     * 
     * @param appVersion String app version name.
     */
    public void archiveAppVersion(final String appVersion) {
    	synchronized (this) {
    		this.settings.put(APP_VERSION_KEY, appVersion);
    		this.archiveSettings();
    	}
    }
    
    
    /**
     * Archives first identity to Shared Preferences.
     * Used when establishing a new identity that is not aliased to any other identity.
     * Nothing is passed to KISSmetrics as a result of archiving a first identity.
     * 
     * @param identity  A new, anonymous identity
     */
    public void archiveFirstIdentity(final String identity) {

    	if (identity == null || identity.length() == 0) {
    		return;
    	}
    	
    	synchronized (this) {
    		this.lastIdentity = identity;
    		this.archiveHasGenericIdentity(true);
    		SharedPreferences pref = this.context.getSharedPreferences(IDENTITY_PREF, 
    																	Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = pref.edit();
			prefEditor.putString("identity", this.lastIdentity);
			prefEditor.commit();
    	}
    }
   
    public void archiveEvent(final String name, final HashMap<String, String> properties, RecordCondition condition) {
    	
    	if (name == null || name.length() == 0) {
    		Log.w("KISSmetricsAPI", 
    			  "Attempted to record an event with null or empty event name. Ignoring");
    		return;
    	}
    	
    	switch (condition) {
    	
    		case RECORD_ALWAYS: 
        		// Nothing else to check, continue with archiving.
        		break;
        		
        	case RECORD_ONCE_PER_IDENTITY:
        		
        		synchronized (this) {
        			if (this.savedIdEvents != null && this.savedIdEvents.contains(name)) {
        				// Recorded event already exists
        				return;
            		} else {
            			this.savedIdEvents.add(name);
            			archiveSavedIdEvents();
            		}
        		}
        		break;
        		
        	case RECORD_ONCE_PER_INSTALL:
        		synchronized (this) {
        			if (this.savedInstallEvents != null && this.savedInstallEvents.contains(name)) {
        				// Recorded event already exists
        				return;
        			} else {
            			this.savedInstallEvents.add(name);
            			archiveSavedInstallEvents();
            		}
        		}
        		break;
        		
        	default: 
        		// Continue with archiving the event.
        		break;
    	}
   
    	// Intentionally called outside of synchronized, method is already synchronized.
    	archiveEvent(name, properties);
    }
    
    
    /**
     * Adds an event (with or without properties) to the sendQueue.
     * Archives the updated sendQueue to Internal Storage.
     * 
     * @param name  Name of the event to record
     * @param properties  A HashMap of 1 or more properties, or null
     */
    private void archiveEvent(final String name, final HashMap<String, String> properties) {

    	synchronized (this) {
			String theUrl = this.queryEncoder.createEventQuery(name, properties, 
																this.lastIdentity, 
																currentTimeSeconds());
			this.sendQueue.add(theUrl);
			archiveSendQueue();
		}
    }
    
    
    /**
     * Adds 1 or more properties to the sendQueue.
     * Archives the updated sendQueue to Internal Storage.
     * 
     * @param properties  HashMap of 1 or more properties.
     */
    public void archiveProperties(final HashMap<String, String> properties) {

    	if (properties == null || properties.isEmpty()) {
    		Log.w("KISSmetricsAPI", 
    			  "Attempted to set properties with no properties in it. Ignoring");
    		return;
    	}
    	
    	synchronized (this) {
        	String theUrl = this.queryEncoder.createPropertiesQuery(properties, this.lastIdentity, 
        															 currentTimeSeconds());
    		this.sendQueue.add(theUrl);
    		archiveSendQueue();
    	}
    }
    
    
    /**
     * Adds 1 property to the sendQueue if the value is different than 
     * the current value for the provided name(key).
     * Archives the updated sendQueue to Internal Storage.
     * Adds this property to mSavedProperties and archives to savedProperties.
     * 
     * @param name  Property name(key)
     * @param value  Property value for name(key)
     */
    public void archiveDistinctProperty(final String name, final String value) {
    	
    	synchronized (this) {
    		
    		String propertyValue = this.savedProperties.get(name);
    		if (propertyValue != null && propertyValue.equals(value)) {
        		//Log.w("KISSmetricsAPI", "Distinct property already set with same value");
        		return;
    		} else {
    			this.savedProperties.put(name, value);
    			archiveSavedProperties();
    		}
    	}
    	
    	// Pass it to the archive method
    	// Intentionally called outside of synchronized block!
    	HashMap<String, String > propertyHashMap = new HashMap<String, String>();
    	propertyHashMap.put(name, value);
    	archiveProperties(propertyHashMap);
    }
    
    
    /**
     * Sets value of mLastIdentity. 
     * Archives mLastIdentity to Shared Preferences. 
     * Adds an alias query to the sendQueue.
     * Archives the updated sendQueue to Internal Storage.
     * 
     * @param identity  A new user identity
     */
    public void archiveIdentity(final String identity) {
    	
    	if (identity == null || identity.length() == 0 || identity.equals(this.lastIdentity)) {
    		Log.w("KISSmetricsAPI", "Attempted to use null, empty or existing identity. Ignoring");
    		return;
    	}
    	
    	String theUrl = this.queryEncoder.createAliasQuery(identity, this.lastIdentity);
    	
    	synchronized (this) {
    		// Now we must update the identity on disk. No need to wait until the alias has been
            // accepted on the server, as calls are FIFO and encoded with current identity.
    		this.lastIdentity = identity;
    		
    		SharedPreferences pref = this.context.getSharedPreferences(IDENTITY_PREF, 
    																	Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = pref.edit();
			prefEditor.putString("identity", this.lastIdentity);
			prefEditor.commit();
    		
			// Only add the alias query if the current identity is a generic identity
			if (this.hasGenericIdentity()) {
				this.archiveHasGenericIdentity(false);
				this.sendQueue.add(theUrl);
	    		archiveSendQueue();
			}
			else {
				// This is expected to be an entirely different user.
				// Clear saved Events and Properties just as we would when clearing an Identity
				this.clearSavedEvents();
				this.clearSavedProperties();
			}
    	}
    }
    

    /**
     * Adds an alias query to the sendQueue.
     * Archives the updated sendQueue to Internal Storage.
     * 
     * @param alias  An alias to an identity
     * @param identity  A known user identity
     */
    public void archiveAlias(final String alias, final String identity) {
    	
    	if (alias == null || alias.length() == 0 || identity == null || identity.length() == 0) {
    		Log.w("KISSmetricsAPI", 
    			  "Attempted to use null or empty identities in " +
    			  "alias ("+alias+" and "+identity+"). Ignoring.");
    		return;
    	}

    	String theUrl = this.queryEncoder.createAliasQuery(alias, identity);
    	
    	synchronized (this) {
			this.sendQueue.add(theUrl);
			archiveSendQueue();
		}
    }

    
    /**
     * Replaces mSendQueue with a new(empty) List.
     * Archives the updated sendQueue to Internal Storage.
     */
    public void clearSendQueue() {
    	
    	synchronized (this) {
    		this.sendQueue = new ArrayList<String>();
    		archiveSendQueue();
    	}
    }
    
    
    /**
     * Replaces mSavedIdEvents with a new(empty) List.
     * Archives the updated savedIdEvents to Internal Storage.
     */
    public void clearSavedIdEvents() {
    	
    	synchronized (this) {
    		this.savedIdEvents = new ArrayList<String>();
    		archiveSavedProperties();
    	}
    }
    
    
    /**
     * Replaces mSavedProperties with a new(empty) HashMap.
     * Archives the updated savedProperties to Internal Storage.
     */
    public void clearSavedProperties() {
    	
    	synchronized (this) {
			this.savedProperties = new HashMap<String, String>();
			archiveSavedProperties();
		}
    }
    
    
    /**
     * Returns the query string from the sendQueue at the specified index.
     * 
     * @param index  Query string index in the sendQueue.
     * @return Query string of the requested index.
     */
    public String getQueryString(final int index) {
    	
    	synchronized (this) {
    		if (sendQueue.isEmpty()) {
    			return null;
    		}
    		return sendQueue.get(index);
    	}
    }
    
    
    /**
     * Removes a query string from the sendQueue at the specified index.
     * Intended to be used to remove successful API queries or malformed queries to prevent 
     * repeated attempts to query the KISSmetrics API with URL strings that will never succeed.
     * 
     * @param index  Query string index in the sendQueue
     */
    public void removeQueryString(final int index) {
    	
    	synchronized (this) {
    		// As an added precaution we check the length of the sendQueue before removing.
    		if (sendQueue.size() > 0) {
    			sendQueue.remove(index);
    			// Persist the shorter queue
    			archiveSendQueue();
    		}
    	}
    }
    
    
    /**
     * @return The number of query strings in the sendQueue.
     */
    public int getQueueCount() {
    	
    	synchronized (this) {
    		if (sendQueue == null) {
    			return 0;
    		}
    		return sendQueue.size();
    	}
    }
    
    
    public String getInstallUuid() {
    	return (String)this.settings.get(INSTALL_UUID_KEY);
    }
    
    
    public long getVerificationExpDate() {
    	
    	if (this.settings.containsKey(VERIFICATION_EXP_DATE_KEY)) {
    		return (Long)this.settings.get(VERIFICATION_EXP_DATE_KEY);
    	}
    	
    	return VERIFICATION_EXP_DATE_DEFAULT;
    }
    
    
    public boolean hasGenericIdentity() {
    	
    	if (this.settings.containsKey(HAS_GENERIC_IDENTITY_KEY)) {
    		return (Boolean) this.settings.get(HAS_GENERIC_IDENTITY_KEY);
    	}
    	
    	return HAS_GENERIC_IDENTITY_DEFAULT;
    }
    
    
    /**
     * @return The last archived appVersion setting. 
     */
    public String getAppVersion() {
    	
    	if (this.settings.containsKey(APP_VERSION_KEY)) {
    		return (String)this.settings.get(APP_VERSION_KEY);
    	}
    	
    	return null;
    }
    
    
    public boolean getDoSend() {
    	
    	if (this.settings.containsKey(DO_SEND_KEY)) {
    		return (Boolean)this.settings.get(DO_SEND_KEY);
    	}
    	
    	return DO_SEND_DEFAULT;
    }
    
    public boolean getDoTrack() {
    	
    	if (this.settings.containsKey(DO_TRACK_KEY)) {
    		return (Boolean)this.settings.get(DO_TRACK_KEY);
    	}
    		
    	return DO_TRACK_DEFAULT;
    }
    
    public String getBaseUrl() {
    	
    	if (this.settings.containsKey(BASE_URL_KEY)) {
    		return (String)this.settings.get(BASE_URL_KEY);
    	}
    	
    	return BASE_URL_DEFAULT;
    }
    
    public String getIdentity() {
    	return lastIdentity;
    }
}
