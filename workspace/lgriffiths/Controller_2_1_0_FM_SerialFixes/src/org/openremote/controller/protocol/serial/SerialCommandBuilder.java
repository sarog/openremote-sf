/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.serial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.CommandUtil;


/**
 * Builds a generic Serial Command
 * 
 * @author Lawrie Griffiths
 */
public class SerialCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String SERIAL_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "serial";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(SERIAL_PROTOCOL_LOG_CATEGORY);
   
   // Instance Fields
   // ---------------------------------------------------------
   
   private Map<String,SerialPort> ports = new HashMap<String,SerialPort>();

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      String name=null, port=null, cmd=null;
      int rate=0;
      SerialPort serialPort = null;
      
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      for(Element ele : propertyEles){
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
         
         if("name".equals(elementName)){
            name = elementValue;
         } else if("port".equals(elementName)){
            port = elementValue;
         } else if("rate".equals(elementName)){
            rate = Integer.parseInt(elementValue);
         } else if("command".equals(elementName)){
            cmd = CommandUtil.parseStringWithParam(element, elementValue);
         }
      }
      
      // Open the port and keep the open ports in a hashmap
      serialPort = (SerialPort) ports.get(port);
      if (serialPort == null) {
         try {
            CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(port);
            serialPort = (SerialPort) id.open("ORSerial", 2000);
            serialPort.setSerialPortParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            // Give port time to open
            Thread.sleep(1000);
            ports.put(port, serialPort);
         } catch (Exception e) {
            logger.error("Serial: Error opening port: " + port, e);
         }
         
      }
      return new SerialCommand(name, port, rate, cmd, serialPort);
   }
}
