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


/*
 * ArchiverImplTest
 * 
 * ArchiverImpl must be tested as part of an integration test as it 
 * requires context to read and write to the application's directory.
 * 
 * URL encoding methods may be moved to static help class and their 
 * tests moved to unit tests on the KISSmetricsAPI library/jar project.
 * 
 */

package com.kissmetrics.sdk;

import java.lang.reflect.Method; 
import android.content.Context;
import android.test.AndroidTestCase;

// Class under test
import com.kissmetrics.sdk.ArchiverImpl;


public class ArchiverImplTest extends AndroidTestCase {
	
	String key = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	
	String reservedString = "!*'();:@&=+$,/?#[]";
    String encodedReservedString = "%21%2A%27%28%29%3B%3A%40%26%3D%2B%24%2C%2F%3F%23%5B%5D";
    String unsafeString = "<>#%{}|\\^~` []";
    String encodedUnsafeString = "%3C%3E%23%25%7B%7D%7C%5C%5E~%60%20%5B%5D";
    String unreservedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
    String encodedUnreservedString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
    
    /**
     * @return The {@link Context} of the test project.
     */
    private Context getTestContext() {
        try {
            Method getTestContext = ArchiverImplTest.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        }
        catch (final Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
    
    
    @Override
    protected void setUp() throws Exception {
      super.setUp();
      
      // We must initialize the Archiver with product key and 
      // context before the sharedArchiver() instance getter
      ArchiverImpl.sharedArchiver(key, getTestContext());

    } // end of setUp() method definition
    
    
	public void testArchiverSharedArchiverReturnsSameInstance() {

		ArchiverImpl arch1 = ArchiverImpl.sharedArchiver();
		ArchiverImpl arch2 = ArchiverImpl.sharedArchiver();
		
		assertEquals("Expected ArchiverImpl.sharedArchiver returns the same instance", arch1, arch2);
	}
	
}
