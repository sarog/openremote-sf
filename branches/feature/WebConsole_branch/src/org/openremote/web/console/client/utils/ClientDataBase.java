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

import java.util.HashMap;
import java.util.Map;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.domain.Screen;
import org.openremote.web.console.domain.UserInfo;

import com.google.gwt.user.client.Cookies;

/**
 * Stores all the UI models here like a database. <br/>
 * 
 */
public class ClientDataBase {

   /** The user info contains username, password, last groupId, last screenId.*/
   public static UserInfo userInfo = new UserInfo();
   
   /** The app setting contains currentServer, currentPanelIdentity, customServers and autoDiscovery. */
   public static AppSetting appSetting = new AppSetting();
   
   /** The Constant panelXmlEntity contains groups, screens and global tabbar. */
   public static final PanelXmlEntity panelXmlEntity = new PanelXmlEntity();
   
   /** The Constant statusMap is for store the sensor status. */
   public static final Map<String, String> statusMap = new HashMap<String, String>();
   
   private ClientDataBase() {
   }
   
   /**
    * Gets the first group of current panel, if not found return null.
    * 
    * @return the first group
    */
   public static Group getFirstGroup() {
      Map<Integer, Group> groups = panelXmlEntity.getGroups();
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
   public static Group getGroupById(int groupId) {
      Map<Integer, Group> groups = panelXmlEntity.getGroups();
      if (!groups.isEmpty()) {
         return groups.get(groupId);
      }
      return null;
   }
   
   /**
    * The default group is the last time visitorial group or the first group.
    * 
    * @return the default group
    */
   public static Group getDefaultGroup() {
      Group group = null;
      if (userInfo.getLastGroupId() > 0) {
         group = getGroupById(userInfo.getLastGroupId());
      }
      if (group == null) {
         group = getFirstGroup();
      }
      return group;
   }
   
   /**
    * Gets the screen by id, if not found return null.
    * 
    * @param screenId the screen id
    * 
    * @return the screen by id
    */
   public static Screen getScreenById(int screenId) {
      Map<Integer, Screen> screens = panelXmlEntity.getScreens();
      if (!screens.isEmpty()) {
         return screens.get(screenId);
      }
      return null;
   }
   
   /**
    * Gets the last time visitorial screen.
    * 
    * @return the last time screen
    */
   public static Screen getLastTimeScreen() {
      if (userInfo.getLastScreenId() > 0) {
         return getScreenById(userInfo.getLastScreenId());
      }
      return null;
   }
   
   public static String getSecuredServer() {
      String currentServer = ClientDataBase.appSetting.getCurrentServer();
      if ("true".equals(Cookies.getCookie(Constants.SSL_STATUS))) {
         int sslPort = Integer.valueOf(Cookies.getCookie(Constants.SSL_PORT));
         if (currentServer.indexOf("http:") != -1) {
            currentServer = currentServer.replaceFirst("http:", "https:");
         }
         if (currentServer.indexOf(":") != -1) {
            currentServer = currentServer.replaceFirst("\\:\\d+", ":" + sslPort);
         }
      }
      return currentServer;
   }
   
   public static String getControlPath() {
      return getSecuredServer() + "/rest/control/";
   }
   
   public static String getResourceRootPath() {
      return ClientDataBase.appSetting.getCurrentServer() + "/resources/";
   }
}
