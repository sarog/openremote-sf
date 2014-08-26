package org.openremote.controller.protocol.serial;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.port.Port;
import org.openremote.controller.protocol.port.PortException;
import org.openremote.controller.protocol.port.PortFactory;
import org.openremote.controller.protocol.port.RXTXPort;
import org.openremote.controller.protocol.port.pad.AbstractPort;
import org.openremote.controller.utils.Logger;

/**
 * 
 * Command builder for parsing rs232 protocol XML entries from controller.xml file.
 * 
 * This is the expected xml structure to configure the command.
 * 
 * <pre>
 * {@code
 * <command id="1053" protocol="serial">
 *    <property name="port" value="/dev/ttyUSB2" />
 *    <property name="rate" value="57600" />
 *    <property name="command" value="A1" />
 *    <property name="name" value="Select A1" />
 * </command>
 *
 * }
 * </pre>
 * 
 * For the time beeing the protocol assume 8 bits, 1 stop bit, no parity. All hardware I have came across use this. Feel
 * free to add remaining properties if needed. Leave the defaults as is.
 * 
 * @author toesterdahl - Torbjörn Österdahl
 */
public class SerialCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------
   public final static String SERIAL_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "serial";
   
   public enum Params {
      COMMAND_CONFIG_PORT_ID("port") {
         @Override
         public boolean validate(String value) {
            return super.checkString(value);
         }
      },
      COMMAND_CONFIG_PORT_BAUDRATE("rate") {
         @Override
         public boolean validate(String value) {
            return super.checkInteger(value);
         }
      },
      COMMAND_CONFIG_PORT_VALUE("command") {
         @Override
         public boolean validate(String value) {
            return super.checkString(value);
         }
      },
      COMMAND_CONFIG_PORT_NAME("name") {
         @Override
         public boolean validate(String value) {
            return super.checkString(value);
         }
      },
      COMMAND_CONFIG_PORT_DATA_BITS("databits") {
         @Override
         public boolean validate(String value) {
            return super.checkInteger(value);
         }
      },
      COMMAND_CONFIG_PORT_PARITY("parity") {
         @Override
         public boolean validate(String value) {
            return super.checkString(value);
         }
      },COMMAND_CONFIG_PORT_STOP_BITS("stopbits") {
         @Override
         public boolean validate(String value) {
            return super.checkString(value);
         }
      },
      ;

      private String elementName;

      Params(String elementName) {
         this.elementName = elementName;
      }

      public String getElementName() {
         return elementName;
      }

      public abstract boolean validate(String value);

      private boolean checkString(String value) {
         return value != null && value.length() > 0;
      }

      private boolean checkInteger(String value) {
         try {
            boolean checkString = checkString(value);
            boolean testValue = Integer.parseInt(value) > Integer.MIN_VALUE;
            return checkString && testValue;
         } catch (NumberFormatException e) {
            return false;
         }
      }
   }

   private final static Logger logger = Logger.getLogger(SerialCommandBuilder.SERIAL_PROTOCOL_LOG_CATEGORY);

   /**
    * @see org.openremote.controller.command.CommandBuilder#build(org.jdom.Element)
    */
   @Override
   public Command build(Element element) {
      try {
         Map<String,Object> configuration = new HashMap<String,Object>();
   
         String name = null;
         String value = null;
         
         // Get the list of properties from Command XML element...
         List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());
   
         for (Element el : propertyElements) {
            String propName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
            String propValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);
   
            if (Params.COMMAND_CONFIG_PORT_ID.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_ID.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_ID + "' property.");
               }
   
               configuration.put(AbstractPort.PORT_ID, propValue);
            } else if (Params.COMMAND_CONFIG_PORT_BAUDRATE.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_BAUDRATE.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_BAUDRATE
                        + "' property.");
               }
   
               configuration.put(AbstractPort.PORT_SPEED, propValue);
            } else if (Params.COMMAND_CONFIG_PORT_VALUE.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_VALUE.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_VALUE
                        + "' property.");
               }
   
               value = propValue;
            } else if (Params.COMMAND_CONFIG_PORT_NAME.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_NAME.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_NAME
                        + "' property.");
               }
   
               name = propValue;
            } else if (Params.COMMAND_CONFIG_PORT_DATA_BITS.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_DATA_BITS.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_DATA_BITS
                        + "' property.");
               }
   
               configuration.put(AbstractPort.PORT_NB_BITS, propValue);
            } else if (Params.COMMAND_CONFIG_PORT_PARITY.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_PARITY.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_PARITY
                        + "' property.");
               }
   
               configuration.put(AbstractPort.PORT_PARITY, propValue);
            } else if (Params.COMMAND_CONFIG_PORT_STOP_BITS.getElementName().equalsIgnoreCase(propName)) {
               if (!Params.COMMAND_CONFIG_PORT_STOP_BITS.validate(propValue)) {
                  throw new NoSuchCommandException("Serial command must have a '" + Params.COMMAND_CONFIG_PORT_STOP_BITS
                        + "' property.");
               }
   
               configuration.put(AbstractPort.PORT_STOPBIT, propValue);
            } 
   
            else {
               logger.warn("Unknown Serial property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \""
                     + propName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propValue + "\"/>'.");
            }
         }
   
         configuration.put(AbstractPort.PORT_TYPE, "serial");
         
         // Only knows RXTX for now. If more serial port implementations would be added rxtx will have to remain the
         // default.
         //Port port = PortFactory.createPhysicalBus(RXTXPort.class.toString());
         Port port = new RXTXPort();
         port.configure(configuration);      
         
         return SerialCommand.createSerialCommand(port, name, value);
      } catch (PortException e) {
         logger.error("Failed in creating SerialCommand", e);
      } catch (IOException e) {
         logger.error("Failed in creating SerialCommand", e);
      }
      
      return null;
   }

}
