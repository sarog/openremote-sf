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

import org.owfs.jowfsclient.OwfsConnection;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireSwitchableCommand extends OneWireExecutableCommand {

	public static final String OFF = "0";
	public static final String ON = "1";

	@Override
	public void setDynamicValue(String dynamicValue) {
		log.debug(this.toString()+" new dynamicValue:'"+dynamicValue+"'");
	}

	@Override
	public void send() {
		if (isItFirstExecution()) {
			tryToReadInitialValue();
		}
		negateDynamicValue();
		super.send();
	}

	/**
	 * Rollback current state value as command was not sent to owfs server
	 * @param e exception thrown during send method
	 */
	@Override
	protected void handleException(Exception e) {
		negateDynamicValue();
		super.handleException(e);
	}

	public boolean isItFirstExecution() {
		return dynamicValue == null;
	}

	private void tryToReadInitialValue() {
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			String value = connection.read(deviceName + "/" + devicePropertyName);
			setState(value);
		} catch (Exception e) {
			log.warn("OneWire cannot read initial value for command: "+this);
			setState(OFF);
		}
	}

	public void setState(String value) {
		if (ON.equals(value)) {
			dynamicValue = ON;
		} else {
			dynamicValue = OFF;
		}
	}

	public void negateDynamicValue() {
		if (ON.equals(dynamicValue)) {
			dynamicValue = OFF;
		} else {
			dynamicValue = ON;
		}
	}
}
