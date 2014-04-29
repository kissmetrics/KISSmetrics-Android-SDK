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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kissmetrics.sdk.ConnectionImpl;

import android.util.Log;


//TestableConnectionImpl
//
//Extends ConectionImpl to inject a mocked HttpURLConnection via method override.
//
final class TestableConnectionImpl extends ConnectionImpl {

	private HttpURLConnection mockConnection;
	
	public void setHttpURLConnection(HttpURLConnection connection) {
		Log.d("KISSmetricsAPI", "TestableConnectionImpl - setHttpURLConnection:" + connection);
		mockConnection = connection;
	}
	
	// Override createHttpURLConnection to inject our mock HttpURLConnection
	@Override
	protected HttpURLConnection createHttpURLConnection(URL url)
			throws IOException {
		Log.d("KISSmetricsAPI", "TestableConnectionImpl - createHttpURLConnection");
		return mockConnection;
	}
}