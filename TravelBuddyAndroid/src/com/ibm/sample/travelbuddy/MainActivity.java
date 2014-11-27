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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private EditText searchTxt;
	private Button searchBtn;
	private Button clearBtn;

	private ListView listView;
	private ArrayAdapter<String> adapter;
	List<String> itemList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchTxt = (EditText) findViewById(R.id.searchTxt);
		searchBtn = (Button) findViewById(R.id.searchBtn);
		clearBtn = (Button) findViewById(R.id.clearBtn);
		listView = (ListView) findViewById(R.id.searchListView);
		adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.item_name,
				itemList);
		listView.setAdapter(adapter);

		searchBtn.setOnClickListener(this);
		clearBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.searchBtn:
			doSearch();
			break;

		case R.id.clearBtn:
			doClear();
			break;

		default:
			break;
		}
	}

	/**
	 * This method clears the text field and the list view
	 */
	private void doClear() {
		searchTxt.setText("");
		itemList.clear();
		adapter.notifyDataSetChanged();
	}

	/**
	 * This method searches the Watson QA service by using the question text
	 */
	private void doSearch() {
		String sTxt = searchTxt.getText().toString();
		if (sTxt.trim().equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please enter your question", Toast.LENGTH_SHORT).show();
			return;
		}

		RequestParams param = new RequestParams();
		param.add("questionText", sTxt);
		HttpUtil.get(param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.i("doSearch", response.toString());

				try {
					String respCode = response.getString("respCode");
					if (respCode.equals(HttpUtil.RESP_SUCCESS)) {
						JSONArray bodyArray = response.getJSONArray("body");
						itemList.clear();
						for (int i = 0; i < bodyArray.length(); i++) {
							JSONObject obj = bodyArray.getJSONObject(i);
							itemList.add("(" + obj.getString("confidence")
									+ ")  " + obj.getString("text"));
						}
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(),
								response.getString("respText"),
								Toast.LENGTH_LONG).show();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
