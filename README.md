# Kount Access Java SDK Repository

This repository contains the Kount Access Java SDK.

Kount Access™ is designed for high-volume login, account creation and affiliate networks. Cross-checking individual component with several other components to calculate the velocity of related login attempts. And providing the client with dozens of velocity checks and essential data to help determine the legitimacy of the user and account owners.

Downloads and use
-----------------

Clone the repository if you want to have a latest-and-greatest version of the Kount Access SDK, or just Download a stable release from the Releases page.

Build the SDK jar by running:
```bash
mvn clean install
```

Use the jar you just built by referring it into your Maven configuration as:
```xml
<dependency>
  <groupId>com.kount.kountaccess</groupId>
  <artifactId>kount-access-java-sdk</artifactId>
  <version>*the latest version number here*</version>
</dependency>
```
