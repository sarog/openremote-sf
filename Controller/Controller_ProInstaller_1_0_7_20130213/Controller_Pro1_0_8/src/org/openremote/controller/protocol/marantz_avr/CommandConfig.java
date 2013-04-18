/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.marantz_avr;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the configuration of commands supported by the Marantz AVR protocol.
 * 
 * It provides the mapping from strings as defined in the Designer configuration
 * (which should be standardized for all protocols in OR) and the strings used
 * by the Marantz protocol "over the wire" to the device. 
 *
 * name is the command name as used in the Designer configuration
 * value is the corresponding command string used by the Marantz procotol
 * 
 * Some commands can be handled by the same generic implementation,
 * whereas others require a specific implementation.
 * 
 * commandClass is the specific MarantzAVRCommand subclass that handles the given command
 * 
 * A similar mapping from generic to Marantz specific strings exists
 * for the command parameters.
 *  
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
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
   
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("Command ");
      buf.append(name);
      buf.append(" : ");
      buf.append(value);
      buf.append(" is handled by class ");
      buf.append(commandClass);
      if (!knownParameters.isEmpty()) {
         buf.append("\n");
         buf.append("Parameters:");
      }
      for (Map.Entry<String,String> e : knownParameters.entrySet()) {
         buf.append("\n  ");
         buf.append(e.getKey());
         buf.append(" : ");
         buf.append(e.getValue());
      }
      return buf.toString();
   }
}
