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

import java.util.HashMap;


/**
 * TrackingRunnables tracking state
 * <p/>
 * Provides Runnable objects for tracking state.
 */
public class TrackingRunnablesTrackingState implements TrackingRunnables {
    /**
     * Creates a Runnable to archive and send an alias.
     *
     * @param alias User alias String.
     * @param identity User identity String.
     * @param archiver The instance of Archiver to use.
     * @param kmapi The instance of KISSmetricsAPI where initiation of
     * recursive send should occur.
     *
     * @return A Runnable object that will archive and send an alias.
     */
    public Runnable alias(final String alias, final String identity, final Archiver archiver,
                          final KISSmetricsAPI kmapi) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveAlias(alias, identity);
                kmapi.sendRecords();
            }
        };

        return runnable;
    }

    /**
     * Creates a Runnable to archive an unassociated user identity.
     *
     * @param identity User identity String.
     * @param archiver The instance of Archiver to use.
     *
     * @return A Runnable object that will archive a new unassociated identity.
     */
    public Runnable clearIdentity(final String newIdentity, final Archiver archiver) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveFirstIdentity(newIdentity);
                archiver.clearSavedIdEvents();
                archiver.clearSavedProperties();
            }
        };

        return runnable;
    }

    /**
     * Creates a Runnable to archive and send an identity.
     *
     * @param identity User identity String.
     * @param archiver The instance of Archiver to use.
     * @param kmapi The instance of KISSmetricsAPI where initiation of
     * recursive
     * send should
     * occur.
     *
     * @return A Runnable object that will archive and send an identity.
     */
    public Runnable identify(final String identity, final Archiver archiver,
                             final KISSmetricsAPI kmapi) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveIdentity(identity);
                kmapi.sendRecords();
            }
        };

        return runnable;
    }

    /**
     * Creates a Runnable to archive and send an event with or without
     * properties(null).
     *
     * @param name Event name.
     * @param properties A HashMap of property names/keys and value pairs or
     * null.
     * @param condition A RecordCondition of always, per installation or per
     * identity.
     * @param archiver The instance of Archiver to use.
     * @param kmapi The instance of KISSmetricsAPI where initiation of
     * recursive
     * send should
     * occur.
     *
     * @return A Runnable object that will archive and send an event.
     */
    public Runnable record(final String name, final HashMap<String, String> properties,
                           final RecordCondition condition, final Archiver archiver,
                           final KISSmetricsAPI kmapi) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveEvent(name, properties, condition);
                kmapi.sendRecords();
            }
        };

        return runnable;
    }


    /**
     * Creates a Runnable to archive and send an event only once per identity.
     *
     * @param name Event name.
     * @param archiver The instance of Archiver to use.
     * @param kmapi The instance of KISSmetricsAPI where initiation of
     * recursive
     * send should
     * occur.
     *
     * @return A Runnable object that will archive and send an event only if the
     * event has never
     * been recorded for the current identity.
     */
    public Runnable recordOnce(final String name, final Archiver archiver,
                               final KISSmetricsAPI kmapi) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveEvent(name, null, RecordCondition.RECORD_ONCE_PER_IDENTITY);
                kmapi.sendRecords();
            }
        };

        return runnable;
    }


    /**
     * Creates a Runnable to archive and send 1 or more user properties.
     *
     * @param properties A HashMap of 1 or more property name/keys and value
     * pairs.
     * @param archiver The instance of Archiver to use.
     * @param kmapi The instance of KISSmetricsAPI where initiation of
     * recursive
     * send should
     * occur.
     *
     * @return A Runnable object that will archive and send 1 or more
     * properties.
     */
    public Runnable set(final HashMap<String, String> properties, final Archiver archiver,
                        final KISSmetricsAPI kmapi) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveProperties(properties);
                kmapi.sendRecords();
            }
        };

        return runnable;
    }


    /**
     * Creates a Runnable to archive and send properties with a new or
     * different
     * value for the
     * provided key.
     *
     * @param key A property name/key.
     * @param value A value for the provided property name/key.
     * @param archiver The instance of Archiver to use.
     * @param kmapi The instance of KISSmetricsAPI where initiation of
     * recursive
     * send should
     * occur.
     *
     * @return A Runnable object that will archive and send a distinct property.
     */
    public Runnable setDistinct(final String key, final String value, final Archiver archiver,
                                final KISSmetricsAPI kmapi) {

        Runnable runnable = new Runnable() {
            public void run() {
                archiver.archiveDistinctProperty(key, value);
                kmapi.sendRecords();
            }
        };

        return runnable;
    }
}
