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
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.bindings.TabBar;

public class XMLEntityDataBase {
   public static TabBar globalTabBar = null;
   public static final Map<Integer, Group> groups = new LinkedHashMap<Integer, Group>();
   public static final HashMap<Integer, Screen> screens = new HashMap<Integer, Screen>();
   public static final HashMap<Integer, Label> labels = new HashMap<Integer, Label>();
   public static final HashSet<String> imageSet = new HashSet<String>();
   public static Group getFirstGroup() {
      if (!groups.isEmpty()) {
         return groups.get(groups.keySet().iterator().next());
      }
      return null;
   }
   
   public static Group getGroup(int groupId) {
      if (!groups.isEmpty()) {
         return groups.get(groupId);
      }
      return null;
   }
   
   public static Screen getScreen(int screenId) {
      if (!screens.isEmpty()) {
         return screens.get(screenId);
      }
      return null;
   }
}
