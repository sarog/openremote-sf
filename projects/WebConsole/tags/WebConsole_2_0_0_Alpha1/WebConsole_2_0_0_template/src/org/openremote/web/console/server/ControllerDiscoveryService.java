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
package org.openremote.web.console.server;

import java.util.ArrayList;


/**
 * Discover controllers in the same network segment.
 */
public class ControllerDiscoveryService {
   public static final String MULTICAST_ADDRESS = "224.0.1.100";
   public static final int MULTICAST_PORT = 3333;
   public static final int TCP_PORT = 2346;
   
   /**
    * Not be Instantiated.
    */
   private ControllerDiscoveryService() {
   }
   
   /**
    * Gets the auto discovery servers by start a Multicast UDP client broadcasting to request and a TCP server to receive response.
    * 
    * @return the auto servers
    */
   public static ArrayList<String> getAutoServers() {
      new Thread(new ControllerDiscoveryServiceReceiver()).start();
      new Thread(new ControllerDiscoveryServiceSender()).start();
      try {
         Thread.sleep(200);
      } catch (InterruptedException e) {
      }
      return ControllerDiscoveryServiceReceiver.autoServers;
   }
}
