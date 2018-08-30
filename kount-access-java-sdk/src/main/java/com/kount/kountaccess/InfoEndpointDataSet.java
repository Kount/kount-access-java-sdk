/**
 *
 */
package com.kount.kountaccess;

/**
 * Builder for the requested set of data elements for the Info Endpoint (the
 * infoFlag).
 *
 * @author Stanislav Milev
 * @version 4.0.0
 */
public class InfoEndpointDataSet {

	public static final int INFO = 1;
	public static final int VELOCITY = 2;
	public static final int DECISION = 4;
	public static final int TRUSTED = 8;
	public static final int BEHAVIOSEC = 16;

	private int value = 0;

	public InfoEndpointDataSet withInfo() {
		value |= INFO;
		return this;
	}

	public InfoEndpointDataSet withVelocity() {
		value |= VELOCITY;
		return this;
	}

	public InfoEndpointDataSet withDecision() {
		value |= DECISION;
		return this;
	}

	public InfoEndpointDataSet withTrustedDevice() {
		value |= TRUSTED;
		return this;
	}

	public InfoEndpointDataSet withBehavioSec() {
		value |= BEHAVIOSEC;
		return this;
	}

	public int build() {
		return value;
	}

}
