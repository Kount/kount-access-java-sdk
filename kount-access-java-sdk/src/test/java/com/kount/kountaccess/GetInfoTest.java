/**
 *
 */
package com.kount.kountaccess;

import static junit.framework.Assert.assertEquals;
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
 * Unit Tests around the Access SDK's info endpoint.
 *
 * @author Stanislav Milev
 */
public class GetInfoTest {

	private static final Logger logger = Logger.getLogger(GetInfoTest.class);

	// Setup data for comparisons.
	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String sessionUrl = "https://" + host + "/api/session=" + session;
	String uniq = "____TODO____";
	String fingerprint = "7041cca3e5f94391a7a02316b0a8f384";
	String responseId = "baa5211cdfbf460c967bd81d574ae353";
	String trustedState = "banned";
	String user = "user";
	String pass = "pass";
	String ipAddress = "10.0.0.1";

	String infoJSONFull = "{\"behavioSec\":{\"isBot\":false,\"isTrained\":false,\"score\":0,\"confidence\":0,"
			+ "\"policyId\":4},\"decision\":{\"errors\":[],\"warnings\":[],\"reply\":{\"ruleEvents\":{"
			+ "\"decision\":\"A\",\"total\":0,\"ruleEvents\":null}}},\"device\":{\"id\":\"" + fingerprint
			+ "\",\"ipAddress\":\"" + ipAddress + "\",\"ipGeo\":\"BG\",\"mobile\":0,\"proxy\":0,\"tor\":0,"
			+ "\"region\":\"53\",\"country\":\"BG\",\"geoLat\":43.8564,\"geoLong\":25.9708},\"response_id\":\""
			+ responseId + "\",\"trusted\":{\"state\":\"" + trustedState
			+ "\"},\"velocity\":{\"account\":{\"dlh\":1,\"dlm\":1,\"iplh\":1,\"iplm\":1,\"plh\":1,\"plm\":1,\"ulh\":1,"
			+ "\"ulm\":1},\"device\":{\"alh\":1,\"alm\":1,\"iplh\":1,\"iplm\":1,\"plh\":1,\"plm\":1,\"ulh\":1,"
			+ "\"ulm\":1},\"ip_address\":{\"alh\":1,\"alm\":1,\"dlh\":1,\"dlm\":1,\"plh\":1,\"plm\":1,\"ulh\":1,"
			+ "\"ulm\":1},\"password\":{\"alh\":1,\"alm\":1,\"dlh\":1,\"dlm\":1,\"iplh\":1,\"iplm\":1,\"ulh\":1,"
			+ "\"ulm\":1},\"user\":{\"alh\":1,\"alm\":1,\"dlh\":1,\"dlm\":1,\"iplh\":1,\"iplm\":1,\"plh\":1,\"plm\":1}}}";

	String infoJSONDevice = "{\"device\":{\"id\":\"" + fingerprint + "\",\"ipAddress\":\"" + ipAddress
			+ "\",\"ipGeo\":\"BG\",\"mobile\":0,\"proxy\":0,\"tor\":0,"
			+ "\"region\":\"53\",\"country\":\"BG\",\"geoLat\":43.8564,\"geoLong\":25.9708},\"response_id\":\""
			+ responseId + "\"}}";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getInfo(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * with full data.
	 */
	@Test
	public void testGetInfoHappyPathWithFullData() {
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
			doReturn(infoJSONFull).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			int infoFlag = new InfoEndpointDataSet().withInfo().withVelocity().withBehavioSec().withDecision()
					.withTrustedDevice().build();
			// test method
			JSONObject info = sdk.getInfo(infoFlag, session, uniq, user, pass);
			logger.debug(info);
			JSONObject behavioSec = info.getJSONObject("behavioSec");
			assertEquals(false, behavioSec.get("isBot"));
			JSONObject decision = info.getJSONObject("decision");
			assertEquals("A", decision.getJSONObject("reply").getJSONObject("ruleEvents").get("decision"));
			JSONObject device = info.getJSONObject("device");
			assertEquals(fingerprint, device.get("id"));
			assertEquals(trustedState, info.getJSONObject("trusted").get("state"));
			JSONObject velocity = info.getJSONObject("velocity");
			assertEquals(1, velocity.getJSONObject("account").get("dlh"));
			assertEquals(1, velocity.getJSONObject("device").get("alh"));
			assertEquals(responseId, info.get("response_id"));

		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getInfo(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * with device info.
	 */
	@Test
	public void testGetInfoHappyPathWithDeviceInfo() {
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
			doReturn(infoJSONDevice).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			int infoFlag = new InfoEndpointDataSet().withInfo().build();
			// test method
			JSONObject info = sdk.getInfo(infoFlag, session, uniq, user, pass);
			logger.debug(info);
			JSONObject device = info.getJSONObject("device");
			assertEquals(fingerprint, device.get("id"));
			assertEquals(responseId, info.get("response_id"));

		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

}
