package com.kount.kountaccess;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.json.JSONObject;
import org.json.JSONException;

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
 * @version 3.2.0
 */
public class AccessSdk {

    private static final Logger logger = Logger.getLogger(AccessSdk.class);

    /**
     * This is the default version of the API Responses that this SDK will request. Future versions are intended to be
     * compatible with this version of the SDK.
     */
    public final String DEFAULT_API_VERSION = "0320";

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
     * Device trust endpoint
     */
    private final String deviceTrustEndpoint;

    /**
     * Device information endpoint
     *
     */
    private final String deviceInfoEndpoint;

    /**
     * Creates an instance of the AccessSdk associated with a specific host and merchant.
     *
     * @param host
     *            FQDN of the host that AccessSdk will communicate with.
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
        this.deviceTrustEndpoint = "https://" + host + "/api/devicetrust";
        this.deviceInfoEndpoint = "https://" + host + "/api/info";

        this.merchantId = merchantId;
        this.apiKey = apiKey;
        this.version = DEFAULT_API_VERSION;

        logger.info("Access SDK using merchantId = " + this.merchantId + ", host = " + host);
        logger.debug("velocity endpoint: " + velocityEndpoint);
        logger.debug("decision endpoint: " + decisionEndpoint);
        logger.debug("device endpoint: " + deviceEndpoint);
        logger.debug("device trust endpoint: " + deviceTrustEndpoint);
        logger.debug("device info endpoint: " + deviceInfoEndpoint);
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
        String response = this.postRequest(decisionEndpoint, parameters);
        if (response != null) {
            return processJSONEntity(response);
        }

        return null;
    }

    /**
     * Gathers multiple points of data about a specific device based on the requested return value.
     * Device data that can be requested: deviceInfo, velocity, threshold, and the trusted device state.
     * Depending on the data requested, different parameters will be required.
     *
     * @param session
     *            Required for all requests.
     *            The Session ID generated for the Data Collector service.
     * @param username
     *            Required for velocity and threshold requests.
     *            The username of the user.
     * @param password
     *            Required for velocity and threshold requests.
     *            The password of the user.
     * @param returnValue
     *            Required for all requests.
     *            The value that will map to what data is requested; 1-15
     *
     *            ------------------------------------------------------------------
     *            | returnvalue | deviceInfo | velocity | threshold | trusted state |
     *            ------------------------------------------------------------------
     *            |      1      |     Y      |    N     |     N     |       N       |
     *            |      2      |     N      |    Y     |     N     |       N       |
     *            |      3      |     Y      |    Y     |     N     |       N       |
     *            |      4      |     N      |    N     |     Y     |       N       |
     *            |      5      |     Y      |    N     |     Y     |       N       |
     *            |      6      |     N      |    Y     |     Y     |       N       |
     *            |      7      |     Y      |    Y     |     Y     |       N       |
     *            |      8      |     N      |    N     |     N     |       Y       |
     *            |      9      |     Y      |    N     |     N     |       Y       |
     *            |     10      |     N      |    Y     |     N     |       Y       |
     *            |     11      |     Y      |    Y     |     N     |       Y       |
     *            |     12      |     N      |    N     |     Y     |       Y       |
     *            |     13      |     Y      |    N     |     Y     |       Y       |
     *            |     14      |     N      |    Y     |     Y     |       Y       |
     *            |     15      |     Y      |    Y     |     Y     |       Y       |
     *            ------------------------------------------------------------------
     *
     * @param deviceId
     *            Required for all requests.
     *            The unique deviceId.
     * @param uniq
     *            Required for trusted state requests.
     *            Merchant assigned account number for the consumer.
     * @return Http response code.
     * @throws AccessException
     *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
     */
    public JSONObject gatherDeviceInfo(String session, String username, String password,
                                       String returnValue, String deviceId, String uniq) throws AccessException {
        return gatherDeviceInfo(session, username, password, returnValue, deviceId, uniq, null);
    }

