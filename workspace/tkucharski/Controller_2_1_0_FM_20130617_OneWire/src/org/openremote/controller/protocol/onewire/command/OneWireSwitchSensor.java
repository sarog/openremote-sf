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

import java.util.Map;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.protocol.onewire.OneWireLoggerFactory;
import org.openremote.controller.utils.Logger;
import org.springframework.security.util.FieldUtils;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireSwitchSensor {
	private final static Logger logger = OneWireLoggerFactory.getLogger();

	private static final String STATE_ON = "on";
	private static final String STATE_OFF = "off";

	private static final String PROPERTY_INPUT = "INPUT";

	private static final String STATE_SENSOR_INTERNAL_FIELD_NAME = "states";

	private StateSensor sensor;
	private boolean state;

	public OneWireSwitchSensor(StateSensor sensor) {
		this.sensor = sensor;
	}

	/**
	 * Possible states of sensed value (old->new)
	 * OFF->OFF (input was pressed and released between scanning) - sensor assume that someone clicked it very fast and owfsclient didn't get sensed value
	 * OFF->ON  (input is being pressed)
	 * ON->OFF  (input was released, but it was pressed since last scanning)
	 * ON->ON   (input was released and pressed between scanning)
	 */
	public void setState(boolean newState) {
		logger.debug(sensor.getName() + ", " + (state?1:0) + "->" + (newState?1:0));
		if (!this.state && !newState) {
			sensor.update(STATE_ON);
			sensor.update(STATE_OFF);
		} else if (!this.state) {
			this.state = true;
			sensor.update(STATE_ON);
		} else if (!newState) {
			this.state = false;
			sensor.update(STATE_OFF);
		} else {
			sensor.update(STATE_OFF);
			sensor.update(STATE_ON);
		}
	}

	/**
	 * This is possible bug in {@link org.openremote.controller.model.xml.Version20SensorBuilder#getDistinctStateMapping(org.jdom.Element)}
	 * as this map is build in reverse order
	 * @return
	 */
	public int getInputNumber() {
		Map<String,String> stateSensorInternalMap = getStateSensorInternalMap();
		return convertValueToInputNumber(stateSensorInternalMap.get(PROPERTY_INPUT));
	}

	Map<String, String> getStateSensorInternalMap() {
		try {
			StateSensor.DistinctStates fieldValue = (StateSensor.DistinctStates)FieldUtils.getFieldValue(sensor, STATE_SENSOR_INTERNAL_FIELD_NAME);
			return (Map<String, String>) FieldUtils.getFieldValue(fieldValue, STATE_SENSOR_INTERNAL_FIELD_NAME);
		} catch (IllegalAccessException e) {
			logger.error("Could not access "+STATE_SENSOR_INTERNAL_FIELD_NAME+" private field in class StateSensor. Sensor: "+sensor);
			throw new NoSuchCommandException("Could not access "+STATE_SENSOR_INTERNAL_FIELD_NAME+" private field in class StateSensor");
		}
	}

	int convertValueToInputNumber(String inputNumber) {
		try {
			return Integer.parseInt(inputNumber);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
