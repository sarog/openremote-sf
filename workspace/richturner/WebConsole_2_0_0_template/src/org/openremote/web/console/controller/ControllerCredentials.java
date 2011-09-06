package org.openremote.web.console.controller;

public class ControllerCredentials {
	private boolean isAlive;
	private boolean isSecure;
	private boolean supportsJson;
	private String name;
	private String url;
	private ControllerLoginCredentials loginCredentials;
	
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	public boolean isSecure() {
		return isSecure;
	}
	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}
	public boolean isSupportsJson() {
		return supportsJson;
	}
	public void setSupportsJson(boolean supportsJson) {
		this.supportsJson = supportsJson;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ControllerLoginCredentials getLoginCredentials() {
		return loginCredentials;
	}
	public void setLoginCredentials(ControllerLoginCredentials loginCredentials) {
		this.loginCredentials = loginCredentials;
	}	
}
