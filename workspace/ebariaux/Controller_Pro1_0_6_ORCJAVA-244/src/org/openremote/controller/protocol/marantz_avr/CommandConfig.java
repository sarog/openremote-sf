package org.openremote.controller.protocol.marantz_avr;

import java.util.HashMap;
import java.util.Map;

// TODO : base class for commandClass should parametrize this class

/**
 * 
 * @author ebariaux
 *
 */
public class CommandConfig {
   
   private String name;
   private String value;
   private Class<? extends MarantzAVRCommand> commandClass;
   private Map<String, String> knownParameters;
   
   public CommandConfig(String name, String value, Class<? extends MarantzAVRCommand> commandClass) {
      super();
      this.name = name;
      this.value = value;
      this.commandClass = commandClass;
      this.knownParameters = new HashMap<String, String>();
   }
   
   public void addParameter(String orParam, String onkyoParam) {
      knownParameters.put(orParam, onkyoParam);
   }

   public String getName() {
      return name;
   }
   
   public String getValue() {
      return value;
   }

   public Class<? extends MarantzAVRCommand> getCommandClass() {
      return commandClass;
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
   
   // TODO : toString
}
