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
package org.openremote.controller.protocol.onewire;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 * Provide wrapper and abstraction for XML command configuration. It reads data directly from JDOM element providing API to read its values via properties
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireConfigurationReader {

	private final static Logger logger = OneWireLoggerFactory.getLogger();

	private final static String XML_COMMAND_ID = "id";

	private final static String XML_PROPERTY_CONFIG_HOSTNAME = "hostname";

	private final static String XML_PROPERTY_CONFIG_PORT = "port";

	private final static String XML_PROPERTY_CONFIG_DEVICE_ADDRESS = "deviceAddress";

	private final static String XML_PROPERTY_CONFIG_FILENAME = "filename";

	private final static String XML_PROPERTY_CONFIG_POLLING_INTERVAL = "pollingInterval";

	private final static String XML_PROPERTY_DYNAMIC = org.openremote.controller.command.Command.DYNAMIC_VALUE_ATTR_NAME;

	private final static int CONFIG_PORT_DEFAULT = 4304;

	/**
	 * Root element representing single command configuration
	 */
	private final Element rootElement;

	private static OneWireDefaultConfiguration controllerConfiguration;

	public OneWireConfigurationReader(Element rootElement) {
		if (controllerConfiguration == null) {
			controllerConfiguration = new OneWireDefaultConfiguration();
		}
		this.rootElement = rootElement;
	}

	/**
	 * Returns unique command id
	 *
	 * @return
	 */
	public String getCommandId() {
		return rootElement.getAttributeValue(XML_COMMAND_ID);
	}

	/**
	 * Actually command type is not defined within xml configuration. As a result its type is defined in different properties.
	 * See OneWireCommandType enum for details
	 *
	 * @return
	 */
	public OneWireCommandType getCommandType() {
		if (OneWireCommandType.ALARMING.toString().equals(getFilenameProperty())) {
			return OneWireCommandType.ALARMING;
		} else if (OneWireCommandType.SWITCHABLE.toString().equals(getDataProperty())) {
			return OneWireCommandType.SWITCHABLE;
		} else if (getPollingInterval() != null) {
			return OneWireCommandType.INTERVAL;
		} else {
			return OneWireCommandType.EXECUTABLE;
		}
	}

	public OneWireHost getOneWireHost() {
		OneWireHost oneWireHost = new OneWireHost();
		oneWireHost.setHostname(getHostName());
		oneWireHost.setPort(getPortNumber());
		oneWireHost.setTemperatureScale(controllerConfiguration.getTemperatureScale());
		return oneWireHost;
	}

	/**
	 * Retrieves command host name. If not specified it loads default from global configuration
	 *
	 * @return owserver host name
	 */
	public String getHostName() {
		String s = readPropertyValue(XML_PROPERTY_CONFIG_HOSTNAME);
		if (StringUtils.isEmpty(s)) {
			return controllerConfiguration.getHost();
		} else {
			return s;
		}
	}

	/**
	 * Retrieves command port number. If not specified it loads default from global configuration.
	 *
	 * @return owserver port number
	 */
	public int getPortNumber() {
		String portNumber = readPropertyValue(XML_PROPERTY_CONFIG_PORT);
		if (StringUtils.isEmpty(portNumber)) {
			portNumber = controllerConfiguration.getPort();
		}
		try {
			return Integer.parseInt(portNumber);
		} catch (NumberFormatException e) {
			logger.warn("Invalid property " + XML_PROPERTY_CONFIG_PORT + ". Found: '" + portNumber + "'. Using default port (" + CONFIG_PORT_DEFAULT + ")");
			return CONFIG_PORT_DEFAULT;
		}

	}

	public String getDeviceName() {
		return readPropertyValue(XML_PROPERTY_CONFIG_DEVICE_ADDRESS);
	}

	public String getFilenameProperty() {
		String property = readPropertyValue(XML_PROPERTY_CONFIG_FILENAME);
		if (property == null) {
			return null;
		} else {
			String[] split = property.split("=");
			return split[0];
		}
	}

	public String getDataProperty() {
		String property = readPropertyValue(XML_PROPERTY_CONFIG_FILENAME);
		if (property == null) {
			return null;
		} else {
			String[] split = property.split("=");
			if (split.length == 1) {
				return null;
			} else {
				return split[split.length - 1];
			}
		}
	}

	public Integer getPollingInterval() {
		String elementValue;
		try {
			elementValue = readPropertyValue(XML_PROPERTY_CONFIG_POLLING_INTERVAL);
		} catch (Exception e) {
			return null;
		}
		try {
			if (null == elementValue || elementValue.trim().length() == 0) {
				return null;
			} else {
				return Strings.convertPollingIntervalString(elementValue);
			}
		} catch (NumberFormatException e) {
			logger.warn("Invalid property " + XML_PROPERTY_CONFIG_POLLING_INTERVAL + ". Found: '" + elementValue + "'.");
			throw new NoSuchCommandException("Invalid property " + XML_PROPERTY_CONFIG_POLLING_INTERVAL + " in device " + getCommandId() +
					". Found: '" + elementValue + "'."
			);
		}
	}

	public String readPropertyValue(String propertyName) {
		List<Element> children = rootElement.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY, rootElement.getNamespace());
		for (Element child : children) {
			String elementName = child.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
			if (elementName.equals(propertyName)) {
				return child.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
			}
		}
		return null;
	}

	public String getDynamicValue() {
		try {
			return readPropertyValue(XML_PROPERTY_DYNAMIC);
		} catch (Exception e) {
			return null;
		}
	}
}
