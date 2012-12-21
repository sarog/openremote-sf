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
package org.openremote.controller.protocol.elexolUSB;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.exception.NoSuchCommandException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * ElexolCommandBuilder is responsible for parsing the XML model from controller.xml and create
 * appropriate Command objects for it. <p>
 *
 * @author John Whitmore
 * @author <a href="mailto:johnfwhitmore@gmail.com>John Whitmore</a>
 */
public class ElexolCommandBuilder implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * A common log category name intended to be used across all classes related to
   * ElexolUSB implementation.
   */
  public final static String ELEXOL_USB_LOG_CATEGORY = "ELEXOL_USB";

  /**
   * String constant for parsing X10 protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for X10 addresses
   * (<code>{@value}</code>):
   *
   * <pre>{@code
   * <command protocol = "x10" >
   *   <property name = "address" value = "A1"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String X10_XMLPROPERTY_ADDRESS = "address";

  /**
   * String constant for parsing X10 protocol XML entries from controller.xml file.
   *
   * This constant is the expected property name value for X10 commands ({@value}):
   *
   * <pre>{@code
   * <command protocol = "x10" >
   *   <property name = "address" value = "A1"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   */
  public final static String X10_XMLPROPERTY_COMMAND = "command";


  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging. Use common Elexol USB log category.
   */
  private static Logger log = Logger.getLogger(ELEXOL_USB_LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------

  private static SerialPortManager usbPort = new SerialPortManager();



  // Implements CommandBuilder --------------------------------------------------------------------

  /**
   * Parses the X10 command XML snippets and builds a corresponding X10 command instance.  <p>
   *
   * The expected XML structure is:
   *
   * <pre>{@code
   * <command protocol = "x10" >
   *   <property name = "address" value = "A1"/>
   *   <property name = "command" value = "ON"/>
   * </command>
   * }</pre>
   *
   * Additional properties not listed here are ignored.
   *
   * @see X10Command
   *
   * @throws org.openremote.controller.exception.NoSuchCommandException
   *            if the X10 command instance cannot be constructed from the XML snippet
   *            for any reason
   *
   * @return an X10 command instance with known configured properties set
   */
  public Command build(Element element)
  {
    /*
     * TODO : ${param} handling
     *
     */

    String address = null;
    String commandAsString = null;
    String portAsString = null;
    String pinAsString = null;

    // Properties come in as child elements...

    List<Element> propertyElements = element.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY,
                                                         element.getNamespace());

    for (Element el : propertyElements)
    {
      String x10CommandPropertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String x10CommandPropertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

      if (X10_XMLPROPERTY_ADDRESS.equalsIgnoreCase(x10CommandPropertyName))
      {
        address = x10CommandPropertyValue;
      }
      else if (X10_XMLPROPERTY_COMMAND.equalsIgnoreCase(x10CommandPropertyName))
      {
        commandAsString = x10CommandPropertyValue;
      }
      else
      {
        log.warn(
            "Unknown X10 property '<" + XML_ELEMENT_PROPERTY + " " +
            XML_ATTRIBUTENAME_NAME + " = \"" + x10CommandPropertyName + "\" " +
            XML_ATTRIBUTENAME_VALUE + " = \"" + x10CommandPropertyValue + "\"/>'."
        );
      }
    }

    // sanity checks...

    if (commandAsString == null || ("").equals(commandAsString))
    {
      throw new NoSuchCommandException(
         "X10 command is missing a mandatory '" + X10_XMLPROPERTY_COMMAND + "' property"
      );
    }

    if (address == null || ("").equals(address))
    {
      throw new NoSuchCommandException(
         "X10 command is missing a mandatory '" + X10_XMLPROPERTY_ADDRESS + "' property"
      );
    }


    // Translate the command string to a type safe ElexolUSBCommandType enum...

    CommandType command = null;

    if (CommandType.SWITCH_ON.isEqual(commandAsString))
    {
      command = CommandType.SWITCH_ON;
    }
    else if (CommandType.SWITCH_OFF.isEqual(commandAsString))
    {
      command = CommandType.SWITCH_OFF;
    }
    else if (CommandType.PULSE.isEqual(commandAsString))
    {
      command = CommandType.PULSE;
    }
    else
    {
      throw new NoSuchCommandException("Elexol USB command '" + commandAsString + "' is not recognized.");
    }

    // Validate X10 addressing...

    String houseCodes = "[A-Pa-p]";           // characters A to P
    String deviceCodes = "([1-9]|(1[0-6]))";  // single digit 0-9 or double digit 10-16

    String pattern = houseCodes + "" + deviceCodes;
    Pattern regex = Pattern.compile(pattern);
    Matcher match = regex.matcher(address);

    if (!match.matches())
    {
      throw new NoSuchCommandException("X10 address '" + address + "' is not recognized.");
    }

    PortType port = null;

    portAsString = new String(address.substring(0,1));

    if (PortType.PORT_A.isEqual(portAsString))
    {
      port = PortType.PORT_A;
    }
    else if (PortType.PORT_B.isEqual(portAsString))
    {
      port = PortType.PORT_B;
    }
    else if (PortType.PORT_C.isEqual(portAsString))
    {
      port = PortType.PORT_C;
    }
    else
    {
      throw new NoSuchCommandException("Elexol USB I/O port '" + portAsString + "' is not recognized.");
    }

    pinAsString = new String(address.substring(1,2));

    PinType pin = PinType.convert(pinAsString);

    if (pin == null){
      throw new NoSuchCommandException("Elexol USB I/O pin '" + pinAsString + "' is not recognized.");
    } 
    // Done!

    ElexolCommand cmd = new ElexolCommand(usbPort, command, port, pin);

    return cmd;
  }

}
       
