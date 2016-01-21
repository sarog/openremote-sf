/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;

public abstract class Proxy extends Thread {

   private static Logger logger = Logger.getLogger(Constants.PROXY_LOG_CATEGORY);
   protected Socket srcSocket;
   protected boolean halted;
   protected int timeout;

   public Proxy(Socket clientSocket, int timeout) throws IOException {
      this.srcSocket = clientSocket;
      this.timeout = timeout;
   }

   @Override
   public void run() {
      try{
         logger.info("Client running");
         // we first need to connect to the endpoint
         Socket dstSocket;
         try {
            dstSocket = openDestinationSocket();
         } catch (IOException e) {
            logger.error("Failed to connect to the destination", e);
            return;
         }
         try {
            logger.info("We got connection to the destination");
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            
            // Get client streams. Make them final so they can
            // be used in the anonymous thread below.
            final InputStream from_client = srcSocket.getInputStream();
            final OutputStream to_client = srcSocket.getOutputStream();
            

            // Get server streams.
            final InputStream from_server = dstSocket.getInputStream();
            final OutputStream to_server = dstSocket.getOutputStream();
            
            // Make a thread to read the client's requests and pass them
            // to the server. We have to use a separate thread because
            // requests and responses may be asynchronous.
            Thread t = new Thread() {
              public void run() {
                int bytes_read;
                try {
                  while ((bytes_read = from_client.read(request)) != -1) {
                    to_server.write(request, 0, bytes_read);
                    to_server.flush();
                  }
                } catch (IOException e) {
                }

                // the client closed the connection to us, so close our
                // connection to the server. This will also cause the
                // server-to-client loop in the main thread exit.
                try {
                  to_server.close();
                } catch (IOException e) {
                }
              }
            };

            // Start the client-to-server request thread running
            t.start();

            // Meanwhile, in the main thread, read the server's responses
            // and pass them back to the client. This will be done in
            // parallel with the client-to-server request thread above.
            int bytes_read;
            try {
              while ((bytes_read = from_server.read(reply)) != -1) {
                to_client.write(reply, 0, bytes_read);
                to_client.flush();
              }
            } catch (IOException e) {
            }

            // The server closed its connection to us, so we close our
            // connection to our client.
            // This will make the other thread exit.
            to_client.close();
            
            logger.info("Done with proxying");
         } catch (Exception e) {
            logger.error("Proxy dead", e);
         }finally{
            // close the dst socket
            try {
               dstSocket.close();
            } catch (IOException x) {
            }
         }
      }finally{
         // close the src socket
         try {
            srcSocket.close();
         } catch (IOException x) {
         }
         onProxyExit();
      }
   }

   protected void onProxyExit() {}

   protected abstract Socket openDestinationSocket() throws IOException;


   public void halt() {
      halted = true;
   }

}

