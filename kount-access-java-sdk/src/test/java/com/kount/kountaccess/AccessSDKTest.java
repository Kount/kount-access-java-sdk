/**
 *
 */
package com.kount.kountaccess;

//import static org.junit.Assert.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.kount.kountaccess.AccessException.AccessErrorType;

/**
 * Unit Tests around the Access SDK
 *
 * @author gjd, abe
 *
 */
public class AccessSDKTest {

	private static final Logger logger = Logger.getLogger(AccessSDKTest.class);

	// Setup data for comparisons.
	int merchantId = 999999;
	String host = merchantId + ".kountaccess.com";
	String apiKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMDAxMDAiLCJhdWQiOiJLb3VudC4wIiwiaWF0IjoxNDI0OTg5NjExLCJzY3AiOnsia2MiOm51bGwsImFwaSI6ZmFsc2UsInJpcyI6ZmFsc2V9fQ.S7kazxKVgDCrNxjuieg5ChtXAiuSO2LabG4gzDrh1x8";

	/**
	 * Test method for {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKHappyPath() {
		try {
			AccessSdk sdk = new AccessSdk(host, merchantId, apiKey);
			assertNotNull(sdk);
		} catch (AccessException ae) {
			fail("Bad exception" + ae.getAccessErrorType().name() + ":" + ae.getMessage());
		}
	}

	/**
	 * Test method for {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKMissingHost() {
		try {
			new AccessSdk(null, merchantId, apiKey);
			fail("Should have failed host");

		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test method for {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKBadMerchant() {
		try {
			new AccessSdk(host, -1, apiKey);
			fail("Should have failed merchantId");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKBadMerchant2() {
		try {
			new AccessSdk(host, 99999, apiKey);
			fail("Should have failed merchantId");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test method for
	 * {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKBadMerchant3() {
		try {
			new AccessSdk(host, 1000000, apiKey);
			fail("Should have failed merchantId");
		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test method for {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKMissingApiKey() {
		try {
			new AccessSdk(host, merchantId, null);
			fail("Should have failed apiKey");

		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

	/**
	 * Test method for {@link com.kount.kountaccess.AccessSdk#AccessSdk(java.lang.String, int, java.lang.String)}.
	 */
	@Test
	public void testConstructorAccessSDKBlankApiKey() {
		try {
			new AccessSdk(host, merchantId, "    ");
			fail("Should have failed apiKey");

		} catch (AccessException ae) {
			assertEquals(AccessErrorType.INVALID_DATA, ae.getAccessErrorType());
		}
	}

}
