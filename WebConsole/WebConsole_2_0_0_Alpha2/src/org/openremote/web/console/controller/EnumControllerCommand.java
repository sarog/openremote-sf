package org.openremote.web.console.controller;

public enum EnumControllerCommand {
	GET_PANEL_LIST,
	GET_PANEL_LAYOUT,
	SEND_COMMAND,
	GET_SENSOR_STATUS,
	DO_SENSOR_POLLING,
	GET_ROUND_ROBIN_LIST,
	IS_ALIVE,
	IS_SECURE;
	
   @Override
   public String toString() {
      return super.toString().toLowerCase();
   }
   
   public static EnumControllerCommand enumValueOf(String commandActionTypeValue) {
   	EnumControllerCommand result = null;
      try {
         result = Enum.valueOf(EnumControllerCommand.class, commandActionTypeValue.toUpperCase());
      } catch (Exception e) {}
      return result;
   }
}
