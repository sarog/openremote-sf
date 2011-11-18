/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.domintell;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.DomintellConfig;
import org.openremote.controller.protocol.domintell.model.DomintellModule;
import org.openremote.controller.protocol.domintell.model.RelayModule;
import org.openremote.controller.protocol.lutron.Dimmer;
import org.openremote.controller.protocol.lutron.GrafikEye;
import org.openremote.controller.protocol.lutron.Keypad;
import org.openremote.controller.protocol.lutron.LutronHomeWorksDeviceException;
import org.openremote.controller.protocol.lutron.MessageQueueWithPriorityAndTTL;

public class DomintellGateway {

   // Class Members --------------------------------------------------------------------------------

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   private static HashMap<String, DomintellModule> moduleCache = new HashMap<String, DomintellModule>();
   
   private static HashMap<String, Class<? extends DomintellModule>> moduleClasses = new HashMap<String, Class<? extends DomintellModule>>();

   // Don't ask this to the config factory when instantiating the bean, this
   // results in infinite recursion
   private DomintellConfig domintellConfig;

   private MessageQueueWithPriorityAndTTL<DomintellCommandPacket> queue = new MessageQueueWithPriorityAndTTL<DomintellCommandPacket>();

   private LoginState loginState = new LoginState();

   private DomintellConnectionThread connectionThread;
   
   static {
      moduleClasses.put("BIR", RelayModule.class);
   }
   
   public synchronized void startGateway() {
     if (domintellConfig == null) {
        domintellConfig = DomintellConfig.readXML();
       log.info("Got Domintell config");
       log.info("Address >" + domintellConfig.getAddress() + "<");
       log.info("Port >" + domintellConfig.getPort() + "<");
     }

     if (connectionThread == null) {
       // Starts some thread that has the responsibility to establish connection and keep it alive
       connectionThread = new DomintellConnectionThread();
       connectionThread.start();
     }
   }
   
   public void sendCommand(String command) {
      log.info("Asked to send command " + command);

      // Ask to start gateway, if it's already done, this will do nothing
      startGateway();
      queue.add(new DomintellCommandPacket(command));
    }

   /**
    * Gets the HomeWorks device from the cache, creating it if not already
    * present.
    * 
    * @param address
    * @return
    * @return
    * @throws LutronHomeWorksDeviceException 
    */
   public DomintellModule getDomintellModule(String moduleType, DomintellAddress address, Class<? extends DomintellModule> moduleClass) throws DomintellModuleException {      
     DomintellModule module = moduleCache.get(moduleType + address);
     if (module == null) {
       // No device yet in the cache, try to create one
       try {
         Constructor<? extends DomintellModule> constructor = moduleClass.getConstructor(DomintellGateway.class, String.class, DomintellAddress.class);
         module = constructor.newInstance(this, moduleType, address);
       } catch (SecurityException e) {
         throw new DomintellModuleException("Impossible to create device instance", moduleType, address, moduleClass, e);
       } catch (NoSuchMethodException e) {
         throw new DomintellModuleException("Impossible to create device instance", moduleType, address, moduleClass, e);
       } catch (IllegalArgumentException e) {
         throw new DomintellModuleException("Impossible to create device instance", moduleType, address, moduleClass, e);
       } catch (InstantiationException e) {
         throw new DomintellModuleException("Impossible to create device instance", moduleType, address, moduleClass, e);
       } catch (IllegalAccessException e) {
         throw new DomintellModuleException("Impossible to create device instance", moduleType, address, moduleClass, e);
       } catch (InvocationTargetException e) {
         throw new DomintellModuleException("Impossible to create device instance", moduleType, address, moduleClass, e);
       }
     }
     if (!(moduleClass.isInstance(module))) {
       throw new DomintellModuleException("Invalid device type found at given address", moduleType, address, moduleClass, null);
     }
     moduleCache.put(moduleType + address, module);
     return module;
   }

   private class DomintellConnectionThread extends Thread {

      DatagramSocket socket;
      
      private DomintellReaderThread readerThread;
      private DomintellWriterThread writerThread;

      @Override
      public void run() {
        if (socket == null) {
          while (!isInterrupted()) {
            try {
               socket = new DatagramSocket();
               
               // We should receive the clock update every minute from the Domintell master, so this should never timeout
               socket.setSoTimeout(60000);

               log.info("Trying to connect to " + domintellConfig.getAddress() + " on port " + domintellConfig.getPort());
              socket.connect(InetAddress.getByName(domintellConfig.getAddress()), domintellConfig.getPort());
              log.info("Socket connected");
              readerThread = new DomintellReaderThread(socket);
              readerThread.start();
              log.info("Reader thread started");
              writerThread = new DomintellWriterThread(socket);
              writerThread.start();
              log.info("Writer thread started");
              // Wait for the read thread to die, this would indicate the connection was dropped
              while (readerThread != null) {
                readerThread.join(1000);
                if (!readerThread.isAlive()) {
                  log.info("Reader thread is dead, clean and re-try to connect");
                  socket.disconnect();
                  readerThread = null;
                  writerThread.interrupt();
                  writerThread = null;
                }
              }
            } catch (SocketException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();

              // We could not connect, sleep for a while before trying again
              try {
                Thread.sleep(15000);
              } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }

            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
              // We could not connect, sleep for a while before trying again
              try {
                Thread.sleep(15000);
              } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }

            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }

        // For now do not support discovery, use information defined in config
      }

    }
   
