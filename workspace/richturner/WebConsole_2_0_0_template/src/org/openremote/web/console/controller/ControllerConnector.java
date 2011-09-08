package org.openremote.web.console.controller;

import org.openremote.web.console.service.ControllerServiceImpl;

public abstract class ControllerConnector {
	
	public abstract void getData(String controllerUrl, EnumControllerCommand command, String[] params, ControllerServiceImpl handler);
	
	public void getData(String controllerUrl, EnumControllerCommand command, ControllerServiceImpl handler) {
		getData(controllerUrl, command, new String[0], handler);
	}
	
	public abstract void sendData();
}
