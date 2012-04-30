/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.shellexe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;

public class ShellExeCommand implements ExecutableCommand, EventListener, Runnable {

   /** The logger. */
   private static Logger logger = Logger.getLogger(ShellExeCommandBuilder.SHELLEXE_PROTOCOL_LOG_CATEGORY);

   /** The full path to executable that should be started */
   private String commandPath;

   /** The params that should be attached to the executable */
   private String commandParams;
   
   /** The regex which is used to extract sensor data from received result */
   private String regex;

   /** The polling interval which is used for the sensor update thread */
   private Integer pollingInterval;

   /** The thread that is used to peridically update the sensor */
   private Thread pollingThread;
   
   /** The sensor which is updated */
   private Sensor sensor;
   
   /** Boolean to indicate if polling thread should run */
   boolean doPoll = false;
   
   /**
    * ShellExeCommand is a protocol to start shell scripts on the controller
    * 
    * @param commandPath
    * @param commandParams
    */
   public ShellExeCommand(String commandPath, String commandParams, String regex, Integer pollingInterval) {
      this.commandPath = commandPath;
      this.commandParams = commandParams;
      this.regex = regex;
      this.pollingInterval = pollingInterval;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      executeCommand();
   }

   @Override
   public void setSensor(Sensor sensor)
   {
     logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
     if (pollingInterval == null) {
       throw new RuntimeException("Could not set sensor because no polling interval was given");
     }
     this.sensor = sensor;
     this.doPoll = true;
     pollingThread = new Thread(this);
     pollingThread.setName("Polling thread for sensor: " + sensor.getName());
     pollingThread.start();
   }

   @Override
   public void stop(Sensor sensor)
   {
     this.doPoll = false;
   }
   
   private String executeCommand() {
      logger.debug("Will start shell command: " + commandPath + " and use params: " + commandParams);
      String result = "";
      try {
         Process proc = null;
         if (commandParams == null) {
            proc = Runtime.getRuntime().exec(new String[] { commandPath });
         } else {
            proc = Runtime.getRuntime().exec(new String[] { commandPath, commandParams });
         }
         BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         StringBuffer resultBuffer = new StringBuffer();
         for (String tmp = reader.readLine(); tmp != null; tmp = reader.readLine()) {
            resultBuffer.append(tmp);
         }
         result = resultBuffer.toString();
      } catch (IOException e) {
         logger.error("Could not execute shell command: " + commandPath, e);
      }
      logger.debug("Shell command: " + commandPath + " returned: " + result);
      return result;
   }
   
   @Override
   public void run() {
      logger.debug("Sensor thread started for sensor: " + sensor);
      while (doPoll) {
         String readValue = this.executeCommand();
         if (regex != null) {
           Pattern regexPattern = Pattern.compile(regex);
           Matcher matcher = regexPattern.matcher(readValue);
           if (matcher.find()) {
             String result = matcher.group();
             logger.info("result of regex evaluation: " + result);
             sensor.update(result);
           } else {
             logger.info("regex evaluation did not find a match");
             sensor.update("N/A");
           }
         } else {
           sensor.update(readValue);
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
}
