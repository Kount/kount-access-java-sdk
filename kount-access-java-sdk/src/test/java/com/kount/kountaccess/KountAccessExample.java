package com.kount.kountaccess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

/**
 * This is an example implementation of the Kount Access API SDK. In this example we will show how to create, prepare,
 * and make requests to the Kount Access API, and what to expect as a result. Before you can make API requests, you'll
 * need to have made collector request(s) prior, and you'll have to use the session id(s) that were returned.
 *
 * @author custserv@kount.com
 * @version 3.2.0
 * @copyright 2015 Kount, Inc. All Rights Reserved.
 */
public class KountAccessExample {

    /**
     * Fake user session (this should be retrieved from the Kount Access Data Collector Client SDK). This will be a value
     * up to 32 characters.
     */
    private String session = "abcdef12345678910abcdef123456789"; // "THIS_IS_THE_USERS_SESSION_FROM_JAVASCRIPT_CLIENT_SDK";

    /**
     * Fake deviceID (this should be retrieved from the Kount Access Data Collector Client SDK). This will be a value
     * up to 32 characters.
     */
    private String deviceID = "abcdef12345678910abcdef123456789";

    /**
     * Merchant's customer ID at Kount. This should be the id you were issued from Kount.
     */
    private int merchantId = 0;

    /**
     * This should be the API Key you were issued from Kount.
     */
    private String apiKey = "PUT_YOUR_API_KEY_HERE";

    /**
     * Sample host. this should be the name of the Kount Access API server you want to connect to. We will use sandbox01
     * as the example.
     */
    private String host = "api-sandbox01.kountaccess.com";

    private Set<String> entityTypes =
            new HashSet<String>(Arrays.asList("account", "device", "ip_address", "password", "user"));
    private Set<String> velocityTypes =
            new HashSet<String>(Arrays.asList("alh", "alm", "dlh", "dlm", "iplh", "iplm", "plh", "plm", "ulh", "ulm"));

    /**
     * Simple Example within the Constructor.
     */
    public KountAccessExample() {
        try {
            // Create the SDK. If any of these values are invalid, an com.kount.kountaccess.AccessException will be
            // thrown along with a message detailing why.
            AccessSdk sdk = new AccessSdk(host, merchantId, apiKey);

            // If you want the device information for a particular user's session, just pass in the sessionId. This
            // contains the id (fingerprint), IP address, IP Geo Location (country), whether the user was using a proxy
            // (and it was bypassed), and ...
            JSONObject deviceInfo = sdk.getDevice(this.session);

            this.printDeviceInfo(deviceInfo.getJSONObject("device"));

            // ... if you want to see the velocity information in relation to the users session and their account
            // information, you can make an access (velocity) request. Usernames and passwords will be hashed prior to
            // transmission to Kount within the SDK. You may optionally hash prior to passing them in as long as the
            // hashing method is consistent for the same value.
            String username = "billyjoe@bobtown.org";
            String password = "notreally";
            JSONObject accessInfo = sdk.getVelocity(session, username, password);

            // Let's see the response
            System.out.println("Response: " + accessInfo);

            // Each Access Request has its own uniqueID
            System.out.println("This is our access response_id: " + accessInfo.getString("response_id"));

            // The device JSONObject is included in an access request:
            this.printDeviceInfo(accessInfo.getJSONObject("device"));

            // Velocity Information is stored in a JSONObject, by entity type
            JSONObject velocity = accessInfo.getJSONObject("velocity");

            // Let's look at the data
            for (String type : entityTypes) {
                this.printVelocityInfo(type, velocity.getJSONObject(type));
            }

            // Or you can access specific Metrics directly. Let's say we want the
            // number of unique user accounts used by the current sessions device
            // within the last hour
            int numUsersForDevice = accessInfo.getJSONObject("velocity").getJSONObject("device").getInt("ulh");
            System.out.println(
                    "The number of unique user access request(s) this hour for this device is:" + numUsersForDevice);

            // Decision Information is stored in a JSONObject, by entity type
            JSONObject decisionInfo = sdk.getDecision(session, username, password);
            JSONObject decision = decisionInfo.getJSONObject("decision");
            // Let's look at the data
            printDecisionInfo(decision);

            // If you want to set the trusted state of a device, pass setDeviceTrust
            // the device ID which is the devices unique identifier, the uniq which is
            // a merchant assigned identifier like an account number for the consumer,
            // and the trusted state; trusted states can be "trusted",
            // "banned", or "not_trusted".
            //
            // Setting the trust state of the device does not return a response
            // if successful, just a 200 response code. If there is an error it will
            // be returned.
            String uniq = "uniqIdentifierMerchantMakes";
            String trustState = "trusted";
            sdk.setDeviceTrust(deviceID, uniq, trustState);


            // Gathering device information endpoint is like the kitchen sink method;
            // it can get you all the desired device information in one call based on the
            // requested return value. To call it provide the session and uniq
            // identifier of the device.
            // returnValue is a bitmap and expects a parameter from 1-15 depending on
            // what information you want; deviceInfo, velocity, threshold, trusted state.
            String returnValue = "15";
            JSONObject deviceInformation = sdk.gatherDeviceInfo(session, username, password, returnValue, uniq);

            // Let's see the response
            System.out.println("Response: " + deviceInformation);

        } catch (AccessException ae) {
            // These can be thrown if there were any issues making the request.
            // See the AccessException class for more information.
            System.out.println("ERROR Type: " + ae.getAccessErrorType());
            System.out.println("ERROR: " + ae.getMessage());
        }
    }

    /**
     * Example method to walk through the device JSONObject data.
     *
     * @param device
     */
    public void printDeviceInfo(JSONObject device) {
        // Fingerprint
        System.out.println("Got Fingerprint:" + device.get("id"));

        // IP Address & Geo information
        System.out.println("Got IP Address:" + device.get("ipAddress"));
        System.out.println("Got IP Geo(Country):" + device.get("ipGeo"));
        System.out.println("is Proxy:" + (device.get("proxy")));

        // whether we detected the use of a mobile device.
        System.out.println("isMobile:" + (device.get("mobile")));

        // whether we detected the use of tor
        System.out.println("isTor:" + (device.get("tor")));
    }

    /**
     * Example method to walk through the velocity JSONObject data.
     *
     * @param entityType
     * @param entity
     */
    public void printVelocityInfo(String entityType, JSONObject entity) {
        System.out.println("Velocity Info for " + entityType);
        for (String vType : velocityTypes) {
            if (entity.has(vType)) {
                System.out.println("     " + vType + ": " + entity.get(vType));
            }
        }
    }

    public void printDecisionInfo(JSONObject decision) {
        System.out.println("Got errors: " + decision.get("errors"));
        System.out.println("Got reply: " + decision.get("reply"));
        System.out.println("Got warnings: " + decision.get("warnings"));
        System.out.println(
                "Got decision: " + decision.getJSONObject("reply").getJSONObject("ruleEvents").get("decision"));
    }

    /**
     * Test main. Just runs the constructor.
     *
     * @param args
     */
    public static void main(String args[]) {
        new KountAccessExample();
    }
}
