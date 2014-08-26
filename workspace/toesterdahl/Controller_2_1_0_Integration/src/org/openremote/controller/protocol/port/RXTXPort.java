/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.port;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.openremote.controller.protocol.port.pad.AbstractPort;
import org.openremote.controller.utils.Logger;

/**
 * Serial port implementation based on the native library RXTX (see http://rxtx.qbang.org/).
 * 
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class RXTXPort implements Port {

   // Constants ------------------------------------------------------------------------------------

   /**
    * Amount of time in milliseconds to wait until the RXTX serial port has been opened: {@value}
    */
   public static final int RXTX_PORT_OPEN_TIMEOUT = 2000;

   /**
    * Amount of time in milliseconds to wait until data has been received: {@value}
    */
   public static final int RXTX_PORT_RECEIVE_TIMEOUT = 100;

   /**
    * Number of bytes allocated for the receive buffer: {@value}
    */
   public static final int RXTX_PORT_RECEIVE_BUFFER_SIZE = 4096;

   /**
    * Number of bytes allocated for the transmit buffer: {@value}
    */
   public static final int RXTX_PORT_TRANSMIT_BUFFER_SIZE = 1024;

   public enum ParityValue {
      N(SerialPort.PARITY_NONE), O(SerialPort.PARITY_ODD), E(SerialPort.PARITY_EVEN), M(SerialPort.PARITY_MARK), S(
            SerialPort.PARITY_SPACE);

      ParityValue(int rxtxConfigValue) {
         this.rxtxConfigValue = rxtxConfigValue;
      }

      private final int rxtxConfigValue;

      public int getRxtxConfigValue() {
         return rxtxConfigValue;
      }

      public static int getRxtxConfigValue(String portValue) {
         return ParityValue.valueOf(ParityValue.class, portValue).getRxtxConfigValue();
      }
   }

   public enum StopbitValue {
      One("1", SerialPort.STOPBITS_1), Two("2", SerialPort.STOPBITS_2), One_Half("1_5", SerialPort.STOPBITS_1_5);

      StopbitValue(String portConfigValue, int rxtxConfigValue) {
         this.portConfigValue = portConfigValue;
         this.rxtxConfigValue = rxtxConfigValue;
      }

      private final String portConfigValue;

      private final int rxtxConfigValue;

      public String getPortConfigValue() {
         return portConfigValue;
      }

      public int getRxtxConfigValue() {
         return rxtxConfigValue;
      }

      public static int getRxtxConfigValue(String portConfigValue) {
         StopbitValue[] values = values();
         for (StopbitValue s : values) {
            if (portConfigValue.equals(s.getPortConfigValue())) {
               return s.getRxtxConfigValue();
            }
         }
         throw new IllegalArgumentException("Code " + portConfigValue
               + " is not a known stop bit value. Enter one of '1', '2', '1_5', or leave it empty.");
      }
   }

   // Class Members --------------------------------------------------------------------------------

   private final static Logger log = Logger.getLogger(PortFactory.PORT_LOG_CATEGORY);

   // Private Instance Fields ----------------------------------------------------------------------

   /**
    * RXTX com port identifier.
    */
   private CommPortIdentifier portIdentifier;

   /**
    * RXTX serial port.
    */
   private SerialPort serialPort;

   /**
    * Input stream for receiving data from the serial port.
    */
   private InputStream inputStream;

   /**
    * Output stream for sending data to the serial port.
    */
   private OutputStream outputStream;

   /**
    * Queue used as inter-thread communication mechanism for receiving data from the serial port event listener.
    */
   private BlockingQueue<Byte> inputQueue;

   /**
    * Queue used as inter-thread communication mechanism for sending data to the serial port writer thread.
    */
   private BlockingQueue<Byte> outputQueue;

   /**
    * Thread for sending data to the serial port.
    */
   private Thread writerThread;

   /**
    * Thread for reading data from serial port.
    */
   private Thread readerThread;

   /**
    * Serial port configuration.
    */
   private Map<String, Object> configuration;

   // Implements Port ------------------------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   @Override
   public void configure(Map<String, Object> configuration) throws IOException, PortException {
      this.configuration = configuration;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void start() throws IOException, PortException {
      String port_id = (String) configuration.get(AbstractPort.PORT_ID);
      int speed = Integer.parseInt((String) configuration.get(AbstractPort.PORT_SPEED));
      int databits = configuration.containsKey(AbstractPort.PORT_NB_BITS) ? Integer.parseInt((String) configuration
            .get(AbstractPort.PORT_NB_BITS)) : SerialPort.DATABITS_8;
      int parity = configuration.containsKey(AbstractPort.PORT_PARITY) ? ParityValue
            .getRxtxConfigValue((String) configuration.get(AbstractPort.PORT_PARITY)) : SerialPort.PARITY_NONE;
      int stopbits = configuration.containsKey(AbstractPort.PORT_STOPBIT) ? StopbitValue
            .getRxtxConfigValue((String) configuration.get(AbstractPort.PORT_STOPBIT)) : SerialPort.STOPBITS_1;

      log.info("RXTXPort, starting with port_id: {0} speed: {1} databits: {2} parity: {3} stopbits: {4}", port_id,
            speed, databits, parity, stopbits);

      try {
         portIdentifier = CommPortIdentifier.getPortIdentifier(port_id);
      } catch (NoSuchPortException e) {
         log.error("Error while configuring serial port : {0}", e, e.getMessage());

         throw new PortException(PortException.INVALID_CONFIGURATION);
      }

      CommPort commPort = null;

      try {
         log.debug("RXTXPort, opening port_id: {0}", port_id);
         commPort = portIdentifier.open("RXTXPort " + port_id, RXTX_PORT_OPEN_TIMEOUT);
         log.info("RXTXPort, opened port_id: {0}", port_id);
      } catch (PortInUseException e) {
         log.error("Error while configuring serial port : {0}", e, e.getMessage());

         throw new PortException(PortException.INVALID_CONFIGURATION);
      }

      if (commPort instanceof SerialPort) {
         serialPort = (SerialPort) commPort;
      } else {
         log.error("Error while configuring port ''{0}'' because it''s not a serial port type.", port_id);

         throw new PortException(PortException.INVALID_CONFIGURATION);
      }

      try {
         serialPort.setSerialPortParams(speed, databits, stopbits, parity);
      } catch (UnsupportedCommOperationException e) {
         log.error("Error while configuring serial port : {0}", e, e.getMessage());

         throw new PortException(PortException.INVALID_CONFIGURATION);
      }

      try {
         serialPort.enableReceiveTimeout(RXTX_PORT_RECEIVE_TIMEOUT);
      } catch (UnsupportedCommOperationException e) {
         log.warn("Error while configuring serial port : {0}", e, e.getMessage());
      }

      inputQueue = new LinkedBlockingQueue<Byte>(RXTX_PORT_RECEIVE_BUFFER_SIZE);
      outputQueue = new LinkedBlockingQueue<Byte>(RXTX_PORT_TRANSMIT_BUFFER_SIZE);

      inputStream = serialPort.getInputStream();
      outputStream = serialPort.getOutputStream();

      // TODO : OpenRemoteRuntime.createThread

      writerThread = new Thread(new SerialWriter(outputStream, outputQueue), "RXTX port writer" + port_id);
      writerThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread t, Throwable e) {
            log.error("Thread ''{0}'' terminated with exception : {1}", e, t.getName(), e.getMessage());
         }
      });

      writerThread.start();

      readerThread = new Thread(new SerialReader(inputStream, inputQueue), "RXTX port reader" + port_id);
      readerThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread t, Throwable e) {
            log.error("Thread ''{0}'' terminated with exception : {1}", e, t.getName(), e.getMessage());
         }
      });

      readerThread.start();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void stop() throws IOException, PortException {
      log.info("RXTXPort, port_id: {0}. Stopping", configuration.get(AbstractPort.PORT_ID));

      if (writerThread != null) {
         // TODO (TOE): Shouldn't the thread get an opportunity to finish writing? Or is that up to the WriterThread
         // impl?
         writerThread.interrupt();

         try {
            log.debug("RXTXPort, port_id: {0}. Waiting to join {1}.", configuration.get(AbstractPort.PORT_ID),
                  writerThread.getName());
            writerThread.join();
            log.debug("RXTXPort, port_id: {0}. Joined {1}.", configuration.get(AbstractPort.PORT_ID),
                  writerThread.getName());
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }

      if (readerThread != null) {
         readerThread.interrupt();

         try {
            log.debug("RXTXPort, port_id: {0}. Waiting to join {1}.", configuration.get(AbstractPort.PORT_ID),
                  readerThread.getName());
            readerThread.join();
            log.debug("RXTXPort, port_id: {0}. Joined {1}.", configuration.get(AbstractPort.PORT_ID),
                  readerThread.getName());
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }

      if (outputStream != null) {
         outputStream.close();
         log.debug("RXTXPort, port_id: {0}. Closed output stream", configuration.get(AbstractPort.PORT_ID));
      }

      if (inputStream != null) {
         inputStream.close();
         log.debug("RXTXPort, port_id: {0}. Closed input stream", configuration.get(AbstractPort.PORT_ID));
      }

      if (serialPort != null) {
         serialPort.close();
         log.debug("RXTXPort, port_id: {0}. Closed port", configuration.get(AbstractPort.PORT_ID));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send(Message message) throws IOException, PortException {
      byte[] data = message.getContent();

      if (data == null) {
         return;
      }

      log.info("RXTXPort, port_id: {0}. Send message: {1}", configuration.get(AbstractPort.PORT_ID),
            Arrays.toString(data));
      for (byte dataByte : data) {
         try {
            log.debug("RXTXPort, port_id: {0}. Send byte: {1}", configuration.get(AbstractPort.PORT_ID), dataByte);
            outputQueue.put(dataByte);
         }

         catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Message receive() throws IOException {
      byte[] data = null;
      int size = inputQueue.size();

      try {
         if (size > 0) {
            data = new byte[size];

            for (int i = 0; i < size; i++) {
               data[i] = inputQueue.take();
               log.debug("RXTXPort, port_id: {0}. Receive bytes: {1}", configuration.get(AbstractPort.PORT_ID), Arrays.toString(data));
            }
         }

         else {
            byte dataByte = inputQueue.take();
            log.debug("RXTXPort, port_id: {0}. Receive bytes: {1}", configuration.get(AbstractPort.PORT_ID), dataByte);

            size = inputQueue.size();

            if (size > 0) {
               data = new byte[size + 1];
               data[0] = dataByte;

               for (int i = 0; i < size; i++) {
                  data[i + 1] = inputQueue.take();
                  log.debug("RXTXPort, port_id: {0}. Receive bytes: {1}", configuration.get(AbstractPort.PORT_ID), Arrays.toString(data));
               }
            }

            else {
               data = new byte[1];
               data[0] = dataByte;
            }
         }
      }

      catch (InterruptedException e) {
         Thread.currentThread().interrupt();

         return new Message(new byte[0]);
      }

      log.debug("RXTXPort, port_id: {0}. Receive message: {1}", configuration.get(AbstractPort.PORT_ID), Arrays.toString(data));

      Message msg = new Message(data);

      return msg;
   }

   // Nested Classes -------------------------------------------------------------------------------

   /**
    * Thread implementation for sending data to the serial port.
    */
   private static class SerialWriter implements Runnable {

      // Private Instance Fields --------------------------------------------------------------------

      /**
       * Output stream for sending data to serial port.
       */
      private OutputStream outputStream;

      /**
       * Queue used as inter-thread communication mechanism for transferring data bytes to the writer thread.
       */
      private BlockingQueue<Byte> outputQueue;

      // Constructors -------------------------------------------------------------------------------

      /**
       * Constructs a new serial writer instance with given output stream and data queue.
       * 
       * @param stream
       *           output stream for sending data to the serial port
       * 
       * @param queue
       *           queue for transferring data to the serial writer thread
       */
      public SerialWriter(OutputStream stream, BlockingQueue<Byte> queue) {
         this.outputStream = stream;
         this.outputQueue = queue;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
         byte dataByte;

         while (true) {
            try {
               dataByte = outputQueue.take();
            }

            catch (InterruptedException e) {
               log.debug("RXTXPort. Exiting thread: {0}", Thread.currentThread().getName());
               Thread.currentThread().interrupt();
               break;
            }

            try {
               log.debug("RXTXPort. Write to stream: {0}", dataByte);
               outputStream.write(dataByte);
            } catch (IOException e) {
               log.error("Thread ''{0}'' terminated with exception : {1}", e, Thread.currentThread().getName(),
                     e.getMessage());

               break;
            }
         }
      }
   }

   /**
    * Thread implementation for sending data to the serial port.
    */
   private static class SerialReader implements Runnable {

      // Private Instance Fields --------------------------------------------------------------------

      /**
       * Input stream for reading data fom the serial port.
       */
      private InputStream inputStream;

      /**
       * Queue used as inter-thread communication mechanism for transferring data bytes from reader thread to a
       * different worker thread.
       */
      private BlockingQueue<Byte> inputQueue;

      // Constructors -------------------------------------------------------------------------------

      /**
       * Constructs a serial port reader instance with given input stream and queue.
       * 
       * @param stream
       *           input stream for reading data from the serial port
       * 
       * @param queue
       *           queue for transferring data from the serial port reader thread to a different worker thread.
       */
      public SerialReader(InputStream stream, BlockingQueue<Byte> queue) {
         this.inputStream = stream;
         this.inputQueue = queue;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
         byte[] buffer = new byte[1024];
         int len = -1;

         try {
            while ((len = inputStream.read(buffer)) > -1) {
               if (len > 0) {
                  log.debug("RXTXPort. Read from stream: {0}", Arrays.toString(Arrays.copyOf(buffer, len)));
               }
               if (Thread.currentThread().isInterrupted()) {
                  log.debug("RXTXPort. Exiting thread: {0}", Thread.currentThread().getName());
                  break;
               }

               try {
                  for (int i = 0; i < len; i++) {
                     inputQueue.put(buffer[i]);
                  }
               }

               catch (InterruptedException e) {
                  log.debug("RXTXPort. Exiting thread: {0}", Thread.currentThread().getName());
                  Thread.currentThread().interrupt();
                  break;
               }
            }
         }

         catch (IOException e) {
            log.error("Thread ''{0}'' terminated with exception : {1}", e, Thread.currentThread().getName(),
                  e.getMessage());
         }
      }
   }
}
