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
import java.util.StringTokenizer;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.openremote.controller.gateway.Protocol;
import org.openremote.controller.gateway.ProtocolInterface;
import org.openremote.controller.component.EnumSensorType;
/**
 * The Telnet Protocol.
 * 
 * Author Rich Turner 2011-02-13
 */
public class TelnetProtocol extends Protocol {

   /** The logger. */
   private static Logger logger = Logger.getLogger(TelnetProtocol.class.getName());

   /** A name to identify event in controller.xml. */
   private String name;

   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private String port;
   
   private TelnetClient telnetClient;

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
    * Connects to the telnet server and establishes the input/output streams
    * Exception handling is done by the Gateway class
    */
   public void connect(int timeOut) throws Exception {
      telnetClient = new TelnetClient();
      this.telnetClient.setConnectTimeout(timeOut);
      this.telnetClient.connect(getIp(), Integer.parseInt(getPort()));
      super.inputStream = this.telnetClient.getInputStream();
      super.outputStream = this.telnetClient.getOutputStream();
   }

   /**
    * Disconnects from the telnet server and nulls the input/output streams
    * Exception handling is done by the Gateway class    
    */
   public void disconnect() throws Exception {
      super.inputStream = null;
      super.outputStream = null;
      this.telnetClient.disconnect();
   }   
   
   /**
    * Ue this method to validate the send command and args for this protocol
    */
   public Boolean validateSendAction(String value, Map<String, String> args) {
      // Look for required send args and make sure value is appropriate for this protocol
      Boolean result = true;
      return result;
   }
}