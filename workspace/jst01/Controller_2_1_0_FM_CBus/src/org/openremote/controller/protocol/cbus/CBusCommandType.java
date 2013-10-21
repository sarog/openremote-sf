/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.cbus;

import java.util.HashMap;
import java.util.Map;

/**
 * CBus command types
 * 
 * @author Jamie Turner
 *
 */
public enum CBusCommandType {
   ON,
   OFF,
   SETVALUE,
   DIM,
   PULSE,
   STATUS
};


/**
 * Applications supported by the CBus protocol
 * 
 * @author Jamie Turner
 *
 */
enum CBusApplicationType
{
   LIGHTING("Lighting"),
   TRIGGER("Trigger Control"),
   ENABLE("Enable Control"),
   VENTILATION("Ventilation"),
   HEATING("Heating"),
   TELEPHONY("Telephony"),
   SECURITY("Security"),
   IRRIGATION("Irrigation"),
   NULL(null);
   
   private CBusApplicationType(String nameInProjectFile)
   {
      this.nameInProjectFile = nameInProjectFile;
   }
   
   private String nameInProjectFile;
   
 private static final Map<String, CBusApplicationType> typesByValue = new HashMap<String, CBusApplicationType>();
   
   static 
   {
      for (CBusApplicationType type : CBusApplicationType.values()) 
      {
         typesByValue.put(type.nameInProjectFile, type);
      }         
   }

   public static CBusApplicationType forProjectName(String value) {
      return typesByValue.get(value);
   }
   
   public String getNameInProjectFile()
   {
      return nameInProjectFile;
   }
   
};

/**
 * Command types used by CGate on the status port
 * 
 * @author Jamie Turner
 *
 */
enum CBusStatusCommandParameterType
{
   ON,
   OFF,
   SET,
   RAMP,
   EVENT
};
