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

import java.util.HashMap;
import java.util.Map;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.protocol.EventListener;
import org.owfs.jowfsclient.alarm.AlarmingDevicesScanner;
import org.owfs.jowfsclient.device.SwitchAlarmingDeviceEvent;
import org.owfs.jowfsclient.device.SwitchAlarmingDeviceListener;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireAlarmingCommand extends OneWireCommand implements EventListener {

	private Map<Integer, OneWireSwitchSensor> inputToSensorMap = new HashMap<Integer, OneWireSwitchSensor>();

	/**
	 * According to {@link org.openremote.controller.protocol.EventListener} this method is called for each sensor associated with this command
	 * As a result you can install several sensor to one reading command, i.e. DS2408 contains 8 inputs numbered 0...7
	 *
	 * @param sensor sensor this event listener is bound to this command
	 */
	@Override
	public void setSensor(final Sensor sensor) {
		OneWireSwitchSensor oneWireSensor = new OneWireSwitchSensor((StateSensor)sensor);
		int inputNumber = oneWireSensor.getInputNumber();
		inputToSensorMap.put(inputNumber, oneWireSensor);
		log.debug("OneWire device:'" + deviceName + "': installing " +"sensor input number: '"+ inputNumber +"', " +sensor);
		tryToInstallAlarmingListenerForDevice();
	}

	/**
	 * Presumably according to {@link org.openremote.controller.protocol.EventListener#setSensor(org.openremote.controller.model.sensor.Sensor)} this method will be
	 * called for each sensor associated with this command.
	 *
	 * @param sensor sensor this event listener is bound to
	 */
	@Override
	public void stop(Sensor sensor) {
		removeSensorFromInputToSensorMap(sensor);
		checkIfStopAlarmingScanner();
	}

	private void removeSensorFromInputToSensorMap(Sensor sensor) {
		OneWireSwitchSensor oneWireSensor = new OneWireSwitchSensor((StateSensor)sensor);
		int inputNumber = oneWireSensor.getInputNumber();
		log.debug("OneWire device:'" + deviceName + "': uninstalling " +"sensor input number: '"+inputNumber +"', " +sensor);
		inputToSensorMap.remove(inputNumber);
	}

	private void checkIfStopAlarmingScanner() {
		if (inputToSensorMap.isEmpty()) {
			AlarmingDevicesScanner alarmingScanner = owfsConnectorFactory.getAlarmingScanner();
			alarmingScanner.removeAlarmingDeviceHandler(deviceName);
		}
	}

	private void tryToInstallAlarmingListenerForDevice() {
		AlarmingDevicesScanner alarmingScanner = owfsConnectorFactory.getAlarmingScanner();
		try {
			if (!alarmingScanner.isAlarmingDeviceOnList(deviceName)) {
				alarmingScanner.addAlarmingDeviceHandler(createSwitchAlarmingDeviceListener());
			}
		} catch (Exception e) {
			log.error("Cannot register alarm listener for device: " + deviceName);
		}
	}

	private SwitchAlarmingDeviceListener createSwitchAlarmingDeviceListener() {
		return new SwitchAlarmingDeviceListener(deviceName, SwitchAlarmingDeviceListener.ALARMING_MASK_8_SWITCHES) {
			@Override
			public void handleAlarm(SwitchAlarmingDeviceEvent event) {
				log.info("OneWire device: " + deviceName + " alarming. " + event);
				boolean[] latchStatusAsArray = event.getLatchStatusAsArray();
				for (int i = 0; i < latchStatusAsArray.length; i++) {
					if (latchStatusAsArray[i]) {
						OneWireSwitchSensor sensor = inputToSensorMap.get(i);
						if (sensor != null) {
							sensor.setState(event.getSensedStatusAsArray()[i]);
						}
					}
				}
			}
		};
	}

	@Override
	public StringBuilder toStringParameterOnly() {
		return super.toStringParameterOnly()
		.append(", inputToSensorMap=").append(inputToSensorMap);
	}
}
