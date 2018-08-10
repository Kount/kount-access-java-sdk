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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.junit.Test;

import net.sf.json.JSONObject;

/**
 * Unit Tests around the Access SDK's decision endpoint.
 *
 * @author Stanislav Milev
 */
public class GetDecisionTest {

	private static final Logger logger = Logger.getLogger(GetDecisionTest.class);

	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String accessUrl = "https://" + host + "/access";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";
	String fingerprint = "75012bd5e5b264c4b324f5c95a769541";
	String ipAddress = "64.128.91.251";
	String ipGeo = "US";
	String responseId = "bf10cd20cf61286669e87342d029e405";
	String decision = "A";
	String session = "askhjdaskdgjhagkjhasg47862345shg";
	String user = "greg@test.com";
	String password = "password";

	String decisionJSON = "{" + "   \"decision\": {" + "       \"errors\": []," + "       \"reply\": {"
			+ "           \"ruleEvents\": {" + "               \"decision\": \"" + decision + "\","
			+ "               \"ruleEvents\": []," + "               \"total\": 0" + "           }" + "       },"
			+ "       \"warnings\": []" + "   }," + "    \"device\": {" + "        \"id\": \"" + fingerprint + "\", "
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

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#getDecision(String, String, String)}.
	 */
	@Test
	public void testGetDecisionHappyPath() {
		try {
			CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
			HttpPost mockPost = mock(HttpPost.class);
			CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
			StatusLine mockStatus = mock(StatusLine.class);
			doReturn(mockResponse).when(mockHttpClient).execute((HttpPost) anyObject());
			AccessSdk sdk = spy(new AccessSdk(host, merchantId, apiKey));
			doReturn(mockHttpClient).when(sdk).getHttpClient();
			doReturn(mockPost).when(sdk).getHttpPost(accessUrl);
			doReturn(decisionJSON).when(sdk).getResponseAsString(mockResponse);
			doReturn(mockStatus).when(mockResponse).getStatusLine();
			doReturn(200).when(mockStatus).getStatusCode();
			JSONObject decisionInfo = sdk.getDecision(session, user, password);
			logger.debug(decisionInfo);
			assertTrue(decisionInfo != null);
			// validate device
			JSONObject decisionn = decisionInfo.getJSONObject("decision");
			JSONObject reply = decisionn.getJSONObject("reply");
			JSONObject ruleEvents = reply.getJSONObject("ruleEvents");
			assertEquals(decision, ruleEvents.get("decision"));
		} catch (IOException ioe) {
			fail("Exception:" + ioe.getMessage());
		} catch (AccessException ae) {
			fail("Exception:" + ae.getMessage());
		}
	}

}
