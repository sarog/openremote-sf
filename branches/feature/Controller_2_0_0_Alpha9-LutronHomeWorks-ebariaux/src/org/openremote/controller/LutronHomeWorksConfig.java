package org.openremote.controller;

public class LutronHomeWorksConfig extends CustomConfiguration {

	public static final String LUTRON_HOMEWORKS_USERNAME = "lutron_homeworks.username";
	public static final String LUTRON_HOMEWORKS_PASSWORD = "lutron_homeworks.password";
	public static final String LUTRON_HOMEWORKS_ADDRESS = "lutron_homeworks.address";
	public static final String LUTRON_HOMEWORKS_PORT = "lutron_homeworks.port";

	private String userName;
	private String password;
	private String address;
	private int port;

	public String getPassword() {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_PASSWORD, password);
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_ADDRESS, address);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_PORT, port);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return preferAttrCustomValue(LUTRON_HOMEWORKS_USERNAME, userName);
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
