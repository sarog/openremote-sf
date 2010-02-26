/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.TabBar;
import org.openremote.android.console.bindings.XScreen;

public class XMLEntityDataBase {
   public static TabBar globalTabBar = null;
   public static final HashMap<Integer, Group> groups = new HashMap<Integer, Group>();
   public static final HashMap<Integer, XScreen> screens = new HashMap<Integer, XScreen>();
   public static final HashSet<String> imageSet = new HashSet<String>();
   public static Group getFirstGroup() {
      if (!groups.isEmpty()) {
         return groups.get(groups.keySet().iterator().next());
      }
      return null;
   }
}
