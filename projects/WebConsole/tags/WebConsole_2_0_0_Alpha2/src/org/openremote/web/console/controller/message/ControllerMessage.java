package org.openremote.web.console.controller.message;

public abstract class ControllerMessage {
	private Type type;
	
	protected ControllerMessage(Type type) {
		this.type = type;
	}
	
	public static enum Type {
		COMMAND_REQUEST,
		COMMAND_RESPONSE,
		SENSOR_VALUE_CHANGE;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
