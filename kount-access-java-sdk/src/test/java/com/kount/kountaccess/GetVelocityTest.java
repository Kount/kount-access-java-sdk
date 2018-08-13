/**
 *
 */
package com.kount.kountaccess;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import com.kount.kountaccess.AccessException.AccessErrorType;

import net.sf.json.JSONObject;

/**
 * Unit Tests around the Access SDK's velocity endpoint.
 *
 * @author Stanislav Milev
 */
public class GetVelocityTest {

	// Setup data for comparisons.
	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String accessUrl = "https://" + host + "/access";
	String fingerprint = "75012bd5e5b264c4b324f5c95a769541";
	String ipAddress = "64.128.91.251";
	String ipGeo = "US";
	String responseId = "bf10cd20cf61286669e87342d029e405";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String user = "greg@test.com";
	String password = "password";

	String velocityJSON = "{" + "    \"device\": {" + "        \"id\": \"" + fingerprint + "\", "
			+ "        \"ipAddress\": \"" + ipAddress + "\", " + "        \"ipGeo\": \"" + ipGeo + "\", "
			+ "        \"mobile\": 1, " + "        \"proxy\": 0" + "    }, " + "    \"response_id\": \"" + responseId
			+ "\", " + "    \"velocity\": {" + "        \"account\": {" + "            \"dlh\": 1, "
			+ "            \"dlm\": 1, " + "            \"iplh\": 1, " + "            \"iplm\": 1, "
			+ "            \"plh\": 1, " + "            \"plm\": 1, " + "            \"ulh\": 1, "
			+ "            \"ulm\": 1" + "        }, " + "        \"device\": {" + "            \"alh\": 1, "
			+ "            \"alm\": 1, " + "            \"iplh\": 1, " + "            \"iplm\": 1, "
			+ "            \"plh\": 1, " + "            \"plm\": 1, " + "            \"ulh\": 1, "
			+ "            \"ulm\": 1" + "        }, " + "        \"ip_address\": {" + "            \"alh\": 1, "
			+ "            \"alm\": 1, " + "            \"dlh\": 1, " + "            \"dlm\": 1, "
			+ "            \"plh\": 1, " + "            \"plm\": 1, " + "            \"ulh\": 1, "
			+ "            \"ulm\": 1" + "        }, " + "        \"password\": {" + "           \"alh\": 1, "
			+ "           \"alm\": 1, " + "           \"dlh\": 1, " + "           \"dlm\": 1, "
			+ "            \"iplh\": 1, " + "            \"iplm\": 1, " + "            \"ulh\": 1, "
			+ "            \"ulm\": 1" + "        }, " + "        \"user\": {" + "            \"alh\": 1, "
			+ "            \"alm\": 1, " + "            \"dlh\": 1, " + "            \"dlm\": 1, "
			+ "            \"iplh\": 1, " + "            \"iplm\": 1, " + "            \"plh\": 1, "
			+ "            \"plm\": 1" + "        }" + "    }" + "}";

	private static final Set<String> entityTypes = new HashSet<>(
			Arrays.asList("account", "device", "ip_address", "password", "user"));

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getVelocity(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetVelocityHappyPath() {

		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
			StatusLine mockStatus = mock(StatusLine.class);
			doReturn(mockResponse).when(mockHttpClient).execute((HttpPost) anyObject());
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			doReturn(velocityJSON).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			JSONObject accessInfo = sdk.getVelocity(session, user, password);
			assertTrue(accessInfo != null);
			// validate device
			JSONObject device = accessInfo.getJSONObject("device");
			assertTrue(accessInfo != null);
			assertEquals(fingerprint, device.get("id"));
			assertEquals(ipAddress, device.get("ipAddress"));
			assertEquals(ipGeo, device.get("ipGeo"));
			assertEquals(1, device.get("mobile"));
			assertEquals(0, device.get("proxy"));
			// validate id
			assertEquals(responseId, accessInfo.getString("response_id"));
			JSONObject velocities = accessInfo.getJSONObject("velocity");
			Iterator<String> iter = entityTypes.iterator();
			while (iter.hasNext()) {
				String entityType = iter.next();
				JSONObject velocityInfo = velocities.getJSONObject(entityType);
				assertNotNull("Velocity Type was null " + entityType, velocityInfo);
				assertEquals(8, velocityInfo.keySet().size());
			}
		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test IllegalArgumentException Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getVelocity(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetVelocityIllegalArgumentException() {

		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk("whetever is bad", merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.getVelocity(session, user, password);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test Client Protocol Exception Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getVelocity(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetVelocityClientProtocolException() {

		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			doThrow(new ClientProtocolException()).when(mockHttpClient).execute((HttpPost) anyObject());
			AccessSdk sdk = spy(new AccessSdk("gty://bad.host.com", merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.getVelocity(session, user, password);
			fail("AccessException Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.NETWORK_ERROR, ae.getAccessErrorType());
		} catch (ClientProtocolException e) {
			fail("Should not have thrown ClientProtocolException");
		} catch (IOException e) {
			fail("Should not have thrown IOException");
		}
	}

	/**
	 * Test IO Exception Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getVelocity(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetVelocityIOException() {

		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			doThrow(new IOException()).when(mockHttpClient).execute((HttpPost) anyObject());
			AccessSdk sdk = spy(new AccessSdk("bad.host.com", merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.getVelocity(session, user, password);
			fail("AccessException Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.NETWORK_ERROR, ae.getAccessErrorType());
		} catch (ClientProtocolException e) {
			fail("Should not have thrown ClientProtocolException");
		} catch (IOException e) {
			fail("Should not have thrown IOException");
		}
	}

	/**
	 * Test UnknownHostException Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getVelocity(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetVelocityUnknownHostException() {

		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk("bad.host.com", merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			when(mockHttpClient.execute((HttpPost) anyObject())).thenThrow(new UnknownHostException());
			sdk.getVelocity(session, user, password);
			fail("AccessException Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.NETWORK_ERROR, ae.getAccessErrorType());
		} catch (ClientProtocolException e) {
			fail("Should not have thrown ClientProtocolException");
		} catch (IOException e) {
			fail("Should not have thrown IOException");
		}
	}

}
