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

public class Sender implements ConnectionDelegate {

	private SenderState readyState;
	private SenderState sendingState;
	private SenderState disabledState;
	
	SenderState state = readyState;
	
	/*
	 * Sender constructor
	 * 
	 * @param disabled
	 * 			Used to determine initialized state.
	 */
	public Sender(boolean disabled) {
		
		readyState = new SenderReadyState(this);
		sendingState = new SenderSendingState(this);
		disabledState = new SenderDisabledState(this);
		
		if (disabled) {
			state = disabledState;
		}
	}
	
	// This method may be overridden to allow passing of a mock connection to any state.
	ConnectionImpl getNewConnection(){
		return new ConnectionImpl();
	}

	
	// Getters
	SenderState getReadyState() {
		return readyState;
	}
	
	SenderState getSendingState() {
		return sendingState;
	}
	
	SenderState getDisabledState() {
		return disabledState;
	}


	//  Setters
	void setState(SenderState state) {
		this.state = state;
	}
	
	
	// Forwarded methods
	public void startSending() {
		state.startSending();
	}
	
	public void disableSending() {
		state.disableSending();
	}
	
	public void enableSending() {
		state.enableSending();
	}
	
	
	// ConnectionDelegate method
	public void connectionComplete(String urlString, boolean success, boolean malformed) {
		state.connectionComplete(urlString, success, malformed);
	}
}
