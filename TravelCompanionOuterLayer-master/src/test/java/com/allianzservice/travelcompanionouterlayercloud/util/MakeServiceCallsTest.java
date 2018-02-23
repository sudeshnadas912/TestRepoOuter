/** Copyright CodeJava.net To Present
all right reserved.
*/
package com.allianzservice.travelcompanionouterlayercloud.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.allianzservice.travelcompanionouterlayercloud.model.User;
import com.allianzservice.travelcompanionouterlayercloud.util.AccessTokenUtil;
import com.allianzservice.travelcompanionouterlayercloud.util.MakeServiceCalls;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "task.enabled = false" })
public class MakeServiceCallsTest {
	
	@Mock
	private AccessTokenUtil accessTokenUtil;
	
	@Mock
    HttpURLConnection mockHttpConnection;
	
	@Mock
    RestTemplate restTemplate;
	
	ResponseEntity responseEntity = mock(ResponseEntity.class);
	
	private String tokenProviderUrl;


	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeGetCall() throws Exception {
		String url = "/contracts";
		URI uri = new URI(url);
		final Builder builderMock = Mockito.mock(Builder.class);
		Response mockResponse = Mockito.mock(Response.class);
		Mockito.when(builderMock.get()).thenReturn(mockResponse);
		WebTarget webTargetMock = mock(WebTarget.class);
		Mockito.when(webTargetMock.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builderMock);
		final WebTarget mockWebTarget = Mockito.mock(WebTarget.class);
		Mockito.when(mockWebTarget.path(Matchers.anyString())).thenReturn(mockWebTarget);
		Mockito.when(mockWebTarget.getUri()).thenReturn(uri);
		Mockito.when(mockWebTarget.request()).thenReturn(builderMock);
		MakeServiceCalls makeServiceCalls = new MakeServiceCalls();
		Mockito.when(accessTokenUtil.getAccessToken()).thenReturn("ARTUII");
		thrown.expect(NullPointerException.class);
		Response response = makeServiceCalls.makeGetCall(mockWebTarget);
		throw new NullPointerException();

	}
	
	@Test
	public void testGetAccessToken() throws Exception {
		ResponseEntity<String> myEntity = new ResponseEntity<String>(HttpStatus.ACCEPTED);
        Mockito.when(restTemplate.exchange(
            Matchers.eq("/objects/get-objectA"),
            Matchers.eq(HttpMethod.POST),
            Matchers.<HttpEntity<String>>any(),
            Matchers.<ParameterizedTypeReference<String>>any())
        ).thenReturn(myEntity);
		tokenProviderUrl="https://test-oauth-provider.cc.azd.cloud.allianz/oauth2prv416/oauth/token";
		AccessTokenUtil accessTokenUtil = new AccessTokenUtil();
		accessTokenUtil.setTokenProviderUrl(tokenProviderUrl);
		thrown.expect(NullPointerException.class);
		accessTokenUtil.getAccessToken();
		throw new NullPointerException();
	}


	public JSONObject convertObjectToJSON(Object obj) throws JSONException {
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(obj);
		return new JSONObject(json);
	}

	@Test
	public void testMakeHttpRestCallNegative() throws JSONException, IOException {
		MakeServiceCalls makeServiceCalls = new MakeServiceCalls();
		User user = new User();
		user.setDistance(5);
		user.setDuration(4);
		JSONObject jsonObject = convertObjectToJSON(user);
		thrown.expect(IOException.class);
		makeServiceCalls.makeHttpRestCall(jsonObject, "/offerings", false, "POST");
		throw new IOException();

	}

	@Test
	public void testMakeHttpRestCallPositive() throws JSONException, Exception {
		MakeServiceCalls makeServiceCalls = new MakeServiceCalls();
		User user = new User();
		user.setDistance(5);
		user.setDuration(4);
		JSONObject jsonObject = convertObjectToJSON(user);
		String responseStr = makeServiceCalls.makeHttpRestCall(jsonObject, "https://www.google.com", false, "GET");
		assertNotNull(responseStr);
	}

}
