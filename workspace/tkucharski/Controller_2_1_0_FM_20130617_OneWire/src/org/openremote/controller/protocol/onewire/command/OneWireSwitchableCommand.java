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

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.owfs.jowfsclient.OwfsConnection;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireSwitchableCommand extends OneWireExecutableCommand implements EventListener {

	private State state;

	private Sensor sensor;

	@Override
	public void setDynamicValue(String dynamicValue) {
		if (dynamicValue != null) {
			setState(State.convert(dynamicValue));
		}
	}

	@Override
	public void send() {
		if (isItFirstExecution()) {
			tryToReadInitialValue();
		}
		setState(state.negate());
		super.send();
		notifySensor();
	}

	/**
	 * Rollback current state value as command was not sent to owfs server
	 *
	 * @param e exception thrown during send method
	 */
	@Override
	protected void handleException(Exception e) {
		setState(state.negate());
		super.handleException(e);
	}

	private boolean isItFirstExecution() {
		return dynamicValue == null;
	}

	private void tryToReadInitialValue() {
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			String value = connection.read(deviceName + "/" + devicePropertyName);
			setState(State.valueOf(value));
		} catch (Exception e) {
			log.warn("OneWire cannot read initial value for command: " + this);
			setState(State.off);
		}
	}

	private void setState(State newState) {
		state = newState;
		super.setDynamicValue(newState.name());
	}

	private void notifySensor() {
		if (sensor != null) {
			log.info("update sensor:" + sensor.getName() + " with value '" + state + "'");
			sensor.update(state.name());
		}
	}

	@Override
	public void setSensor(Sensor sensor) {
		log.info("Installing sensor on " + this.toString());
		this.sensor = sensor;
	}

	@Override
	public void stop(Sensor sensor) {
		log.info("Uninstalling sensor on " + this.toString());
		this.sensor = null;
	}

	enum State {
		on("1"),
		off("2");

		private String oneWireState;

		State(String oneWireState) {
			this.oneWireState = oneWireState;
		}

		public static State convert(String s) {
			for (State state : values()) {
				if (state.oneWireState.equals(s) || state.name().equals(s)) {
					return state;
				}
			}
			return off;
		}

		public State negate() {
			if (this == on) {
				return off;
			} else {
				return on;
			}
		}

	}
}
