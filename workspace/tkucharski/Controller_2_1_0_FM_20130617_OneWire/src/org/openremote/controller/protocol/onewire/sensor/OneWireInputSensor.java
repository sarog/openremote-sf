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
package org.openremote.controller.protocol.onewire.sensor;

import java.util.Map;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.protocol.onewire.OneWireLogger;
import org.openremote.controller.protocol.onewire.command.OneWireSwitchSensorState;
import org.owfs.jowfsclient.device.SwitchAlarmingDeviceEvent;
import org.springframework.security.util.FieldUtils;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireInputSensor implements OneWireSensor<SwitchAlarmingDeviceEvent> {

	private static final String PROPERTY_INPUT = "INPUT";

	private static final String STATE_SENSOR_INTERNAL_FIELD_NAME = "states";

	private final Sensor sensor;

	private int inputNumber;

	public OneWireInputSensor(Sensor sensor) {
		this.sensor = sensor;
		loadInputNumberFromSensorConfiguration();
	}

	/**
	 * Updates sensor value. As it is alarming device value is stored in "true,false,true..." format
	 *
	 * @param newEvent alarm received from owserver via jowfsclient
	 */
	@Override
	public void update(SwitchAlarmingDeviceEvent oldEvent, SwitchAlarmingDeviceEvent newEvent) {
		boolean[] latchStatus = newEvent.getLatchStatusAsArray();
		if (inputNumber >= latchStatus.length || inputNumber < 0) {
			OneWireLogger.warn("Invalid " + PROPERTY_INPUT + " property configuration in " + sensor.getName() + " sensor. " +
					"Value must be between 0 and " + (latchStatus.length - 1)
			);
		} else if (latchStatus[inputNumber]) {
			//if this is first execution oldEvent is null
			boolean oldState = oldEvent != null && oldEvent.getSensedStatusAsArray()[inputNumber];
			boolean newState = newEvent.getSensedStatusAsArray()[inputNumber];
			setState(oldState, newState);
		}
	}

	/**
	 * Possible states of sensed value (old->new)
	 * OFF->OFF (input was pressed and released between scanning) - sensor assume that someone clicked it very fast and owfsclient didn't get sensed value
	 * OFF->ON  (input is being pressed)
	 * ON->OFF  (input was released, but it was pressed since last scanning)
	 * ON->ON   (input was released and pressed between scanning)
	 */
	public void setState(boolean state, boolean newState) {
		OneWireLogger.info(sensor.getName() + ": " + OneWireSwitchSensorState.onOffValue(state) + "->" + OneWireSwitchSensorState.onOffValue(newState));
		if (!state && !newState) {
			update(true);
			update(false);
		} else if (!state) {
			update(true);
		} else if (!newState) {
			update(false);
		} else {
			update(false);
			update(true);
		}
	}

	private void update(boolean newValue) {
		String state = OneWireSwitchSensorState.onOffValue(newValue);
		OneWireLogger.info(sensor.getName()+": "+state);
		sensor.update(state);
	}

	private void loadInputNumberFromSensorConfiguration() {
		try {
			StateSensor.DistinctStates fieldValue = (StateSensor.DistinctStates) FieldUtils.getFieldValue(sensor, STATE_SENSOR_INTERNAL_FIELD_NAME);
			Map<String, String> stateSensorInternalMap = (Map<String, String>) FieldUtils.getFieldValue(fieldValue, STATE_SENSOR_INTERNAL_FIELD_NAME);
			inputNumber = convertValueToInputNumber(stateSensorInternalMap.get(PROPERTY_INPUT));
		} catch (IllegalAccessException e) {
			OneWireLogger.error("Could not access " + STATE_SENSOR_INTERNAL_FIELD_NAME + " private field in class StateSensor. Sensor: " + sensor, e);
			throw new NoSuchCommandException("Could not access " + STATE_SENSOR_INTERNAL_FIELD_NAME + " private field in class StateSensor. Sensor: " + sensor);
		}
	}

	int convertValueToInputNumber(String inputNumber) {
		try {
			return Integer.parseInt(inputNumber);
		} catch (NumberFormatException e) {
			OneWireLogger.warn("Invalid " + PROPERTY_INPUT + " property configuration in " + sensor.getName() + " sensor.");
			throw new NoSuchCommandException("Invalid " + PROPERTY_INPUT + " property configuration in " + sensor.getName() + " sensor.", e);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OneWireInputSensor{");
		sb.append("sensor=").append(sensor.getName());
		sb.append(", input=").append(inputNumber);
		sb.append('}');
		return sb.toString();
	}
}
