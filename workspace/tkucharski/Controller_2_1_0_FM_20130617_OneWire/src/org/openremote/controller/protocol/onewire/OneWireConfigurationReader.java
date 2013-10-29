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
import org.openremote.controller.protocol.onewire.container.OneWireDeviceConfiguration;
import org.openremote.controller.protocol.onewire.container.OneWireHost;
import org.openremote.controller.utils.Strings;

/**
 * Provide wrapper and abstraction for XML command configuration. It reads data directly from JDOM element providing API to read its values via properties
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireConfigurationReader {

	private final static String ALARMING = "ALARMING";

	private final static String SWITCHABLE = "SWITCHABLE";

	private final static String XML_PROPERTY_CONFIG_NAME = "name";

	private final static String XML_PROPERTY_CONFIG_HOSTNAME = "hostname";

	private final static String XML_PROPERTY_CONFIG_PORT = "port";

	private final static String XML_PROPERTY_CONFIG_DEVICE_ADDRESS = "deviceAddress";

	private final static String XML_PROPERTY_CONFIG_FILENAME = "filename";

	private final static String XML_PROPERTY_CONFIG_DATA = "data";

	/**
	 * It is sensor specific property and is used only in setSensor method. It should be sensor specific parameter
	 */
	private final static String XML_PROPERTY_CONFIG_POLLING_INTERVAL = "pollingInterval";

	private final static String XML_PROPERTY_DYNAMIC = org.openremote.controller.command.Command.DYNAMIC_VALUE_ATTR_NAME;

	private final static int CONFIG_PORT_DEFAULT = 4304;

	/**
	 * Single instance of default configuration. If changes are made in default configuration all OpenRemote server has to be restarted due to lack of
	 * command builder lifecycle.
	 */
	private static OneWireDefaultConfiguration controllerConfiguration;

	private Map<String, String> values = new HashMap<String, String>();

	/**
	 * Root element representing single command configuration
	 */
	public OneWireConfigurationReader(Element rootElement) {
		if (controllerConfiguration == null) {
			controllerConfiguration = new OneWireDefaultConfiguration();
		}
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

	public OneWireDeviceConfiguration getDeviceConfiguration() {
		OneWireDeviceConfiguration oneWireDeviceConfiguration = new OneWireDeviceConfiguration();
		oneWireDeviceConfiguration.setOneWireHost(getOneWireHost());
		oneWireDeviceConfiguration.setDeviceAddress(getDeviceAddress());
		oneWireDeviceConfiguration.setDeviceProperty(getFilenameProperty());
		return oneWireDeviceConfiguration;
	}

	public OneWireHost getOneWireHost() {
		OneWireHost oneWireHost = new OneWireHost();
		oneWireHost.setHostname(getHostName());
		oneWireHost.setPort(getPortNumber());
		oneWireHost.setTemperatureScale(controllerConfiguration.getTemperatureScale());
		oneWireHost.setAlarmingInitialDelay(controllerConfiguration.getAlarmingInitialDelay());
		return oneWireHost;
	}

	public boolean isAlarming() {
		return OneWireConfigurationReader.ALARMING.equals(getFilenameProperty());
	}

	public boolean isSwitchable() {
		return OneWireConfigurationReader.SWITCHABLE.equals(getDataProperty());
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
			OneWireLogger.warn("Invalid property " + XML_PROPERTY_CONFIG_PORT + ". Found: '" + portNumber + "'. Using default port (" + CONFIG_PORT_DEFAULT + ")");
			return CONFIG_PORT_DEFAULT;
		}

	}

	public String getCommandName() {
		return values.get(XML_PROPERTY_CONFIG_NAME);
	}

	public String getDeviceAddress() {
		return values.get(XML_PROPERTY_CONFIG_DEVICE_ADDRESS);
	}

	public String getFilenameProperty() {
		return values.get(XML_PROPERTY_CONFIG_FILENAME);
	}

	public String getDataProperty() {
		return values.get(XML_PROPERTY_CONFIG_DATA);
	}

	public String getDynamicValue() {
		return values.get(XML_PROPERTY_DYNAMIC);
	}

	/**
	 * It returns null (meaning no value specified) if
	 * 1. value is not specified or in invalid format
	 * 2. value is empty
	 * 3. value equals 0 (so far designer requires polling interval to be specified
	 * If none of it is true value specified in this field is transformed from String to Integer using Strings.convertPollingIntervalString method
	 *
	 * @return value baes
	 */
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
				int value = Strings.convertPollingIntervalString(elementValue);
				if (value > 0) {
					return value;
				} else {
					return null;
				}
			}
		} catch (NumberFormatException e) {
			OneWireLogger.warn("Invalid property " + XML_PROPERTY_CONFIG_POLLING_INTERVAL + ". Found: '" + elementValue + "'.");
			throw new NoSuchCommandException("Invalid property " + XML_PROPERTY_CONFIG_POLLING_INTERVAL + " in command " + getCommandName() +
					". Found: '" + elementValue + "'."
			);
		}
	}
}
