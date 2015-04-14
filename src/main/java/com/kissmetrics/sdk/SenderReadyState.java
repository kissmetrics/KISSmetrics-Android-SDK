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

import java.util.concurrent.Executors;

public class SenderReadyState implements SenderState {
	private Sender sender;
	
	public SenderReadyState(Sender sender) {
		this.sender = sender;
	}
	
	private void sendTopRecord() {
		// Assemble the full query string by prepending the current baseUrl as last archived.
		String apiQuery = ArchiverImpl.sharedArchiver().getBaseUrl()+ArchiverImpl.sharedArchiver().getQueryString(0);
		ConnectionImpl connection = sender.getNewConnection();
		connection.sendRecord(apiQuery, sender);
	}
	
	public void startSending() {
		// Ignore if we have nothing to send
		if (ArchiverImpl.sharedArchiver().getQueueCount() == 0) {
			return;
		}
		
		// Change to sendingState first so that sendingState is ready 
		// to handle ConnectionDelegate's connectionComplete.
		sender.setState(sender.getSendingState());
		
		// Begin sending
		Runnable runnable = new Runnable() { 
			@Override
			public void run() {
				sendTopRecord();
		    }
		};
		
		sender.executorService = Executors.newFixedThreadPool(1);
		
		try {
			sender.executorService.execute(runnable);
		} catch(Exception e) {
			// Any failure to execute must place the Sender in a ready state;
			sender.setState(sender.getReadyState());
		}
	}
	
	public void disableSending() {
		// Switch to disabled state first so that ConnectionDelegate's 
		// connectionComplete is handled by the disabled state while we 
		// clear the send queue.
		sender.setState(sender.getDisabledState());
		
		// Empty the archived send queue
		ArchiverImpl.sharedArchiver().clearSendQueue();
	}
	
	public void enableSending() {
		// Ignored, already enabled
	}
	
	public void connectionComplete(String urlString, boolean success, boolean malformed) {
		// Ignored, connectionComplete should only be handled by the sending state.
		// The only way that this method could be called in this state is if a record 
		// is being sent at the time that the Sender is disabled and re-enabled.
		// We take no action because the queue would have already been emptied when 
		// switching to the disabled state.
	}
}
