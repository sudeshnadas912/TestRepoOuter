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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class AccessTokenUtil 
{	private static final Logger LOGGER = Logger.getLogger(AccessTokenUtil.class.getName());

	@Value("${innerLayeroAuth.base.url}")
	private  String cislUrl;
	
	@Value("${innerLayeroAuth.oauth.url}")
	private  String oAuthUrl;
	
	@Value("${innerLayeroAuth.oauth.accessTypeJson}")
	private String cislAccessMediaType;
	
	@Value("${innerLayeroAuth.oauth.clientID}")
	private String clientId;
	
	@Value("${innerLayeroAuth.oauth.clientSecret}")
	private String clientSecret;
	
	@Value("${innerLayeroAuth.oauth.grantType}")
	private String grantType;
	
	 @Autowired
	    private RestTemplate restTemplate;
	@Value("${apigee.oauth.url}")
	 private String tokenProviderUrl;
	
	
	
	
	public AccessTokenUtil(String cislUrl, String oAuthUrl, String cislAccessMediaType, String clientId,
			String clientSecret, String grantType, RestTemplate restTemplate, String tokenProviderUrl) {
		this.cislUrl = cislUrl;
		this.oAuthUrl = oAuthUrl;
		this.cislAccessMediaType = cislAccessMediaType;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.grantType = grantType;
		this.restTemplate = restTemplate;
		this.tokenProviderUrl = tokenProviderUrl;
	}
	
	

	public AccessTokenUtil() {
		//Do nothing
	}



	public  String getAccessToken() throws JsonProcessingException, IOException 
	{       final HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", "application/x-www-form-urlencoded");

	        final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();

	        final ResponseEntity<String> serviceResponse = restTemplate //
	            .exchange( //
	                UriComponentsBuilder.fromHttpUrl(tokenProviderUrl) //
	                    // Query parameters
	                    .queryParam("grant_type", "client_credentials") // C
	                    .toUriString(), //
	                HttpMethod.POST, //
	                new HttpEntity<Object>(body, headers), //
	                String.class //
	        );
	        LOGGER.info("response bosy-->"+serviceResponse.getBody());
	        

	        final JsonNode oauthResponseJson = new ObjectMapper().readTree(serviceResponse.getBody());

	        final JsonNode accessToken = oauthResponseJson.get("access_token");
	        final JsonNode refreshToken = oauthResponseJson.get("refresh_token");

	        final String acToken = accessToken != null ? accessToken.textValue() : "";
	        //Not required
	        //final String reToken = refreshToken != null ? refreshToken.textValue() : "";

	        LOGGER.info("accessToken-->"+acToken);
		
			return acToken;
	}

	public void setTokenProviderUrl(String tokenProviderUrl) 
	{this.tokenProviderUrl = tokenProviderUrl;
	}
	
	
}
