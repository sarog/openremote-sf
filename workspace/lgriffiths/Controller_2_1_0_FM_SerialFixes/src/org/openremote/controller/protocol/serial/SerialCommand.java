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
package org.openremote.controller.protocol.serial;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.*;

import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.model.sensor.Sensor;

/**
 * 
 * Based on TCP/IP Socket protocol
 * 
 * @author Lawrie Griffiths
 */
public class SerialCommand implements ExecutableCommand, StatusCommand {

   // Constants ------------------------------------------------------------------------------------

   public final static String SERIAL_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "serial";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(SERIAL_PROTOCOL_LOG_CATEGORY);

   // Instance Fields
   // ----------------------------------------------------------

   /** A name to identify event in controller.xml. */
   private String name;

   /** A pipe separated list of command string that are sent over the socket */
   private String command;

   /** The name of the com port that is opened */
   private String port;

   /** The data rate for the serial port */
   private int rate;
   
   // The open serial port
   private SerialPort serialPort;

   public SerialCommand(String name, String port, int rate, String command, SerialPort serialPort) {
      this.name = name;
      this.port = port;
      this.rate = rate;
      this.command = command;
      this.serialPort = serialPort;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      requestData();
   }

   private String requestData() {
      String reply;
      try {
         serialPort.setSerialPortParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

         OutputStream out = serialPort.getOutputStream();
         InputStream in = serialPort.getInputStream();

         StringTokenizer st = new StringTokenizer(command, "|");
         while (st.hasMoreElements()) {
            String cmd = (String) st.nextElement();
            byte[] bytes;
            if (cmd.startsWith("0x")) {
               String tmp = command.substring(2);
               bytes = hexStringToByteArray(tmp.replaceAll(" ", "").toLowerCase());
            } else {
               bytes = (cmd).getBytes();
            }

            out.write(bytes);
         }

         reply = readReply(in);
         
         out.close();
         in.close();
         return reply;
      } catch (Exception e) {
         logger.error("Serial: Error sending command to port: " + port, e);
         return null;
      }
   }

   private String readReply(InputStream inputStream) throws IOException {
      StringBuilder b = new StringBuilder();
      while (true) {
         int c = readByte(inputStream);
         if (c == '\r') return b.toString();
         else
            b.append((char) c);
      }
   }

   public int readByte(InputStream input) throws IOException {
      for (;;) {
         int i = input.read();
         if (i >= 0) return (i & 0xFF);
      }
   }

   private byte[] hexStringToByteArray(String s) {
      int len = s.length();
      byte[] data = new byte[len / 2];
      for (int i = 0; i < len; i += 2) {
         data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }
      return data;
   }

   @Override
   public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
      String regexResult = null;
      String strState = null;

      // Request data from the serial device

      String rawResult = requestData();

      // Strip response from control characters (non-ASCII)...
      //
      // Patch provided by Phillip Lavender

      Pattern p = Pattern.compile("\\p{Cntrl}");
      Matcher m = p.matcher(rawResult);
      regexResult = m.replaceAll("");

      if ("".equals(regexResult)) {
         return UNKNOWN_STATUS;
      }

      switch (sensorType) {
      case RANGE:
         break;

      case LEVEL:
         String min = stateMap.get(Sensor.RANGE_MIN_STATE);
         String max = stateMap.get(Sensor.RANGE_MAX_STATE);

         try {
            int val = Integer.valueOf(regexResult);

            if (min != null && max != null) {
               int minVal = Integer.valueOf(min);
               int maxVal = Integer.valueOf(max);

               return String.valueOf(100 * (val - minVal) / (maxVal - minVal));
            }
         }

         catch (ArithmeticException e) {
            logger.warn("Level sensor values cannot be parsed: " + e.getMessage(), e);
         }

         break;

      default:// NOTE: if sensor type is RANGE, this map only contains min/max states.

         // If custom sensor type has been configured, map the 'raw' return value to configured
         // 'wanted' return value
         //
         // TODO :
         // no reason to put this implementation burden on protocol implementations, the
         // calling code could do it instead

         for (String state : stateMap.keySet()) {
            strState = stateMap.get(state);

            if (regexResult.equals(strState)) {
               return state;
            }
         }
      }

      return regexResult;
   }
}
