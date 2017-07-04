package com.kount.kountaccess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.kount.kountaccess.AccessException.AccessErrorType;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * The AccessSdk module contains functions for a client to call the Kount Access API Service.
 * <p>
 * In order to use the SDK, you must construct it using three valid fields:
 * <ul>
 * <li>host - The fully qualified host name of the Kount Access server you are connecting to. (e.g.,
 * api-sandbox1.kountaccess.com)</li>
 * <li>merchantId - The Kount assigned merchant number (six digits)</li>
 * <li>apiKey - The API key assigned to the merchant.</li>
 * </ul>
 * <p>
 *
 * @author custserv@kount.com
 * 
 * @version 2.1.0
 */
public class AccessSdk {

	private static final Logger logger = Logger.getLogger(AccessSdk.class);

	/**
	 * This is the default version of the API Responses that this SDK will request. Future versions are intended to be
	 * compatible with this version of the SDK.
	 */
	public final String DEFAULT_API_VERSION = "0210";

	/**
	 * Merchant's ID
	 */
	private int merchantId;

	/**
	 * Merchants API Key
	 */
	private String apiKey;

	/**
	 * Array of alphanum characters
	 */
	protected final static char[] hexArray = "0123456789abcdef".toCharArray();

	/**
	 * Version of the API response to use.
	 */
	private String version;

	/**
	 * Velocity endpoint
	 */
	private final String velocityEndpoint;

	/**
	 * Decision endpoint
	 */
	private final String decisionEndpoint;

	/**
	 * Device endpoint
	 */
	private final String deviceEndpoint;

	/**
	 * Creates an instance of the AccessSdk associated with a specific host and merchant.
	 * 
	 * @param host
	 *            FQDN of the host that AccessSdk will talk to.
	 * @param merchantId
	 *            Merchant ID (6 digit value).
	 * @param apiKey
	 *            The API Key for the merchant.
	 * @throws AccessException
	 *             Thrown if any of the values are invalid. ({@link AccessErrorType#INVALID_DATA}).
	 */
	public AccessSdk(String host, int merchantId, String apiKey) throws AccessException {
		if (host == null || host.isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing host");
		}

		if (apiKey == null) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing apiKey");
		}

		if (apiKey.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Invalid apiKey(" + apiKey + ")");
		}

