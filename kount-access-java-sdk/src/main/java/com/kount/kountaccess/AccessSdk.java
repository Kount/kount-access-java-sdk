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

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * The AccessSdk module contains functions for a client to call the Kount Access API Service.
 * <p>
 * In order to use the SDK, you must construct it using three valid fields:
 * <ul>
 * <li>host - The fully qualified host name of the Kount Access server you are connecting to. (e.g.,
 * api-sandbox01.kountaccess.com)</li>
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
	public final String DEFAULT_API_VERSION = "0400";

	/**
	 * Trusted state - trusted.
	 */
	public static final String TRUSTED_STATE_TRUSTED = "trusted";

	/**
	 * Trusted state - not_trusted.
	 */
	public static final String TRUSTED_STATE_NOT_TRUSTED = "not_trusted";

	/**
	 * Trusted state - banned.
	 */
	public static final String TRUSTED_STATE_BANNED = "banned";

	/**
	 * Bahavio Data Endpoint Prefix
	 */
	private static final String BEHAVIO_DATA_ENDPOINT_PREFIX = "https://";

	/**
	 * Bahavio Data Endpoint Postfix
	 */
	private static final String BEHAVIO_DATA_ENDPOINT_POSTFIX = "/behavio/data";

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
	 * devicetrustbydevice endpoint
	 */
	private final String deviceTrustByDeviceEndpoint;

	/**
	 * devicetrustbysession endpoint
	 */
	private final String deviceTrustBySessionEndpoint;

	/**
	 * getdevices endpoint
	 */
	private final String getDevicesEndpoint;

	/**
	 * getuniques endpoint
	 */
	private final String getUniquesEndpoint;

	/**
	 * Info endpoint
	 */
	private final String infoEndpoint;

	/**
	 * Authorization header
	 */
	private String authorizationHeader;

	/**
	 * Creates an instance of the AccessSdk associated with a specific host and
	 * merchant.
	 *
	 * @param host
	 *            FQDN of the host that AccessSdk will communicate with.
	 * @param merchantId
	 *            Merchant ID (6 digit value).
	 * @param apiKey
	 *            The API Key for the merchant.
	 * @throws AccessException
	 *             Thrown if any of the values are invalid.
	 *             ({@link AccessErrorType#INVALID_DATA}).
	 */
	public AccessSdk(String host, int merchantId, String apiKey) throws AccessException {
		if ((host == null) || host.isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing host");
		}

		if (apiKey == null) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing apiKey");
		}

		if (apiKey.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Invalid apiKey(" + apiKey + ")");
		}

		if ((merchantId < 100000) || (merchantId > 999999)) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Invalid merchantId");
		}

		// initialize the Access SDK endpoints
		this.velocityEndpoint = "https://" + host + "/api/velocity";
		this.deviceEndpoint = "https://" + host + "/api/device";
		this.decisionEndpoint = "https://" + host + "/api/decision";
		this.deviceTrustByDeviceEndpoint = "https://" + host + "/api/devicetrustbydevice";
		this.deviceTrustBySessionEndpoint = "https://" + host + "/api/devicetrustbysession";
		this.getDevicesEndpoint = "https://" + host + "/api/getdevices";
		this.getUniquesEndpoint = "https://" + host + "/api/getuniques";
		this.infoEndpoint = "https://" + host + "/api/info";

		this.merchantId = merchantId;
		this.apiKey = apiKey;
		this.version = DEFAULT_API_VERSION;

		logger.info("Access SDK using merchantId = " + this.merchantId + ", host = " + host + ", version = " + version
				+ " and API key starting with " + apiKey.substring(0, 4));
		logger.debug("velocity endpoint: " + velocityEndpoint);
		logger.debug("decision endpoint: " + decisionEndpoint);
		logger.debug("device endpoint: " + deviceEndpoint);
		logger.debug("devicetrustbydevice endpoint: " + deviceTrustByDeviceEndpoint);
		logger.debug("devicetrustbysession endpoint: " + deviceTrustBySessionEndpoint);
		logger.debug("getdevices endpoint: " + getDevicesEndpoint);
		logger.debug("getuniques endpoint: " + getUniquesEndpoint);
		logger.debug("info endpoint: " + infoEndpoint);
	}

	/**
	 * Creates instance of the AccessSdk, allowing the client to specify version of responses to request.
	 *
	 * @param host
	 *            FQDN of the host that AccessSdk will communicate with.
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
	 *            The Session ID generated for the Data Collector service.
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
	 *            The Session ID generated for the Data Collector service.
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
		long startTime = System.currentTimeMillis();
		String response = this.postRequest(velocityEndpoint, parameters);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime) + ", response = " + response);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * Gets the device information for the session.
	 *
	 * @param session
	 *            The Session ID generated for the Data Collector service.
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
	 *            The Session ID generated for the Data Collector service.
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
	 */
	public JSONObject getDevice(String session, Map<String, String> additionalParameters) throws AccessException {

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

		long startTime = System.currentTimeMillis();
		String response = this.getRequest(urlString);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime) + ", response = " + response);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * Gets an array of devices for the given uniq customer ID.
	 *
	 * @param deviceId
	 *            Device ID(fingerprint).
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public JSONObject getDevices(String uniq) throws AccessException {
		return getDevices(uniq, null);
	}

	/**
	 * Gets an array of devices for the given uniq customer ID.
	 *
	 * @param deviceId
	 *            Device ID(fingerprint).
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public JSONObject getDevices(String uniq, Map<String, String> additionalParameters) throws AccessException {
		if ((uniq == null) || uniq.isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing uniq.");
		}

		StringBuilder parameters = new StringBuilder("?");
		// version and uniq
		parameters.append("v=").append(version).append("&uniq=").append(uniq);

		// Add the additional parameters, if they exist.
		if (additionalParameters != null) {
			for (Map.Entry<String, String> entry : additionalParameters.entrySet()) {
				parameters.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}

		String urlString = getDevicesEndpoint + parameters;

		logger.debug("getdevices request: url = " + urlString);
		long startTime = System.currentTimeMillis();
		String response = this.getRequest(urlString);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime) + ", response = " + response);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * devicetrustbydevice Endpoint. Sets the device trusted state by device id.
	 *
	 * @param deviceId
	 *            Device ID(fingerprint).
	 * @param uniq
	 *            customer IDs
	 * @param trustedState
	 *            trusted state (possible values: trusted, not_trusted and
	 *            banned)
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public void setDeviceTrustByDevice(String deviceId, String uniq, String trustedState) throws AccessException {
		setDeviceTrustByDevice(deviceId, uniq, trustedState, null);
	}

	/**
	 * devicetrustbydevice Endpoint. Sets the device trusted state by device id.
	 * Contains argument for passing additional parameters.
	 *
	 * @param deviceId
	 *            Device ID(fingerprint).
	 * @param uniq
	 *            customer IDs
	 * @param trustedState
	 *            trusted state (possible values: trusted, not_trusted and
	 *            banned)
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public void setDeviceTrustByDevice(String deviceId, String uniq, String trustedState,
			Map<String, String> additionalParameters) throws AccessException {
		if ((deviceId == null) || deviceId.isEmpty() || deviceId.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing deviceId.");
		}
		if ((uniq == null) || uniq.isEmpty() || uniq.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing uniq.");
		}
		verifyTrustedState(trustedState);

		if (additionalParameters == null) {
			additionalParameters = new HashMap<>();
		}
		additionalParameters.put("d", deviceId);
		additionalParameters.put("uniq", uniq);
		additionalParameters.put("ts", trustedState);

		List<NameValuePair> parameters = createRequestParameters(null, null, null, additionalParameters);
		logger.debug("devicetrustbydevice request: host = " + deviceTrustByDeviceEndpoint + ", parameters = "
				+ parameters.toString());
		long startTime = System.currentTimeMillis();
		this.postRequest(deviceTrustByDeviceEndpoint, parameters);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * devicetrustbysession Endpoint. Sets device trusted state by session.
	 *
	 * @param session
	 *            The Session ID generated for the Data Collector service.
	 * @param uniq
	 *            customer IDs
	 * @param trustedState
	 *            trusted state (possible values: trusted, not_trusted and
	 *            banned)
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public void setDeviceTrustBySession(String session, String uniq, String trustedState) throws AccessException {
		setDeviceTrustBySession(session, uniq, trustedState, null);
	}

	/**
	 * devicetrustbysession Endpoint. Sets device trusted state by session.
	 * Contains argument for passing additional parameters.
	 *
	 * @param session
	 *            The Session ID generated for the Data Collector service.
	 * @param uniq
	 *            customer IDs
	 * @param trustedState
	 *            trusted state (possible values: trusted, not_trusted and
	 *            banned)
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return The JSONObject with data about the device.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public void setDeviceTrustBySession(String session, String uniq, String trustedState,
			Map<String, String> additionalParameters) throws AccessException {
		verifySessionId(session);
		if ((uniq == null) || uniq.isEmpty() || uniq.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing or empty uniq.");
		}
		verifyTrustedState(trustedState);

		if (additionalParameters == null) {
			additionalParameters = new HashMap<>();
		}
		additionalParameters.put("uniq", uniq);
		additionalParameters.put("ts", trustedState);

		List<NameValuePair> parameters = createRequestParameters(session, null, null, additionalParameters);
		logger.debug("devicetrustbysession request: host = " + deviceTrustBySessionEndpoint + ", parameters = "
				+ parameters.toString());
		long startTime = System.currentTimeMillis();
		this.postRequest(deviceTrustBySessionEndpoint, parameters);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Gets the threshold decision and velocity data for the session's username and password.
	 *
	 * @param session
	 *            The Session ID generated for the Data Collector service.
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
	 *            The Session ID generated for the Data Collector service.
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
		long startTime = System.currentTimeMillis();
		String response = this.postRequest(decisionEndpoint, parameters);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime) + ", response = " + response);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * Gets an array of uniq customer IDs for the given deviceId.
	 *
	 * @param deviceId
	 *            Device ID(fingerprint).
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public JSONObject getUniques(String deviceId) throws AccessException {
		return getUniques(deviceId, null);
	}

	/**
	 * Gets an array of uniq customer IDs for the given deviceId.
	 *
	 * @param deviceId
	 *            Device ID(fingerprint).
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public JSONObject getUniques(String deviceId, Map<String, String> additionalParameters) throws AccessException {
		if ((deviceId == null) || deviceId.isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing deviceId.");
		}

		StringBuilder parameters = new StringBuilder("?");
		// version and deviceId
		parameters.append("v=").append(version).append("&d=").append(deviceId);

		// Add the additional parameters, if they exist.
		if (additionalParameters != null) {
			for (Map.Entry<String, String> entry : additionalParameters.entrySet()) {
				parameters.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}

		String urlString = getUniquesEndpoint + parameters;

		logger.debug("getuniques request: url = " + urlString);
		long startTime = System.currentTimeMillis();
		String response = this.getRequest(urlString);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime) + ", response = " + response);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	/**
	 * Sets behavio data for a uniq customer identifier.
	 *
	 * @param host
	 *            of the behavio data endpoint
	 * @param environment
	 *            as in
	 *            https://api.behavio.kaptcha.com/<environment>/behavio/data
	 * @param session
	 *            The Session ID generated for the Data Collector service.
	 * @param timing
	 *            data gathered from a BehavioSec collection
	 * @param uniq
	 *            customer identifier
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public void setBehavioData(String host, String environment, String session, String timing, String uniq)
			throws AccessException {
		setBehavioData(host, environment, session, timing, uniq, null);
	}

	/**
	 * Sets behavio data for a uniq customer identifier.
	 *
	 * @param host
	 *            of the behavio data endpoint
	 * @param environment
	 *            as in
	 *            https://api.behavio.kaptcha.com/<environment>/behavio/data
	 * @param session
	 *            The Session ID generated for the Data Collector service.
	 * @param timing
	 *            data gathered from a BehavioSec collection
	 * @param uniq
	 *            customer identifier
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	private void setBehavioData(String host, String environment, String session, String timing, String uniq,
			Map<String, String> additionalParameters) throws AccessException {
		verifySessionId(session);
		verifyBehavioData(host, environment, timing, uniq);

		if (additionalParameters == null) {
			additionalParameters = new HashMap<>();
		}
		additionalParameters.put("m", Integer.toString(merchantId));
		additionalParameters.put("timing", timing);
		additionalParameters.put("uniq", uniq);

		List<NameValuePair> parameters = createRequestParameters(session, null, null, additionalParameters);
		String behavioDataEndpoint = BEHAVIO_DATA_ENDPOINT_PREFIX + host + "/" + environment
				+ BEHAVIO_DATA_ENDPOINT_POSTFIX;
		logger.debug("info request: host = " + behavioDataEndpoint + ", parameters = " + parameters.toString());
		long startTime = System.currentTimeMillis();
		this.postRequest(behavioDataEndpoint, parameters);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * Gets the device info, threshold decision, velocity data, Trusted Device
	 * information and/or BehavioSec. Which data sets will be returned depends
	 * on the infoFlag that is build with
	 * {@link com.kount.kountaccess.InfoEndpointDataSet}, the unique customer
	 * identifier and the supplied for the session's username and password.
	 *
	 * @param infoFlag
	 *            the requested set of data elements | int (bytes represented
	 *            inside) | mandatory
	 * @param session
	 *            The Session ID generated for the Data Collector service.
	 * @param uniq
	 *            customer identifier
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public JSONObject getInfo(int infoFlag, String session, String uniq, String username, String password)
			throws AccessException {
		return getInfo(infoFlag, session, uniq, username, password, null);
	}

	/**
	 * Gets the device info, threshold decision, velocity data, Trusted Device
	 * information and/or BehavioSec. Which data sets will be returned depends
	 * on the infoFlag that is build with
	 * {@link com.kount.kountaccess.InfoEndpointDataSet}, the unique customer
	 * identifier and the supplied for the session's username and password.
	 *
	 * @param infoFlag
	 *            the requested set of data elements | int (bytes represented
	 *            inside) | mandatory
	 * @param session
	 *            The Session ID generated for the Data Collector service.
	 * @param uniq
	 *            customer identifier
	 * @param username
	 *            The username of the user.
	 * @param password
	 *            The password of the user.
	 * @param additionalParameters
	 *            Additional parameters to send to server.
	 * @return A JSONObject containing the response.
	 * @throws AccessException
	 *             Thrown if any of the parameter values are invalid or there
	 *             was a problem getting a response.
	 */
	public JSONObject getInfo(int infoFlag, String session, String uniq, String username, String password,
			Map<String, String> additionalParameters) throws AccessException {

		verifySessionId(session);
		verifyInfoParams(infoFlag, uniq, username, password);

		if (additionalParameters == null) {
			additionalParameters = new HashMap<>();
		}
		additionalParameters.put("i", Integer.toString(infoFlag));
		additionalParameters.put("uniq", uniq);

		List<NameValuePair> parameters = createRequestParameters(session, username, password, additionalParameters);
		logger.debug("info request: host = " + infoEndpoint + ", parameters = " + parameters.toString());
		long startTime = System.currentTimeMillis();
		String response = this.postRequest(infoEndpoint, parameters);
		logger.debug("request elapsed time = " + (System.currentTimeMillis() - startTime) + ", response = " + response);
		if (response != null) {
			return processJSONEntity(response);
		}

		return null;
	}

	private void verifyInfoParams(int infoFlag, String uniq, String username, String password) throws AccessException {
		if ((infoFlag < 1) || (infoFlag > 31)) {
			throw new AccessException(AccessErrorType.INVALID_DATA,
					"Invalid infoFlag (" + infoFlag + ").  Must be an integer between 1 and 31 (including).");
		}

		int behavio = new InfoEndpointDataSet().withBehavioSec().build();
		int decision = new InfoEndpointDataSet().withDecision().build();
		int trusted = new InfoEndpointDataSet().withTrustedDevice().build();
		int velocity = new InfoEndpointDataSet().withVelocity().build();

		// uniq is required for trusted and behavio request
		if (((infoFlag & trusted) == trusted) || ((infoFlag & behavio) == behavio)) {
			if ((uniq == null) || uniq.isEmpty() || uniq.trim().isEmpty()) {
				throw new AccessException(AccessErrorType.INVALID_DATA,
						"Missing uniq (" + uniq + ").  Must be present for trusted and behavio requests.");
			}
		}

		// user and password are required for velocity and decision
		if (((infoFlag & velocity) == velocity) || ((infoFlag & decision) == decision)) {
			if ((username == null) || username.isEmpty() || username.trim().isEmpty() || (password == null)
					|| password.isEmpty() || password.trim().isEmpty()) {
				throw new AccessException(AccessErrorType.INVALID_DATA, "Missing username/password (" + username + "/"
						+ password + ").  Must be present for velocity and decision requests.");
			}
		}
	}

	private void verifySessionId(String session) throws AccessException {
		if ((session == null) || (session.length() != 32)) {
			throw new AccessException(AccessErrorType.INVALID_DATA,
					"Invalid sessionid (" + session + ").  Must be 32 characters in length");
		}
	}

	private void verifyTrustedState(String trustedState) throws AccessException {
		if ((trustedState == null) || trustedState.isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing trustedState.");
		}
		if (!(TRUSTED_STATE_TRUSTED.equals(trustedState) || TRUSTED_STATE_NOT_TRUSTED.equals(trustedState)
				|| TRUSTED_STATE_BANNED.equals(trustedState))) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Invalid trustedState (" + trustedState
					+ "). Must be one of the following values: " + TRUSTED_STATE_TRUSTED + ".");
		}
	}

	private void verifyBehavioData(String host, String environment, String timing, String uniq) throws AccessException {
		if ((host == null) || host.isEmpty() || host.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing host.");
		}
		if ((environment == null) || environment.isEmpty() || environment.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing environment.");
		}
		if ((timing == null) || timing.isEmpty() || timing.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing timing data.");
		}
		try {
			if (timing.startsWith("[")) {
				JSONArray.fromObject(timing);
			} else {
				JSONObject.fromObject(timing);
			}
		} catch (JSONException e) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Timing is not a valid json.", e);
		}
		if ((uniq == null) || uniq.isEmpty() || uniq.trim().isEmpty()) {
			throw new AccessException(AccessErrorType.INVALID_DATA, "Missing uniq customer identifier.");
		}
	}

	private List<NameValuePair> createRequestParameters(String session, String username, String password,
			Map<String, String> additionalParameters) {

		List<NameValuePair> values = new ArrayList<>();
		values.add(new BasicNameValuePair("v", this.version));
		if (session != null) {
			values.add(new BasicNameValuePair("s", session));
		}
		if (username != null) {
			values.add(new BasicNameValuePair("uh", hashValue(username)));
		}
		if (password != null) {
			values.add(new BasicNameValuePair("ph", hashValue(password)));
		}
		if ((username != null) || (password != null)) {
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
	 * Creates the authentication header.
	 */
	private String getAuthorizationHeader() {
		if (authorizationHeader != null) {
			return authorizationHeader;
		}
		String header = merchantId + ":" + apiKey;
		try {
			authorizationHeader = "Basic " + DatatypeConverter.printBase64Binary(header.getBytes("UTF8"));
			return authorizationHeader;
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
		if ((value == null) || value.isEmpty()) {
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
				hexChars[(j * 2) + 1] = hexArray[v & 0x0F];
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
		try (CloseableHttpClient client = getHttpClient()) {
			HttpGet request = this.getHttpGet(urlString);
			request.addHeader("Authorization", this.getAuthorizationHeader());
			request.addHeader("Content-Type", "JSON");

			try (CloseableHttpResponse response = client.execute(request)) {
				StatusLine status = response.getStatusLine();
				if (status.getStatusCode() != 200) {
					throw new AccessException(AccessErrorType.NETWORK_ERROR,
						"Bad Response(" + status.getStatusCode() + ")" + status.getReasonPhrase() + " " + urlString);

				}
				return this.getResponseAsString(response);
			}
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
		try (CloseableHttpClient client = getHttpClient()) {
			HttpPost request = getHttpPost(urlString);
			request.addHeader("Authorization", this.getAuthorizationHeader());
			HttpEntity entity = new UrlEncodedFormEntity(values);
			request.setEntity(entity);

			try (CloseableHttpResponse response = client.execute(request)) {
				StatusLine status = response.getStatusLine();
				if (status.getStatusCode() != 200) {
					throw new AccessException(AccessErrorType.NETWORK_ERROR,
						"Bad Response(" + status.getStatusCode() + ")" + status.getReasonPhrase() + " " + urlString);

				}
				return getResponseAsString(response);
			}
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public JSONObject getAccessData(String session, String username, String password,
			Map<String, String> additionalParams) throws AccessException {
		return getVelocity(session, username, password, additionalParams);
	}

}