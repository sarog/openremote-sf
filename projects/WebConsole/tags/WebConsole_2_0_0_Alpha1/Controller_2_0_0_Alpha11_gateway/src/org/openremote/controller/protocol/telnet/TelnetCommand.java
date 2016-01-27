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
package org.openremote.controller.protocol.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * The Telnet Event.
 * 
 * @author Marcus 2009-4-26
 */
public class TelnetCommand implements ExecutableCommand, StatusCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(TelnetCommand.class.getName());

   /** The default timeout used to wait for a result */
   public static final int DEFAULT_TIMEOUT = 1;

   /** A name to identify event in controller.xml. */
   private String name;

   /**
    * A pipe separated list of command strings that are sent over the connection It must have the format
    * <waitFor>|<send>|<waitFor>|<send>
    */
   private String command;

   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private String port;

   /**
    * Gets the command.
    * 
    * @return the command
    */
   public String getCommand() {
      return command;
   }

   /**
    * Sets the command.
    * 
    * @param command
    *           the new command
    */
   public void setCommand(String command) {
      this.command = command;
   }

   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
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
      return ip;
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
      return port;
   }

   /**
    * Sets the port 
    * @param port the new port
    */
   public void setPort(String port) {
      this.port = port;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      TelnetClient tc = null;
      try {
         tc = new TelnetClient();
         tc.connect(getIp(), Integer.parseInt(getPort()));
         StringTokenizer st = new StringTokenizer(getCommand(), "|");
         int count = 0;
         while (st.hasMoreElements()) {
            String cmd = (String) st.nextElement();
            if (count % 2 == 0) {
               waitForString(cmd, tc);
            } else {
               sendString(cmd, tc);
            }
            count++;
         }
      } catch (Exception e) {
         logger.error("could not perform telnetEvent", e);
      } finally {
         if (tc != null) {
            try {
               tc.disconnect();
            } catch (IOException e) {
               logger.error("could not disconnect from telnet", e);
            }
         }
      }
   }

   /**
    * Read from the telnet session until the string we are waiting for is found or the timeout has been reached. The
    * timeout is 1 second
    * 
    * @param s The string to wait on
    * @param tc The instance of the TelnetClient
    */
   private void waitForString(String s, TelnetClient tc) throws Exception {
      InputStream is = tc.getInputStream();
      StringBuffer sb = new StringBuffer();
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.SECOND, DEFAULT_TIMEOUT);
      while (sb.toString().indexOf(s) == -1) {
         while (Calendar.getInstance().before(endTime) && is.available() == 0) {
            Thread.sleep(250);
         }
         if (is.available() == 0) {
            logger.info("Read before running into timeout: " + sb.toString());
            throw new Exception("Response timed-out waiting for \"" + s + "\"");
         }
         sb.append((char) is.read());
      }
      logger.info("received: " + sb.toString());
   }

   /**
    * Write this string to the telnet session.
    * 
    * @param the string to write
    * @param tc The instance of the TelnetClient
    */
   private void sendString(String s, TelnetClient tc) throws Exception {
      OutputStream os = tc.getOutputStream();
      os.write((s + "\n").getBytes());
      logger.info("send: " + s);
      os.flush();
   }

   @Override
   public String read(EnumSensorType sensoryType, Map<String, String> statusMap) {
      // TODO Auto-generated method stub
      return null;
   }

}
