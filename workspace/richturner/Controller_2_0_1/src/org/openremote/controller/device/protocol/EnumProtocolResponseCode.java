package org.openremote.controller.device.protocol;

public enum EnumProtocolResponseCode {
   OK (200,"OK"),
   FORBIDDEN (403, "Access Denied"),
   NOT_FOUND (404, "Device Not Found"),
   COMMAND_ERROR (418, "Command Failed"),
   COMMAND_INVALID (423, "No Such Command"),
   UNKNOWN_ERROR (999, "Unkown Protocol Error"); 
   
   private final int code;
   private final String description;
   
   EnumProtocolResponseCode(int code, String description) {
      this.code = code;
      this.description = description;
   }
   
   public int getCode() {
      return code;
   }
   
   public String getDescription() {
      return description;
   }
   
   public static EnumProtocolResponseCode getResponseCode(int code) {
      EnumProtocolResponseCode result = EnumProtocolResponseCode.UNKNOWN_ERROR;
      for (EnumProtocolResponseCode message : EnumProtocolResponseCode.values()) {
         if (message.getCode() == code) {
            result = message;
            break;
         }
      }
      return result;
   }
}
