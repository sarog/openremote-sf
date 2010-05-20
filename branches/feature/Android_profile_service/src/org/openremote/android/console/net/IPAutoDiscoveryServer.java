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
package org.openremote.android.console.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.openremote.android.console.Constants;

import android.util.Log;

/**
 * Controller IP auto discovery server, this is a TCP server receiving IP from Controllers.
 * 
 * @author Tomsky Wang
 *
 */
public class IPAutoDiscoveryServer implements Runnable {

   public static ArrayList<String> autoServers = new ArrayList<String>();
   public static boolean isInterrupted;
   public void run() {
      boolean moreQuotes = true;
      ServerSocket srvr = null;
      try {
         srvr = new ServerSocket(Constants.LOCAL_SERVER_PORT);
         autoServers.clear();
         srvr.setSoTimeout(1000);
      } catch (BindException e) {
         Log.e("AUTO DISCOVER", "auto discovery server setup failed, the address is already in use");
         autoServers.clear();
         return;
      } catch (IOException e) {
         Log.e("AUTO DISCOVER", "auto discovery server setup failed", e);
         autoServers.clear();
         return;
      }
      while (moreQuotes && !isInterrupted) {
         try {
            Socket connectionSocket = srvr.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String line = inFromClient.readLine();
            if (line != null && !"".equals(line)) {
               autoServers.add(line);
            }
            connectionSocket.close();
            Log.i("AUTO DISCOVER", "auto discovery result: " + autoServers);
            Thread.sleep(3);
         } catch (SocketTimeoutException e) {
            moreQuotes = false;
         } catch (InterruptedException e) {
            moreQuotes = false;
         } catch (IOException e) {
            moreQuotes = false;
         }
      }
      try {
         srvr.close();
      } catch (IOException e) {
         Log.e("AUTO DISCOVER", "auto discovery ServerSocket close failed " , e);
      }
   }

}
