package org.openremote.web.console.controller;

import org.openremote.web.console.controller.message.ControllerRequestMessage;
import org.openremote.web.console.service.ControllerService;

public interface ControllerConnector {
	
	void getData(int requestId, String controllerUrl, ControllerRequestMessage message, ControllerCallBackHandler handler);
	
	void sendData();
	
	boolean isAlive(int requestId, String controllerUrl);
	
	boolean isSecure(int requestId, String controllerUrl);
}
