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
package com.allianzservice.travelcompanionouterlayercloud.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.allianzservice.travelcompanionouterlayercloud.model.PackageinfoVO;
import com.allianzservice.travelcompanionouterlayercloud.model.ProductInfoVO;
import com.allianzservice.travelcompanionouterlayercloud.model.User;
import com.allianzservice.travelcompanionouterlayercloud.util.AccessTokenUtil;
import com.allianzservice.travelcompanionouterlayercloud.util.MakeServiceCalls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

@PropertySource("classpath:application.properties")
@Service
public class TravelCompanionService {

	private static final String COST_REIMBURSEMENT = "adds alternative travel cost reimbursement";
	private static final String COMPONENT_SPECIALLUGGAGE = "component.specialLuggage";

	@Autowired
	private MakeServiceCalls makeServiceCalls;

	@Autowired
	private Environment env;

	@Autowired
	private ProductInfoVO productInfoVO;

	@Autowired
	private AccessTokenUtil accessTokenUtil;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger logger = Logger.getLogger(TravelCompanionService.class.getName());

	// To get the Product structure from APL
	public ProductInfoVO getProductInfo(String productName) throws JsonSyntaxException, JSONException {

		Client client = ClientBuilder.newClient();
		// Set the product name for the ProductVO
		productInfoVO.setProductName(productName);
		if (!env.getProperty("mock").equalsIgnoreCase("true")) {
			Response response = makeServiceCalls.makeGetCall(client.target(""));
			productInfoVO = response.readEntity(ProductInfoVO.class);
		} else {
			productInfoVO.setPackageList(getPackageMock());
		}

		return productInfoVO;
	}

	// To make fetch the contract details and file a policy for that contract
	public boolean fileMobilityInsurance(User user) throws JSONException {

		boolean status = true;
		if (!env.getProperty("mock").equalsIgnoreCase("true")) {
			JSONObject jsonObject = new JSONObject(
					makeServiceCalls.makeHttpRestCall(convertObjectToJSON(user), "urlToBePassed", true, "POST"));
			status = jsonObject.getBoolean("status");
		}
		return status;
	}

	// To get the quote from the inner Layer
	public String getTheQuote(User user) throws JSONException {

		String response = "";
		if (!env.getProperty("mock").equalsIgnoreCase("true")) {
			response = makeServiceCalls.makeHttpRestCall(convertObjectToJSON(user), "urlToBePassed", true, "POST");
		} else {
			JSONObject jsonObject = new JSONObject(getTarrifDetails(user));
			response = jsonObject.toString();
		}

		return response;
	}

	// To Test OAuth Service To connect to inner layer
	public String testoAuth() throws JsonProcessingException, IOException {

		return accessTokenUtil.getAccessToken();
	}

	public String testInnerOuterConnection() throws JsonProcessingException, IOException {

		String tokenProviderUrl = "https://sandbox-cisl-bff.apps.dadpi.azd.cloud.allianz/testInnerOuter";
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessTokenUtil.getAccessToken());

		final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();

		final ResponseEntity<String> serviceResponse = restTemplate //
				.exchange( //
						UriComponentsBuilder.fromHttpUrl(tokenProviderUrl) //
								// Query parameters
								// C
								.toUriString(), //
						HttpMethod.POST, //
						new HttpEntity<Object>(body, headers), //
						String.class //
		);

