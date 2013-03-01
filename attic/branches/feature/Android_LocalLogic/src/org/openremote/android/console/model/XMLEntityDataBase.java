/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.android.console.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.Label;
import org.openremote.android.console.bindings.LocalCommand;
import org.openremote.android.console.bindings.LocalLogic;
import org.openremote.android.console.bindings.LocalSensor;
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.bindings.TabBar;

/**
 * Stores all the UI models here like a database. <br/>
 */
public class XMLEntityDataBase {
   public static TabBar globalTabBar = null;
   public static final Map<Integer, Group> groups = new LinkedHashMap<Integer, Group>();
   public static final HashMap<Integer, Screen> screens = new HashMap<Integer, Screen>();
   
   /** The all panel's labels. */
   public static final HashMap<Integer, Label> labels = new HashMap<Integer, Label>();
   
   /** The all panel's images. */
   public static final HashSet<String> imageSet = new HashSet<String>();
   
   /** The local logic running on the console */
   public static LocalLogic localLogic = null;
   
   /**
    * Gets the first group of current panel, if not found return null.
    * 
    * @return the first group
    */
   public static Group getFirstGroup() {
      if (!groups.isEmpty()) {
         return groups.get(groups.keySet().iterator().next());
      }
      return null;
   }
   
   /**
    * Gets the group of current panel by id, if not found return null.
    * 
    * @param groupId the group id
    * 
    * @return the group by id
    */
   public static Group getGroup(int groupId) {
      if (!groups.isEmpty()) {
         return groups.get(groupId);
      }
      return null;
   }
   
   /**
    * Gets the screen by id, if not found return null.
    * 
    * @param screenId the screen id
    * 
    * @return the screen by id
    */
   public static Screen getScreen(int screenId) {
      if (!screens.isEmpty()) {
         return screens.get(screenId);
      }
      return null;
   }
   
   /**
    * Gets a local sensor by id, if not found return null.
    * 
    * @param sensorId the sensor id
    * 
    * @return the sensor with given id
    */
   public static LocalSensor getLocalSensor(int sensorId) {
	   if (localLogic != null) {
		   return localLogic.getLocalSensor(sensorId);
	   }
	   return null;
   }

   /**
    * Gets a local command by id, if not found return null.
    * 
    * @param commandId the command id
    * 
    * @return the command with given id
    */
   public static LocalCommand getLocalCommand(int commandId) {
	   if (localLogic != null) {
		   return localLogic.getLocalCommand(commandId);
	   }
	   return null;
   }
}
