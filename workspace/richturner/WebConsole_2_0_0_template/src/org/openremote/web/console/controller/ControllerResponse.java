package org.openremote.web.console.controller;

public class ControllerResponse {

	public enum Message {	
		OK (200,"OK"),
		BAD_COMMAND (400, "Invalid Command Request"),
		FORBIDDEN (403, "Access Denied"),
		NOT_FOUND (404, "Controller Not Found"),
		COMMAND_ERROR (418, "Command Build Failed"),
		COMPONENT_INVALID (419, "No Such Component"),
		COMMAND_BUILDER_ERROR (420, "No Such Command Builder"),
		CONTROLLER_XML_NOT_FOUND (422, "Controller XML Not Found"),
		COMMAND_INVALID (423, "No Such Command"),
		CONTROLLER_XML_INVALID (424, "Controller XML is Invalid"),
		PANEL_XML_NOT_FOUND (426, "Panel XML Not Found"),
		PANEL_XML_INVALID (427, "Panel XML is Invalid"),
		XML_ERROR (429, "Invalid Controller XML Elemnt"),
		UNKNOWN (9999, "Unkown Response");	
		
		private final int code;
		private final String description;
		
		Message(int code, String description) {
			this.code = code;
			this.description = description;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getDescription() {
			return description;
		}
		
		public static Message getMessage(int code) {
			Message result = Message.UNKNOWN;
			for (Message message : Message.values()) {
				if (message.getCode() == code) {
					result = message;
					break;
				}
			}
			return result;
		}
	}
}
