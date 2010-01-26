package org.openremote.android.console.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.openremote.android.console.Constants;

public class IPAutoDiscoveryServer implements Runnable {

   public static ArrayList<String> autoServers = new ArrayList<String>();

   public void run() {
      boolean moreQuotes = true;
      ServerSocket srvr = null;
      try {
         srvr = new ServerSocket(Constants.LOCAL_SERVER_PORT);
         autoServers.clear();
         srvr.setSoTimeout(1000);
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      while (moreQuotes) {
         try {
            Socket connectionSocket = srvr.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String line = inFromClient.readLine();
            if (line != null && !"".equals(line)) {
               autoServers.add(line);
            }
            connectionSocket.close();
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
         e.printStackTrace();
      }
   }

}
