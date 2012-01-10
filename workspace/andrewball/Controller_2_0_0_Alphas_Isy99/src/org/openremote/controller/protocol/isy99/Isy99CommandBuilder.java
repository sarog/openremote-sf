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
package org.openremote.controller.protocol.isy99;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * 
 * based on work for the lutron
 * 
 * @author <a href="mailto:andrew.puch.1@gmail.com">Andrew Puch </a>
 *
 */
public class Isy99CommandBuilder implements CommandBuilder
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * A common log category name intended to be used across all classes related
   * to isy99 implementation.
   */
  public final static String ISY99_LOG_CATEGORY = "ISY99";

  /**
   * String constant for parsing isy99 protocol XML entries from
   * controller.xml file.
   */
  public final static String ISY99_XMLPROPERTY_ADDRESS = "address";

  /**
   * String constant for parsing  protocol XML entries from
   * controller.xml file.
   */
  public final static String ISY99_XMLPROPERTY_COMMAND = "command";

  // Class Members --------------------------------------------------------------------------------

  /**
   *isy99 logger. Uses a common category for allisy99 related logging.
   */
  private final static Logger log = Logger.getLogger(Isy99CommandBuilder.ISY99_LOG_CATEGORY);

  // Instance Fields ------------------------------------------------------------------------------
  
  private String hostname;
  private String username;
  private String password;

  // Constructors ---------------------------------------------------------------------------------

  /**
   * @param hostname hostname or IP address for the ISY-99
   * @param username username for authentication to the ISY-99
   * @param password password for authentication to the ISY-99
   */
  public Isy99CommandBuilder(String hostname, String username, String password)
  {
    this.hostname = hostname;
    this.username = username;
    this.password = password;
  }

  // Implements EventBuilder ----------------------------------------------------------------------

  /**
   * Parses the isy99  command XML snippets and builds a
   * corresponding isy99  command instance.
   * <p>
   * 
   * The expected XML structure is:
   * 
   * <pre>
   * @code
   * REST API for isy99 
   * http://www.universal-devices.com/mwiki/index.php?title=ISY-99i_Series_INSTEON:REST_Interface
   * TODO remove %20 
   * <command protocol = "isy99" >
   *   <property name = "address" value = "17%2054%20AE%201"/>
   *   <property name = "command" value = "DON"/>
   * </command>
   * }
   * </pre>
   * 
   * Additional properties not listed here are ignored.
   * 
   * @throws NoSuchCommandException
   *             if the isy99  command instance cannot be
   *             constructed from the XML snippet for any reason
   * 
   * @return an immutable isy99  command instance with known
   *         configured properties set
   */
  @Override
  public Command build(Element element)
  {
    String addressAsStr = null;
    String commandAsStr = null;
    String commandParmsAsStr = null;

    // Get the list of properties from XML...
    String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);

    @SuppressWarnings("unchecked")
    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY,
        element.getNamespace());

    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);
      log.debug("parsing controler.xml " + propertyName + " = " + propertyValue);
      if (ISY99_XMLPROPERTY_ADDRESS.equalsIgnoreCase(propertyName))
      {
        addressAsStr = propertyValue;
      }
      else if (ISY99_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName))
      {
        commandAsStr = propertyValue;
      }
      else
      {
        log.warn("Unknown ISY-99 property '<" + XML_ELEMENT_PROPERTY + " " +
            XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE +
            " = \"" + propertyValue + "\"/>'.");
      }

      // 1st dimmer code 
      /* if (null != el.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME) )
      {
        commandParmsAsStr = el.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      }
      */
    }

    // Sanity check on mandatory property'command'

    if (commandAsStr == null || "".equals(commandAsStr))
    {
      throw new NoSuchCommandException("ISY-99 command must have a '" + ISY99_XMLPROPERTY_COMMAND + "' property.");
    }

    if (addressAsStr == null || "".equals(addressAsStr))
    {
      throw new NoSuchCommandException("ISY-99 address  must have a '" + ISY99_XMLPROPERTY_ADDRESS + "' property.");
    }

    // If an address was provided, attempt to buildisy99 Address
    // instance...

    // TODO validate Insteon or X-10 address

//		if (addressAsString != null && !"".equals(addressAsString)) {
//			log.info("Will attemp to build address");
//
//			try {
//				address = newisy99HomeWorksAddress(addressAsString.trim());
//			} catch (InvalidLutronHomeWorksAddressException e) {
//			  log.error("Invalidisy99 HomeWorks address", e);
//				// TODO: re-check, message is not clear when address is invalid
//
//				throw new NoSuchCommandException(e.getMessage(), e);
//			}
//		}

    // Translate the command string to a type safe isy99 Command types...

    // Command cmd =isy99HomeWorksCommand.createCommand(commandAsString, gateway, address, scene, key, level);

    // Needed for dimmer like commands for dynamic values 
    // controller rest calls like                     dimmerSwitch#/Set to value # 
    // defined in controller.xml 
    // http://localhost:8080/controller/rest/control/100/255

    String commandParam = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
    Isy99Command cmd;

    log.info("Created ISY-99 Host "+ hostname + " username "+ username +" Password: " +
        password + " Command " + commandAsStr + " for address '" + addressAsStr + "'" +
        "DYNAMIC_VALUE_ATTR_NAME" + "'" + commandParam + "'");

    if (commandParam == null || commandParam.equals(""))
    {
      cmd = new Isy99Command(hostname, username, password, addressAsStr, commandAsStr);
    }
    else
    {
      cmd = new Isy99Command(hostname, username, password, addressAsStr, commandAsStr,
          commandParam );
    }

    // Done!
    return cmd; 
  }

  // Getters / Setters ----------------------------------------------------------------------------
}
