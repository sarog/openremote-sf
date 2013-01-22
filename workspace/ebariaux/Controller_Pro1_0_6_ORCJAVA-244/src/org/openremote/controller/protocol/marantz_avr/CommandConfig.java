package org.openremote.controller.protocol.marantz_avr;

import java.util.HashMap;
import java.util.Map;

public class CommandConfig {
   
   private String command;
   private Map<String, String> knownParameters;
   
   public CommandConfig(String command) {
      super();
      this.command = command;
      this.knownParameters = new HashMap<String, String>();
   }
   
   public void addParameter(String orParam, String onkyoParam) {
      knownParameters.put(orParam, onkyoParam);
   }

   public String getCommand() {
      return command;
   }
   
   public String getParameter(String orParam) {
      return knownParameters.get(orParam);
   }

   /**
    * Reverse look-up in the parameters table.
    * Naive implementation at this stage, considering that the mapping for parameters used to send command
    * is identical to one for the feedback values received.
    * 
    * @param param
    * @return
    */
   public String lookupResponseParam(String param) {
     for (Map.Entry<String, String> e : knownParameters.entrySet()) {
        if (e.getValue().equals(param)) {
           return e.getKey();
        }
     }
     return null;
   }
}
