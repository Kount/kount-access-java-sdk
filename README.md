# Kount Access API SDK
[![Build Status](https://travis-ci.org/Kount/kount-access-java-sdk.svg?branch=master)](https://travis-ci.org/Kount/kount-access-java-sdk)

## Overview

**Kount Access** is here to help you fight login fraud.  It's designed for high-volume and targeted for account creation and affiliate networks.  It cross-checks individual components with several other components to calculate the velocity of related login attempts, and provides you with dozens of velocity checks and essential data to help determine the legitimacy of the user and account owners.

After your user submits their credentials for login (data collection is completed), you can request:
* information about the device
* velocity details (the tallies of credential combinations used) about the user's login attempt(s)
* an automated decision to approve the login, based on tolerances you set based on velocity

Each response is returned in JSON format.
This information enables you to make business decisions on how your site should to proceed with a user's login.

The **Kount Access API SDK** is used directly in your website (or authentication service) that handles user login.
The Kount Access API SDK should be integrated into the client code so that it is called
after the user submits their login, typically where the login form `POST` is handled.
This will be used regardless of whether the login is successful or not.

For more information on what Kount Access is, go to http://www.kount.com

## Requirements

Using the Kount Access SDK requires `Java 7` or above.

Ensure you have the information needed to instantiate the library in your app.

*  **merchant ID** - Your merchant ID provided to you from Kount.
*  **API Key** - The API key you generated from Kount (or was provided to you).
*  **Server Name** - The DNS name of the server you want to connect. These are also provided by Kount.  You should have one server assigned for testing and one for production.
*  **version** - The version of the API to access (defaults to the current version).  The version is in the form of a 4 digit string.

## Supported Versions

Kount Access API Versions:

* 0200
* 0210
* 0400 (default)

For more information on using this library, consult the wiki section.

