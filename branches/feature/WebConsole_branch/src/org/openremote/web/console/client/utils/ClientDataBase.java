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
package org.openremote.web.console.client.utils;

import java.util.Map;

import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.domain.Screen;
import org.openremote.web.console.domain.UserInfo;

/**
 * The Class ClientDataBase.
 */
public class ClientDataBase {

   public static UserInfo userInfo = new UserInfo();
   
   public static AppSetting appSetting = new AppSetting();
   
   public static final PanelXmlEntity panelXmlEntity = new PanelXmlEntity();
   
   private ClientDataBase() {
   }
   
   public static Group getFirstGroup() {
      Map<Integer, Group> groups = panelXmlEntity.getGroups();
      if (!groups.isEmpty()) {
         return groups.get(groups.keySet().iterator().next());
      }
      return null;
   }
   
   public static Group getGroupById(int groupId) {
      Map<Integer, Group> groups = panelXmlEntity.getGroups();
      if (!groups.isEmpty()) {
         return groups.get(groupId);
      }
      return null;
   }
   
   public static Group getDefaultGroup() {
      if (userInfo.getLastGroupId() > 0) {
         return getGroupById(userInfo.getLastGroupId());
      } else {
         return getFirstGroup();
      }
   }
   
   public static Screen getScreenById(int screenId) {
      Map<Integer, Screen> screens = panelXmlEntity.getScreens();
      if (!screens.isEmpty()) {
         return screens.get(screenId);
      }
      return null;
   }
   
   public static Screen getLastTimeScreen() {
      if (userInfo.getLastScreenId() > 0) {
         return getScreenById(userInfo.getLastScreenId());
      }
      return null;
   }
}
