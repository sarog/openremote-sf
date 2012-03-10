package org.openremote.web.console.controller;

import org.openremote.web.console.service.AutoBeanService;

public class Controller implements ControllerCredentials {
	private boolean isAlive;
	private boolean isSecure;
	private boolean isSupported;
	private String name;
	private String url;
	private String username;
	private String password;
	private String defaultPanel;
	
	public Controller() {
		this(null);
	}
	
	public Controller(ControllerCredentials credentials) {
		if (credentials != null) {
			this.name = credentials.getName();
			this.url = credentials.getUrl();
			this.username = credentials.getUsername();
			this.password = credentials.getPassword();
			this.defaultPanel = credentials.getDefaultPanel();
		}
	}
	
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
	
	public boolean isSupported() {
		return isSupported;
	}
	
	public void setIsSupported(boolean isSupported) {
		this.isSupported = isSupported;
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
	
	public ControllerCredentials getCredentials() {
		return AutoBeanService.getInstance().getFactory().create(ControllerCredentials.class, this).as();
	}
}
