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
import org.openremote.controller.protocol.onewire.OneWireConfigurationReader;
import org.openremote.controller.protocol.onewire.container.OneWireDevice;
import org.owfs.jowfsclient.OwfsConnectionFactory;

/**
 * Basic 1-wire protocol command implementation for OpenRemote. Program accesses 1-wire network (owserver) via
 * library jowfsclient.
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireCommand<T> implements Command {

	/**
	 * Command name
	 */
	private String commandName;

	/**
	 * Connection factory for given connection
	 */
	protected OwfsConnectionFactory owfsConnectorFactory;

	/**
	 * device connected to this command. Device is found and installed in OneWireCommandBuilder
	 */
	private OneWireDevice<T> device;

	/**
	 * device attribute value to be set, should be overwritten by dynamic value if dynamic value is defined
	 */
	protected String deviceData;

	/**
	 * dynamic value, handled in subclasses
	 */
	protected String dynamicValue;

	public void setDevice(OneWireDevice<T> device) {
		this.device = device;
	}

	public OneWireDevice<T> getDevice() {
		return device;
	}

	public void setOwfsConnectorFactory(OwfsConnectionFactory owfsConnectorFactory) {
		this.owfsConnectorFactory = owfsConnectorFactory;
	}

	public OwfsConnectionFactory getOwfsConnectorFactory() {
		return owfsConnectorFactory;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}

	public void setDynamicValue(String dynamicValue) {
		this.dynamicValue = dynamicValue;
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
				.append("commandName='").append(commandName).append('\'')
				.append("host=")
				.append("'" + owfsConnectorFactory.getConnectionConfig().getHostName() + ":" + owfsConnectorFactory.getConnectionConfig().getPortNumber() + "'")
				.append(", deviceName='").append(getDevice().getAddress()).append('\'')
				.append(", deviceProperty='").append(getDevice().getProperty()).append('\'')
				.append(", deviceData='").append(deviceData).append('\'')
				.append(", dynamicValue='").append(dynamicValue).append('\'');
	}

	public void configure(OneWireConfigurationReader configuration) {
		setCommandName(configuration.getCommandName());
		setDeviceData(configuration.getDataProperty());
		setDynamicValue(configuration.getDynamicValue());
	}
}
