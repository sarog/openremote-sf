package org.openremote.web.console.controller.message;

import org.openremote.web.console.controller.EnumControllerCommand;

public class ControllerRequestMessage extends ControllerMessage {
	private EnumControllerCommand command;
	private String[] params;
	
	public ControllerRequestMessage(EnumControllerCommand command, String[] params) {
		super(ControllerMessage.Type.COMMAND_REQUEST);
		this.command = command;
		this.params = params;
	}

	public EnumControllerCommand getCommand() {
		return command;
	}

	public String[] getParams() {
		return params;
	}
}
