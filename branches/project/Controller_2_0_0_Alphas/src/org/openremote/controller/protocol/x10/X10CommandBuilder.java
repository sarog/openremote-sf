/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.x10;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.CommandBuildException;

import java.util.List;

/**
 * X10CommandBuilder is responsible for parsing the XML model from controller.xml and create
 * appropriate Command objects for it. <p>
 *
 * The structure of a X10 command XML snippet from controller.xml is shown below:
 *
 * <pre>{@code
 * <command protocol = "x10" >
 *   <property name = "command" value = "ON|OFF|ALL_OFF"/>
 *   <property name = "address" value = "[A..P][1..16]"/>
 * </command>
 * }</pre>
 *
 * The protocol identifier is "x10" as is shown in the command element's protocol attribute. <p>
 *
 * Nested are a number of properties to be included with the KNX command. Properties named
 * {@link #X10_XMLPROPERTY_COMMAND} and {@link #X10_XMLPROPERTY_ADDRESS} are mandatory. <p>
 *
 * Valid X10 addresses range from A1..A16 to P1..P16. The valid values for command
 * property are implemented in {@link X10CommandType} -- command values can be localized.
 *
 * @see org.openremote.controller.command.CommandBuilder
 * @see X10CommandType
 *
 * @author Jerome Velociter
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 * @author Dan 2009-4-30
 */
public class X10CommandBuilder implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * A common log category name intended to be used across all classes related to
   * X10 implementation.
   */
  public final static String X10_LOG_CATEGORY = "X10";

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



  // Instance Fields ------------------------------------------------------------------------------

  private X10ControllerManager connectionManager = new X10ControllerManager();



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

     List<Element> propertyElements = element.getChildren("property", element.getNamespace());

     for (Element el : propertyElements)
     {
       String x10CommandPropertyName = el.getAttributeValue("name");

       if (X10_XMLPROPERTY_ADDRESS.equals(x10CommandPropertyName))
       {
         address = el.getAttributeValue("value");
       }
       else if (X10_XMLPROPERTY_COMMAND.equals(x10CommandPropertyName))
       {
         commandAsString = el.getAttributeValue("value");
       }
     }

     if (commandAsString == null || commandAsString.trim().equals("") ||
         address == null || address.trim().equals(""))
     {
       throw new CommandBuildException(
          "Can not build a X10Command with empty command: " + commandAsString +
          " or address: " + address
       );
     }

     //String address = element.getAttributeValue(ADDRESS_XML_ATTRIBUTE);
     //String commandAsString = element.getAttributeValue(COMMAND_XML_ATTRIBUTE);

     X10CommandType commandType = null;

     if (X10CommandType.ALL_UNITS_OFF.isEqual(commandAsString))
     {
       commandType = X10CommandType.ALL_UNITS_OFF;
     }
     else if (X10CommandType.SWITCH_ON.isEqual(commandAsString))
     {
       commandType = X10CommandType.SWITCH_ON;
     }
     else if (X10CommandType.SWITCH_OFF.isEqual(commandAsString))
     {
       commandType = X10CommandType.SWITCH_OFF;
     }

// TODO : integrate ${param} handling
//        CommandUtil.parseStringWithParam(element, ele.getAttributeValue("value")

     X10Command event = new X10Command(connectionManager, address, commandType);

     return event;
   }

}
       