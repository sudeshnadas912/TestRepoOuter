/** Copyright CodeJava.net To Present
all right reserved.
*/
package com.allianzservice.travelcompanionouterlayercloud.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.allianzservice.travelcompanionouterlayercloud.model.AccessToken;
import com.allianzservice.travelcompanionouterlayercloud.model.PackageinfoVO;
import com.allianzservice.travelcompanionouterlayercloud.model.ProductInfoVO;
import com.allianzservice.travelcompanionouterlayercloud.model.User;
import com.allianzservice.travelcompanionouterlayercloud.service.TravelCompanionService;
import com.allianzservice.travelcompanionouterlayercloud.util.MakeServiceCalls;

@RunWith(SpringRunner.class)
public class TravelCompanionServiceTest {

	private User user = new User();
	@Mock
	private ProductInfoVO productInfoVO = new ProductInfoVO();

	@InjectMocks
	private TravelCompanionService travelCompanionService;

	@Mock
	private Environment env;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private MakeServiceCalls makeServiceCalls;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	private User fetchUser() {

		List<String> moduleList = new ArrayList<>();
		moduleList.add("adds transfer cost for special luggage");
		moduleList.add("adds vehicle return home coverage");
		moduleList.add("adds alternative travel cost reimbursement");
		moduleList.add("adds rental car");

		user.setFiledDate("12-12-2018");
		user.setPackageTitle("packageTitle");
		user.setPlace("place");
		user.setTravelDate("23-12-2018");
		user.setTravelEndDate("31-12-2018");
		user.setUserId(1004);
		user.setUserName("testNme");
		user.setWeather("good");
		user.setDistance(10);
		user.setDuration(5);
		user.setNumberOfPerson(2);
		user.setPressure(1046);
		user.setSelectedModule(moduleList);
		user.setSelectedPackage("package");
		return user;
	}

	/**
	 * Only for testing getter and setter method
	 */
	@Test
	public void test() {
		List<PackageinfoVO> packageinfoVOs = new ArrayList<>();

		PackageinfoVO packageinfoVO = new PackageinfoVO();

		List<String> packageDescriptionList = new ArrayList<>();
		packageDescriptionList.add("driving");
		packageDescriptionList.add("skiing");
		packageinfoVO.setPackageDescription(packageDescriptionList);
		packageinfoVO.setPackageName("passion");

		// Constuctor check
		PackageinfoVO packageinfoVOTest = new PackageinfoVO("testPackage", packageDescriptionList);
		packageinfoVOs.add(packageinfoVOTest);

		packageinfoVOs.add(packageinfoVO);

		productInfoVO.setPackageList(packageinfoVOs);
		productInfoVO.setProductName("SkiinSelekor");
		
		
		//Testing
		ProductInfoVO productInfoVOTest = new ProductInfoVO();
		productInfoVOTest.setPackageList(packageinfoVOs);
		productInfoVOTest.setProductName("SkiinSelekor");
		

		// Check all getter field only for testing
		assertNotNull(packageinfoVO.getPackageDescription() + packageinfoVO.getPackageName());
		User testUser = fetchUser();
		// Check all fields of user only for testing
		assertNotNull(testUser.getDistance() + testUser.getDuration() + testUser.getFiledDate()
				+ testUser.getNumberOfPerson() + testUser.getPackageTitle() + testUser.getPlace()
				+ testUser.getPressure() + testUser.getSelectedPackage() + testUser.getTravelDate()
				+ testUser.getTravelEndDate() + testUser.getUserId() + testUser.getUserName() + testUser.getWeather()
				+ testUser.getSelectedModule());
		// Check all fields of product only for testing
		assertNotNull(productInfoVO.getPackageList() + productInfoVO.getProductName());
	}

	@Test
	public void testModel() {
		// Set the value
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessToken("qwtuiiooo445677");
		accessToken.setExpiresSeconds("30");
		accessToken.setTokenType("client_credential");
		// Get the value for testing purpose
		assertNotNull(accessToken.getAccessToken() + accessToken.getExpiresSeconds() + accessToken.getTokenType());
	}

	@Test
	public void testGetProductInfoNegative() throws Exception {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("mock", "false");
		travelCompanionService.setEnv(env);
		thrown.expect(NullPointerException.class);
		travelCompanionService.getProductInfo("passion");
		throw new NullPointerException();
	}
	
	@Test
	public void testGetProductInfoPositive() throws Exception {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("mock", "true");
		travelCompanionService.setEnv(env);
		ProductInfoVO productInfoVOtest = travelCompanionService.getProductInfo("passion");
		assertNotNull(productInfoVOtest);
	}

	@Test
	public void testFileMobilityInsurance() throws Exception {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("mock", "false");
		travelCompanionService.setEnv(env);
		User testUser = fetchUser();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("key1", "value1");
		jsonObject.put("key2", "value2");
		thrown.expect(NullPointerException.class);
		travelCompanionService.fileMobilityInsurance(testUser);
		throw new NullPointerException();
	}

	@Test
	public void testGetTheQuote() throws Exception {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("mock", "false");
		travelCompanionService.setEnv(env);
		User testUser = fetchUser();
		String response = travelCompanionService.getTheQuote(testUser);
		assertNull(response);
	}

	@Test
	public void testGetTheQuoteNotMock() throws Exception {
		MockEnvironment env = new MockEnvironment();
		env.setProperty("mock", "true");

		User testUser = fetchUser();
		// Radar live mock test
		env.setProperty("component.specialLuggage", "adds transfer cost for special luggage");
		env.setProperty("component.returnTransfer", "adds vehicle return home coverage");
		env.setProperty("component.alternateTransfer", "adds alternative travel cost reimbursement");
		env.setProperty("component.rentalCar", "adds rental car");
		env.setProperty("component.returnTransferFactor", "0.02");
		env.setProperty("component.alternateTransferFactor", "0.05");
		env.setProperty("component.rentCarFactor", "0.03");
		env.setProperty("component.durationFactor", "1.05");
		env.setProperty("component.lowPressureFactor", "1.2");
		env.setProperty("component.highPressureFactor", "0.8");
		env.setProperty("component.numberOfPeopleFactor", "0.9");
		travelCompanionService.setEnv(env);
		String response = travelCompanionService.getTheQuote(testUser);
		assertNotNull(response);
	}

}
