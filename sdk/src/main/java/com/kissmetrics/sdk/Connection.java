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
 * Connection interface
 */
interface Connection {


    /**
     * Makes a request to the provided API query urlString.
     * Handles the response and notifies the provided ConnectionDelgate on
     * completion.
     *
     * @param urlString URL encoded API query string
     *
     * @delegate delegate Object implementing the ConnectionDelegate interface
     */
    public void sendRecord(String urlString, ConnectionDelegate delegate);
}
