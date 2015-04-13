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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.util.Log;

/**
 * QueryEncoder
 * 
 * URL encoder for KISSmetricsAPI queries.
 * 
 */
public class QueryEncoder {
	private final String key;
	private final String clientType;
	private final String userAgent;
	
	private static final String EVENT_PATH = "/e";
	private static final String PROP_PATH  = "/s";
	private static final String ALIAS_PATH = "/a";

	/**
	 * QueryEncoder init method
	 * 
	 * @param key  KISSmetrics product key.
	 * @param clientType  client identifier and version string.
	 */
	public QueryEncoder(String key, String clientType, String userAgent) {
	    this.key = key;
	    this.clientType = clientType;
	    this.userAgent = userAgent;
	}

  /**
   * Checks provided properties HashMap for _d & _t timestamp keys.
   *
   * @param properties  HashMap of user or event properties
   * @return boolean  true if timestamp has been set in these properties
   */
  private boolean propertiesContainTimestamp(HashMap<String, String> properties) {
    if (properties != null && properties.containsKey("_d") && properties.containsKey("_t")) {
      return true;
    }

    return false;
  }

	/**
	 * URL encodes a string
	 * 
	 * @param queryString  Unencoded query string.
	 * @return the URL encoded string.
	 */
	public String encode(String queryString) {
		String url = "";
		
		try {
			url = URLEncoder.encode(queryString, "UTF-8");
			
			// URLEncoder's encode method does not encode *, ~ or + as we expect from our mobile SDKs.
			url = url.replace("*", "%2A");
			url = url.replace("%7E", "~");
			url = url.replace("+", "%20");
			
		} catch (UnsupportedEncodingException e) {
			Log.w(KISSmetricsAPI.TAG, "Unable to url encode string:" + queryString);
		}
		
		return url;
	}

	/**
	 * URL encodes an identity
	 * 
	 * @param identity  User identity.
	 * @return the URL encoded identity
	 */
	public String encodeIdentity(String identity) {
		String url = encode(identity);
		return url;	
	}

	/**
	 * URL encodes an event
	 * 
	 * @param unencodedEvent  Unencoded query string.
	 * @return the URL encoded identity
	 */
	public String encodeEvent(String unencodedEvent) {
    String encodedEventName = encode(unencodedEvent);
    return encodedEventName;
  }
	
	/**
	 * URL encodes properties
	 * 
	 * @param properties  User or Event properties.
	 * @return the URL encoded properties
	 */
	public String encodeProperties(HashMap<String, String> properties) {
		if (properties == null || properties.isEmpty()) {
			return "";
		}
		
		StringBuilder propertiesStringBuilder = new StringBuilder();
		
		for (String stringKey : properties.keySet()) {
			// Check for valid key
			if (stringKey.length() == 0) {
				Log.w(KISSmetricsAPI.TAG,
					    "Property keys must not be empty strings. Dropping property.");
				continue;
			}
			
			// Check for valid encoded key length
      String escapedKey = encode(stringKey);
      if (escapedKey.length() > 255) {
        Log.w(KISSmetricsAPI.TAG,
            "Property key cannot be longer than 255 characters. When URL escaped, " +
            "your key is" + escapedKey.length() + "characters long (the submitted " +
            "value is "+stringKey+", the URL escaped value is "+escapedKey+"). " +
            "Dropping property");
        continue;
      }
    		
      // Check for valid value
      String stringValue = properties.get(stringKey);
      if (stringValue == null || stringValue.length() == 0) {
        Log.w(KISSmetricsAPI.TAG,
            "Property values must not be null or empty strings. Dropping property.");
        continue;
      }

      String escapedValue = encode(stringValue);
    		
      // Append key to value (use StringBuilder in for loops)
      StringBuilder propStringBuilder = new StringBuilder();
      propStringBuilder.append("&");
      propStringBuilder.append(escapedKey);
      propStringBuilder.append("=");
      propStringBuilder.append(escapedValue);

      // Append full property
      propertiesStringBuilder.append(propStringBuilder.toString());
		}
		
		return propertiesStringBuilder.toString();
	}

	/**
	 * Assembles an event query into an encoded URL string.
	 * 
	 * @param name  Event name
	 * @param properties  Event properties
	 * @param identity  User identity
	 * @param timestamp  A unix epoch timestamp to apply if _t & _d have not been set in properties
	 * @return the URL encoded query string
	 */
	public String createEventQuery(String name,
                                 HashMap<String, String>properties,
								                 String identity,
                                 long timestamp)
  {
		String theUrl = String.format("%s?_k=%s&_c=%s&_u=%s&_p=%s&_n=%s", EVENT_PATH, key, 
									                clientType, userAgent, encodeIdentity(identity),
									                encodeEvent(name));

		if (!propertiesContainTimestamp(properties)) {
			theUrl += String.format("&_d=1&_t=%d", timestamp);
		}

		theUrl += encodeProperties(properties);
		
		return theUrl;
  }

	/**
	 * Assembles a properties query into and encoded URL string.
	 * 
	 * @param properties  User properties
	 * @param identity  User identity
	 * @param timestamp  A unix epoch timestamp to apply if _t & _d have not been set in properties
	 * @return the URL encoded query string
	 */
  public String createPropertiesQuery(HashMap<String, String>properties,
                                      String identity,
                                      long timestamp)
  {
    String theUrl = String.format("%s?_k=%s&_c=%s&_u=%s&_p=%s", PROP_PATH, key,
                                  clientType, userAgent, encodeIdentity(identity));

    if (!propertiesContainTimestamp(properties)) {
      theUrl += String.format("&_d=1&_t=%d", timestamp);
    }

    theUrl += encodeProperties(properties);

    return theUrl;
  }

  /**
   * Assembles an alias query into and encoded URL string.
   *
   * @param alias  User alias to apply to an identity
   * @param identity  User identity
   * @return the URL encoded query string
   */
  public String createAliasQuery(final String alias, final String identity) {
    return String.format("%s?_k=%s&_c=%s&_u=%s&_p=%s&_n=%s", ALIAS_PATH,
                         key, clientType, userAgent, encodeIdentity(alias),
                         encodeIdentity(identity));
  }
}
