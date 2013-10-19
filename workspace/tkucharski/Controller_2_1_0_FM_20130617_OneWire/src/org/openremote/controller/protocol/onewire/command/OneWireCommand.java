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
package org.openremote.controller.protocol.onewire.command;

import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.onewire.OneWireLoggerFactory;
import org.openremote.controller.utils.Logger;
import org.owfs.jowfsclient.OwfsConnectionFactory;

/**
 * Basic 1-wire protocol command implementation for OpenRemote. Program accesses 1-wire network (owserver) via
 * library jowfsclient.
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireCommand implements Command {

	protected final static Logger log = OneWireLoggerFactory.getLogger();

	/**
	 * Connection factory for given connection
	 */
	protected OwfsConnectionFactory owfsConnectorFactory;

	/**
	 * address of the 1-wire device, such as /1F.E9E803000000/main/28.25E9E3010000
	 */
	protected String deviceName;

	/**
	 * sensor attribute - is filename in owfs that holds values, such as "temperature", "temperature9", "humidity" or similar
	 */
	protected String devicePropertyName;

	/**
	 * dynamic value, handled in subclasses
	 */
	protected String dynamicValue;

	public void setOwfsConnectorFactory(OwfsConnectionFactory owfsConnectorFactory) {
		this.owfsConnectorFactory = owfsConnectorFactory;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setDevicePropertyName(String devicePropertyName) {
		this.devicePropertyName = devicePropertyName;
	}

	public void setDynamicValue(String dynamicValue) {
		log.debug(this+" new dynamic value: '"+dynamicValue+"'");
		this.dynamicValue = dynamicValue;
	}

	/**
	 * Validate if basic devices property are set correctly
	 * @throws NoSuchCommandException if validation fails
	 */
	public void validate() throws NoSuchCommandException {
		if (deviceName == null || devicePropertyName == null || owfsConnectorFactory.getConnectionConfig().getHostName() == null) {
			throw new NoSuchCommandException("Unable to create OneWireCommand, missing configuration parameter(s). "+this);
		}
	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder(getClass().getSimpleName() + '{')
				.append(toStringParameterOnly().toString())
				.append('}');
		return sb.toString();
	}

	public StringBuilder toStringParameterOnly() {
		return new StringBuilder()
				.append("host=")
				.append("'"+owfsConnectorFactory.getConnectionConfig().getHostName()+":"+owfsConnectorFactory.getConnectionConfig().getPortNumber()+"'")
				.append(", deviceName='").append(deviceName).append('\'')
				.append(", devicePropertyName='").append(devicePropertyName).append('\'');
	}
}
