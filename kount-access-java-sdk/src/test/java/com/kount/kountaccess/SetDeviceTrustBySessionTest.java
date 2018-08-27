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
 * Unit Tests around the Access SDK's devicetrustbysession endpoint.
 *
 * @author Stanislav Milev
 */
public class SetDeviceTrustBySessionTest {

	private static final Logger logger = Logger.getLogger(SetDeviceTrustBySessionTest.class);

	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String accessUrl = "https://" + host + "/access";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String uniq = "customer identifier";
	String session = "askhjdaskdgjhagkjhasg47862345shg";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionHappyPath() {
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
			sdk.setDeviceTrustBySession(session, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test IllegalArgumentException for missing session Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionMissingSession() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(null, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank session Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionBlankSession() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession("", uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for session Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionSpaceForSession() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(" ", uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing uniq(customer identifier) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionMissingUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, null, AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank uniq(customer identifier) Test
	 * method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionBlankUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, "", AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for uniq(customer identifier)
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionSpaveForUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, " ", AccessSdk.TRUSTED_STATE_TRUSTED);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionMissingTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, uniq, null);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionBlankTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, uniq, "");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionSpaceForTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, uniq, " ");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for wrong trustedState Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setDeviceTrustBySession(String, String, String)}
	 */
	@Test
	public void testSetDeviceTrustBySessionWrongTrustedState() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setDeviceTrustBySession(session, uniq, "some random text");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

}
