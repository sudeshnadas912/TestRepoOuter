/*******************************************************************************
 * Copyright 2018 TCS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.allianzservice.travelcompanionouterlayercloud.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MakeServiceCalls 
{   private static final Logger LOGGER = Logger.getLogger(MakeServiceCalls.class.getName());
	public static final int RESPONSE_CODE = 200;
	

	@Autowired
	private AccessTokenUtil accessTokenUtil;

	
	
	public MakeServiceCalls(AccessTokenUtil accessTokenUtil) {
		this.accessTokenUtil = accessTokenUtil;
	}
	
	
	public MakeServiceCalls() {
		//Do nothing
	}



	public Response makeGetCall(WebTarget webTarget) 
	{	LOGGER.info("urlGet-->" + webTarget.getUri());
		Builder request = null;

		Response response = null;
		try {
			request = webTarget.request().header("Authorization", "Bearer " + accessTokenUtil.getAccessToken());
			request.accept(MediaType.APPLICATION_JSON);
			response = request.get();
			LOGGER.info("respoonse-->" + response.toString() + "status code-->" + response.getStatus());
		} catch (IOException e) {
			LOGGER.info(e);
		}
		return response;
	}

	public String makeHttpRestCall(JSONObject jsonObject, String urlToBePassed, boolean authRequired,
			String httpMethod) 
	{	String response = "";
		LOGGER.info("Method--> " + httpMethod + " url-->" + urlToBePassed);
		try {
			URL url = new URL(urlToBePassed);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod(httpMethod);
			conn.setRequestProperty("Content-Type", "application/json");
			if (authRequired) {
				conn.setRequestProperty("Authorization", "Bearer " + accessTokenUtil.getAccessToken());
			}

			String input = jsonObject.toString();
			LOGGER.info("json to string==>" + input);
			OutputStream os;

			os = conn.getOutputStream();

			os.write(input.getBytes());
			os.flush();
			LOGGER.info("response code-->" + conn.getResponseCode());

			BufferedReader br = null;

			if (conn.getResponseCode() == RESPONSE_CODE) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			LOGGER.info("json-->" + sb.toString());
			response = sb.toString();
		} catch (IOException e) {
			response = e.getMessage();
			LOGGER.info(e);
			LOGGER.info(response);
		} catch (Exception ex) {
			response = ex.getMessage();
			LOGGER.error("error Message " + ex);
		}

		return response;

	}

}
