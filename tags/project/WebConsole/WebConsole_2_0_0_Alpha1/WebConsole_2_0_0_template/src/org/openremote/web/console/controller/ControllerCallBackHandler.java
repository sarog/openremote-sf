package org.openremote.web.console.controller;

import org.openremote.web.console.controller.message.ControllerResponseMessage;

public interface ControllerCallBackHandler {
	public void processControllerResponse(ControllerResponseMessage message);
}
