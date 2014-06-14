/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.cbus;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.CbusConfig;

/**
 * Represents the gateway to the CGate application, which directly accesses CBus. This code is based on the existing Lutron OpenRemote protocol.
 * 
 * @author Jamie Turner
 *
 */
public class CBusGateway
{

   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);

   /**
    * The number times to retry a command
    */
   protected static final int COMMAND_RETRIES = 5;

   /**
    * The interval in ms between retries
    */
   protected static final int RETRY_INTERVAL_MSECS = 500;


   /**
    * The CBus system linked to this gateway
    */
   private static CBusSystem system = null;

   /**
    * The thread handling the command interface to CGate
    */
   protected CBusConnectionThread connectionThread;

   /**
    * The thread handling the status interface to CGate
    */
   protected CBusConnectionThread statusConnectionThread;

   /**
    * The thread handling the event interface to CGate
    */
   protected CBusConnectionThread eventConnectionThread;

   /**
    * The CBus CGate configuration   
    */
   protected CbusConfig config;

   /**
    * Message queue for CBus commands
    */
   protected CBusCommandQueue<CGateDeviceCommand> queue = new CBusCommandQueue<CGateDeviceCommand>();

   /**
    * Start the gateway
    */
   public synchronized void startGateway() {
      initialiseConfig();

      try
      {
         //initialise system
         this.getSystem().initialiseProjects(config.getProjectFile());
         

         if (connectionThread == null) 
         {
            // Starts some thread that has the responsibility to establish connection and keep it alive
            connectionThread = new CBusConnectionThread();
            connectionThread.name = "CBUS Main Port";
            connectionThread.hasWriterThread = true;
            connectionThread.port = config.getPort();
            addInitialCommandsToWriteQueue();
            connectionThread.start();
         }

         if (statusConnectionThread == null)
         {
            // Starts some thread that has the responsibility to establish connection and keep it alive
            statusConnectionThread = new CBusConnectionThread();
            statusConnectionThread.name = "CBUS Status Port";
            statusConnectionThread.hasWriterThread = false;
            statusConnectionThread.port = config.getStatusPort();
            statusConnectionThread.start();
         }

         if (eventConnectionThread == null) 
         {
            // Starts some thread that has the responsibility to establish connection and keep it alive
            eventConnectionThread = new CBusConnectionThread();
            eventConnectionThread.name = "CBUS Event Port";
            eventConnectionThread.hasWriterThread = false;
            eventConnectionThread.port =  config.getEventPort();
            eventConnectionThread.start();
         }
      }
      catch(CBusException cbe)
      {
         log.error("**CBUS ERROR** CBus gateway could not be started using project file: " + config.getProjectFile() + ", REASON: " + cbe);
      }

   }

   /**
    * Get the initial configuration
    */
   private synchronized void initialiseConfig()
   {
      if (config == null) 
      {
         config = CbusConfig.readXML();
         // Check config, report error if any
         log.info("Got CBUS config");
         log.info("IP Address >" + config.getAddress() + "<");
         log.info("Port >" + config.getPort() + "<");
         log.info("Event Port >" + config.getEventPort() + "<");
         log.info("Status Port >" + config.getStatusPort() + "<");
         log.info("Project File >" + config.getProjectFile() + "<");

      }
   }
   
   /**
    * adds commands to the queue to start the CBUS project. These commands are not required if CGate automatically
    * starts a project
    */
   private synchronized void addInitialCommandsToWriteQueue()
   {
      queue.add(new CGateDeviceCommand("PROJECT USE " + getSystem().getProjectToRun()));
      queue.add(new CGateDeviceCommand("PROJECT START"));
   }

   /**
    * Send a command to CGate
    * @param command
    *           The command string to send
    */
   public void sendCommand(String command)
   {
      log.info("sendCommand called");

      // Ask to start gateway, if it's already done, this will do nothing
      startGateway();
      queue.add(new CGateDeviceCommand(command));
   }  


   /**
    * Get the current CBus system associated with the gateway
    * @return A CBus system
    */
   public synchronized CBusSystem getSystem() 
   {
      if(system == null)
      {
         initialiseConfig();            
         system = new CBusSystem(this);
         try
         {
            system.initialiseProjects(config.getProjectFile());
         }
         catch(CBusException cbe)
         {
            log.error("**CBUS ERROR** Could not initialise project using project file: " + config.getProjectFile() + ", REASON: " + cbe);
         }
       }

      return system;
   }
   
   
   /**
    * A connection thread for communicating with CGate
    * @author Jamie Turner
    *
    */
   private class CBusConnectionThread extends Thread 
   {

      /**
       * Thread to receive messages from CGate      
       */
      protected CGateReaderThread readerThread;

      /**
       * Thread to write messages to CBus
       */
      protected CGateWriterThread writerThread;

      /**
       * Port to read/write 
       */
      private int port;

      /**
       * Whether a thread should be created to write data - false if we only want to listen on the port
       */
      private boolean hasWriterThread = false;

      /**
       * Name of the thread for logging purposes
       */
      private String name;

      /**
       * The socket to read/write
       */
      private Socket sock = null;

      public void run() 
      {


         while (!isInterrupted()) 
         {
            try 
            {

               log.info("Trying to connect to " + name + " at " + config.getAddress() + " on port " + port);

               sock = new Socket();
               sock.connect(new InetSocketAddress(config.getAddress(), port), 5000); //times out pretty quickly
               log.info(name + " socket connected");
               readerThread = new CGateReaderThread(sock, log);
               readerThread.start();
               log.info("Reader thread for " + name + " started");
               if(hasWriterThread)
               {
                  writerThread = new CGateWriterThread(sock, log);
                  writerThread.start();
                  log.info("Writer thread for " + name + " started");
               }
               else
                  log.info(name + " has no writer thread");


               // Wait for the read thread to die, this would indicate the connection was dropped
               while (readerThread != null) 
               {
                  readerThread.join(1000);
                  if (!readerThread.isAlive()) 
                  {
                     log.info("Reader thread for " + name + " is dead, clean and re-try to connect");
                     sock.close();
                     readerThread = null;
                     if(hasWriterThread)
                     {
                        writerThread.interrupt();
                        writerThread = null;
                     }
                  }

                  //turn off any expired pulse commands if we are in the main thread
                  if(hasWriterThread)
                     getSystem().turnOffExpiredPulses();
               }
            } 
            catch (SocketException e) 
            {
               e.printStackTrace();
               log.error("SocketException occurred for " + name + ": " + e.getMessage());

               // We could not connect, sleep for a while before trying again
               try 
               {
                  Thread.sleep(15000);
               } 
               catch (InterruptedException e1) 
               {
                  e1.printStackTrace();
               }

            } 
            catch (IOException e) 
            {
               e.printStackTrace();
               log.error("IOException occurred for " + name + ": " + e.getMessage());
               // We could not connect, sleep for a while before trying again
               try 
               {
                  Thread.sleep(15000);
               } 
               catch (InterruptedException e1) 
               {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
               }

            } 
            catch (InterruptedException e) 
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }

      }

      /**
       * The thread that writes to the CGate port
       * 
       * @author Jamie Turner
       *
       */
      protected class CGateWriterThread extends Thread 
      {
         /**
          * The logger
          */
         private Logger log;

         /**
          * The socket the main controlling thread has opened
          */
         protected Socket sock;
         
         
         /**
          * Constructor
          * 
          * @param socket
          *           The socket from the parent connection thread
          * @param log
          *           The logger to use
          */
         public CGateWriterThread(Socket sock, Logger log) 
         {
            super();
            this.sock = sock;
            this.log = log;
         }


         @Override
         public void run() 
         {
            log.info("Writer thread starting");

            try
            {
               if(sock == null)
                  throw new Exception("Socket is null");
               
               OutputStream os = sock.getOutputStream();
               PrintWriter pr = new PrintWriter(os, true);

               while (!isInterrupted()) 
               {
                  CGateDeviceCommand cmd = queue.blockingPoll();
                  if (cmd != null && sock != null && !sock.isClosed() && sock.isConnected()) 
                  {
                     log.debug("Sending >" + cmd.toString() + "< to CGate");
                     pr.print(cmd.toString() + cmd.getCommandTerminator());
                     pr.flush();                   
                  }
               }
            }
            catch(Exception e)
            {
               log.error("*CBUS ERROR* Error occurred in writer thread " + this.getName() + ": " + e.getStackTrace()[0]);
            }
            
            //close the stream if we're exiting
            try
            {
               if(sock != null && sock.getOutputStream() != null)
                  sock.getOutputStream().close();
            }
            catch(Exception ex)
            {
               //do nothing
            }
            
            log.info("CBus writer thread exiting");
         }

      }

      /**
       * The thread for reading from a CGate port
       * 
       * @author Jamie Turner
       *
       */
      protected class CGateReaderThread extends Thread {

         /**
          * The logger
          */
         private Logger log;

         /**
          * The socket the main controlling thread has opened
          */
         protected Socket sock;

         /**
          * Constructor
          * 
          * @param is
          *           The input stream to read from
          *           
          */
         public CGateReaderThread(Socket sock, Logger log) 
         {
            super();
            this.sock = sock;
            this.log = log;
         }

         @Override
         public void run() 
         {
            log.debug("Reader thread starting");

            

            String line = null;
            BufferedReader br = null;
            try 
            {
               br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
               if(br == null || sock == null || sock.isClosed() || !sock.isConnected())
                  line = null;
               else
                  line = br.readLine();
            } 
            catch (Exception e1) 
            {
               log.error("*CBUS ERROR*: I/O Error while reading line from ip stream: " + e1 + " at " + e1.getStackTrace()[0]);
               
            }
            do 
            {
               try 
               {
                  log.debug("Reader thread got line >" + line + "<" + " from CGate");

                  // Try parsing the line as a feedback / response from the system
                  CGateBaseResponse response = parseResponse(line);
                  if(response != null)
                  {
                     try
                     {
                        actOnResponse(response);                         
                     }
                     catch(Exception ex)
                     {
                        log.error("*CBUS ERROR* An exception occurred while handling a device response: " + ex.getMessage(), ex);
                     }
                  }                  

                  if(br == null || sock == null || sock.isClosed() || !sock.isConnected())
                     line = null;
                  else
                     line = br.readLine();
               } 
               catch (Exception e) 
               {
                  // TODO Auto-generated catch block
                  log.error("*CBUS ERROR* An exception occurred reading from stream: " + e + " at " + e.getStackTrace()[0], e);
               }
            } 
            while (line != null && !isInterrupted());
            
            //close the stream if we're exiting
            try
            {
               if(sock != null && sock.getInputStream() != null)
                  sock.getInputStream().close();
            }
            catch(Exception ex)
            {
               //do nothing
            }
            
            log.warn("*CBUS WARNING* Reader thread exiting...");
         }

      }

   }

   /**
    * Act on a message received from CGate
    * 
    * @param resp
    *           The message received
    */
   protected void actOnResponse(CGateBaseResponse resp) 
   {
      if(resp == null)
         return;

      if(resp instanceof CGateStatusResponse)
         processStatusResponse((CGateStatusResponse) resp);
      else
         processMainResponse((CGateDeviceResponse) resp);
   }

   /**
    * Process a message received on the main CGate port
    * 
    * @param response
    *           The message received
    */
   private void processMainResponse(CGateDeviceResponse response)
   {
      if(response == null)
         return;

      if(!response.isHyphenatedResponse())
      {
         //ignore hyphenated (multi-line) responses

         //get the code - we only want to act on 2xx and 3xx responses and raise errors with 4xx and 5xx responses
         String code = response.getResponseCode();
         if(code.startsWith("4") || code.startsWith("5"))
         {
            //an error
            log.error("*CBUS ERROR* Error response received on main thread: " + response.fullResponse);
         }
         else if(code.startsWith("3") || code.startsWith("7"))
         {
            log.debug("2xx or 3xx response received on main thread");
            if(code.equals("300") || code.equals("701")) //300|701 //TURNER/254/56/0: level=0
            {
               String address = response.getNonCodeParameter(1);
               if(address != null && address.length() > 0)
               {
                  log.debug("300/701 address parameter=" + address);
                  if(address.endsWith(":"))
                     address = address.substring(0, address.length() - 1);

                  String stateNVP = response.getNonCodeParameter(2);
                  log.debug("300/701 state name-value pair=" + stateNVP);

                  if(stateNVP != null && stateNVP.length() > 0 && stateNVP.contains("="))
                  {
                     String nameAndValue[] = stateNVP.split("=");
                     if(nameAndValue != null && nameAndValue.length == 2)
                     {
                        if(nameAndValue[0].equals("level"))
                        {
                           try
                           {
                              //parse the value of the address
                              int val = Integer.parseInt(nameAndValue[1]);
                              getSystem().processUpdate(address, val);
                           }
                           catch(NumberFormatException nfe)
                           {
                              log.error("*CBUS ERROR* Invalid value for level received for address: " + address + ", value=" + nameAndValue[1] + 
                                    ", nvp=" + stateNVP);
                           }
                        }
                     }
                  }

               }
            }
         }

      }
   }

   /**
    * Process a message received on the status port
    * 
    * @param response
    *           The message received
    *           
    */
   private void processStatusResponse(CGateStatusResponse response) 
   {
      if(response == null)
         return;

      if(response.getCommandType() != null)         
      {
         switch(response.getCommandType())
         {
         case LIGHTING:
            processLightingStatusResponse(response);
            break;

         case ENABLE:
            processEnableStatusResponse(response);
            break;

         case TRIGGER:
            processTriggerStatusResponse(response);
            break;

         default:
            log.error("*CBUS Error* Invalid status command type in response: " + response.getCommandType().toString());
            break;
         }
      }

   }

   /**
    * Process a lighting status message
    * 
    * @param response
    *           Lighting status message
    */
   private void processLightingStatusResponse(CGateStatusResponse response) 
   {
      log.debug("Processing LIGHTING response!");

      //only interested if second parameter is ramp, on or off
      CBusStatusCommandParameterType eventType = response.getCommandParameter();
      String address = response.getParameter(3);

      if(eventType != null && address != null && address.length() > 0)
      {
         switch(eventType)
         {
         case OFF:
            getSystem().processUpdate(address, 0);
            break;

         case ON:
            getSystem().processUpdate(address, 255);
            break;

         case RAMP:
            //next parameter is the final value of the dim (the value after is the duration)
            String rampFinalValue = response.getParameter(4);
            if(rampFinalValue != null && rampFinalValue.length() > 0)
            {
               try
               {
                  getSystem().processUpdate(address, Integer.parseInt(rampFinalValue));
               }
               catch(NumberFormatException nfe)
               {
                  log.error("*CBUS ERROR* Invalid ramp value in lighting status command: " + rampFinalValue + ", full response=" + response.fullResponse);
               }
            }
            break;

         default:
            log.debug("*CBUS Warning* Unsupported lighting status update response: " + response.fullResponse);
            break;
         }
      }
   }

   /**
    * Process a status message about the trigger application
    * 
    * @param response
    *           The trigger status message
    */
   private void processTriggerStatusResponse(CGateStatusResponse response)
   {
      log.debug("Processing TRIGGER response!");

      //only interested if second parameter is event
      CBusStatusCommandParameterType eventType = response.getCommandParameter();
      String address = response.getParameter(3);
      String eventVal = response.getParameter(4);

      if(eventType != null && address != null && address.length() > 0 && eventVal != null && eventVal.length() > 0)
      {
         switch(eventType)
         {
         case EVENT:
            try
            {
               getSystem().processUpdate(address, Integer.parseInt(eventVal));
            }
            catch(NumberFormatException nfe)
            {
               log.error("*CBUS ERROR* Invalid event value for TRIGGER EVENT response: " + eventVal + ", full response="+ response.fullResponse);
            }
            break;


         default:
            log.debug("*CBUS Warning* Unsupported trigger status update response: " + response.fullResponse);
            break;
         }
      }
   }

   /**
    * Process a status message about the Enable application
    * 
    * @param response
    *           The status message
    */
   private void processEnableStatusResponse(CGateStatusResponse response) 
   {
      log.debug("Processing ENABLE response!");

      //only interested if second parameter is set
      CBusStatusCommandParameterType enableType = response.getCommandParameter();
      String address = response.getParameter(3);
      String enableVal = response.getParameter(4);

      if(enableType != null && address != null && address.length() > 0 && enableVal != null && enableVal.length() > 0)
      {
         switch(enableType)
         {
         case SET:
            try
            {
               getSystem().processUpdate(address, Integer.parseInt(enableVal));
            }
            catch(NumberFormatException nfe)
            {
               log.error("*CBUS ERROR* Invalid enable set value for EVENT SET response: " + enableVal + ", full response="+ response.fullResponse);
            }
            break;


         default:
            log.debug("*CBUS Warning* Unsupported enable status update response: " + response.fullResponse);
            break;
         }
      }
   }

   /**
    * Parse the message received from CGate
    * @param responseText
    *           The message
    *           
    * @return The parsed response
    */
   protected CGateBaseResponse parseResponse(String responseText) 
   {

      //need to detect whether the response is an event response or a response to the main thread
      //the first 3 characters being numbers and the 4th not indicate it is a response on the main thread
      if(responseHasResponseCode(responseText))
      {
         return parseCodedResponse(responseText);
      }
      else
      {
         return parseStatusResponse(responseText);
      }

   }


   /**
    * Parse a message received on the status port
    * @param responseText
    *           The message
    *           
    * @return The parsed response object
    */
   private CGateStatusResponse parseStatusResponse(String responseText)
   {
      log.debug("Status thread response: " + responseText);

      //some responses have an = and some don't
      if(responseText != null && responseText.length() > 0)
         return new CGateStatusResponse(responseText);         
      else
         return null;
   }

   /**
    * Parse a message from CGate that has a code at the beginning (on the main and event ports)
    * @param responseText
    *           The message
    *           
    * @return The parsed response object
    */
   private CGateDeviceResponse parseCodedResponse(String responseText) 
   {
      log.debug("Main/Event coded thread response: " + responseText);

      //some responses have an = and some don't
      if(responseText != null && responseText.length() > 0)
         return new CGateDeviceResponse(responseText);         
      else
         return null;
   }

   /**
    * Check whether a message received starts with a status code
    * 
    * @param responseText
    *           The message
    *           
    * @return True if the response contains a status code, false if not
    */
   private boolean responseHasResponseCode(String responseText) 
   {
      boolean returnValue = false;
      if(responseText != null && responseText.length() > 3)
      {
         try
         {
            //first 3 characters must be a number to have a response code
            int x = Integer.parseInt(responseText.substring(0, 3));
            try
            {
               //4th character must not be a number
               int y = Integer.parseInt(responseText.substring(3, 4));
            }
            catch(NumberFormatException nfe1)
            {
               returnValue = true;
            }
         }
         catch(NumberFormatException nfe)
         {             
         }
      }

      return returnValue;
   }

   /**
    * Represents a command to pass to CGate
    * 
    * @author Jamie Turner
    *
    */
   public class CGateDeviceCommand
   {

      /**
       * Terminator for CGate
       */
      public static final String COMMAND_TERMINATOR = "\r\n";

      /**
       * Command string
       */
      protected String command;

      /**
       * Constructor
       * 
       * @param command
       *           Raw command to pass to CGate (should not contain any line terminators like CRLF)
       */
      public CGateDeviceCommand(String command) 
      {
         this.command = command;             
      }

      /**
       * Overridden
       */
      public String toString()
      {
         return command;
      }

      /**
       * Get the current command terminator value
       * 
       * @return The command terminator
       */
      public String getCommandTerminator() 
      {
         return COMMAND_TERMINATOR;
      }       

   }


   /**
    * There are two types of response - events and responses to commands
    * EVENT/STATUS examples:
    * lighting on //[PROJECT]/254/56/31  #sourceunit=150 OID=6072d900-c6e5-102e-a8e3-81efb819aa6b
    * lighting off //[PROJECT]/254/56/41  #sourceunit=150 OID=60757110-c6e5-102e-a8f4-81efb819aa6b
    * enable set //[PROJECT]/254/203/6 255 #sourceunit=150 OID=60791a90-c6e5-102e-a99a-81efb819aa6b
    * lighting ramp //[PROJECT]/254/56/74 50 60 #sourceunit=152 OID=60732720-c6e5-102e-a8aa-81efb819aa6b sessionId=cmd5 commandId={none}
    * trigger event //[PROJECT]/254/202/4 0 #sourceunit=152 OID=6074adc0-c6e5-102e-a97d-81efb819aa6b sessionId=cmd5 commandId={none}
    *
    * COMMAND response examples:
    * 300 //[PROJECT]/254/56/0: level=0
    * 200 OK: //[PROJECT]/254/56/74
    * 401 Bad object or device ID.

    */    
   private class CGateBaseResponse
   {
      /**
       * The full response
       */
      protected String fullResponse;

      /**
       * Contains an array of parameters/status code in the message from CGate
       */
      protected String[] parametersAndCode;

      /**
       * Constructor
       * 
       * @param responseText
       *           Message received from CGate
       */
      public CGateBaseResponse(String responseText) 
      {
         fullResponse = responseText;          
         parametersAndCode = responseText.split(" ");
      }

      /**
       * Get a parameter from the CGate message
       * @param index
       *           The index of the parameter
       * @return The parameter
       */
      public String getParameter(int index)
      {
         if(index > 0)
         {
            if(parametersAndCode.length >= index)
               return parametersAndCode[index - 1];
            else
               return null;
         }
         else
         {
            log.error("*CBUS ERROR* getParameter index must be > 0: " + index);
            return null;
         }
      }
   }

   /**
    * Represents a response from CGate
    * 
    * @author Jamie Turner
    *
    */
   private class CGateDeviceResponse extends CGateBaseResponse 
   {

      /**
       * Constructor
       * 
       * @param responseText
       *           The message received from CGate
       */
      public CGateDeviceResponse(String responseText) 
      {
         super(responseText);         
      }

      /**
       * Get the status code on the response if it has one
       * 
       * @return The status code, or null if there isn't any
       */
      public String getResponseCode()
      {         
         if(this.fullResponse != null && this.fullResponse.length() > 2)
            return fullResponse.substring(0, 3);
         else
            return null;
      }

      /**
       * Check whether the response from CGate contains a hyphen character at position 4
       *  
       * @return True if it does, false if not
       */
      public boolean isHyphenatedResponse()
      {
         if(fullResponse != null && fullResponse.length() > 3)
            return (fullResponse.substring(3,4).equals("-"));
         else
            return false;
      }

      /**
       * Get a specific parameter not including status codes
       * @param index
       *           The index of the parameter to get
       *           
       * @return The parameter
       */
      public String getNonCodeParameter(int index)
      {
         return getParameter(index + 1);

      }

   }

   /**
    * Represents a response from the status/event port
    * 
    * @author Jamie Turner
    *
    */
   private class CGateStatusResponse extends CGateBaseResponse
   {

      /**
       * Constructor
       * 
       * @param responseText
       *           The message received from CGate
       */
      public CGateStatusResponse(String responseText) 
      {
         super(responseText);         
      }

      /**
       * Gets the CBus application indicated in the message
       *  
       * @return The CBus application associated with the message
       */
      public CBusApplicationType getCommandType()
      {
         CBusApplicationType returnValue = null;
         String commandType = getParameter(1);
         if(commandType != null && commandType.length() > 0)
         {
            try
            {
               returnValue = CBusApplicationType.valueOf(commandType.toUpperCase());
            }
            catch(Exception ex)
            {
               log.debug("*CBUS Warning* Unsupported event/status response command: " + commandType + ", full response=" + fullResponse);
            }
         }

         return returnValue;
      }

      /**
       * Gets the status command parameter associated with the message from CGate
       * 
       * @return The parameter, or null if it couldn't be determined
       */
      public CBusStatusCommandParameterType getCommandParameter()
      {
         CBusStatusCommandParameterType returnValue = null;
         String parameter = getParameter(2);
         if(parameter != null && parameter.length() > 0)
         {
            try
            {
               returnValue = Enum.valueOf(CBusStatusCommandParameterType.class, parameter.toUpperCase());
            }
            catch(Exception ex)
            {
               log.debug("*CBUS Warning* Unsupported event/status response command parameter: " + parameter + ", full response=" + fullResponse);
            }
         }

         return returnValue;
      }

   }

}