		return serviceResponse.toString();
	}

	private JSONObject convertObjectToJSON(Object obj) throws JSONException {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(obj);
		return new JSONObject(json);
	}

	// Mock Services from here
	private List<PackageinfoVO> getPackageMock() {
		List<PackageinfoVO> packageInfoList = new ArrayList<PackageinfoVO>();
		List<String> breakDownComponents = new ArrayList<String>();
		breakDownComponents.add("adds transfer cost for special luggage");
		breakDownComponents.add("adds vehicle return home coverage");
		breakDownComponents.add(COST_REIMBURSEMENT);
		PackageinfoVO packageInfoBreak = new PackageinfoVO("Breakdown Module", breakDownComponents);

		List<String> destinationBussinessComponents = new ArrayList<String>();
		destinationBussinessComponents.add("adds vehicle return home coverage");
		destinationBussinessComponents.add(COST_REIMBURSEMENT);
		PackageinfoVO packageInfoDestBuss = new PackageinfoVO("DestinationBusiness Module",
				destinationBussinessComponents);

		List<String> destinationFamilyComponents = new ArrayList<String>();
		destinationFamilyComponents.add("adds rental car");
		destinationFamilyComponents.add("adds transfer cost for special luggage");
		PackageinfoVO packageInfoDestFamily = new PackageinfoVO("DestinationFamily Module",
				destinationFamilyComponents);

		List<String> trainFlightComponents = new ArrayList<String>();
		trainFlightComponents.add(COST_REIMBURSEMENT);
		PackageinfoVO packageInfoTrainFlight = new PackageinfoVO("TrainFlight Module", trainFlightComponents);

		packageInfoList.add(packageInfoBreak);
		packageInfoList.add(packageInfoDestBuss);
		packageInfoList.add(packageInfoDestFamily);
		packageInfoList.add(packageInfoTrainFlight);
		return packageInfoList;

	}

	public Map getTarrifDetails(User user) {
		logger.info("special luggage-->" + env.getProperty(COMPONENT_SPECIALLUGGAGE));
		List<String> selectedComponents = user.getSelectedModule();
		Double totalTarrif = 0.0;
		Map<String, Double> selectedModuleTarrifMap = new HashMap<String, Double>();
		for (String component : selectedComponents) {
			if (component.equalsIgnoreCase(env.getProperty(COMPONENT_SPECIALLUGGAGE))) {
				selectedModuleTarrifMap.put(env.getProperty(COMPONENT_SPECIALLUGGAGE), 20.0);
				totalTarrif += 20;
			} else if (component.equalsIgnoreCase(env.getProperty("component.returnTransfer"))) {
				selectedModuleTarrifMap.put(env.getProperty("component.returnTransfer"),
						(user.getDistance() * Double.valueOf(env.getProperty("component.returnTransferFactor"))));
				totalTarrif += (user.getDistance() * Double.valueOf(env.getProperty("component.returnTransferFactor")));
			} else if (component.equalsIgnoreCase(env.getProperty("component.alternateTransfer"))) {
				selectedModuleTarrifMap.put(env.getProperty("component.alternateTransfer"),
						(user.getDistance() * Double.valueOf(env.getProperty("component.alternateTransferFactor"))));
				totalTarrif += (user.getDistance()
						* Double.valueOf(env.getProperty("component.alternateTransferFactor")));
			} else if (component.equalsIgnoreCase(env.getProperty("component.rentalCar"))) {
				selectedModuleTarrifMap.put(env.getProperty("component.rentalCar"),
						(user.getDistance() * Double.valueOf(env.getProperty("component.rentCarFactor"))));
				totalTarrif += (user.getDistance() * Double.valueOf(env.getProperty("component.rentCarFactor")));
			}
		}
		if ((user.getDuration() > 1) && (user.getDuration() <= 15)) {
			selectedModuleTarrifMap.put("duration of days Factor",
					Double.valueOf(env.getProperty("component.durationFactor")));
			totalTarrif *= Double.valueOf(env.getProperty("component.durationFactor"));
		}
		if ((user.getPressure() > 500) && (user.getPressure() <= 980)) {
			selectedModuleTarrifMap.put("Weather Factor",
					Double.valueOf(env.getProperty("component.lowPressureFactor")));
			totalTarrif *= Double.valueOf(env.getProperty("component.lowPressureFactor"));

		}
		if ((user.getPressure() > 1040) && (user.getPressure() <= 1060)) {
			selectedModuleTarrifMap.put("Weather Factor",
					Double.valueOf(env.getProperty("component.highPressureFactor")));
			totalTarrif *= Double.valueOf(env.getProperty("component.highPressureFactor"));

		}
		if ((user.getNumberOfPerson() > 1) && (user.getNumberOfPerson() <= 5)) {
			selectedModuleTarrifMap.put("Number Of Person Factor",
					Double.valueOf(env.getProperty("component.numberOfPeopleFactor")));
			totalTarrif *= Double.valueOf(env.getProperty("component.numberOfPeopleFactor"));

		}
		selectedModuleTarrifMap.put("Grand Total Tarrif", totalTarrif);

		return selectedModuleTarrifMap;

	}

	// Code changes only for testing purpose
	public void setEnv(Environment env) {
		this.env = env;
	}

}
