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

import java.util.ArrayList;
import java.util.List;

public class User 
{

	
	private long userId;
	private String userName;
	private String selectedPackage;
	private List<String> selectedModule;
	private int pressure;
	private String packageTitle;
	private String filedDate;
	private String place;
	private String travelDate;
	private String weather;
	private String travelEndDate;
	private float distance;
	private int duration;
	private int numberOfPerson;
	
	public User(){
		//Do Nothing
	}
	
	public int getNumberOfPerson() 
	{
		return numberOfPerson;
	}



	public void setNumberOfPerson(int numberOfPerson) 
	{
		this.numberOfPerson = numberOfPerson;
	}



	public int getPressure() 
	{
		return pressure;
	}



	public void setPressure(int pressure) 
	{
		this.pressure = pressure;
	}



	public float getDistance() 
	{
		return distance;
	}



	public void setDistance(float distance) 
	{
		this.distance = distance;
	}



	public int getDuration() 
	{
		return duration;
	}



	public void setDuration(int duration) 
	{
		this.duration = duration;
	}



	public long getUserId() 
	{
		return userId;
	}



	public void setUserId(long userId)
	{
		this.userId = userId;
	}



	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	
	
	
	public String getTravelEndDate() 
	{
		return travelEndDate;
	}



	public void setTravelEndDate(String travelEndDate) 
	{
		this.travelEndDate = travelEndDate;
	}



	public String getWeather()
	{
		return weather;
	}



	public void setWeather(String weather) 
	{
		this.weather = weather;
	}







	



	public String getPlace()
	{
		return place;
	}



	public void setPlace(String place) 
	{
		this.place = place;
	}



	public String getTravelDate() 
	{
		return travelDate;
	}



	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate;
	}



	public String getFiledDate() 
	{
		return filedDate;
	}



	public void setFiledDate(String filedDate) 
	{
		this.filedDate = filedDate;
	}



	public String getPackageTitle()
	{
		return packageTitle;
	}



	public void setPackageTitle(String packageTitle)
	{
		this.packageTitle = packageTitle;
	}



	public String getUserName() {
		return userName;
	}

	

	public List<String> getSelectedModule() 
	
	{
		return new ArrayList<>(selectedModule);
	}



	public void setSelectedModule(List<String> selectedModule)
	{
		this.selectedModule = new ArrayList<>(selectedModule);
	}



	public String getSelectedPackage() 
	{
		return selectedPackage;
	}

	public void setSelectedPackage(String selectedPackage)
	{
		this.selectedPackage = selectedPackage;
	}


	
	
	
}
