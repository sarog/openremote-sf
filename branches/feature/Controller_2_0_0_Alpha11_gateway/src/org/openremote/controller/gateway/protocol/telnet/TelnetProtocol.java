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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.openremote.controller.gateway.Protocol;
import org.openremote.controller.gateway.ProtocolInterface;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.gateway.EnumGatewayConnectionState;
import org.openremote.controller.gateway.EnumProtocolIOResult;
import org.openremote.controller.exception.GatewayProtocolException;
/**
 * The Telnet Protocol.
 * 
 * Author Rich Turner 2011-02-13
 */
public class TelnetProtocol extends Protocol {

   /** The logger. */
   private static Logger logger = Logger.getLogger(TelnetProtocol.class.getName());

   /** The default timeout used to wait for a result */
   public static final int DEFAULT_TIMEOUT = 1000;

   /** A name to identify event in controller.xml. */
   private String name;

   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private String port;

   /** The status default response */
   private String statusDefault;
   
   /** The wait timeout period in seconds */
   private Integer timeOut = DEFAULT_TIMEOUT;
   
   private TelnetClient telnetClient;

   private String promptString;

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
   public String getPort() {
      return this.port;
   }

   /**
    * Sets the port 
    * @param port the new port
    */
   public void setPort(String port) {
      this.port = port;
   }

  /**
    * Gets the prompt string
    * 
    * @return the prompt string
    */
   public String getPromptString() {
      return this.promptString;
   }

   /**
    * Sets the prompt string
    * @param promptString the new prompt string
    */
   public void setPromptString(String promptString) {
      this.promptString = promptString;
   }
   
   /**
    * Sets the timeOut
    * @param timeOut the new timeOut
    */   
   public void setTimeOut(String timeOut) {
      try {
         Integer tempValue = Integer.parseInt(timeOut.trim());
         if (tempValue > 0) {
            this.timeOut = tempValue;
         }
      }
      catch (NumberFormatException e) {
         logger.error("time out property in controller.xml is not an integer", e);
      }
   }
   
   /**
    * Gets the timeOut
    * @return the timeOut
    */
   public String getTimeOut() {
      return this.timeOut.toString();  
   }

   public EnumGatewayConnectionState connect() {
      EnumGatewayConnectionState responseState = EnumGatewayConnectionState.DISCONNECTED;
      this.telnetClient = new TelnetClient();
      try {
         this.telnetClient.connect(getIp(), Integer.parseInt(getPort()));
         super.inputStream = this.telnetClient.getInputStream();
         super.outputStream = this.telnetClient.getOutputStream();
         if (super.inputStream != null & super.outputStream != null) {
            responseState = EnumGatewayConnectionState.CONNECTED;
         }         
      }
      catch (Exception e) {
         logger.warn("Telnet protocol connection failed to connect. " + e.getMessage(), e);
      }
      return responseState;
   }
   
   public void disconnect() {
      super.inputStream = null;
      super.outputStream = null;
      try {
         this.telnetClient.disconnect();
      } catch (Exception e) {
         // Indicates that connection is already dead so ignore
      }
   }   

   public void send(String value, Map<String, String> args) {
      try {
         this.outputStream.write((value + "\n").getBytes());
         this.outputStream.flush();
      } catch (Exception e) {
         throw new GatewayProtocolException("Telnet protocol send failure: " + e.getMessage(), e);
      }
   }
      
   public String read(String value, Map<String, String> args) throws Exception {
      Integer timeOut = Integer.parseInt(getTimeOut());
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.MILLISECOND, timeOut);
      String readString = "";
      while (Calendar.getInstance().before(endTime)) {
         while (super.inputStream.available() != 0) {
            readString += (char) super.inputStream.read();
         }
         if (this.promptString != null && !"".equals(this.promptString)) {
            // Look for prompt string in response somewhere past first character
            // just in case response starts with prompt from previous command
            if (readString.indexOf(this.promptString) > 0) {
               break;
            }
         } else if (readString.length() > 0) {
            break;
         }
         Thread.sleep(50);
      }
      if (readString.length() == 0) {
           throw new GatewayProtocolException("Telnet protocol read timeout waiting for response");
      }
      return readString;
   }
   
   public void clearInputStream() {
      // Just read inputStream content but don't store it
      try {
         while (super.inputStream.available() != 0) {
            super.inputStream.read();
         }
      } catch (Exception e) {
         //Don't do anything about this
      }
   }
   
   public Boolean validateSendAction(String value, Map<String, String> args) {
      // Look for required send args and make sure value is appropriate for this protocol
      Boolean result = true;
      
      return result;
   }
}