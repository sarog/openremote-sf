package org.openremote.web.console.controller;

public class ControllerCredentialsImpl implements ControllerCredentials {
	private String name;
	private String url;
	private String username;
	private String password;
	private String defaultPanel;
	
	public ControllerCredentialsImpl(String name, String url, String username, String password, String defaultPanel) {
		this.name = name;
		this.url = url;
		this.username = username;
		this.password = password;
		this.defaultPanel = defaultPanel;
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
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDefaultPanel() {
		return defaultPanel;
	}
	
	public void setDefaultPanel(String defaultPanel) {
		this.defaultPanel = defaultPanel;
	}
}
