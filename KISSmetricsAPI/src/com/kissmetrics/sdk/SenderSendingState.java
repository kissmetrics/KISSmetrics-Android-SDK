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

public class SenderSendingState implements SenderState {
	
	Sender sender;
	
	public SenderSendingState(Sender sender) {
		this.sender = sender;
	}
	
	public void startSending() {
		// Ignored. We're already sending.
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
		// Ignored, already enabled.
	}
	
	public void connectionComplete(String urlString, boolean success, boolean malformed) {
		
		if (success || malformed) {
			
			// We call to remove the query string in the current thread.
			// This call will be synchronized within the Archiver and ensure
			// that following reads from the Archiver's sendQueue will 
			// provide the correct value.
			ArchiverImpl.sharedArchiver().removeQueryString(0);
		}
		
		if (success) {
			
			if (ArchiverImpl.sharedArchiver().getQueueCount() == 0) {
				
				// Nothing left to send, switch to the ready state.
				sender.setState(sender.getReadyState());
				return;
			}

			// Begin sending
			Runnable runnable = new Runnable() { 
				@Override
				public void run() {
					
					// Assemble the full query string by prepending the current baseUrl as last archived.
					String apiQuery = ArchiverImpl.sharedArchiver().getBaseUrl()+ArchiverImpl.sharedArchiver().getQueryString(0);
					ConnectionImpl connection = sender.getNewConnection();
					connection.sendRecord(apiQuery, sender);
			    }
			};
			
			new Thread(runnable);
		}
	}
}
