/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
			setName(credentials.getName());
			setUrl(credentials.getUrl());
			setUsername(credentials.getUsername());
			setPassword(credentials.getPassword());
			setDefaultPanel(credentials.getDefaultPanel());
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
		if (!url.endsWith("/")) url += "/";
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
