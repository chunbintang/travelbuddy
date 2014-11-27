/*
 * Copyright 2014 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.sample.travelbuddy;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {

	// The mobile back end URL to be defined
	private final static String MOBILE_BACKEND_URL = "http://qa002.mybluemix.net/MobileServlet";

	public final static String RESP_SUCCESS = "1000";

	private static AsyncHttpClient client = new AsyncHttpClient();

	static {
		client.setTimeout(10000);
	}

	public static void get(RequestParams params, JsonHttpResponseHandler res) {
		client.get(MOBILE_BACKEND_URL, params, res);
	}
}
