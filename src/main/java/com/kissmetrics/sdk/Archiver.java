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

import com.kissmetrics.sdk.KISSmetricsAPI.RecordCondition;


/**
 * Archiver interface
 * 
 */
public interface Archiver {

	public void archiveInstallUuid(final String installUuid);
	public void archiveDoTrack(final boolean doTrack);
	public void archiveDoSend(final boolean doSend);
	public void archiveVerificationExpDate(final long expDate);
	public void archiveHasGenericIdentity(final boolean hasGenericIdentity);
	public void archiveAppVersion(final String appVersion);
	public void archiveFirstIdentity(final String identity);
	public void archiveEvent(final String name, final HashMap<String, String> properties, RecordCondition condition);
	public void archiveProperties(final HashMap<String, String> properties);
	public void archiveDistinctProperty(final String name, final String value);
	public void archiveIdentity(final String identity);
	public void archiveAlias(final String alias, final String identity);
	
	public void clearSendQueue();
	public void clearSavedIdEvents();
	public void clearSavedProperties();
	public String getQueryString(final int index);
	public void removeQueryString(final int index);
	public int getQueueCount();
	
	public String getInstallUuid();
	public long getVerificationExpDate();
	public String getBaseUrl();
	public String getIdentity();
	public String getAppVersion();
	public boolean hasGenericIdentity();
	public boolean getDoSend();
	public boolean getDoTrack();
}