    /**
     * Gathers multiple points of data about a specific device based on the requested return value. Contains argument for passing additional
     * parameters.
     *
     * @param session
     *            Required for all requests.
     *            The Session ID generated for the Data Collector service.
     * @param username
     *            Required for velocity and threshold requests.
     *            The username of the user.
     * @param password
     *            Required for velocity and threshold requests.
     *            The password of the user.
     * @param returnValue
     *            Required for all requests.
     *            The value that will map to what data is requested; 1-15
     *
     *            ------------------------------------------------------------------
     *            | returnvalue | deviceInfo | velocity | threshold | trusted state |
     *            ------------------------------------------------------------------
     *            |      1      |     Y      |    N     |     N     |       N       |
     *            |      2      |     N      |    Y     |     N     |       N       |
     *            |      3      |     Y      |    Y     |     N     |       N       |
     *            |      4      |     N      |    N     |     Y     |       N       |
     *            |      5      |     Y      |    N     |     Y     |       N       |
     *            |      6      |     N      |    Y     |     Y     |       N       |
     *            |      7      |     Y      |    Y     |     Y     |       N       |
     *            |      8      |     N      |    N     |     N     |       Y       |
     *            |      9      |     Y      |    N     |     N     |       Y       |
     *            |     10      |     N      |    Y     |     N     |       Y       |
     *            |     11      |     Y      |    Y     |     N     |       Y       |
     *            |     12      |     N      |    N     |     Y     |       Y       |
     *            |     13      |     Y      |    N     |     Y     |       Y       |
     *            |     14      |     N      |    Y     |     Y     |       Y       |
     *            |     15      |     Y      |    Y     |     Y     |       Y       |
     *            ------------------------------------------------------------------
     *
     * @param deviceId
     *            Required for all requests.
     *            The unique deviceId.
     * @param uniq
     *            Required for trusted state requests.
     *            Merchant assigned account number for the consumer.
     * @param additionalParameters
     *            Additional parameters to send to server.
     * @return Http response code.
     * @throws AccessException
     *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
     */
    public JSONObject gatherDeviceInfo(String session, String username, String password,
            String returnValue, String deviceId, String uniq, Map<String, String> additionalParameters) throws AccessException {

        List<NameValuePair> parameters = createRequestParameters(session, username, password, additionalParameters);
        verifyGatherInfoParameters(session, returnValue, deviceId, uniq);

        logger.debug("gather device info request: host = " + deviceInfoEndpoint + ", parameters = " + parameters.toString());
        String response = this.postRequest(deviceInfoEndpoint, parameters);

        if (response != null) {
            return processJSONEntity(response);
        }

        return null;
    }

    /**
     * Creates or updates the trust state for device using the TDI service.
     *
     * @param username
     *            The username of the user.
     * @param password
     *            The password of the user.
     * @param deviceId
     *            The unique deviceId.
     * @param uniq
     *            Merchant assigned account number for the consumer.
     * @param trustState
     *            The state to set the device to.
     *            Accepted values are: "trusted", "banned", or "not_trusted".
     * @return Http response code.
     * @throws AccessException
     *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
     */
    public JSONObject setDeviceTrust(String username, String password, String deviceId, String uniq, String trustState) throws AccessException {
        return setDeviceTrust(username, password, deviceId, uniq, trustState, null);
    }

