/**
 *
 */
package com.kount.kountaccess;

/**
 * This is thrown if there are issues with the {@link AccessSdk}.
 *
 * @author custserv@kount.com
 * @version 1.0
 */
public class AccessException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@link AccessException} error types
	 *
	 */
	public enum AccessErrorType {
		/**
		 * Any Network Error (host not available, host not found, HTTP 404, etc.)
		 */
		NETWORK_ERROR,
		/**
		 * Problems encrypting/decrypting data.
		 */
		ENCRYPTION_ERROR,
		/**
		 * Missing or malformed data (bad hostnames, missing/empty fields)
		 */
		INVALID_DATA,
		/**
		 * Internal error
		 */
		INTERNAL_ERROR,
	}

	/**
	 * Access Error Type for this exception
	 */
	private AccessErrorType error;

	/**
	 * Custom exception for problems encountered while using the Kount Access API.
	 *
	 * @param errorType
	 *            The Error type that was thrown
	 * @param message
	 *            Additional information about the error condition.
	 */
	public AccessException(AccessErrorType errorType, String message) {
		super(message);
		this.error = errorType;

	}

	/**
	 * Custom exception for problems encountered while using the Kount Access
	 * API.
	 *
	 * @param errorType
	 *            The Error type that was thrown
	 * @param message
	 *            Additional information about the error condition.
	 * @param e
	 *            source exception
	 */
	public AccessException(AccessErrorType errorType, String message, Exception e) {
		super(message, e);
		this.error = errorType;

	}

	/**
	 * Returns the specific AccessErrorType
	 *
	 * @return The error type
	 */
	public AccessErrorType getAccessErrorType() {
		return this.error;
	}
}
