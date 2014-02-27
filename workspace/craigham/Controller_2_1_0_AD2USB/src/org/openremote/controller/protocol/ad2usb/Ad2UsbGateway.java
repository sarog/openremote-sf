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
package org.openremote.controller.protocol.ad2usb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;
import org.openremote.controller.Constants;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.ad2usb.model.ArmedStatus;
import org.openremote.controller.protocol.ad2usb.model.Partition;
import org.openremote.controller.protocol.ad2usb.model.SecuritySystem;
import org.openremote.controller.protocol.ad2usb.model.SecuritySystem.ChimeStatus;
import org.openremote.controller.protocol.dscit100.PanelState.AlarmState;
import org.openremote.controller.protocol.lutron.MessageQueueWithPriorityAndTTL;
import org.openremote.controller.utils.Logger;

public class Ad2UsbGateway {
   private final static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ad2usb");

   private static final int TELNET_TIMOUT = 10000;
   private static final int COMMUNICATION_ERROR_RETRY_DELAY = 15000;

   private AD2USBConnectionThread connectionThread;
   private MessageQueueWithPriorityAndTTL<KeyPressCommand> mQueue = new MessageQueueWithPriorityAndTTL<KeyPressCommand>();

   private String mHostName;

   private int mPortNumber;

   private SecuritySystem mSecuritySystem = null;

   public Ad2UsbGateway(String hostName, int portNumber) {
      mHostName = hostName;
      mPortNumber = portNumber;
   }

   public synchronized void startGateway() {

      if (connectionThread == null) {
         // Starts some thread that has the responsibility to establish connection and keep it alive
         connectionThread = new AD2USBConnectionThread();
         connectionThread.start();
      }
   }

   public void sendCommand(KeyPressCommand command) {
      log.info("Asked to send command " + command);

      // Ask to start gateway, if it's already done, this will do nothing
      startGateway();
      mQueue.add(command);
   }

   private class AD2USBConnectionThread extends Thread {

      private AD2USBReaderThread readerThread;
      private AD2USBWriterThread writerThread;

      @Override
      public void run() {
         String address = mHostName;
         int connectPort = mPortNumber;
         TelnetClient tc = new TelnetClient();
         tc.setConnectTimeout(TELNET_TIMOUT);
         while (!isInterrupted()) {
            try {
               log.info("Trying to connect to " + address + " on port " + connectPort);
               tc.connect(address, connectPort);
               log.info("Telnet client connected");
               readerThread = new AD2USBReaderThread(tc.getInputStream());
               readerThread.start();
               log.info("Reader thread asked to start");
               writerThread = new AD2USBWriterThread(tc.getOutputStream());
               writerThread.start();
               log.info("Writer thread asked to start");
               // Wait for the read thread to die, this would indicate the connection was dropped
               while (readerThread != null) {
                  readerThread.join(1000);
                  if (!readerThread.isAlive()) {
                     log.info("Reader thread is dead, clean and re-try to connect");
                     tc.disconnect();
                     readerThread = null;
                     writerThread.interrupt();
                     writerThread = null;
                  }
               }
            } catch (SocketException e) {
               log.error("Connection to Ad2Usb impossible, sleeping and re-trying later", e);
               // We could not connect, sleep for a while before trying again
               try {
                  Thread.sleep(COMMUNICATION_ERROR_RETRY_DELAY);
               } catch (InterruptedException e1) {
                  log.warn("Interrupted during our sleep", e1);
               }
            } catch (IOException e) {
               log.error("Connection to Ad2Usb impossible, sleeping and re-trying later", e);
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
      }

   }

   private class AD2USBWriterThread extends Thread {

      private OutputStream os;

      public AD2USBWriterThread(OutputStream os) {
         super();
         this.os = os;
      }

      @Override
      public void run() {

         log.info("Writer thread starting");
         PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));
         while (!isInterrupted()) {
            KeyPressCommand cmd = mQueue.blockingPoll();
            if (cmd != null) {
               log.info("Sending >" + cmd.getCommandOutput() + "< on print writer " + pr);
               pr.println(cmd.getCommandOutput() + "\n");
               pr.flush();
            }
         }
      }
   }

   // ---
   private class AD2USBReaderThread extends Thread {

      private InputStream is;

      public AD2USBReaderThread(InputStream is) {
         super();
         this.is = is;
      }

      @Override
      public void run() {
         log.info("Reader thread starting");
         log.debug("TC input stream " + is);
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         log.debug("Buffered reader " + br);

         String line = null;
         try {
            line = br.readLine();
         } catch (IOException e1) {
            log.warn("Could not read from Ad2Usb", e1);
         }
         do {
            try {
               log.debug("Reader thread got line >" + line + "<");
               // Try parsing the line as a feedback / response from the system
               if (line.startsWith("[")) {
                  Ad2UsbUpdateParser parser = new Ad2UsbUpdateParser(line);
                  if (parser.fields != null && parser.fields.length > 2) {
                     if (parser.fields[3].contains("*DISARMED*")) {
                        // let's update security panel property
                        if (parser.fields[3].contains("FAULTED")) {
                           getSecuritySystem().setArmedStatus(ArmedStatus.DISARMED_READY);
                           updateArmedStatusChangedListeners(ArmedStatus.DISARMED_READY);
                        } else {
                           getSecuritySystem().setArmedStatus(ArmedStatus.DISARMED_FAULTS);
                           updateArmedStatusChangedListeners(ArmedStatus.DISARMED_FAULTS);
                        }
                     } else if (parser.fields[3].contains("*ARMED*")) {
                        log.debug("Double check if this is correct, in Ad2UsbGateway read thread");
                        getSecuritySystem().setArmedStatus(ArmedStatus.ARMED);
                        updateArmedStatusChangedListeners(ArmedStatus.ARMED);
                     }

                     if (mSensor != null) {
                        mSensor.update(parser.fields[3]);
                     }
                  }
               }
               line = br.readLine();
            } catch (IOException e) {
               log.warn("Could not read from Ad2Usb", e);
            }
         } while (line != null && !isInterrupted());
      }
   }

   private Sensor mSensor = null;

   public void registerSensor(Sensor sensor) {
      mSensor = sensor;
   }

   public SecuritySystem getSecuritySystem() {
      if (mSecuritySystem == null) mSecuritySystem = new SecuritySystem();
      return mSecuritySystem;
   }

   public void removeSensor(Sensor sensor) {
      mSensor = null;

   }

   private List<Sensor> mAlarmStatusListeners = new ArrayList<Sensor>();

   public void addArmedStatusChangedListener(Sensor sensor) {
      mAlarmStatusListeners.add(sensor);
      sensor.update(mSecuritySystem.getArmedStatus().toString());
   }

   public void removeArmedStatusChangedListener(Sensor sensor) {
      mAlarmStatusListeners.remove(sensor);
   }

   private void updateArmedStatusChangedListeners(ArmedStatus newStatus) {
      for (Sensor sensor : mAlarmStatusListeners) {
         sensor.update(newStatus.toString());

      }
   }
}
