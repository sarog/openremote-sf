package org.openremote.controller.protocol.lutron;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

public class LutronHomeWorksCommandBuilder implements CommandBuilder {

	// Constants ------------------------------------------------------------------------------------

	/**
	 * A common log category name intended to be used across all classes related
	 * to Lutron implementation.
	 */
	public final static String LUTRON_LOG_CATEGORY = "Lutron";

	/**
	 * String constant for parsing Lutron Homeworks protocol XML entries from
	 * controller.xml file.
	 */
	public final static String LUTRON_XMLPROPERTY_ADDRESS = "address";

	/**
	 * String constant for parsing Lutron Homeworks protocol XML entries from
	 * controller.xml file.
	 */
	public final static String LUTRON_XMLPROPERTY_COMMAND = "command";

	/**
	 * String constant for parsing Lutron Homeworks protocol XML entries from
	 * controller.xml file.
	 */
	public final static String LUTRON_XMLPROPERTY_KEY = "key";

	/**
	 * String constant for parsing Lutron Homeworks protocol XML entries from
	 * controller.xml file.
	 */
	public final static String LUTRON_XMLPROPERTY_SCENE = "scene";
	
	/**
	 * String constant for parsing Lutron Homeworks protocol XML entries from
	 * controller.xml file.
	 */
	public final static String LUTRON_XMLPROPERTY_LEVEL = "level";

	// Class Members --------------------------------------------------------------------------------

	/**
	 * Lutron logger. Uses a common category for all Lutron related logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	// Instance Fields ------------------------------------------------------------------------------

	private LutronHomeWorksGateway gateway;

	// Implements EventBuilder ----------------------------------------------------------------------

	/**
	 * Parses the Lutron HomeWorks command XML snippets and builds a
	 * corresponding Lutron HomeWorks command instance.
	 * <p>
	 * 
	 * The expected XML structure is:
	 * 
	 * <pre>
	 * @code
	 * <command protocol = "lutron_homeworks" >
	 *   <property name = "address" value = ""/>
	 *   <property name = "command" value = ""/>
	 *   <property name = "scene" value = ""/>
	 *   <property name = "key" value = ""/>
	 *   <property name = "level" value = ""/>
	 * </command>
	 * }
	 * </pre>
	 * 
	 * Additional properties not listed here are ignored.
	 * 
	 * @throws NoSuchCommandException
	 *             if the Lutron HomeWorks command instance cannot be
	 *             constructed from the XML snippet for any reason
	 * 
	 * @return an immutable Lutron HomeWorks command instance with known
	 *         configured properties set
	 */
	@Override
	public Command build(Element element) {

		String addressAsString = null;
		String commandAsString = null;
		String sceneAsString = null;
		String keyAsString = null;
		String levelAsString = null;

		LutronHomeWorksAddress address = null;
		Integer scene = null;
		Integer key = null;
		Integer level = null;
		
		// Get the list of properties from XML...

		List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

		for (Element el : propertyElements) {
			String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
			String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

			if (LUTRON_XMLPROPERTY_ADDRESS.equalsIgnoreCase(propertyName)) {
				addressAsString = propertyValue;
			}

			else if (LUTRON_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName)) {
				commandAsString = propertyValue;
			}

			else if (LUTRON_XMLPROPERTY_KEY.equalsIgnoreCase(propertyName)) {
				keyAsString = propertyValue;
			}

			else if (LUTRON_XMLPROPERTY_SCENE.equalsIgnoreCase(propertyName)) {
				sceneAsString = propertyValue;
			}
			
			else if (LUTRON_XMLPROPERTY_LEVEL.equalsIgnoreCase(propertyName)) {
				levelAsString = propertyValue;
			}

			else {
				log.warn("Unknown KNX property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
			}
		}

		// Sanity check on mandatory property'command'

		if (commandAsString == null || "".equals(commandAsString)) {
			throw new NoSuchCommandException("Lutron HomeWorks command must have a '" + LUTRON_XMLPROPERTY_COMMAND + "' property.");
		}

		// If an address was provided, attempt to build Lutron Address
		// instance...

		if (addressAsString != null && !"".equals(addressAsString)) {
			System.out.println("Will attemp to build address");

			try {
				address = new LutronHomeWorksAddress(addressAsString.trim());
			} catch (InvalidLutronHomeWorksAddressException e) {

				// TODO: re-check, message is not clear when address is invalid

				throw new NoSuchCommandException(e.getMessage(), e);
			}
		}

		// If a scene was provided, attempt to convert to integer
		if (sceneAsString != null && !"".equals(sceneAsString)) {
			try {
				scene = Integer.parseInt(sceneAsString);
			} catch (NumberFormatException e) {
				throw new NoSuchCommandException(e.getMessage(), e);
			}
		}
		
		// If a key was provided, attempt to convert to integer
		if (keyAsString != null && !"".equals(keyAsString)) {
			try {
				key = Integer.parseInt(keyAsString);
			} catch (NumberFormatException e) {
				throw new NoSuchCommandException(e.getMessage(), e);
			}
		}
		
		// If a level was provided, attempt to convert to integer
		if (levelAsString != null && !"".equals(levelAsString)) {
			try {
				level = Integer.parseInt(levelAsString);
			} catch (NumberFormatException e) {
				throw new NoSuchCommandException(e.getMessage(), e);
			}
		}
		
		if (level == null) {
			// No specific level provided, check for parameter (passed in from Slider)
			String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
			if (paramValue != null && !paramValue.equals("")) {
				try {
					level = Integer.parseInt(paramValue);
				} catch (NumberFormatException e) {
					throw new NoSuchCommandException(e.getMessage(), e);
				}
			}
		}
		
		// Translate the command string to a type safe Lutron Command types...

		Command cmd = LutronHomeWorksCommand.createCommand(commandAsString, gateway, address, scene, key, level);

		log.info("Created Lutron Command " + cmd + " for address '" + address + "'");

		return cmd;

	}
	
	// Getters / Setters ----------------------------------------------------------------------------

	public LutronHomeWorksGateway getGateway() {
		return gateway;
	}

	public void setGateway(LutronHomeWorksGateway gateway) {
		this.gateway = gateway;
	}

}
