package org.openremote.controller.device.protocol;

public enum PayloadFormat {
   TEXT,
   BINARY,
   MIXED;
   
   public static PayloadFormat fromString(String string)
   {
      PayloadFormat result = null;
      if (string != null) {
         String enumStr = string.trim().toUpperCase();
         for (PayloadFormat format : PayloadFormat.values()) {
            if (format.toString() == enumStr) {
               result = format;
               break;
            }
         }
      }
      return result;
   }
}
