/* 
 * ========================================================================
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.apache.cactus.mock;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Mock implementation of <code>HttpURLConnection</code>.
 *
 * @version $Id: MockHttpURLConnection.java 238991 2004-05-22 11:34:50Z vmassol $
 */
public class MockHttpURLConnection extends HttpURLConnection
{
    /**
     * Store the header fields that the <code>getHeaderField()</code> will
     * return.
     */
    private String getHeaderFieldValue;
    
    private long getHeaderFieldDateValue = -1;

    /**
     * Store the input streams that the <code>getInputStream()</code> will
     * return.
     */
    private InputStream getInputStreamValue;

    // -----------------------------------------------------------------------
    // Methods overriding those from HttpURLConnection
    // -----------------------------------------------------------------------

    /**
     * @param theURL the underlying URL
     */
    public MockHttpURLConnection(URL theURL)
    {
        super(theURL);
    }

    // -----------------------------------------------------------------------
    // Methods added on top of those found in HttpURLConnection
    // -----------------------------------------------------------------------

    /**
     * Sets the header field value that will be returned by
     * <code>getHeaderField()</code>.
     *
     * @param theValue the header field value
     */
    public void setExpectedGetHeaderField(String theValue)
    {
        this.getHeaderFieldValue = theValue;
    }
    
    public void setExpectedGetHeaderFieldDate(long theValue)
    {
    	this.getHeaderFieldDateValue = theValue;
    }

    /**
     * Sets the input stream value that will be returned by
     * <code>getInputStream()</code>.
     *
     * @param theValue the input stream value
     */
    public void setExpectedGetInputStream(InputStream theValue)
    {
        this.getInputStreamValue = theValue;
    }

    /**
     * {@inheritDoc}
     * @see HttpURLConnection#getHeaderField(int)
     */
    public String getHeaderField(int theFieldNumber)
    {
        if (this.getHeaderFieldValue == null)
        {
            throw new RuntimeException(
                "Must call setExpectedGetHeaderField() first !");
        }
        
        return this.getHeaderFieldValue;
    }
    
    public long getHeaderFieldDate(String name, long Default)
    {
    	if (this.getHeaderFieldDateValue == -1)
    	{
    		throw new RuntimeException(
    			"Must call setExpectedGetHeaderFieldDate() first !");
    	}
    	
    	return this.getHeaderFieldDateValue;
    }

    /**
     * {@inheritDoc}
     * @see HttpURLConnection#getInputStream()
     */
    public InputStream getInputStream()
    {
        if (this.getInputStreamValue == null)
        {
            throw new RuntimeException(
                "Must call setExpectedGetInputStream() first !");
        }

        return this.getInputStreamValue;
    }

    // -----------------------------------------------------------------------
    // Methods needed because HttpURLConnection is an abstract class
    // -----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see HttpURLConnection#usingProxy()
     */
    public boolean usingProxy()
    {
        return false;
    }

    /**
     * @see HttpURLConnection#disconnect()
     */
    public void disconnect()
    {
    }

    /**
     * @see HttpURLConnection#connect()
     */
    public void connect()
    {
    }
}