		if (merchantId < 99999 || merchantId > 1000000) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Invalid merchantId");
		}

		// initialize the Access SDK endpoints
		this.velocityEndpoint = "https://" + host + "/api/velocity";
		this.deviceEndpoint = "https://" + host + "/api/device";
		this.decisionEndpoint = "https://" + host + "/api/decision";

		this.merchantId = merchantId;
		this.apiKey = apiKey;
		this.version = DEFAULT_API_VERSION;

		logger.info("Access SDK using merchantId = " + this.merchantId + ", host = " + host);
		logger.debug("velocity endpoint: " + velocityEndpoint);
		logger.debug("decisionendpoint: " + decisionEndpoint);
		logger.debug("device endpoint: " + deviceEndpoint);
	}

	/**
	 * Creates instance of the AccessSdk, allowing the client to specify version of responses to request.
	 * 
	 * @param host
	 *            FQDN of the host that AccessSdk will talk to.
	 * @param merchantId
	 *            Merchant ID (6 digit value).
	 * @param apiKey
	 *            The API Key for the merchant.
	 * @param version
	 *            The version of the API response to return.
	 * @throws AccessException
	 *             Thrown if any of the values are invalid.
	 */
	public AccessSdk(String host, int merchantId, String apiKey, String version) throws AccessException {
		this(host, merchantId, apiKey);
		this.version = version;
	}

	/**
	 * Gets the access (velocity) data for the session's username and password.
	 *
	 * @param session
	 *            The Session ID returned from the Javascript data collector client SDK.
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getVelocity(String session, String username, String password) throws AccessException {
		return getVelocity(session, username, password, null);
	}

	/**
	 * Gets the access (velocity) data for the session's username and password. Contains argument for passing additional
	 * parameters.
	 *
	 * @param session
	 *            The Session ID returned from the Javascript data collector client SDK
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getVelocity(String session, String username, String password,
			Map<String, String> additionalParameters) throws AccessException {

		verifySessionId(session);

		List<NameValuePair> parameters = createRequestParameters(session, username, password, additionalParameters);

		logger.debug("velocity request: host = " + velocityEndpoint + ", parameters = " + parameters.toString());
		String response = this.postRequest(velocityEndpoint, parameters);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * Gets the device information for the session.
	 *
	 * @param session
	 *            The session to lookup
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getDevice(String session) throws AccessException {
		return getDevice(session, null);
	}

	/**
	 * Gets the device information for the session. Contains argument for passing additional parameters.
	 *
	 * @param session
	 *            The session to lookup
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getDevice(String session, HashMap<String, String> additionalParameters) throws AccessException {

		verifySessionId(session);

		StringBuilder parameters = new StringBuilder("?");
		// version and session
		parameters.append("v=").append(version).append("&s=").append(session);

		// Add the additional parameters, if they exist.
		if (additionalParameters != null) {
			for (Map.Entry<String, String> entry : additionalParameters.entrySet()) {
				parameters.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}

		String urlString = deviceEndpoint + parameters;

		logger.debug("device info request: url = " + urlString);

		String response = this.getRequest(urlString);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * Gets the threshold decision and velocity data for the session's username and password.
	 *
	 * @param session
	 *            The Session ID returned from the Javascript data collector client SDK.
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getDecision(String session, String username, String password) throws AccessException {
		return getDecision(session, username, password, null);
	}

	/**
	 * Gets the threshold decision and velocity data for the session's username and password. Contains argument for
	 * passing additional parameters.
	 *
	 * @param session
	 *            The Session ID returned from the Javascript data collector client SDK.
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getDecision(String session, String username, String password,
			Map<String, String> additionalParameters) throws AccessException {

		verifySessionId(session);

		List<NameValuePair> parameters = createRequestParameters(session, username, password, additionalParameters);
		logger.debug("decision request: host = " + decisionEndpoint + ", parameters = " + parameters.toString());
		String response = this.postRequest(decisionEndpoint, parameters);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	private void verifySessionId(String session) throws AccessException {
		if (null == session || session.length() != 32) {
			throw new AccessException(AccessErrorType.INVALID_DATA,
					"Invalid sessionid (" + session + ").  Must be 32 characters in length");
		}
	}

	private List<NameValuePair> createRequestParameters(String session, String username, String password,
			Map<String, String> additionalParameters) {

		List<NameValuePair> values = new ArrayList<>();
		values.add(new BasicNameValuePair("v", this.version));
		values.add(new BasicNameValuePair("s", session));

		if (username != null) {
			values.add(new BasicNameValuePair("uh", hashValue(username)));
		}
		if (null != password) {
			values.add(new BasicNameValuePair("ph", hashValue(password)));
		}
		if (null != username || null != password) {
			values.add(new BasicNameValuePair("ah", hashValue(username + ":" + password)));
		}

		// Add the additional parameters, if they exist.
		if (additionalParameters != null) {
			for (Map.Entry<String, String> entry : additionalParameters.entrySet()) {
				values.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		return values;
	}

	/**
	 * Converts the auth header.
	 */
	private String getAuthorizationHeader() {
		String header = merchantId + ":" + apiKey;
		try {
			String encoded = "Basic " + DatatypeConverter.printBase64Binary(header.getBytes("UTF8"));
			return encoded;
		} catch (UnsupportedEncodingException e) {
			logger.warn("Could not create authorization header", e);
		}
		return null;
	}

	/**
	 * Returns a SHA-256 hashed value for a string.
	 *
	 * @param value
	 *            The String to convert
	 * @return The converted string.
	 */
	private String hashValue(String value) {
		// Don't do anything if the value is empty
		if (null == value || value.isEmpty()) {
			return null;
		}
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(value.getBytes("UTF8"), 0, value.length());
			byte[] hash = md.digest();
			char[] hexChars = new char[hash.length * 2];
			int v;
			for (int j = 0; j < hash.length; j++) {
				v = hash[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}
			return new String(hexChars);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// ignoring
			logger.warn("Could not hash parameter value", e);
		}

		return null;
	}

	/**
	 * Handles the get request for the device info
	 */
	private String getRequest(String urlString) throws AccessException {
		try {
			CloseableHttpClient client = getHttpClient();
			HttpGet request = this.getHttpGet(urlString);
			request.addHeader("Authorization", this.getAuthorizationHeader());
			request.addHeader("Content-Type", "JSON");
			CloseableHttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				throw new AccessException(AccessErrorType.NETWORK_ERROR,
						"Bad Response(" + status.getStatusCode() + ")" + status.getReasonPhrase() + " " + urlString);

			}
			return this.getResponseAsString(response);
		} catch (UnknownHostException uhe) {
			throw new AccessException(AccessErrorType.NETWORK_ERROR, "UNKNOWN HOST(" + urlString + ")");
		} catch (IOException e) {
			throw new AccessException(AccessErrorType.NETWORK_ERROR, "UNKNOWN NETWORK ISSUE, try again later)");
		} catch (IllegalArgumentException iae) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "BAD URL(" + urlString + ")");
		}
	}

	/**
	 * Handles the post for the access request.
	 *
	 * @param urlString
	 *            The URL to post to
	 * @param values
	 *            The Form parameters
	 * @return The Response as a String.
	 * @throws AccessException
	 *             Thrown if the URL is bad or we can't connect or parse the response.
	 */
	private String postRequest(String urlString, List<NameValuePair> values) throws AccessException {
		try {
			CloseableHttpClient client = getHttpClient();
			HttpPost request = getHttpPost(urlString);
			request.addHeader("Authorization", this.getAuthorizationHeader());
			HttpEntity entity = new UrlEncodedFormEntity(values);
			request.setEntity(entity);

			CloseableHttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				throw new AccessException(AccessErrorType.NETWORK_ERROR,
						"Bad Response(" + status.getStatusCode() + ")" + status.getReasonPhrase() + " " + urlString);

			}
			return getResponseAsString(response);
		} catch (UnknownHostException uhe) {
			throw new AccessException(AccessErrorType.NETWORK_ERROR, "UNKNOWN HOST(" + urlString + ")");
		} catch (IOException e) {
			throw new AccessException(AccessErrorType.NETWORK_ERROR, "UNKNOWN NETWORK ISSUE, try again later)");
		} catch (IllegalArgumentException iae) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "BAD URL(" + urlString + ")");
		}
	}

	/**
	 * Processes the Response to generate a JSONObject.
	 *
	 * @param response
	 *            The Http response data as a string
	 * @return The access JSONObject or null.
	 */
	private JSONObject processJSONEntity(String response) throws AccessException {
		JSONObject result = null;
		try {
			result = JSONObject.fromObject(response);
		} catch (JSONException e) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Unable to parse response.");
		}
		return result;
	}

	/**
	 * Converts the Response into a String.
	 *
	 * @param response
	 *            The Response to convert
	 * @return Response converted toString (if possible), or null if it's null.
	 * @throws AccessException
	 *             Thrown if unable to parse the response.
	 */
	String getResponseAsString(CloseableHttpResponse response) throws AccessException {
		if (response != null) {
			try {
				return EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				throw new AccessException(AccessErrorType.INVALID_DATA, "Unable to parse Response");
			} catch (IOException e) {
				throw new AccessException(AccessErrorType.INVALID_DATA, "Unable to parse Response");
			}
		}
		return null;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Methods that require mocks when testing
	//
	// Not present in documentation
	//
	///////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Gets the HttpPost object by itself to we can mock it easier.
	 *
	 * @return An HttpPost object
	 */
	HttpPost getHttpPost(String url) throws IllegalArgumentException {
		return new HttpPost(url);
	}

	/*
	 * Gets the HttpGet object by itself to we can mock it easier.
	 *
	 * @return An HttpGet object
	 */
	HttpGet getHttpGet(String url) throws IllegalArgumentException {
		return new HttpGet(url);
	}

	/*
	 * Getting the httpclient by itself so we can mock it.
	 *
	 * @return A CloseableHttpClient object.
	 */
	CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Deprecated methods
	//
	// Not present in documentation
	//
	///////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the device information for the session.
	 *
	 * @deprecated Use getDevice().
	 *
	 * @param session
	 *            The session to lookup
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the param values are invalid or there was a problem getting a response.
	 */
	public JSONObject getDeviceInfo(String session) throws AccessException {
		return getDevice(session);
	}

	/**
	 * Gets the velocity data for the session's username and password.
	 *
	 * @deprecated Use getVelocity().
	 *
	 * @param session
	 *            The Session ID returned from the Javascript data collector. client SDK
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the param values are invalid or there was a problem getting a response.
	 */
	public JSONObject getAccessData(String session, String username, String password) throws AccessException {
		return getVelocity(session, username, password, null);
	}

	/**
	 * Gets the velocity data for the session's username and password. Contains argument for passing additional
	 * parameters.
	 *
	 * @deprecated Use getVelocity().
	 *
	 * @param session
	 *            The Session ID returned from the Javascript data collector.
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @param additionalParams
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the param values are invalid or there was a problem getting a response.
	 */
	public JSONObject getAccessData(String session, String username, String password,
			Map<String, String> additionalParams) throws AccessException {
		return getVelocity(session, username, password, additionalParams);
	}

	/**
	 * Get the help page for getDeviceInfo
	 *
	 * @deprecated Use helpGetDevice().
	 *
	 * @return HTML String.
	 * @throws AccessException
	 */
	public String helpGetDeviceInfo() throws AccessException {
		return helpGetDevice();
	}

	/**
	 * Get the help page for getDevice
	 *
	 * @deprecated
	 * 
	 * @return HTML String.
	 * @throws AccessException
	 */
	public String helpGetDevice() throws AccessException {
		String urlString = deviceEndpoint + "?v=" + version + "&help=";
		String response = this.getRequest(urlString);
		return response;
	}

	/**
	 * Get the help page for getAccessData.
	 *
	 * @deprecated Use helpGetVelocity().
	 *
	 * @return HTML String.
	 * @throws AccessException
	 */
	public String helpGetAccessData() throws AccessException {
		return helpGetVelocity();
	}

	/**
	 * Get the help page for getVelocity.
	 *
	 * @deprecated
	 * 
	 * @return HTML String.
	 * @throws AccessException
	 */
	public String helpGetVelocity() throws AccessException {
		List<NameValuePair> values = new ArrayList<NameValuePair>();

		values.add(new BasicNameValuePair("v", this.version));
		values.add(new BasicNameValuePair("help", ""));

		return this.postRequest(velocityEndpoint, values);
	}

	/**
	 * Get the help page for getVelocity.
	 *
	 * @deprecated
	 * 
	 * @return HTML String.
	 * @throws AccessException
	 */
	public String helpGetDecision() throws AccessException {
		List<NameValuePair> values = new ArrayList<NameValuePair>();

		values.add(new BasicNameValuePair("v", this.version));
		values.add(new BasicNameValuePair("help", ""));

		return this.postRequest(decisionEndpoint, values);
	}

}