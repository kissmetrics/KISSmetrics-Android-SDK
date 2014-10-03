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

public class SenderDisabledState implements SenderState {

    Sender sender;

    public SenderDisabledState(Sender sender) {
        this.sender = sender;
    }

    public void connectionComplete(String urlString, boolean success, boolean malformed) {
        // Ignored, the queue should already be emptied as a result of switching to a disabled state.
    }

    public void disableSending() {
        // Ignored. Already in disabled state;
    }

    public void enableSending() {
        sender.setState(sender.getReadyState());
    }

    public void startSending() {
        // Ignored. Sending is disabled.
    }
}
