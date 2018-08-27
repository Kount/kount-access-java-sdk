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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.kount.kountaccess.AccessException.AccessErrorType;

/**
 * Unit Tests around the Access SDK's devicetrustbydevice endpoint.
 *
 * @author Stanislav Milev
 */
public class SetDeviceTrustByDeviceTest {

	private static final Logger logger = Logger.getLogger(SetDeviceTrustByDeviceTest.class);

	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String accessUrl = "https://" + host + "/access";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String fingerprint = "75012bd5e5b264c4b324f5c95a769541";
	String uniq = "customer identifier";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceHappyPath() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
			StatusLine mockStatus = mock(StatusLine.class);
			doReturn(mockResponse).when(mockHttpClient).execute((HttpPost) anyObject());
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			doReturn("").when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			sdk.setDeviceTrustByDevice(fingerprint, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test IllegalArgumentException for missing deviceId(fingerprint) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceMissingDeviceId() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(null, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank deviceId(fingerprint) Test method
	 * for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceBlankDeviceId() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice("", uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for deviceId(fingerprint) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceSpaceForDeviceId() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(" ", uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing uniq(customer identifier) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceMissingUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, null, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank uniq(customer identifier) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceBlankUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, "", AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for uniq(customer identifier)
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceSpaceForUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, " ", AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceMissingTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, uniq, null);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceBlankTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, uniq, "");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceSpaceForTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, uniq, " ");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for wrong trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustByDevice(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustByDeviceWrongTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustByDevice(fingerprint, uniq, "some random text");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

}
