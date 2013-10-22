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

import org.openremote.controller.protocol.EventListener;
import org.owfs.jowfsclient.OwfsConnection;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireSwitchableCommand extends OneWireWriteCommand implements EventListener {

	private OneWireSwitchSensorState state;

	@Override
	public void setDynamicValue(String dynamicValue) {
		if (dynamicValue != null) {
			// convert to negated value as it will be negated once again in execute() method
			setState(OneWireSwitchSensorState.convert(dynamicValue).negate());
		}
	}

	private void setState(OneWireSwitchSensorState newState) {
		state = newState;
		super.setDynamicValue(newState.name());
	}

	@Override
	public void execute(OwfsConnection connection) {
		if (isItFirstExecution()) {
			tryToReadInitialValue();
		}
		setState(state.negate());
		super.execute(connection);
		updateSensor(state.name());
	}

	/**
	 * Rollback current state value as command was not sent to owfs server
	 *
	 * @param e exception thrown during send method
	 */
	@Override
	protected void handleException(Exception e) {
		setState(state.negate());
		log.error("Unable to send command to owfs server. Command: " + this, e);
	}

	private boolean isItFirstExecution() {
		return dynamicValue == null;
	}

	private void tryToReadInitialValue() {
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			String value = connection.read(deviceName + "/" + deviceProperty);
			setState(OneWireSwitchSensorState.valueOf(value));
		} catch (Exception e) {
			log.warn("OneWire cannot read initial value for command: " + this);
			setState(OneWireSwitchSensorState.off);
		}
	}
}
