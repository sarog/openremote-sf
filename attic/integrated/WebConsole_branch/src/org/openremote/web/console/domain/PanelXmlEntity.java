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
package org.openremote.web.console.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import flexjson.JSON;

/**
 * The Class PanelXmlEntity is for store the groups,screens,global tabbar and labels.
 * It is used by client codes to display views.
 */
public class PanelXmlEntity implements Serializable {

   private static final long serialVersionUID = -2687298880011259591L;

   private Map<Integer, Group> groups;
   private Map<Integer, Screen> screens;
   public TabBar globalTabBar;
   
   /** The tmp labels is for store parsed labels. */
   public transient Map<Integer, Label> tmpLabels = new HashMap<Integer, Label>();
   
   /** The tmp images is for store parsed images. */
   public transient List<Image> tmpImages = new ArrayList<Image>();
   
   public PanelXmlEntity() {
      groups = new LinkedHashMap<Integer, Group>();
      screens = new HashMap<Integer, Screen>();
   }

   public Map<Integer, Group> getGroups() {
      return groups;
   }

   public Map<Integer, Screen> getScreens() {
      return screens;
   }

   public void setGroups(Map<Integer, Group> groups) {
      this.groups = groups;
   }

   public void setScreens(Map<Integer, Screen> screens) {
      this.screens = screens;
   }

   public TabBar getGlobalTabBar() {
      return globalTabBar;
   }

   public void setGlobalTabBar(TabBar globalTabBar) {
      this.globalTabBar = globalTabBar;
   }

   @JSON(include=false)
   public Map<Integer, Label> getTmpLabels() {
      return tmpLabels;
   }

   @JSON(include=false)
   public List<Image> getTmpImages() {
      return tmpImages;
   }

}
