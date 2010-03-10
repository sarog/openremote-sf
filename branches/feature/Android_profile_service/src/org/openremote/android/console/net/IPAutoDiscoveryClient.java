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
package org.openremote.android.console.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.openremote.android.console.Constants;

public class IPAutoDiscoveryClient implements Runnable {
   
   public static boolean IS_EMULATOR;
   public void run() {
      try {
         DatagramSocket socket = new DatagramSocket();
         byte[] b = new byte[512];
         DatagramPacket dgram;
         dgram = new DatagramPacket(b, b.length, InetAddress.getByName(getMulticastAddress()), Constants.MULTICAST_PORT);
         socket.send(dgram);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
   
   public static String getMulticastAddress() {
      if (IS_EMULATOR) {
         return Constants.EMULATOR_MULTICAST_ADDRESS;
      }
      return Constants.MULTICAST_ADDRESS;
   }
}
