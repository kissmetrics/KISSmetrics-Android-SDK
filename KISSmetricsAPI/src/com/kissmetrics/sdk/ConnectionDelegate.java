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


/**
 * Connection Delegate interface
 * 
 */
public interface ConnectionDelegate {
	
	
	/**
	 * Callback method for completed HttpURLConnections of KISSmetrics API queries  
	 * 
	 * @param urlString URL encoded API query string of the completed connection.
	 * @param success  True for acceptable query response codes.
	 * @param malformed  True if the provided query string was malformed.
	 */
	void connectionComplete(String urlString, boolean success, boolean malformed);
}
