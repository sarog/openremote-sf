/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.net.telnet.TelnetClient;
import org.openremote.controller.OpenRemoteRuntime;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

/**
 * TODO
 * 
 * @author Marcus 2009-4-26
 * @author Rich Turner 2011-2-5
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TelnetCommand implements ExecutableCommand, EventListener, Runnable {

   private static Logger logger = Logger.getLogger(TelnetCommandBuilder.TELNET_PROTOCOL_LOG_CATEGORY);

   /** The default timeout used to wait for a result (in seconds) */
   public final static int DEFAULT_TIMEOUT = 1;

  /**
   * The default polling interval (in milliseconds)
   */
  public final static int DEFAULT_POLLING_INTERVAL = 2000;

   /**
    * A pipe separated list of command strings that are sent over the connection It must have the format
    * <waitFor>|<send>|<waitFor>|<send>
    */
   private String command;

   /** The IP to which the socket is opened */
   private String ip;

   /** The port that is opened */
   private Integer port;

   /** The response from read command */
   private String response;

   /** The response filter */
   private String responseFilter;
   
   /** The response filter group */
   private Integer responseFilterGroup = 0;
   
   /** The status default response */
   private String statusDefault;
   
   /** The wait timeout period in seconds */
   private Integer timeOut = DEFAULT_TIMEOUT;
   
   /** The polling interval which is used for the sensor update thread (in milliseconds) */
   private Integer pollingInterval = DEFAULT_POLLING_INTERVAL;

   /** The thread that is used to peridically update the sensor */
   private Thread pollingThread;
   
   /** The sensor which is updated */
   private Sensor sensor;
   
   /** Boolean to indicate if polling thread should run */
   boolean doPoll = false;
   
   
   


   
  public TelnetCommand(String command, String ip, Integer pollingInterval, Integer port,
                       String responseFilter, Integer responseFilterGroup, String statusDefault,
                       Integer timeOut)
  {
    this.command = command;
    this.ip = ip;
    this.port = port;
    this.responseFilter = responseFilter;
    this.responseFilterGroup = responseFilterGroup;
    this.statusDefault = statusDefault;

    if (pollingInterval != null && pollingInterval > 0)
    {
      this.pollingInterval = pollingInterval;
    }

    if (timeOut != null && timeOut > 0)
    {
      this.timeOut = timeOut;
    }
  }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      send(false);
   }

   public void send(boolean readResponse) {
      TelnetClient tc = null;
      if (readResponse) {
         response = "";
      }
      try {
         tc = new TelnetClient();
         tc.connect(ip, port);
         StringTokenizer st = new StringTokenizer(command, "|");
         int count = 0;
         if(command.startsWith("|")) {
            count++;
         }
         String waitFor = "";
         while (st.hasMoreElements()) {
            String cmd = (String) st.nextElement();
            if (count % 2 == 0) {
               waitFor = cmd;
               if (!"null".equals(cmd)) {
                  waitForString(cmd, tc);
               }
            }

            else
            {
              sendString(cmd, tc);

              if (readResponse)
              {
                // TODO :
                //   ORCJAVA-327 - should always wait for 'null' string in readString() ?
                //   It's an arbitrary state response, we never know the value in advance.
                //   It's unclear if this change will impact existing users, should only
                //   be added with a configurable 'backwards-compatibility' flag.

                readString(waitFor, tc);
              }
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
      endTime.add(Calendar.SECOND, timeOut);

      while (sb.toString().indexOf(s) == -1) {
         while (Calendar.getInstance().before(endTime) && is.available() == 0) {
            Thread.sleep(250);
         }
         if (is.available() == 0) {
            logger.info("WaitForString read before running into timeout: " + sb.toString());
            throw new Exception("waitForString response timed-out waiting for \"" + s + "\"");
         }
         sb.append((char) is.read());
      }

      logger.info("WaitForString received: " + sb.toString());
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

   private void readString(String s, TelnetClient tc) throws Exception {
      InputStream is = tc.getInputStream();
      StringBuffer sb = new StringBuffer();
      Calendar endTime = Calendar.getInstance();
      endTime.add(Calendar.SECOND, timeOut);
      while (sb.toString().indexOf(s) == -1) {
         while (Calendar.getInstance().before(endTime) && is.available() == 0) {
            Thread.sleep(250);
         }
         if (is.available() == 0) {
            if ("null".equals(s)) {
               break;
            }
            logger.info("Read before running into timeout: " + sb.toString());
            throw new Exception("Response timed-out waiting for \"" + s + "\"");
         }
         sb.append((char) is.read());
      }
      logger.info("received: " + sb.toString());

     // TODO
     //        The addition of linefeed character to response values seems odd and there's
     //        no obvious reason why it is being done here. The issue ORCJAVA-326 (parsing
     //        return value 'on' to Java boolean true) fails because of this and the fix
     //        in ORCJAVA-326 trims this extra linefeed. Not dropping the linefeed here yet,
     //        since unclear whether it was added for a reason but it should be considered
     //        for removal once the implementation is stable with proper tests to back it up. [JPL]
     //

      this.response += sb.toString() + "\n";
   }

  @Override public void setSensor(Sensor sensor)
  {
    logger.debug("Initializing sensor for telnet protocol: " + sensor);

    if (pollingInterval == null)
    {
      logger.warn(
        "Polling interval was not set. Falling back to default value of {0} milliseconds",
        DEFAULT_POLLING_INTERVAL
      );

      pollingInterval = DEFAULT_POLLING_INTERVAL;
    }

    this.sensor = sensor;
    this.doPoll = true;

    pollingThread = OpenRemoteRuntime.createThread(
        "Telnet Polling thread for sensor: " + sensor.getName(), this
    );

    pollingThread.start();
  }

  @Override public void stop(Sensor sensor)
  {
    this.doPoll = false;
  }
   
   @Override
   public void run() {
      logger.debug("Sensor thread started for sensor: " + sensor);
      while (doPoll) {
         logger.debug("Executing status command for sensor: " + sensor);
   	   String filteredResponse = "";
   	   send(true);
   	   logger.debug("Telnet status command received value: " + response);
   	   try {
      	   if ("".equals(responseFilter) || responseFilter == null) {
         	   filteredResponse = response;
      	   } else {
               Pattern p = Pattern.compile(responseFilter, Pattern.CANON_EQ | Pattern.UNICODE_CASE);
               Matcher m = p.matcher(response);
               boolean b = m.matches();
               if (b) {
                  String matchedGroup = m.group(responseFilterGroup);
                  if (matchedGroup != null) {
                     filteredResponse = matchedGroup;
                  }
               } else {
                  filteredResponse = statusDefault;
                  logger.warn("Telnet Read Status: No Match using Regex: '" + responseFilter + "' on response from command '" + command + "'");
               } 
            }            
         }
         catch (PatternSyntaxException e) {
            logger.error("Telnet Read Status: Invalid filter expression", e);
         }
         
         logger.debug("Telnet status command value after regex: " + filteredResponse);

         if (!"".equals(filteredResponse))
         {
           if (sensor instanceof SwitchSensor)
           {
             filteredResponse = filteredResponse.toLowerCase().trim().replaceAll("1|on", "true");

             Boolean bool = Boolean.parseBoolean(filteredResponse);

             if (bool)
             {
               logger.debug("Parsed telnet response '" + filteredResponse + "' to 'on' and updating switch sensor.");

               sensor.update("on");
             }

             else
             {
               logger.debug("Parsed telnet response '" + filteredResponse + "' to 'off' and updating switch sensor.");

               sensor.update("off");
             }
           }

           else
           {
             logger.debug(
                 "Telnet status command updating sensor '" + sensor.getName() + "' (ID: " +
                 sensor.getSensorID() + ")  with value '" + filteredResponse + "'"
             );

             sensor.update(filteredResponse);
           }
         }

         else
         {
           logger.debug(
               "An empty response value was received. Telnet status command updating sensor '" +
               sensor.getName() + "' (ID: " + sensor.getSensorID() + ") with value 'N/A'."
           );

           sensor.update("N/A");
         }


         try {
            Thread.sleep(pollingInterval); // We wait for the given pollingInterval before requesting URL again
         } catch (InterruptedException e) {
            doPoll = false;
            pollingThread.interrupt();
         }
      }
      logger.debug("*** Out of run method: " + sensor);
   }

   
   
   // Getters used by testsuite -----------------------------------------------------------------------
   
   public String getCommand() {
      return command;
   }

   public String getIp() {
      return ip;
   }

   public Integer getPort() {
      return port;
   }
   
   
   
}
