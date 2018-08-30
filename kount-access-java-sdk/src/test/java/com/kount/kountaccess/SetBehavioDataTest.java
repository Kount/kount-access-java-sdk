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
 * Unit Tests around the Access SDK's behavio/data endpoint.
 *
 * @author Stanislav Milev
 */
public class SetBehavioDataTest {

	private static final Logger logger = Logger.getLogger(SetBehavioDataTest.class);

	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String accessUrl = "https://" + host + "/access";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String fingerprint = "75012bd5e5b264c4b324f5c95a769541";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String uniq = "customer identifier";
	String behavioHost = "api.behavio.kaptcha.com";
	String environment = "sandbox";
	String timing = "{\"valid\":\"json\"}";

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataHappyPath() {
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
			sdk.setBehavioData(behavioHost, environment, session, timing, uniq);
		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

	/**
	 * Test IllegalArgumentException for missing behavioHost Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehaviorDataMissingBehavioHost() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(null, environment, session, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			ae.printStackTrace();
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank behavioHost Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataBlankBehavioHost() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData("", environment, session, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for behavioHost Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataSpaceForBehavioHost() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(" ", environment, session, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing environment Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataMissingEnvironment() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, null, session, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			ae.printStackTrace();
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank environment Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataBlankEnvironment() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, "", session, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for environment Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataSpaceForEnvironment() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, " ", session, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing session Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataMissingSession() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, null, timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			ae.printStackTrace();
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank session Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataBlankSession() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, "", timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for session Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataSpaceForSession() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, " ", timing, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing timing Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataMissingTiming() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, null, uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			ae.printStackTrace();
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank timing Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataBlankTiming() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, "", uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for timing Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataSpaceForTiming() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, " ", uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with not a json for timing Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataNotJSONTiming() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, "not a json", uniq);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for missing uniq Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataMissingUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, timing, null);
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			ae.printStackTrace();
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException for blank uniq Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataBlankUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, timing, "");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test IllegalArgumentException with space for uniq Test method for
	 * {@link com.kount.kountaccess.AccessSdk#setBehavioData(String, String, String, String, String)}
	 */
	@Test
	public void testSetBehavioDataSpaceForUniq() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			sdk.setBehavioData(behavioHost, environment, session, timing, " ");
			fail("Exception Not thrown");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

}
