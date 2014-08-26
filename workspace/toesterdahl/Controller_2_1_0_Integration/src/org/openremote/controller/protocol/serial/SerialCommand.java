package org.openremote.controller.protocol.serial;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.CharSet;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.port.Message;
import org.openremote.controller.protocol.port.Port;
import org.openremote.controller.protocol.port.PortException;
import org.openremote.controller.utils.Logger;

/**
 * @author toesterdahl - Torbjörn Österdahl
 */
public class SerialCommand implements ExecutableCommand {

    // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(SerialCommandBuilder.SERIAL_PROTOCOL_LOG_CATEGORY);
   
   private static final Pattern PTN_CR = Pattern.compile("\\\\r");

   private static final Pattern PTN_LN = Pattern.compile("\\\\n");

   private static final Charset US_ASCII = Charset.forName("US-ASCII");
   
   private Port port = null;
   
   private String name = null;
   
   private String command = null;

   private SerialCommand(Port port, String name, String command) {
      this.port = port;
      this.name = name;
      this.command = command;
   }

   public static SerialCommand createSerialCommand(Port port, String name, String value) throws PortException, IOException {
      SerialCommand command  = new SerialCommand(port, name, value);
      
      return command;
   }
   
   public void send() {
      logger.info("SerialCommand command: " + command);
      try {
         // TODO (TOE) Consider if ports should be held in some kind of pool. Would it be possible to allow a protocol to hold a port of a limited time and automatically return it if it is not used again within a certain time-out?
         port.start();
      } catch (Exception e) {
         logger.error("Error opening port ", e);
      }

      try {
         requestData();
      } catch (Exception e) {
         logger.error("Error opening port ", e);
      } 

      try {
         port.stop();
      } catch (Exception e) {
         logger.error("Error opening port ", e);
      } 
   }

   private String requestData() {
      String reply;
      try {
         StringTokenizer st = new StringTokenizer(command, "|");
         while (st.hasMoreElements()) {
            String cmd = (String) st.nextElement();
            byte[] bytesIn;
            if (cmd.startsWith("0x")) {
               String tmp = command.substring(2);
               bytesIn = hexStringToByteArray(tmp.replaceAll(" ", "").toLowerCase());
            } else {
               String unescapedCmd = unescapeCmd(cmd);
               bytesIn = unescapedCmd.getBytes(US_ASCII);
            }
            port.send(new Message(bytesIn));
         }
         Message out = port.receive();
         byte[] bytesOut = out.getContent();
         reply = new String(bytesOut, US_ASCII);
         return reply;
      } catch (Exception e) {
         logger.error("Serial: Error sending command to port: " + port, e);
         return null;
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
   
   private String unescapeCmd(String cmd) {
//      String s1 = cmd.replaceAll("\\\\r", "\r");
//      String s2 = s1.replaceAll("\\\\n", "\n");
      Matcher matchCR = PTN_CR.matcher(cmd);
      String s1 = matchCR.replaceAll("\r");
      Matcher matchLN = PTN_LN.matcher(s1);
      String s2 =matchLN.replaceAll("\n");
      return s2;
   }
   
   public static void main(String[] args) {
      SerialCommand app = new SerialCommand(null, "/dev/ttyUSB0", "A1\\r");
      String unescaped = app.unescapeCmd(app.command);
      byte[] bytes = unescaped.getBytes(US_ASCII);
      for (byte b:bytes) {
         System.out.println("" + b);
      }
   }
}
