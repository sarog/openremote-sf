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

import org.openremote.web.console.service.AsyncControllerCallback;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.ControllerService;
import org.openremote.web.console.service.JSONControllerConnector;
import org.openremote.web.console.service.JSONPControllerConnector;
import org.openremote.web.console.util.BrowserUtils;
/**
 * Implementation of Controller Credentials that contains all the information
 * about a given controller.
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class Controller implements ControllerCredentials {
	private boolean isAlive;
	private boolean isSecure;
	private boolean isEncrypted; //HTTPS flag - not used at present
	private boolean isSupported;
	private boolean isSameOrigin;
	private boolean isValid = true;
	
	private String name;
	private String url;
	private String username;
	private String password;
	private String defaultPanel;
	
	// Initialisation variables
	private boolean sopChecked;
	private boolean aliveChecked;
	private boolean securedChecked;
	private AsyncControllerCallback<Boolean> initCallback;
	
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
	

	/**
	 * This method will initialise the controller by checking it's
	 * alive, secure, same origin and supported statuses.
	 */
	public void initialise(AsyncControllerCallback<Boolean> callback) {
		initCallback = callback;
		
		// Check if same origin
		BrowserUtils.isURLSameOrigin(getUrl(), new AsyncControllerCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				isSameOrigin = result;
				sopChecked = true;
				checkAliveSecureStatus();
			}}
		);
	}

	
	private void initCompleteCheck() {
		if (sopChecked && aliveChecked && securedChecked) {
			callCallback();
		}
	}
	
	
	private void checkAliveSecureStatus() {
		// If not SOP then have to use JSONP
		if (!isSameOrigin) {
			ControllerService.getInstance().setConnector(new JSONPControllerConnector());
		} else {
			ControllerService.getInstance().setConnector(new JSONControllerConnector());
		}
		
		// Check if secure
		ControllerService.getInstance().isSecure(getController(), new AsyncControllerCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				isSecure = result;
				securedChecked = true;
				isAlive = true;
				aliveChecked = true;
				initCompleteCheck();
			}
			
			@Override
			public void onFailure(EnumControllerResponseCode code) {
				// Had a response but not what expected
				isAlive = true;
				aliveChecked = true;
				isSecure = false;
				securedChecked = true;
				isValid = false;
				initCompleteCheck();
			}
			
			@Override
			public void onFailure(Throwable e) {
				// The request either went un-answered or not correctly formatted
				isAlive = false;
				aliveChecked = true;
				isSecure = false;
				securedChecked = true;
				isValid = false;
				initCompleteCheck();
			}
		});
	}
	
	private Controller getController() {
		return this;
	}
	
	private void callCallback() {
		initCallback.onSuccess(true);
	}
	
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	public boolean isProxied() {
		if (url.toLowerCase().indexOf("openremote.com") > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isSecure() {
		return isSecure;
	}
	
	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}
	
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	
	public boolean isSupported() {
		return isSupported;
	}
	
	public void setIsSupported(boolean isSupported) {
		this.isSupported = isSupported;
	}
	
	public void setIsSameOrigin(boolean isSameOrigin) {
		this.isSameOrigin = isSameOrigin;
	}
	
	public boolean isSameOrigin() {
		return isSameOrigin;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		//if (username.length() > 0) return "http://" + username + ":" + password + "@" + url.substring(7);
		return url;
	}
	
	public void setUrl(String url) {
		if (url.indexOf("https://") == 0) setEncrypted(true);
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
