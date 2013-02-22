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

import org.openremote.android.console.bindings.Panel;
import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.Label;
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.bindings.TabBar;

/**
 * Stores all the UI models here like a database. <br/>
 */
public class XMLEntityDataBase {
   public static TabBar globalTabBar = null;
   public static final HashMap<Integer, Group> groups = new HashMap<Integer, Group>();
   public static final HashMap<Integer, Screen> screens = new HashMap<Integer, Screen>();
   /** Panels */
   public static final HashMap<String, Panel> panels = new LinkedHashMap<String, Panel>();

   
   /** The all panel's labels. */
   public static final HashMap<Integer, Label> labels = new HashMap<Integer, Label>();
   
   /** The all panel's images. */
   public static final HashSet<String> imageSet = new HashSet<String>();
   
   
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
   
   public static Panel getPanel(String panelId) {
	   if (!panels.isEmpty()) {
		   return panels.get(panelId);
	   }
	   return null;
   }
}