    /**
     * Creates or updates the trust state for device using the TDI service. Contains argument for passing additional
     * parameters.
     *
     * @param username
     *            The username of the user.
     * @param password
     *            The password of the user.
     * @param deviceId
     *            The unique deviceId.
     * @param uniq
     *            Merchant assigned account number for the consumer.
     * @param trustState
     *            The state to set the device to.
     *            Accepted values are: "trusted", "banned", or "not_trusted".
     * @param additionalParameters
     *            Additional parameters to send to server.
     * @return Http response code.
     * @throws AccessException
     *             Thrown if any of the parameter values are invalid or there was a problem getting a response.
     */
    public JSONObject setDeviceTrust(String username, String password, String deviceId, String uniq, String trustState, Map<String, String> additionalParameters) throws AccessException {

        List<NameValuePair> parameters = createRequestParameters(null, username, password, additionalParameters);
        verifyTrustState(trustState);
        verifyUniq(uniq);
        verifyDeviceId(deviceId);

        parameters.add(new BasicNameValuePair("ts", trustState));

        logger.debug("device trust request: host = " + deviceTrustEndpoint + ", parameters = " + parameters.toString());
        String response = this.postRequest(deviceTrustEndpoint, parameters);

        if (response != null) {
            return processJSONEntity(response);
        }

        return null;
    }

    private void verifyReturnValue(String returnValue) throws AccessException {
        final int bitMin = 1;
        final int bitMax = 15;
        int rValue = Integer.valueOf(returnValue);
        if (null == returnValue || rValue > bitMax || rValue < bitMin) {
            throw new AccessException(AccessErrorType.INVALID_DATA,
                    String.format("Invalid returnValue (%s).  Must be an integer between %d and %d", returnValue, bitMin, bitMax));
        }
    }

    // Check we have the right parameters passed for the information requested.
    private void verifyGatherInfoParameters(String session, String returnValue, String deviceId, String uniq) throws AccessException {
        int deviceInfoBits   = 0b0001;
        int velocityBits     = 0b0010;
        int thresholdBits    = 0b0100;
        int trustedStateBits = 0b1000;
        int returnBits   = Integer.parseInt(returnValue);

        // Everything needs these, check we have them
        verifySessionId(session);
        verifyReturnValue(returnValue);
        verifyDeviceId(deviceId);

        if ((deviceInfoBits & returnBits) == deviceInfoBits) {
            logger.debug("gatherDeviceInfo requested deviceInfo");
        }
        if ((velocityBits & returnBits) == velocityBits) {
            logger.debug("gatherDeviceInfo requested velocity");
        }
        if ((thresholdBits & returnBits) == thresholdBits) {
            logger.debug("gatherDeviceInfo requested threshold");
        }
        if ((trustedStateBits & returnBits) == trustedStateBits) {
            logger.debug("gatherDeviceInfo requested trustedState");
            verifyUniq(uniq);
        }
    }

    private void verifyDeviceId(String deviceId) throws AccessException {
        if (null == deviceId || deviceId.length() != 32) {
            throw new AccessException(AccessErrorType.INVALID_DATA,
                    String.format("Invalid deviceId ().  Must be 32 characters in length", deviceId));
        }
    }

    private void verifyTrustState(String trustState) throws AccessException {
        final String validTDI[] = {"trusted", "banned", "not_trusted" };
        if (null == trustState || !(Arrays.asList(validTDI).contains(trustState.toLowerCase()))) {
            throw new AccessException(AccessErrorType.INVALID_DATA,
                    "Invalid device trust state (" + trustState +"). Must be one of " + Arrays.toString(validTDI));
        }
    }

    private void verifyUniq(String uniq) throws AccessException {
        if (uniq.length() > 32) {
            throw new AccessException(AccessErrorType.INVALID_DATA,
                    "Invalid uniq value (" + uniq + ").  Must not exceed 32 characters in length");
        }
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

        if (session != null) {
            values.add(new BasicNameValuePair("s", session));
        }
        if (null != username) {
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
     * Creates the authentication header.
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
            result = new JSONObject(response);
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
     * @param additionalParameters
     *            Additional parameters to send to server.
     * @return A JSONObject containing the response.
     * @throws AccessException
     *             Thrown if any of the param values are invalid or there was a problem getting a response.
     */
    public JSONObject getAccessData(String session, String username, String password,
            Map<String, String> additionalParameters) throws AccessException {
        return getVelocity(session, username, password, additionalParameters);
    }
}
