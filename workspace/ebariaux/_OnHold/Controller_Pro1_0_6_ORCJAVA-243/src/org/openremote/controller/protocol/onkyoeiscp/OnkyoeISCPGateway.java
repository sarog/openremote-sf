/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.onkyoeiscp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.controller.OnkyoeISCPConfig;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL;
import org.openremote.controller.protocol.MessageQueueWithPriorityAndTTL.Coalescable;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 * 
 */
public class OnkyoeISCPGateway {

   // Class Members --------------------------------------------------------------------------------

   private static final int TCP_TIMEOUT = 10000;
   private static final int COMMUNICATION_ERROR_RETRY_DELAY = 15000;

   /**
    * Onkyo eISCP logger. Uses a common category for all Onkyo eISCP related logging.
    */
   private final static Logger log = Logger.getLogger(OnkyoeISCPCommandBuilder.ONKYO_EISCP_LOG_CATEGORY);

   // Don't ask this to the config factory when instantiating the bean, this
   // results in infinite recursion
   private OnkyoeISCPConfig onkyoConfig;

   private MessageQueueWithPriorityAndTTL<OnkyoCommand> queue = new MessageQueueWithPriorityAndTTL<OnkyoCommand>();

   private OnkyoConnectionThread connectionThread;

   private Map<String, List<OnkyoeISCPCommand>> registeredCommands = new HashMap<String, List<OnkyoeISCPCommand>>();
   
   public synchronized void startGateway() {
      if (onkyoConfig == null) {
         onkyoConfig = OnkyoeISCPConfig.readXML();
         // Check config, report error if any -> TODO: auto discovery ?
         log.info("Got Onkyo config");
         log.info("Address >" + onkyoConfig.getAddress() + "<");
         log.info("Port >" + onkyoConfig.getPort() + "<");
      }

      if (connectionThread == null) {
         // Starts some thread that has the responsibility to establish connection and keep it alive
         connectionThread = new OnkyoConnectionThread();
         connectionThread.start();
      }
   }

   public void sendCommand(String command, String parameter) {
      log.info("Asked to send command " + command);

      // Ask to start gateway, if it's already done, this will do nothing
      startGateway();
      queue.add(new OnkyoCommand(command, parameter));
   }

   // ---

   private class OnkyoConnectionThread extends Thread {

      private OnkyoReaderThread readerThread;
      private OnkyoWriterThread writerThread;

      @Override
      public void run() {
         Socket socket;
         while (!isInterrupted()) {
            try {
               log.info("Trying to connect to " + onkyoConfig.getAddress() + " on port " + onkyoConfig.getPort());
               socket = new Socket();
               socket.connect(new InetSocketAddress(onkyoConfig.getAddress(), onkyoConfig.getPort()), TCP_TIMEOUT);
               log.info("Socket client connected");
               readerThread = new OnkyoReaderThread(socket.getInputStream());
               readerThread.start();
               log.info("Reader thread asked to start");
               writerThread = new OnkyoWriterThread(socket.getOutputStream());
               writerThread.start();
               log.info("Writer thread asked to start");
               // Wait for the read thread to die, this would indicate the connection was dropped
               while (readerThread != null) {
                  readerThread.join(1000);
                  if (!readerThread.isAlive()) {
                     log.info("Reader thread is dead, clean and re-try to connect");
                     socket.close();
                     readerThread = null;
                     writerThread.interrupt();
                     writerThread = null;
                  }
               }
            } catch (SocketException e) {
               log.error("Connection to Onkyo receiver impossible, sleeping and re-trying later", e);
               // We could not connect, sleep for a while before trying again
               try {
                  Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
               } catch (InterruptedException e1) {
                  log.warn("Interrupted during our sleep", e1);
               }
            } catch (IOException e) {
               log.error("Connection to Onkyo receiver impossible, sleeping and re-trying later", e);
               // We could not connect, sleep for a while before trying again
               try {
                  Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
               } catch (InterruptedException e1) {
                  log.warn("Interrupted during our sleep", e1);
               }
            } catch (InterruptedException e) {
               log.warn("Interrupted during our sleep", e);
            }
         }
         // For now do not support discovery, use information defined in config
      }

   }

   // ---

   private class OnkyoWriterThread extends Thread {

      private OutputStream os;

      public OnkyoWriterThread(OutputStream os) {
         super();
         this.os = os;
      }

      @Override
      public void run() {

         log.info("Writer thread starting");

         PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));

