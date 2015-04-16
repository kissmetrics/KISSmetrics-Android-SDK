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

import java.util.Map;

import com.kissmetrics.sdk.KISSmetricsAPI.RecordCondition;

/**
 * Archiver interface
 */
public interface Archiver {
  void archiveInstallUuid(String installUuid);

  void archiveDoTrack(boolean doTrack);

  void archiveDoSend(boolean doSend);

  void archiveVerificationExpDate(long expDate);

  void archiveHasGenericIdentity(boolean hasGenericIdentity);

  void archiveAppVersion(String appVersion);

  void archiveFirstIdentity(String identity);

  void archiveEvent(String name, Map<String, String> properties, RecordCondition condition);

  void archiveProperties(Map<String, String> properties);

  void archiveDistinctProperty(String name, String value);

  void archiveIdentity(String identity);

  void archiveAlias(String alias, String identity);

  void clearSendQueue();

  void clearSavedIdEvents();

  void clearSavedProperties();

  String getQueryString(int index);

  void removeQueryString(int index);

  int getQueueCount();

  String getInstallUuid();

  long getVerificationExpDate();

  String getBaseUrl();

  String getIdentity();

  String getAppVersion();

  boolean hasGenericIdentity();

  boolean getDoSend();

  boolean getDoTrack();
}
