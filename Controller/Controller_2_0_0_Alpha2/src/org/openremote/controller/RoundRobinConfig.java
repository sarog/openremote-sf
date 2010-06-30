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
package org.openremote.controller;

import java.util.HashSet;
import java.util.Set;


/**
 * Configuration of RoundRobin.
 * 
 * @author Handy.Wang 2009-12-23
 */
public class RoundRobinConfig {
   
   private Boolean isRoundRobinOn;
   
   /**  Discovery multicast address of round robin. */
   private String roundRobinMulticastAddress;
   
   /** Discovery multicast port of round robin. */
   private int roundRobinMulticastPort;
   
   /** Group name of Controller app. */
   private String controllerGroupName;
   
   /** ApplicationName of Controller. */
   private String controllerApplicationName;

   /** TCP server socket port of Round Robin */
   public int roundRobinTCPServerSocketPort;
   
   /** Group members url */
   public String[] groupMembersURLs;

   public Boolean getIsRoundRobinOn() {
      return isRoundRobinOn;
   }

   public void setIsRoundRobinOn(Boolean isRoundRobinOn) {
      this.isRoundRobinOn = isRoundRobinOn;
   }

   public String getRoundRobinMulticastAddress() {
      return roundRobinMulticastAddress;
   }

   public void setRoundRobinMulticastAddress(String roundRobinMulticastAddress) {
      this.roundRobinMulticastAddress = roundRobinMulticastAddress;
   }

   public int getRoundRobinMulticastPort() {
      return roundRobinMulticastPort;
   }

   public void setRoundRobinMulticastPort(int roundRobinMulticastPort) {
      this.roundRobinMulticastPort = roundRobinMulticastPort;
   }

   public String getControllerGroupName() {
      return controllerGroupName;
   }

   public void setControllerGroupName(String controllerGroupName) {
      this.controllerGroupName = controllerGroupName;
   }

   public String getControllerApplicationName() {
      return controllerApplicationName;
   }

   public void setControllerApplicationName(String controllerApplicationName) {
      this.controllerApplicationName = controllerApplicationName;
   }

   public int getRoundRobinTCPServerSocketPort() {
      return roundRobinTCPServerSocketPort;
   }

   public void setRoundRobinTCPServerSocketPort(int roundRobinTCPServerSocketPort) {
      this.roundRobinTCPServerSocketPort = roundRobinTCPServerSocketPort;
   }
   
   public String[] getGroupMembersURLs() {
      return this.groupMembersURLs;
   }

   public Set<String> getGroupMembersURLsSet() {
      Set<String> urlsSet = new HashSet<String>();
      for (String groupMembersURL : groupMembersURLs) {
         urlsSet.add(groupMembersURL);
      }
      return urlsSet;
   }

   public void setGroupMembersURLs(String[] groupMembersURLs) {
      this.groupMembersURLs = groupMembersURLs;
   }

}

