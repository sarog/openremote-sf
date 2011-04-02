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
package org.openremote.controller.gateway.protocol.telnet;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.exception.GatewayConnectionException;
import org.openremote.controller.gateway.command.Action;
import org.openremote.controller.gateway.Protocol;
import org.openremote.controller.gateway.ProtocolInterface;
import org.openremote.controller.gateway.EnumGatewayConnectionType;
import org.openremote.controller.gateway.EnumGatewayPollingMethod;
import org.openremote.controller.gateway.command.EnumCommandActionType;

/**
 * The Telnet Protocol.
 * 
 * Author Rich Turner 2011-02-13
 */
public class TelnetProtocol extends Protocol {

   // Common Protocol Properties --------------------------------------------------------
   /** The logger. */
   private static Logger logger = Logger.getLogger(TelnetProtocol.class.getName());

   /** A name to identify event in controller.xml. */
   private String name;
   
   /* Set supported connection Types */
   List<EnumGatewayConnectionType> supportedConnectionTypes = Arrays.asList(EnumGatewayConnectionType.MANAGED, EnumGatewayConnectionType.PERMANENT, EnumGatewayConnectionType.TIMED);
   
   /* Set supported polling methods */
   List<EnumGatewayPollingMethod> supportedPollingMethods = Arrays.asList(EnumGatewayPollingMethod.QUERY, EnumGatewayPollingMethod.BROADCAST);
   
   private InputStream inputStream;
   private OutputStream outputStream;
   
   /* This is the time in milliseconds before connection attempt stops */
   private int connectTimeout = Gateway.CONNECT_TIMEOUT;

   /* This is the time in milliseconds before read command attempt stops */
   private int readTimeout = Gateway.READ_TIMEOUT;
   
   /* This is the string that marks the end of a command */
   private String sendTerminator = "\n";
   
   // Protocol Properties ----------------------------------------------------------------
   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private int port;
   
   private TelnetClient telnetClient;

   public TelnetProtocol() {
      super.supportedConnectionTypes = this.supportedConnectionTypes;
      super.supportedPollingMethods = this.supportedPollingMethods;
   }
   
   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      String name;
      if (this.name == null) {
         name = "Telnet " + getIp() + ":" + getPort();
      } else {
         name = this.name;
      }
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name
    *           the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the ip
    * 
    * @return the ip
    */
   public String getIp() {
      return this.ip;
   }

   /**
    * Sets the ip
    * @param ip the new ip
    */
   public void setIp(String ip) {
      this.ip = ip;
   }

   /**
    * Gets the port
    * 
    * @return the port
    */
   public int getPort() {
      return this.port;
   }

   /**
    * Sets the port 
    * @param port the new port
    */
   public void setPort(String port) {
      this.port = Integer.parseInt(port);
   }
   
   /**
    * Gets the connect timeout
    * 
    * @return the connect timeout
    */
   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   /**
    * Sets the read timeout
    * @param connect timeout
    */
   public void setConnectTimeout(String timeout) {
      try {
         int num = Integer.parseInt(timeout);
         if (num <= 60000) {
            this.connectTimeout = num;
         }
      } catch (NumberFormatException e) {
         logger.error("Invalid connect timeout parameter supplied to gateway");  
      }
   }

   /**
    * Gets the read timeout
    * 
    * @return the read timeout
    */
   public int getReadTimeout() {
      return this.readTimeout;
   }

   /**
    * Sets the read timeout
    * @param read timeout
    */
   public void setReadTimeout(String timeout) {
      try {
         int num = Integer.parseInt(timeout);
         if (num <= 5000) {
            this.readTimeout = num;
         }
      } catch (NumberFormatException e) {
         logger.error("Invalid read timeout parameter supplied to gateway");  
      }
   }

   /**
    * Gets the send terminator
    * 
    * @return the send terminator
    */
   public String getSendTerminator() {
      return this.sendTerminator;
   }

   /**
    * Sets the send terminator
    * @param send terminator
    */
   public void setSendTerminator(String terminator) {
      this.sendTerminator = terminator;
   }
   
   /**
    * Connects to the telnet server and establishes the input/output streams
    * Exception handling is done by the Gateway class
    */
   public void connect() throws Exception {
      telnetClient = new TelnetClient();
      this.telnetClient.setConnectTimeout(this.connectTimeout);
      this.telnetClient.connect(getIp(), getPort());
      inputStream = this.telnetClient.getInputStream();
      outputStream = this.telnetClient.getOutputStream();
      
      // Flush out any connection messages returned from the server
      try {
         Thread.sleep(50);
      } catch (Exception e) {}
      clearBuffer();
   }

   /**
    * Disconnects from the telnet server and nulls the input/output streams
    * Exception handling is done by the Gateway class    
    */
   public void disconnect() throws Exception {
      this.telnetClient.disconnect();
      inputStream = null;
      outputStream = null;
   }
   
   /* Clear the inputStream buffer when requested by the gateway */
   public void clearBuffer() throws Exception {
      while (inputStream.available() != 0) {
         inputStream.read();
      }  
   }
   
   /**
    * Ue this method to validate a command action for this protocol
    */
   public Boolean isValidAction(Action action) {
      // Ensure required args are supplied and action type is supported
      Boolean result = true;
      return result;
   }
   
   public String doAction(Action commandAction) throws Exception {
      Map<String, String> args = commandAction.getArgs();
      EnumCommandActionType actionType = commandAction.getType();
      String actionResult = "";
      
      switch (actionType) {
         case SEND:
            String sendTerminator = this.sendTerminator;
            if (args.containsKey("sendterminator")) {
               sendTerminator = args.get("sendterminator");  
            }
            outputStream.write((args.get("command") + sendTerminator).getBytes());
            outputStream.flush();
            break;
         case READ:
            Calendar endTime = Calendar.getInstance();
            endTime.add(Calendar.MILLISECOND, this.readTimeout);
            try {
               while (Calendar.getInstance().before(endTime) && inputStream.available() == 0) {
                  try {
                     Thread.sleep(50);
                  } catch (Exception e) {}
               }
               
               while (inputStream.available() > 0) {
                  actionResult += (char) inputStream.read();
               }

               // If data received then assume this is what we're waiting for
               if (actionResult.length() > 0) {
                  break;
               }
            } catch (Exception e) {
               throw new GatewayConnectionException("Gateway connection read error: " + e.getMessage(), e);
            }
            break;
      }
      
      return actionResult;
   }
}