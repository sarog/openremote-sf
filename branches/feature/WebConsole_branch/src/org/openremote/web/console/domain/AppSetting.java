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
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

/**
 * The Class AppSetting is for storing the settings info.
 * 
 * It contains auto discovery, current server, current panel identity and custom servers.
 */
public class AppSetting implements Serializable {

   private static final long serialVersionUID = -3580463734417781801L;

   private boolean autoDiscovery;
   private String currentServer;
   private String currentPanelIdentity;
   private List<String> customServers;
   
   public AppSetting() {
      this.autoDiscovery = true;
      this.currentServer = "";
      this.currentPanelIdentity = "";
   }
   
   public boolean isAutoDiscovery() {
      return autoDiscovery;
   }
   public String getCurrentServer() {
      return currentServer;
   }
   public String getCurrentPanelIdentity() {
      return currentPanelIdentity;
   }
   public void setAutoDiscovery(boolean autoDiscovery) {
      this.autoDiscovery = autoDiscovery;
   }
   public void setCurrentServer(String currentServer) {
      this.currentServer = currentServer;
   }
   public void setCurrentPanelIdentity(String currentPanelIdentity) {
      this.currentPanelIdentity = currentPanelIdentity;
   }

   public List<String> getCustomServers() {
      return customServers;
   }

   public void setCustomServers(List<String> customServers) {
      this.customServers = customServers;
   }
   
   public void addCustomServer(String customServer) {
      if (customServer == null || "".equals(customServer)) {
         return;
      }
      if (customServers == null) {
         customServers = new ArrayList<String>();
      }
      customServers.add(customServer);
   }
   
   public void removeCustomServer(String customServer) {
      if (customServer == null || "".equals(customServer) || customServers == null) {
         return;
      }
      customServers.remove(customServer);
   }
   
   /**
    * Convert the AppSetting object to JSONObject, and return as a string.
    * 
    * The method is just calling by client code.
    * @return the string
    */
   public String toJson() {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("autoDiscovery", JSONBoolean.getInstance(this.autoDiscovery));
      jsonObj.put("currentServer", new JSONString(this.currentServer));
      jsonObj.put("currentPanelIdentity", new JSONString(this.currentPanelIdentity));
      if (customServers == null) {
         jsonObj.put("customServers", JSONNull.getInstance());
      } else {
         JSONArray jsonArray = new JSONArray();
         int serverSize = customServers.size();
         for (int i = 0; i < serverSize; i++) {
            jsonArray.set(i, new JSONString(customServers.get(i)));
         }
         jsonObj.put("customServers", jsonArray);
      }
      return jsonObj.toString();
   }
   
   /**
    * Inits the AppSetting's properties from json string.
    * 
    * The method is just calling by client code.
    * @param jsonStr the json string
    */
   public void initFromJson(String jsonStr) {
      if (jsonStr == null || "".equals(jsonStr)) {
         return;
      }
      JSONObject jsonObj = JSONParser.parse(jsonStr).isObject();    
      if (jsonObj == null) {
         return;
      }
      
      this.autoDiscovery = jsonObj.get("autoDiscovery").isBoolean().booleanValue();
      this.currentServer = jsonObj.get("currentServer").isString().stringValue();
      this.currentPanelIdentity = jsonObj.get("currentPanelIdentity").isString().stringValue();
      JSONArray jsonArray = jsonObj.get("customServers").isArray();
      if (jsonArray != null) {
         if (customServers == null) {
            customServers = new ArrayList<String>();
         }
         for(int i = 0; i < jsonArray.size(); i++) {
            customServers.add(jsonArray.get(i).isString().stringValue());
        }
      }
   }
   
}
