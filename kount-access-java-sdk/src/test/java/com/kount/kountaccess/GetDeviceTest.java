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

import net.sf.json.JSONObject;

/**
 * Unit Tests around the Access SDK's device endpoint.
 *
 * @author Stanislav Milev
 */
public class GetDeviceTest {

	private static final Logger logger = Logger.getLogger(GetDeviceTest.class);

	// Setup data for comparisons.
	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String sessionUrl = "https://" + host + "/api/session=" + session;
	String fingerprint = "75012bd5e5b264c4b324f5c95a769541";
	String ipAddress = "64.128.91.251";
	String ipGeo = "US";
	String responseId = "bf10cd20cf61286669e87342d029e405";

	String deviceJSON = "{" + "    \"device\": {" + "        \"id\": \"" + fingerprint + "\", "
			+ "        \"ipAddress\": \"" + ipAddress + "\", " + "        \"ipGeo\": \"" + ipGeo + "\", "
			+ "        \"mobile\": 1, " + "        \"proxy\": 0" + "    }," + "    \"response_id\": \"" + responseId
			+ "\"" + "}";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getDevice(java.lang.String)}.
	 */
	@Test
	public void testGetDeviceHappyPath() {
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
			doReturn(deviceJSON).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			// test method
			JSONObject deviceInfo = sdk.getDevice(session);
			logger.debug(deviceInfo);
			JSONObject device = deviceInfo.getJSONObject("device");
			assertTrue(device != null);
			assertEquals(fingerprint, device.get("id"));
			assertEquals(ipAddress, device.get("ipAddress"));
			assertEquals(ipGeo, device.get("ipGeo"));
			assertEquals(1, device.get("mobile"));
			assertEquals(0, device.get("proxy"));
			assertEquals(responseId, deviceInfo.get("response_id"));

		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

}
