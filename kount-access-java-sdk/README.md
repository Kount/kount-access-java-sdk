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

Set the trusted state of a device by its id:

```java
  String deviceId = "device id(fingerprint)";
  String uniq = "uniq(customer identifier)";
  // Setting a trust state of a device by the deviceId/fingerprint
  sdk.setDeviceTrustByDevice(deviceId, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
```

Set the trusted state of a device by session ID:

```
  sdk.setDeviceTrustBySession(sessionId, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
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

Retrieve devices information related to a customer (uniq):

```java
  JSONObject devicesInfo = sdk.getDevices(uniq);
  System.out.println("This is our getdevices response_id: " + devicesInfo.getString("response_id"));
  JSONArray devices = devicesInfo.getJSONArray("devices");
  for (int i = 0; i < devices.size(); i++) {
    JSONObject device = devices.getJSONObject(i);
    System.out.println("Device " + i);
    System.out.println("ID (fingerprint):" + device.get("deviceid"));
    System.out.println("Trusted state:" + device.get("truststate"));
    System.out.println("Date first seen:" + device.get("datefirstseen"));
    System.out.println("Friendly name:" + device.get("friendlyname"));
  }
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

And last, the `decision` endpoint usage:

```java
  JSONObject decisionInfo = sdk.getDecision(sessionId, username, password); // those again are hashed internally
  JSONObject decision = decisionInfo.getJSONObject("decision");
  System.out.println("errors: " + decision.get("errors"));
  System.out.println("warnings: " + decision.get("warnings"));
  // and the Kount Access decision itself
  System.out.println("decision: " + decision.getJSONObject("reply").getJSONObject("ruleEvents").get("decision"));
```

