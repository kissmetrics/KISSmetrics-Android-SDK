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


/**
 * TrackingRunnables non-tracking state
 * 
 * Provides Runnable objects for non-tracking state.
 * 
 */
public class TrackingRunnablesNonTrackingState implements TrackingRunnables {

	
	/**
	 * Ignores calls to set a user identity while in a non-tracking state.
	 * 
	 * @param identity  User identity String.
	 * @param archiver  The instance of Archiver to use.
	 * @param kmapi  The instance of KISSmetricsAPI where initiation of recursive send should occur.
	 * @return A no action Runnable.
	 */
	public Runnable identify(String identity, Archiver archiver, KISSmetricsAPI kmapi) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}

	
	/**
	 * Ignores calls to set a user alias while in a non-tracking state.
	 * 
	 * @param alias  User alias String.
	 * @param identity  User identity String.
	 * @param archiver  The instance of Archiver to use.
	 * @param kmapi  The instance of KISSmetricsAPI where initiation of recursive send should occur.
	 * @return A no action Runnable.
	 */
	public Runnable alias(String alias, String identity, Archiver archiver, KISSmetricsAPI kmapi) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}
	
	
	/**
	 * Ignores calls to archive an unassociated user identity while in a non-tracking state. 
	 * 
	 * @param identity  User identity String.
	 * @param archiver  The instance of Archiver to use.
	 * @return A no action Runnable.
	 */
	public Runnable clearIdentity(String newIdentity, Archiver archiver) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}

	
	/**
	 * Ignores calls to record an event while in a non-tracking state.
	 * 
	 * @param name  Event name.
	 * @param properties  A HashMap of property names/keys and value pairs or null.
	 * @param archiver  The instance of Archiver to use.
	 * @param kmapi  The instance of KISSmetricsAPI where initiation of recursive send should occur.
	 * @return A no action Runnable.
	 */
	public Runnable record(String name, HashMap<String, String> properties, Archiver archiver, 
						   KISSmetricsAPI kmapi) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}

	
	/**
	 * Ignores calls to archive and send an event once while in a non-tracking state.
	 * 
	 * @param name  Event name.
	 * @param archiver  The instance of Archiver to use.
	 * @param kmapi  The instance of KISSmetricsAPI where initiation of recursive send should occur.
	 * @return A no action Runnable.
	 */
	public Runnable recordOnce(String name, Archiver archiver, KISSmetricsAPI kmapi) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}

	
	/**
	 * Creates a Runnable to archive and send properties while in a non-tracking state.
	 * 
	 * @param properties  A HashMap of 1 or more property name/keys and value pairs.
	 * @param archiver  The instance of Archiver to use.
	 * @param kmapi  The instance of KISSmetricsAPI where initiation of recursive send should occur.
	 * @return A no action Runnable.
	 */
	public Runnable set(HashMap<String, String> properties, Archiver archiver, 
						KISSmetricsAPI kmapi) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}

	
	/**
	 * Creates a Runnable to archive and send distinct properties while in a non-tracking state.
	 * 
	 * @param key  A property name/key.
	 * @param value A value for the provided property name/key.
	 * @param archiver  The instance of Archiver to use.
	 * @param kmapi  The instance of KISSmetricsAPI where initiation of recursive send should occur.
	 * @return A no action Runnable.
	 */
	public Runnable setDistinct(String key, String value, Archiver archiver, KISSmetricsAPI kmapi) {
		
		Runnable runnable = new Runnable() { 
			public void run() {
				// No action taken
			}
		};

		return runnable;
	}

}
