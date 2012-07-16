package org.openremote.web.console.unit;

public enum EnumConsoleErrorCode {
	
	UNKNOWN_ERROR (1000,"Unknown console error"),
	PANEL_DEFINITION_ERROR (1001, "Panel definition is not correct"),
	TABBAR_ERROR (1002,"Failed to build tab bar"),
	SCREEN_ERROR (1003,"Failed to build screen definition"),
	PANEL_LIST_ERROR (1004,"Failed to get Panel List");
	
	private final int code;
	private final String description;
	
	EnumConsoleErrorCode(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
}
