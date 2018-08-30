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
 * Unit Tests around the Access SDK's getuniques endpoint.
 *
 * @author Stanislav Milev
 */
public class GetUniquesTest {

	private static final Logger logger = Logger.getLogger(GetUniquesTest.class);

	// Setup data for comparisons.
	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String sessionUrl = "https://" + host + "/api/session=" + session;
	String uniq = "____TODO____";
	String fingerprint = "4b3c971fbe744080b6a37a79540c31f0";
	String responseId = "0b0932d66a5c40f0a2bc9d7a8995008f";
	String trustedState = "banned";

	String uniquesJSON = "{\"response_id\":\"" + responseId + "\",\"uniques\":[{\"unique\":\"" + uniq
			+ "\",\"datelastseen\":\"2018-08-22T10:20:15.025Z\",\"truststate\":\"" + trustedState + "\"}]}";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getUniques(java.lang.String)}.
	 */
	@Test
	public void testGetUniquesHappyPath() {
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
			doReturn(uniquesJSON).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			// test method
			JSONObject uniquesInfo = sdk.getUniques(fingerprint);
			logger.debug(uniquesInfo);
			JSONArray uniques = uniquesInfo.getJSONArray("uniques");
			assertTrue(uniques != null);
			assertEquals(1, uniques.size());
			JSONObject unique = uniques.getJSONObject(0);
			assertEquals(uniq, unique.get("unique"));
			assertEquals(trustedState, unique.get("truststate"));
			assertEquals(responseId, uniquesInfo.get("response_id"));

		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test IllegalArgumentException for missing deviceId (fingerprint) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#getUniques(java.lang.String)}.
	 */
	@Test
	public void testGetUniquesMissingDeviceId() {
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
			sdk.getUniques(null);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank deviceId (fingerprint) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#getUniques(java.lang.String)}.
	 */
	@Test
	public void testGetUniquesBlankDeviceId() {
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
			sdk.getUniques("");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for deviceId (fingerprint) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#getUniques(java.lang.String)}.
	 */
	@Test
	public void testGetUniquesSpaceForDeviceId() {
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
			sdk.getUniques(" ");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

}
