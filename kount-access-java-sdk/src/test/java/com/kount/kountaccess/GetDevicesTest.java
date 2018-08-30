/**
 *
 */
package com.kount.kountaccess;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.kount.kountaccess.AccessException.AccessErrorType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Unit Tests around the Access SDK's getdevices endpoint.
 *
 * @author Stanislav Milev
 */
public class GetDevicesTest {

	private static final Logger logger = Logger.getLogger(GetDevicesTest.class);

	// Setup data for comparisons.
	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String sessionUrl = "https://" + host + "/api/session=" + session;
	String uniq = "uniq";
	String fingerprint = "4b3c971fbe744080b6a37a79540c31f0";
	String fingerprint2 = "7041cca3e5f94391a7a02316b0a8f384";
	String responseId = "bf10cd20cf61286669e87342d029e405";
	String trustedState = "trusted";

	String devicesJSON = "{\"response_id\":\"" + responseId + "\",\"devices\":[{\"deviceid\":\"" + fingerprint
			+ "\",\"truststate\":\"" + trustedState + "\",\"datefirstseen\":\"2018-08-21T13:04:38.396Z\","
			+ "\"friendlyname\":\"\"},{\"deviceid\":\"" + fingerprint2 + "\",\"truststate\":\"" + trustedState
			+ "\",\"datefirstseen\":\"2018-08-21T13:28:32.166Z\",\"friendlyname\":\"\"}]}";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getDevices(java.lang.String)}.
	 */
	@Test
	public void testGetDevicesHappyPath() {
		try {
			// class to test
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			// mock objects
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpGet mockGet = mock(HttpGet.class);
			CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
			StatusLine mockStatus = mock(StatusLine.class);
			// mock responses
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockGet).when(sdk).getHttpGet(sessionUrl);
			doReturn(mockResponse).when(mockHttpClient).execute((HttpGet) anyObject());
			doReturn(devicesJSON).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			// test method
			JSONObject devicesInfo = sdk.getDevices(uniq);
			logger.debug(devicesInfo);
			JSONArray devices = devicesInfo.getJSONArray("devices");
			assertTrue(devices != null);
			assertEquals(2, devices.size());
			JSONObject device1 = devices.getJSONObject(0);
			JSONObject device2 = devices.getJSONObject(1);
			assertEquals(fingerprint, device1.get("deviceid"));
			assertEquals(trustedState, device1.get("truststate"));
			assertEquals(fingerprint2, device2.get("deviceid"));
			assertEquals(trustedState, device2.get("truststate"));
			assertEquals(responseId, devicesInfo.get("response_id"));

		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test IllegalArgumentException for missing uniq Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getDevices(java.lang.String)}.
	 */
	@Test
	public void testGetDevicesMissingUniq() {
		try {
			// class to test
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			// mock objects
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpGet mockGet = mock(HttpGet.class);
			// mock responses
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockGet).when(sdk).getHttpGet(sessionUrl);
			// test method
			sdk.getDevices(null);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank uniq Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getDevices(java.lang.String)}.
	 */
	@Test
	public void testGetDevicesBlankUniq() {
		try {
			// class to test
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			// mock objects
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpGet mockGet = mock(HttpGet.class);
			// mock responses
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockGet).when(sdk).getHttpGet(sessionUrl);
			// test method
			sdk.getDevices("");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for uniq Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getDevices(java.lang.String)}.
	 */
	@Test
	public void testGetDevicesSpaceForUniq() {
		try {
			// class to test
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			// mock objects
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpGet mockGet = mock(HttpGet.class);
			// mock responses
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockGet).when(sdk).getHttpGet(sessionUrl);
			// test method
			sdk.getDevices(" ");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

}
