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

import com.kissmetrics.sdk.KISSmetricsAPI.RecordCondition;

import java.util.Map;

/**
 * Classes implementing this interface provide Runnable objects to
 * handle tracking for specific tracking states.
 */
public interface TrackingRunnables {
  /**
   * Creates a Runnable to archive and send an identity for the current tracking state.
   *
   * @param identity User identity String.
   * @param archiver The instance of Archiver to use.
   * @param kmapi    The instance of KISSmetricsAPI where initiation of recursive send should occur.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable identify(String identity, Archiver archiver, KISSmetricsAPI kmapi);

  /**
   * Creates a Runnable to archive and send an alias for the current tracking state.
   *
   * @param alias    User alias String.
   * @param identity User identity String.
   * @param archiver The instance of Archiver to use.
   * @param kmapi    The instance of KISSmetricsAPI where initiation of recursive send should occur.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable alias(String alias, String identity, Archiver archiver, KISSmetricsAPI kmapi);

  /**
   * Creates a Runnable to archive an unassociated user identity for the current tracking state.
   *
   * @param newIdentity User identity String.
   * @param archiver    The instance of Archiver to use.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable clearIdentity(String newIdentity, Archiver archiver);

  /**
   * Creates a Runnable to archive and send an event with or without properties(null) for the
   * current tracking state.
   *
   * @param name       Event name.
   * @param properties A HashMap of property names/keys and value pairs or null.
   * @param condition  A RecordCondition of always, per installation or per identity.
   * @param archiver   The instance of Archiver to use.
   * @param kmapi      The instance of KISSmetricsAPI where initiation of recursive send should occur.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable record(String name, Map<String, String> properties,
                  RecordCondition condition, Archiver archiver,
                  KISSmetricsAPI kmapi);

  /**
   * Creates a Runnable to archive and send an event only once per identity for the current
   * tracking state.
   *
   * @param name     Event name.
   * @param archiver The instance of Archiver to use.
   * @param kmapi    The instance of KISSmetricsAPI where initiation of recursive send should occur.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable recordOnce(String name, Archiver archiver, KISSmetricsAPI kmapi);

  /**
   * Creates a Runnable to archive and send 1 or more user properties for the current tracking
   * state.
   *
   * @param properties A HashMap of 1 or more property name/keys and value pairs.
   * @param archiver   The instance of Archiver to use.
   * @param kmapi      The instance of KISSmetricsAPI where initiation of recursive send should occur.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable set(Map<String, String> properties, Archiver archiver, KISSmetricsAPI kmapi);

  /**
   * Creates a Runnable to archive and send properties with a new or different value for the
   * provided key for the current tracking state.
   *
   * @param key      A property name/key.
   * @param value    A value for the provided property name/key.
   * @param archiver The instance of Archiver to use.
   * @param kmapi    The instance of KISSmetricsAPI where initiation of recursive send should occur.
   * @return A Runnable object that will run appropriate tasks for the current tracking state.
   */
  Runnable setDistinct(String key, String value, Archiver archiver, KISSmetricsAPI kmapi);
}
