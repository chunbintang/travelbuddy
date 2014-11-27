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

package com.ibm.mds;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class MobileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String RESP_SUCCESS = "1000";
	private static final String RESP_ERR_COMMAND_NOT_CORRECT = "1001";
	private static final String RESP_TXT_COMMAND_NOT_CORRECT = "Command Not In Well Format";

	private String serviceName = "question_and_answer";

	// If running locally complete the variables below with the information in
	// VCAP_SERVICES
	private String baseURL = "<service url>";
	private String username = "<service username>";
	private String password = "<service password>";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String question = req.getParameter("questionText");

		if (question == null || question.trim().equals("")) {
			doResp(formartErrJsonMsg(RESP_ERR_COMMAND_NOT_CORRECT,
					RESP_TXT_COMMAND_NOT_CORRECT), resp);
			return;
		}

		// create the { 'question' : {
		// 'questionText:'...',
		// 'evidenceRequest': { 'items': 5} } json as requested by the service
		JSONObject questionJson = new JSONObject();
		questionJson.put("questionText", question);
		JSONObject evidenceRequest = new JSONObject();
		evidenceRequest.put("items", 5);
		questionJson.put("evidenceRequest", evidenceRequest);

		JSONObject postData = new JSONObject();
		postData.put("question", questionJson);

		try {
			Executor executor = Executor.newInstance().auth(username, password);
			URI serviceURI = new URI(baseURL + "/v1/question/travel").normalize();

			String answersJsonStr = executor
					.execute(
							Request.Post(serviceURI)
									.addHeader("Accept", "application/json")
									.addHeader("X-SyncTimeout", "30")
									.bodyString(postData.toString(),
											ContentType.APPLICATION_JSON))
					.returnContent().asString();

			JSONObject resultObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			JSONArray pipelines = JSONArray.parse(answersJsonStr);
			// the response has two pipelines, lets use the first one
			JSONObject answersJson = (JSONObject) pipelines.get(0);
			JSONArray answers = (JSONArray) ((JSONObject) answersJson
					.get("question")).get("evidencelist");

			for (int i = 0; i < answers.size(); i++) {
				JSONObject answer = (JSONObject) answers.get(i);
				double p = Double.parseDouble((String) answer.get("value"));
				p = Math.floor(p * 100);
				JSONObject obj = new JSONObject();
				obj.put("confidence", Double.toString(p) + "%");
				obj.put("text", (String) answer.get("text"));
				jsonArray.add(obj);
			}

			resultObject.put("respCode", RESP_SUCCESS);
			resultObject.put("body", jsonArray);

			doResp(resultObject.toString(), resp);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doResp(String jsonMsg, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(jsonMsg);
		resp.setStatus(200);
	}

	private String formartErrJsonMsg(String err, String errTxt) {
		JSONObject resultObject = new JSONObject();
		resultObject.put("respCode", err);
		resultObject.put("respText", errTxt);
		return resultObject.toString();
	}

	@Override
	public void init() throws ServletException {
		super.init();
		processVCAP_Services();
	}

	/**
	 * If exists, process the VCAP_SERVICES environment variable in order to get
	 * the username, password and baseURL
	 */
	private void processVCAP_Services() {
		
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		if (VCAP_SERVICES == null) {
			return;
		}
		
		JSONObject sysEnv = null;
		
		try {
			sysEnv = JSONObject.parse(VCAP_SERVICES);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Processing VCAP_SERVICES");
		if (sysEnv == null)
			return;
		System.out.println("Looking for: " + serviceName);

		if (sysEnv.containsKey(serviceName)) {
			JSONArray services = (JSONArray) sysEnv.get(serviceName);
			JSONObject service = (JSONObject) services.get(0);
			JSONObject credentials = (JSONObject) service.get("credentials");
			baseURL = (String) credentials.get("url");
			username = (String) credentials.get("username");
			password = (String) credentials.get("password");
			System.out.println("baseURL  = " + baseURL);
			System.out.println("username   = " + username);
			System.out.println("password = " + password);
		} else {
			System.out.println(serviceName
					+ " is not available in VCAP_SERVICES, "
					+ "please bind the service to your application");
		}
	}
}
