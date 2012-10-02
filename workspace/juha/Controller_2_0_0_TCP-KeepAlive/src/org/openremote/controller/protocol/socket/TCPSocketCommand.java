/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.socket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.utils.Logger;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Marcus Redeker 2009-4-26
 * @author Phillip Lavender
 * @author Ivan Martinez
 */
public class TCPSocketCommand extends ReadCommand implements ExecutableCommand
{

  // Class Members --------------------------------------------------------------------------------

  private static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "tcp");



  // Instance Fields ------------------------------------------------------------------------------

  private String name;
  private TCPSocketCommandBuilder.TCPConnection connection;
  private String payload;
  private Boolean waitForResponse;


  // Constructors ---------------------------------------------------------------------------------

  TCPSocketCommand(String name, String payload, TCPSocketCommandBuilder.TCPConnection connection,
                   Boolean waitForResponse)
  {
    log.info("Created TCP command ''{0}'' for connection {1} with unparsed payload ''{2}''",
        name, connection, payload);

    this.connection = connection;
    this.payload = payload;
    this.name = name;
    this.waitForResponse = waitForResponse;
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  @Override public void send()
  {
    if (waitForResponse)
    {
      sendAndReceive();
    }

    else
    {
      sendOnly();
    }
  }

  // Implements ReadCommand -----------------------------------------------------------------------

  @Override public String read(Sensor s)
  {
    String rawResult = sendAndReceive();

    // Strip response from control characters (non-ASCII)...

    Pattern p = Pattern.compile("\\p{Cntrl}");
    Matcher m = p.matcher(rawResult);
    String regexResult = m.replaceAll("");

    if ("".equals(regexResult))
    {
       return Sensor.UNKNOWN_STATUS;
    }

    return regexResult;
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private void sendOnly()
  {
    try
    {
      Set<Message> messages = parseMessages(payload);

      for (Message m : messages)
      {
        log.info("Sent " + m);

        connection.send(m);
      }
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private String sendAndReceive()
  {
    StringBuffer sbuf = new StringBuffer();

    try
    {
      Set<Message> messages = parseMessages(payload);

      for (Message m : messages)
      {
        log.info("Sent " + m);

        connection.send(m);

        Message response = connection.receive();

        sbuf.append(new String(response.getContent()));
      }
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }

    return sbuf.toString();
  }

  private Set<Message> parseMessages(String payload)
  {
    StringTokenizer st = new StringTokenizer(payload, "|");

    Set<Message> msgs = new HashSet<Message>();


    while (st.hasMoreElements())
    {
      msgs.add(new ByteArrayMessage(st.nextToken()));
    }

    return msgs;
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class ByteArrayMessage extends Message
  {
    private static byte[] parse(String unparsedPayload)
    {
      byte[] bytes;

      if (unparsedPayload.startsWith("0x"))
      {
         String tmp = unparsedPayload.substring(2);
         bytes = hexStringToByteArray(tmp.replaceAll(" ", "").toLowerCase());
      }

      else
      {
         bytes = (unparsedPayload + "\r").getBytes();
      }

      return bytes;
    }

    private static byte[] hexStringToByteArray(String s)
    {
      int len = s.length();
      byte[] data = new byte[len / 2];

      for (int i = 0; i < len; i += 2)
      {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
      }

      return data;
    }

    private ByteArrayMessage(String unparsedPayload)
    {
      super(parse(unparsedPayload));
    }

    @Override public String toString()
    {
      return new String(super.getContent());
    }
  }







//   private String requestSocket() {
//      Socket socket = null;
//      try {
//         socket = new Socket(getIp(), Integer.parseInt(getPort()));
//         OutputStream out = socket.getOutputStream();
//
//         StringTokenizer st = new StringTokenizer(getCommand(), "|");
//         while (st.hasMoreElements()) {
//            String cmd = (String) st.nextElement();
//+            byte[] bytes;
//+            if (cmd.startsWith("0x")) {
//+               String tmp = getCommand().substring(2);
//+               bytes = hexStringToByteArray(tmp.replaceAll(" ", "").toLowerCase());
//+            } else {
//+               bytes = (cmd + "\r").getBytes();
//+            }
//+            out.write(bytes);
//         }
//
//         String result = readReply(socket);
//         logger.info("received message: " + result);
//         return result;
//      } catch (Exception e) {
//         logger.error("Socket event could not execute", e);
//      } finally {
//         if (socket != null) {
//            try {
//               socket.close();
//            } catch (IOException e) {
//               logger.error("Socket could not be closed", e);
//            }
//         }
//      }
//      return "";
//   }
//
//   private String readReply(java.net.Socket socket) throws IOException {
//      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//      char[] buffer = new char[200];
//      int readChars = bufferedReader.read(buffer, 0, 200); // blocks until message received
//      if (readChars > 0) {
//         String reply = new String(buffer, 0, readChars);
//         return reply;
//      } else {
//         return "";
//      }
//   }
//
//  @Override public String read(EnumSensorType sensorType, Map<String, String> stateMap)
//  {
//    String regexResult = null;
//    String strState = null;
//
//    // Write the command to socket...
//    //
//    // TODO : rather poorly named method
//
//    String rawResult = requestSocket();
//
//    // Strip response from control characters (non-ASCII)...
//    //
//    // Patch provided by Phillip Lavender
//
//    Pattern p = Pattern.compile("\\p{Cntrl}");
//    Matcher m = p.matcher(rawResult);
//    regexResult = m.replaceAll("");
//
//    if ("".equals(regexResult))
//    {
//       return UNKNOWN_STATUS;
//    }
//
//    switch (sensorType)
//    {
//      case RANGE:
//         break;
//
//      case LEVEL:
//         String min = stateMap.get(Sensor.RANGE_MIN_STATE);
//         String max = stateMap.get(Sensor.RANGE_MAX_STATE);
//
//         try
//         {
//            int val = Integer.valueOf(regexResult);
//
//            if (min != null && max != null)
//            {
//               int minVal = Integer.valueOf(min);
//               int maxVal = Integer.valueOf(max);
//
//               return String.valueOf(100 * (val - minVal)/ (maxVal - minVal));
//            }
//         }
//
//         catch (ArithmeticException e)
//         {
//            logger.warn("Level sensor values cannot be parsed: " + e.getMessage(), e);
//         }
//
//         break;
//
//      default://NOTE: if sensor type is RANGE, this map only contains min/max states.
//
//        // If custom sensor type has been configured, map the 'raw' return value to configured
//        // 'wanted' return value
//        //
//        // TODO :
//        //   no reason to put this implementation burden on protocol implementations, the
//        //   calling code could do it instead
//
//        for (String state : stateMap.keySet())
//        {
//          strState = stateMap.get(state);
//
//          if (regexResult.equals(strState))
//          {
//            return state;
//          }
//        }
//      }
//
//
//      return regexResult;
//  }

}
