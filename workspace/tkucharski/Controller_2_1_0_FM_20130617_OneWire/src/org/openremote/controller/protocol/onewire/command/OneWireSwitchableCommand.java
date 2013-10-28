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

import java.io.IOException;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.onewire.OneWireLogger;
import org.owfs.jowfsclient.OwfsConnection;
import org.owfs.jowfsclient.OwfsException;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireSwitchableCommand extends OneWireWriteCommand implements EventListener {

	/**
	 * This method is overriden because it converts value send from OpenRemote server to on/off values. If proper value is sent (on/off) it stays untouched.
	 * @param dynamicValue dynamic value overriding data parameter
	 */
	@Override
	public void setDynamicValue(String dynamicValue) {
		if (dynamicValue != null) {
			super.setDynamicValue(OneWireSwitchSensorState.onOffValue(dynamicValue));
		}
	}

	/**
	 * Executes switch command. Read value from 1-wire if device value is unknown to negate value correctly.
	 * Updates value only if writing to 1-wire did not throw exception
	 *
	 * @param connection owserver connection
	 */
	@Override
	public void execute(OwfsConnection connection) throws IOException, OwfsException {
		if (isItFirstExecution()) {
			tryToReadInitialValue();
		}
		String value = OneWireSwitchSensorState.negateToNumerical(dynamicValue != null ? dynamicValue : getDevice().getValue());
		connection.write(getDevice().getPath(), value);
		getDevice().setValue(OneWireSwitchSensorState.onOffValue(value));
	}

	private boolean isItFirstExecution() {
		return getDevice().getValue() == null;
	}

	private void tryToReadInitialValue() {
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			String value = connection.read(getDevice().getPath());
			getDevice().setValue(OneWireSwitchSensorState.onOffValue(value));
		} catch (Exception e) {
			OneWireLogger.warn("OneWire cannot read initial value for command: " + this);
		}
	}
}
