package org.openremote.web.console.controller.message;

import org.openremote.web.console.controller.EnumControllerCommand;

public class ControllerMessage {
	private Type messageType;
	
	public ControllerMessage() {
	}
	
	public static enum Type {
		COMMAND_RESPONSE,
		SENSOR_VALUE_CHANGE;
	}
}
