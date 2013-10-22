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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public final static String ALARMING = "ALARMING";

	public final static String SWITCHABLE = "SWITCHABLE";

	private final static Logger logger = OneWireLoggerFactory.getLogger();

	private final static String XML_COMMAND_ID = "id";

	private final static String XML_PROPERTY_CONFIG_HOSTNAME = "hostname";

	private final static String XML_PROPERTY_CONFIG_PORT = "port";

	private final static String XML_PROPERTY_CONFIG_DEVICE_ADDRESS = "deviceAddress";

	private final static String XML_PROPERTY_CONFIG_FILENAME = "filename";

	private final static String XML_PROPERTY_CONFIG_DATA = "data";

	private final static String XML_PROPERTY_CONFIG_POLLING_INTERVAL = "pollingInterval";

	private final static String XML_PROPERTY_DYNAMIC = org.openremote.controller.command.Command.DYNAMIC_VALUE_ATTR_NAME;

	private final static int CONFIG_PORT_DEFAULT = 4304;

	private static OneWireDefaultConfiguration controllerConfiguration;

	private String commandId;

	private Map<String, String> values = new HashMap<String, String>();

	/**
	 * Root element representing single command configuration
	 */
	public OneWireConfigurationReader(Element rootElement) {
		if (controllerConfiguration == null) {
			controllerConfiguration = new OneWireDefaultConfiguration();
		}
		commandId = rootElement.getAttributeValue(XML_COMMAND_ID);
		saveConfigurationInMap(rootElement);
	}

	public void saveConfigurationInMap(Element rootElement) {
		List<Element> children = rootElement.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY, rootElement.getNamespace());
		for (Element child : children) {
			String key = child.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
			String value = child.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
			values.put(key, value);
		}
	}

	/**
	 * Returns unique command id
	 *
	 * @return
	 */
	public String getCommandId() {
		return commandId;
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
		String s = values.get(XML_PROPERTY_CONFIG_HOSTNAME);
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
		String portNumber = values.get(XML_PROPERTY_CONFIG_PORT);
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
		return values.get(XML_PROPERTY_CONFIG_DEVICE_ADDRESS);
	}

	public String getFilenameProperty() {
		return values.get(XML_PROPERTY_CONFIG_FILENAME);
	}

	public String getDataProperty() {
		return values.get(XML_PROPERTY_CONFIG_DATA);
	}

	public Integer getPollingInterval() {
		String elementValue;
		try {
			elementValue = values.get(XML_PROPERTY_CONFIG_POLLING_INTERVAL);
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

	public String getDynamicValue() {
		try {
			return values.get(XML_PROPERTY_DYNAMIC);
		} catch (Exception e) {
			return null;
		}
	}
}
