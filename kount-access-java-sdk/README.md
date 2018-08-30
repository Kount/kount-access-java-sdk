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

```java
  sdk.setDeviceTrustBySession(sessionId, uniq, AccessSdk.TRUSTED_STATE_TRUSTED);
```

Set behaviour data collected for a customer:
```java
  String behavioHost = "api.behavio.kaptcha.com";
  String environment = "sandbox";
  String timing = "{\"valid\":\"json\"}";
  sdk.setBehaviorData(behavioHost, environment, session, timing, uniq);
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

Retrieve unique users by a device:

```java
  JSONObject uniques = sdk.getUniques(deviceId);
  System.out.println("This is our getuniques response_id: " + uniquesInfo.getString("response_id"));
  JSONArray uniques = uniquesInfo.getJSONArray("uniques");
  for (int i = 0; i < uniques.size(); i++) {
    JSONObject device = uniques.getJSONObject(i);
    System.out.println("Unique " + i);
    System.out.println("Unique (user):" + device.get("unique"));
    System.out.println("Date last seen:" + device.get("datelastseen"));
    System.out.println("Trusted state:" + device.get("truststate"));
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

The `decision` endpoint usage:

```java
  JSONObject decisionInfo = sdk.getDecision(sessionId, username, password); // those again are hashed internally
  JSONObject decision = decisionInfo.getJSONObject("decision");
  System.out.println("errors: " + decision.get("errors"));
  System.out.println("warnings: " + decision.get("warnings"));
  // and the Kount Access decision itself
  System.out.println("decision: " + decision.getJSONObject("reply").getJSONObject("ruleEvents").get("decision"));
```

And last, the 'info' endpoint usage:

```java
  int infoFlag = new InfoEndpointDataSet().withInfo().withVelocity().withBehavioSec().withDecision().withTrustedDevice().build();
  JSONObject info = sdk.getInfo(infoFlag, session, uniq, user, pass);
  
  //JSONObject for Decision is available
  JSONObject decision = info.getJSONObject("decision");
  
  //Device info is available
  JSONObject device = info.getJSONObject("device");
  
  //Trusted state
  System.out.println("Trusted state: " + info.getJSONObject("trusted").get("state"));
  
  //Response ID
  System.out.println("response_id:" + info.get("response_id"));
  
  //Velocity
  JSONObject velocity = info.getJSONObject("velocity");
  
  //And behavioSec
  JSONObject behavioSec = info.getJSONObject("behavioSec");
  System.out.println("Is Bot:" + behavioSecData.get("isBot"));
  System.out.println("Is Trained:" + behavioSecData.get("isTrained"));
  System.out.println("Score:" + behavioSecData.get("score"));
  System.out.println("Confidence:" + behavioSecData.get("confidence"));
  System.out.println("Policy ID:" + behavioSecData.get("policyId"));
```

