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

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

/**
 * The Class is for storing the user info, include the username,password,lastGroupId and lastScreenId.
 */
public class UserInfo implements Serializable{
   
   private static final long serialVersionUID = 2988635019156071218L;
   
   private String username;
   private String password;
   private int lastGroupId;
   private int lastScreenId;
   
   public UserInfo() {
      username = "";
      password = "";
   }
   
   public String getUsername() {
      return username;
   }
   public String getPassword() {
      return password;
   }
   public void setUsername(String username) {
      this.username = username;
   }
   public void setPassword(String password) {
      this.password = password;
   }
   public int getLastGroupId() {
      return lastGroupId;
   }
   public int getLastScreenId() {
      return lastScreenId;
   }
   public void setLastGroupId(int lastGroupId) {
      this.lastGroupId = lastGroupId;
   }
   public void setLastScreenId(int lastScreenId) {
      this.lastScreenId = lastScreenId;
   }
   
   /**
    * Convert the UserInfo object to JSONObject, and return as a string.
    * 
    * The method is just calling by client code.
    * @return the string
    */
   public String toJson() {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("username", new JSONString(this.username));
      jsonObj.put("password", new JSONString(this.password));
      jsonObj.put("lastGroupId", new JSONNumber(this.lastGroupId));
      jsonObj.put("lastScreenId", new JSONNumber(this.lastScreenId));
      
      return jsonObj.toString();
   }
   
   /**
    * Inits the UserInfo's properties from json string.
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
      
      this.username = jsonObj.get("username").isString().stringValue();
      this.password = jsonObj.get("password").isString().stringValue();
      this.lastGroupId = (int) jsonObj.get("lastGroupId").isNumber().doubleValue();
      this.lastScreenId = (int) jsonObj.get("lastScreenId").isNumber().doubleValue();
   }
}
