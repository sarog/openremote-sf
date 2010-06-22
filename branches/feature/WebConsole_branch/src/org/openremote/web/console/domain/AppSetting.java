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

public class AppSetting implements Serializable {

   private static final long serialVersionUID = -3580463734417781801L;

   private boolean autoMode;
   private String currentServer;
   private String currentPanelIdentity;
   private List<String> customServers;
   
   public AppSetting() {
      this.autoMode = true;
      this.currentServer = "";
      this.currentPanelIdentity = "";
   }
   
   public boolean isAutoMode() {
      return autoMode;
   }
   public String getCurrentServer() {
      return currentServer;
   }
   public String getCurrentPanelIdentity() {
      return currentPanelIdentity;
   }
   public void setAutoMode(boolean autoMode) {
      this.autoMode = autoMode;
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
}
