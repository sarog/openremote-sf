package org.openremote.web.console.controller.message;

import org.openremote.web.console.controller.EnumControllerResponseCode;

public class ControllerResponseMessage extends ControllerMessage {
	private int requestId;
	private EnumControllerResponseCode responseCode;
	private Object responseObject;
	
	public ControllerResponseMessage(int requestId, EnumControllerResponseCode responseCode, Object responseObject) {
		super(ControllerMessage.Type.COMMAND_RESPONSE);
		this.requestId = requestId;
		this.responseCode = responseCode;
		this.responseObject = responseObject;
	}

	public int getRequestId() {
		return requestId;
	}

	public EnumControllerResponseCode getResponseCode() {
		return responseCode;
	}

	public Object getResponseObject() {
		return responseObject;
	}	
}