   private class DomintellWriterThread extends Thread {

      private DatagramSocket socket;

      public DomintellWriterThread(DatagramSocket socket) {
        super();
        this.socket = socket;
      }

      @Override
      public void run() {

        log.info("Writer thread starting");

        while (!isInterrupted()) {
          synchronized (loginState) {
            while (!loginState.loggedIn) {
              try {
                while (!loginState.needsLogin && !loginState.loggedIn) {
                  log.info("Not logged in, waiting to be woken up");
                  // We're not logged in, wait until the reader thread ask to login and confirms we're logged in
                  loginState.wait();
                  log.info("Woken up on loggedIn, loggedIn: " + loginState.loggedIn + "- needsLogin: " + loginState.needsLogin);
                }
                if (!loginState.loggedIn) {
                  // We've been awakened and we're not yet logged in. It means we need to send login info
                   byte[] buf = "LOGIN".getBytes();
                   try {
                      DatagramPacket p = new DatagramPacket(buf, buf.length, InetAddress.getByName(domintellConfig.getAddress()), domintellConfig.getPort());
                     socket.send(p);
                  } catch (IOException e) {
                     log.warn("Could not send LOGIN packet");
                  }
                   loginState.needsLogin = false;
                  log.info("Sent log in info");
                }
                // We've been awakened and we're logged in, we'll just go out of the loop and proceed with normal execution
              } catch (InterruptedException e) {
                // We'll loop and test again for login
              }
            }
          }
          DomintellCommandPacket cmd = queue.blockingPoll();
          if (cmd != null) {
            log.info("Sending >" + cmd.toString() + "< on socket");
            byte[] buf = cmd.toString().getBytes();
            try {
               DatagramPacket p = new DatagramPacket(buf, buf.length, InetAddress.getByName(domintellConfig.getAddress()), domintellConfig.getPort());
               socket.send(p);
            } catch (IOException e) {
               log.warn("Could not send packet >" + cmd.toString() + "<");
            }
          }
        }
      }

   }

   private class DomintellReaderThread extends Thread {
      private DatagramSocket socket;

      public DomintellReaderThread(DatagramSocket socket) {
        super();
        this.socket = socket;
      }

      @Override
      public void run() {
        log.info("Reader thread starting");
        // We're starting, login
        synchronized (loginState) {
           // If we though we were already logged in, reset that
           loginState.loggedIn = false;
           loginState.needsLogin = true;
           // Notify writer thread to send login
           loginState.notify();
        }

        do {
          try {
             byte[] buffer = new byte[256];
             DatagramPacket p = new DatagramPacket(buffer, buffer.length);
             socket.receive(p);
             String packetText = new String(p.getData(), 0, p.getLength(), "ISO-8859-1");
            log.info("Reader thread got packet >" + packetText + "<");
            if (packetText.startsWith("INFO:Session opened:INFO")) {
              synchronized (loginState) {
                loginState.loggedIn = true;
                loginState.invalidLogin = false;
                // We're logged in, notify writer thread to start sending messages
                loginState.notify();
              }
            } /*else if (line.startsWith("LOGIN:")) {
              log.info("Asked to login, wakening writer thread");
              synchronized (loginState) {
                // If we though we were already logged in, reset that
                loginState.loggedIn = false;

                loginState.needsLogin = true;
                // System asks for login, notify writer thread to send it
                loginState.notify();
              }
            } */else if (packetText.startsWith("INFO:Auth failed:INFO")) {
              // TODO: close the connection, nothing we can do
              synchronized (loginState) {
                loginState.loggedIn = false;
                loginState.invalidLogin = true;
              }
            } else if (packetText.startsWith("INFO:Access denied. Close current session:INFO")) {
              synchronized (loginState) {
                loginState.loggedIn = false;
              }
              // Get out of our read loop, this will terminate the thread
              break;
            } else {
               // First 3 chars in module type
               // Next 6 is address
               // Then 1 char is "type of data"

               // Note that time / date info can also be reported
               String moduleType = packetText.substring(0, 3);
               String address = packetText.substring(3, 9);

               log.info("Module type " + moduleType + " address " + address);
               log.info("Module classes " + moduleClasses);
               try {
                  Class<? extends DomintellModule> moduleClass = moduleClasses.get(moduleType);
                  log.info("Module class " + moduleClass);
                  if (moduleClass != null) {
                     DomintellModule module = getDomintellModule(moduleType, new DomintellAddress("0x" + address), moduleClass);                     
                    if (module != null) {
                       module.processUpdate(packetText.substring(9).trim());
                    }
                  }
               } catch (DomintellModuleException e) {
                  log.error("Impossible to get module", e);
               }
              } /*else {
                // Unknown response
                // TODO
              }*/
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } while (!isInterrupted());
      }

   }
   
   private class DomintellCommandPacket {
   
      private String command;

      public DomintellCommandPacket(String command) {
        this.command = command;
      }

      public String toString() {
         return command;
      }
   }
   
   private class LoginState {

      /**
       * Indicates that we must send the login information.
       */
      public boolean needsLogin;

      /**
       * Indicates if we're logged into the system, if not commands must be queued.
       */
      public boolean loggedIn;

      /**
       * Indicates if we tried logging in and been refused the login, if so do not try again.
       * TODO: there must be a way to reset this.
       */
      public boolean invalidLogin;
    }
}
