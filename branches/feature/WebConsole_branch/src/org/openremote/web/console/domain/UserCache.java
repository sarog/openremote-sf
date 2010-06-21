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

public class UserCache implements Serializable{
   
   private static final long serialVersionUID = 2988635019156071218L;
   
   private String username;
   private String password;
   private int lastGroupId;
   private int lastScreenId;
   
   public UserCache() {
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
   
}
