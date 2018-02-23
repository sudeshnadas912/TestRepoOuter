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
package com.allianzservice.travelcompanionouterlayercloud.model;

public class AccessToken 
{

	private String accessToken;
	private String tokenType;
	private String expiresSeconds;
	public String getAccessToken() {
		return accessToken;
	}
	public AccessToken(){
		
		//Do nothing
	}
	public void setAccessToken(String accessToken) 
	{
		this.accessToken = accessToken;
	}
	public String getTokenType() 
	{
		return tokenType;
	}
	public void setTokenType(String tokenType) 
	{
		this.tokenType = tokenType;
	}
	public String getExpiresSeconds() 
	{
		return expiresSeconds;
	}
	public void setExpiresSeconds(String expiresSeconds) 
	{
		this.expiresSeconds = expiresSeconds;
	}
	
}
