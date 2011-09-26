package org.openremote.web.console.panel;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;


public class PanelCredentialsImpl implements PanelCredentials {
	private String controllerUrl;
	private String name;
	private int id;
	
	public PanelCredentialsImpl(String controllerUrl, int id, String name) {
		this.controllerUrl = controllerUrl;
		this.name = name;
		this.id = id;
	}
	
	public PanelCredentialsImpl(String controllerUrl, PanelIdentity identity) {
		this(controllerUrl, identity.getId(), identity.getName());
	}
	
	@Override
	public String getControllerUrl() {
		return controllerUrl;
	}
	
	public void setControllerUrl(String controllerUrl) {
		this.controllerUrl = controllerUrl;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
