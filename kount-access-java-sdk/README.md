# kount-access-java-sdk

This is the actual Kount Access Java SDK.

Longer version of the code samples can be found [here](https://github.com/Kount/kount-access-java-sdk/wiki/Kount-Access-Examples)

Required:
* Merchant ID
* API Key
* Kount Access service host

Create an SDK object:
```java
  AccessSdk sdk = new AccessSdk(accessHost, merchantId, apiKey);
```

Retrieve device information collected by the Data Collector:

```java
  // sessionId, 32-character identifier, applied for customer session, provided to data collector
  JSONObject deviceInformation = sdk.getDevice(sessionId).getJSONObject("device");

  // IP address
  System.out.println("IP Address: " + deviceInformation.get("ipAddress"));

  // mobile device?
  System.out.println("Mobile: " + (deviceInformation.get("isMobile"))); // 1 (true) or 0 (false)
```

Get velocity for one of our customers:
```java
  // for greater security, username and password are internally hashed before transmitting the request
  // you can hash them yourself, this wouldn't affect the Kount Access Service
  JSONObject accessInfo = sdk.getVelocity(sessionId, username, password);

  // you can get the device information from the accessInfo object
  accessInfo.getJSONObject("device");
  
  JSONObject velocity = accessInfo.getJSONObject("velocity");
  System.out.println(velocity.toString()); // this is the full response, which may be huge

  // and let's get the number of unique user accounts used by the current sessions device within the last hour
  int numUsersForDevice = accessInfo.getJSONObject("velocity").getJSONObject("device").getInt("ulh");
  System.out.println(
    "The number of unique user access request(s) this hour for this device is:" + numUsersForDevice);
```

The `decision` endpoint usage:

```java
  JSONObject decisionInfo = sdk.getDecision(sessionId, username, password); // those again are hashed internally
  JSONObject decision = decisionInfo.getJSONObject("decision");
  System.out.println("errors: " + decision.get("errors"));
  System.out.println("warnings: " + decision.get("warnings"));
  // and the Kount Access decision itself
  System.out.println("decision: " + decision.getJSONObject("reply").getJSONObject("ruleEvents").get("decision"));
```

Setting the trusted state:

```java
  // The uniq is a merchant assigned identifier like an account number
  String uniq = "uniqIdentifierMerchantMakes";

  // Trusted states are "trusted", "banned", "not_trusted"
  String trustState = "trusted";

  // No response is passed back from the method. An AccessException will be thrown if it failed
  sdk.setDeviceTrust(deviceID, uniq, trustState);
```

Gather device info returns all desired device information:

```java
  // returnValue is a bitmap and expects to be a parameter from 1-15 depending on what information
  // is requested
  String returnValue = "15";

  JSONObject deviceInformation = sdk.gatherDeviceInfo(session, username, password, returnValue, uniq);

  // Let's see the response
  System.out.println("Response: " + deviceInformation);
```
