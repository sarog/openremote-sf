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

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.polling.JsonResultReader;
import org.openremote.web.console.client.polling.SimpleScriptTagProxy;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

/**
 * The Class ORRoundRobin is for detect group member and switch server.
 */
public class ORRoundRobin {

   private ORRoundRobin() {
   }
   
   /**
    * Detect group members in the same network segment.
    */
   public static void detectGroupMembers() {
      String currentServer = ClientDataBase.appSetting.getCurrentServer();
      if(!"".equals(currentServer)) {
         SimpleScriptTagProxy roundRobinProxy = new SimpleScriptTagProxy(currentServer + "/rest/servers", new JsonResultReader() {
            public void read(JSONObject jsonObj) {
               if (jsonObj.containsKey("servers")) {
                  JSONObject serversObj = jsonObj.get("servers").isObject();
                  if (serversObj.containsKey("server")) {
                     Cookies.setCookie(Constants.GROUP_MEMBERS, serversObj.get("server").toString());
                  }
               }
            }
         });
         roundRobinProxy.load();
      }
   }
   
   /**
    * Do switch to a group member, not self.
    */
   public static void doSwitch() {
      String memberStr = Cookies.getCookie(Constants.GROUP_MEMBERS);
      if (memberStr != null && !"".equals(memberStr)) {
         JSONValue jsonValue = JSONParser.parse(memberStr);
         JSONArray groupArray = jsonValue.isArray();
         String member = null;
         String currentServer = ClientDataBase.appSetting.getCurrentServer();
         if (groupArray != null) {
            for (int i = 0; i < groupArray.size(); i++) {
               member = groupArray.get(i).isObject().get("@url").isString().stringValue();
               if (!currentServer.equals(member)) {
                  break;
               }
            }
         } else {
            member = jsonValue.isObject().get("@url").isString().stringValue();
         }
         if (member != null && !currentServer.equals(member)) {
            ClientDataBase.appSetting.setCurrentServer(member);
            Cookies.setCookie(Constants.CONSOLE_SETTINGS, ClientDataBase.appSetting.toJson());
            Window.Location.reload();
         }
      }
   }
}