         while (!isInterrupted()) {
            OnkyoCommand cmd = null;
            try {
               cmd = queue.blockingPoll();
            } catch (InterruptedException e) {
               break;
            }
            if (cmd != null) {
               log.info("Sending >" + cmd.toString() + "< on print writer " + pr);
               
               log.info("Hex >" + hexDump(cmd.toString()));
               
               pr.print(cmd.toString());
               pr.flush();
            }
         }
         log.info("Out of writer thread");
      }
   }

   // ---

   private class OnkyoReaderThread extends Thread {

      private InputStream is;

      public OnkyoReaderThread(InputStream is) {
         super();
         this.is = is;
      }

      @Override
      public void run() {
         log.info("Reader thread starting");
         log.info("Socket input stream " + is);
         
         
         // Don't do a readline, use binary read and check start marker + lengths ...
         BufferedInputStream bis = new BufferedInputStream(is);
         byte[] packetHeader = new byte[16];
         int numBytesRead;
         log.info("Buffered input stream " + bis);

         try {
            while (((numBytesRead = bis.read(packetHeader)) != -1) && !isInterrupted()) {
               log.info("Read header " + hexDump(packetHeader));
               if (numBytesRead != 16) {
                  log.error("Invalid eISCP packet header size, only got " + numBytesRead + " bytes");
                  continue;
               }
               System.out.println(">" + new String(packetHeader, 0, 4, "US-ASCII") + "<");
               long headerSize = (packetHeader[4] << 24) + (packetHeader[5] << 16) + (packetHeader[6] << 8) + packetHeader[7];
               long dataSize = (packetHeader[8] << 24) + (packetHeader[9] << 16) + (packetHeader[10] << 8) + packetHeader[11];
               System.out.println("Header size: " + headerSize);
               System.out.println("Data size: " + dataSize);
               byte[] msgBuffer = new byte[(int)dataSize];
               numBytesRead = bis.read(msgBuffer, 0, (int)dataSize);
               System.out.println("Read " + numBytesRead + "bytes");
               System.out.println("Msg >" + hexDump(msgBuffer));
               String text = new String(msgBuffer, 2, msgBuffer.length - 5, "US-ASCII");
               System.out.println(">" + text + "<");
               OnkyoResponse response = parseResponse(text);
               
               List<OnkyoeISCPCommand> commands = registeredCommands.get(response.command);
               if (commands != null) {
                  for (OnkyoeISCPCommand command : commands) {
                     command.updateWithResponse(response);
                  }
               }
            }
         } catch (IOException e) {
            log.error("Error reading from eISCP connection", e);
         }
         log.info("Out of reader thread");
      }
   }

   private OnkyoResponse parseResponse(String responseText) {
      if (responseText == null) {
         return null;
      }

      OnkyoResponse response = null;
      if (responseText.length() >= 3) {
         response = new OnkyoResponse();
         response.command = responseText.substring(0, 3);
         response.parameter = responseText.substring(3);
      }
      return response;
   }
   
   public void registerCommand(String commandString, OnkyoeISCPCommand command) {
      List<OnkyoeISCPCommand> commands = registeredCommands.get(commandString);
      if (commands == null) {
         commands = new ArrayList<OnkyoeISCPCommand>();
         registeredCommands.put(commandString, commands);
      }
      commands.add(command);
   }
   
   public void unregisterCommand(String commandString, OnkyoeISCPCommand command) {
      List<OnkyoeISCPCommand> commands = registeredCommands.get(commandString);
      if (commands != null) {
         commands.remove(command);
      }
   }

   public class OnkyoCommand implements Coalescable {

      private String command;
      private String parameter;

      public OnkyoCommand(String command, String parameter) {
         this.command = command;
         this.parameter = parameter;
      }

      public String toString() {
         String data = command + parameter;
         log.info("data >" + data + "<");

         StringBuffer buf = new StringBuffer("ISCP");
         
         // Header size 0x00000010 by protocol definition
         buf.append((char)0x0);
         buf.append((char)0x0);
         buf.append((char)0x0);
         buf.append((char)0x10);
         
         // Data size
         String converted = "0000" + Integer.toString(5 + 19, 16);
         converted = converted.substring(converted.length() - 4, converted.length());
         // TODO : handle case where length > 255
         buf.append((char)0x0);
         buf.append((char)0x0);
         buf.append((char)0x0);
         buf.append((char)Integer.parseInt(converted, 16));
         
         buf.append((char)0x01); // Version
         // Reserved
         buf.append((char)0x0);
         buf.append((char)0x0);
         buf.append((char)0x0);

         buf.append("!"); // Start of packet
         buf.append("1"); // Destination Unit Type : 1 = receiver
         
         buf.append(data);
         
         buf.append((char)0x0D); // EOF
         return buf.toString();
      }

      @Override
      public boolean isCoalesable(Coalescable other) {
         if (!(other instanceof OnkyoCommand)) {
            return false;
         }
         OnkyoCommand otherCommand = (OnkyoCommand) other;

         return false;

         // TODO: if we want to be coalesable, need to have more fine grained information e.g. must have distinct
         // channel or level value

      }

   }

   public class OnkyoResponse {

      public String command;
      public String parameter;

   }

   private String hexDump(String msg) {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < msg.length(); i++) {
        char c = msg.charAt(i);
        String s = "00" + Integer.toString(c, 16);
        buf.append("0x" + s.substring(s.length() - 2, s.length()));
        if (i < msg.length() - 1) {
          buf.append(" ");
        }
      }
      return buf.toString();
   }

   private String hexDump(byte[] msg) {
      StringBuffer buf = new StringBuffer();
      for (int i = 0; i < msg.length; i++) {
        byte c = msg[i];
        String s = "00" + Integer.toString(c, 16);
        buf.append("0x" + s.substring(s.length() - 2, s.length()));
        if (i < msg.length - 1) {
          buf.append(" ");
        }
      }
      return buf.toString();
   }

}
