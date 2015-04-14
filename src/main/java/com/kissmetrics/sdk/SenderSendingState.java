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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SenderSendingState implements SenderState {
	private Sender sender;
	
	public SenderSendingState(Sender sender) {
		this.sender = sender;
	}
	
	private void sendTopRecord() {
		// Assemble the full query string by prepending the current baseUrl as last archived.
		String apiQuery = ArchiverImpl.sharedArchiver().getBaseUrl()+ArchiverImpl.sharedArchiver().getQueryString(0);
		ConnectionImpl connection = sender.getNewConnection();
		connection.sendRecord(apiQuery, sender);
	}
	
	private void shutdownExecutor(final ExecutorService es) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        es.shutdown();

        try {
          if (!es.awaitTermination(60, TimeUnit.SECONDS)) {
            es.shutdownNow();
          }
        } catch (InterruptedException e) {
          // (Re-)Cancel if current thread also interrupted
          es.shutdownNow();
          // Preserve interrupt status
          Thread.currentThread().interrupt();
        }
      }
    });
  }
	
	public void startSending() {
		// Ignored. We're already sending.
	}
	
	public void disableSending() {

		shutdownExecutor(sender.executorService);
		
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
			ArchiverImpl.sharedArchiver().removeQueryString(0);
		}
		
		if (success) {
			// If there's nothing left to send, switch to the ready state
			if (ArchiverImpl.sharedArchiver().getQueueCount() == 0) {
				shutdownExecutor(sender.executorService);
				sender.setState(sender.getReadyState());
				
				return;
			}

			// Begin sending the next record by adding it to executorService
			Runnable runnable = new Runnable() { 
				@Override
				public void run() {
					sendTopRecord();
			    }
			};

			try {
				sender.executorService.execute(runnable);
			} catch(Exception e) {
				// Any failure to execute must place the Sender in a ready state;
				sender.setState(sender.getReadyState());
			}
			
		} else {
			// Failure to succeed will likely be due to connectivity issues.
			// Stop sending and place the Sender in the ready state.
			shutdownExecutor(sender.executorService);
			sender.setState(sender.getReadyState());
		}
	}
}
