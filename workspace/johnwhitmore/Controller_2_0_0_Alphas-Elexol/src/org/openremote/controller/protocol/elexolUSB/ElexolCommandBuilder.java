/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
     * String constants for parsing Elexol USB protocol XML entries from controller.xml file.
     *
     * <pre>{@code
     * <command protocol = "elexol-USB" >
     *   <attr name = "usbPort" label = "USB Port">
     *   <attr name = "ioPort" label = "I/O Port">
     *   <attr name = "pinNumber" label = "I/O Pin">
     *   <attr name = "command" label = "Command">
     *   <attr name = "pulseDuration" label = "Pulse Duration">
     * </command>
     * }</pre>
     */
    public final static String ELEXOL_USB_XMLPROPERTY_USB_PORT = "usbPort";
    public final static String ELEXOL_USB_XMLPROPERTY_IO_PORT = "ioPort";
    public final static String ELEXOL_USB_XMLPROPERTY_PIN_NUMBER = "pinNumber";
    public final static String ELEXOL_USB_XMLPROPERTY_COMMAND = "command";
    public final static String ELEXOL_USB_XMLPROPERTY_PULSE_DURATION = "pulseDuration";


    // Class Members --------------------------------------------------------------------------------

    /**
     * Logging. Use common Elexol USB log category.
     */
    public static Logger log = Logger.getLogger(ELEXOL_USB_LOG_CATEGORY);
    
    /**
     * Parses the Elexol USB command XML snippets and builds a corresponding command instance.  <p>
     *
     * The expected XML structure is:
     *
     * <pre>{@code
     * <command protocol = "elexol-USB" >
     *   <attr name = "usbPort" label = "USB Port">
     *   <attr name = "ioPort" label = "I/O Port">
     *   <attr name = "pinNumber" label = "I/O Pin">
     *   <attr name = "command" label = "Command">
     *   <attr name = "pulseDuration" label = "Pulse Duration">
     * </command>
     * }</pre>
     *
     * Additional properties not listed here are ignored.
     *
     * @see ElexolCommand
     *
     * @throws org.openremote.controller.exception.NoSuchCommandException
     *            if the Elexol command instance cannot be constructed from the XML snippet
     *            for any reason
     *
     * @return an Elexol command instance with known configured properties set
     */
    public Command build(Element element)
    {
	String usbPort = null;
	String ioPort = null;
	String pinNumber = null;
	String commandString = null;
	String pulseDuration = null;
	Integer duration = 0;
	
	// Properties come in as child elements...
	
	List<Element> propertyElements = element.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY,
							     element.getNamespace());

	for (Element el : propertyElements){
	    String commandPropertyName = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
	    String commandPropertyValue = el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

	    if (ELEXOL_USB_XMLPROPERTY_USB_PORT.equalsIgnoreCase(commandPropertyName)){
		usbPort = commandPropertyValue;
	    }
	    else if (ELEXOL_USB_XMLPROPERTY_IO_PORT.equalsIgnoreCase(commandPropertyName)){
		ioPort = commandPropertyValue;
	    }
	    else if (ELEXOL_USB_XMLPROPERTY_PIN_NUMBER.equalsIgnoreCase(commandPropertyName)){
		pinNumber = commandPropertyValue;
	    }
	    else if (ELEXOL_USB_XMLPROPERTY_COMMAND.equalsIgnoreCase(commandPropertyName)){
		commandString = commandPropertyValue;
	    }
	    else if (ELEXOL_USB_XMLPROPERTY_PULSE_DURATION.equalsIgnoreCase(commandPropertyName)){
		pulseDuration = commandPropertyValue;
	    }
	    else{
		log.warn(
		     "Unknown Elexol USB property '<" + XML_ELEMENT_PROPERTY + " " +
		     XML_ATTRIBUTENAME_NAME + " = \"" + commandPropertyName + "\" " +
		     XML_ATTRIBUTENAME_VALUE + " = \"" + commandPropertyValue + "\"/>'."
		     );
	    }
	}

	// sanity checks...
	if (usbPort == null || ("").equals(usbPort)){
	    throw new NoSuchCommandException(
		      "Elexol USB command is missing a mandatory '" + ELEXOL_USB_XMLPROPERTY_USB_PORT + "' property"
					     );
	}

	if (ioPort == null || ("").equals(ioPort)){
	    throw new NoSuchCommandException(
		      "Elexol USB command is missing a mandatory '" + ELEXOL_USB_XMLPROPERTY_IO_PORT + "' property"
					     );
	}

	if (pinNumber == null || ("").equals(pinNumber)){
	    throw new NoSuchCommandException(
		      "Elexol USB command is missing a mandatory '" + ELEXOL_USB_XMLPROPERTY_PIN_NUMBER + "' property"
					     );
	}

	if (commandString == null || ("").equals(commandString)){
	    throw new NoSuchCommandException(
		      "Elexol USB command is missing a mandatory '" + ELEXOL_USB_XMLPROPERTY_COMMAND + "' property"
					     );
	}

	// Translate the command string to a type safe ElexolUSBCommandType enum...

	CommandType command = null;

	if (CommandType.SWITCH_ON.isEqual(commandString)){
	    command = CommandType.SWITCH_ON;
	}
	else if (CommandType.SWITCH_OFF.isEqual(commandString)){
	    command = CommandType.SWITCH_OFF;
	}
	else if (CommandType.PULSE.isEqual(commandString)){
	    command = CommandType.PULSE;

	    if (pulseDuration == null || ("").equals(pulseDuration)){
		/*
		 * If no Pulse Duration is given use 50mS as default
		 */
		duration = 50;
	    } else {
		duration = Integer.parseInt(pulseDuration);
	    }
	}
	else{
	    throw new NoSuchCommandException("Elexol USB command '" + commandString + "' is not recognized.");
	}

	PortType port = null;

	if (PortType.PORT_A.isEqual(ioPort)){
	    port = PortType.PORT_A;
	}
	else if (PortType.PORT_B.isEqual(ioPort)){
	    port = PortType.PORT_B;
	}
	else if (PortType.PORT_C.isEqual(ioPort)){
	    port = PortType.PORT_C;
	}
	else{
	    throw new NoSuchCommandException("Elexol USB I/O port '" + ioPort + "' is not recognized.");
	}


	PinType pin = PinType.convert(pinNumber);

	if (pin == null){
	    throw new NoSuchCommandException("Elexol USB I/O Pin Number '" + pinNumber + "' is not recognized.");
	} 


	ElexolCommand cmd = new ElexolCommand(usbPort, port, pin, command, duration);

	log.debug("ElexolCommand Created");

	return cmd;
    }
}
       
