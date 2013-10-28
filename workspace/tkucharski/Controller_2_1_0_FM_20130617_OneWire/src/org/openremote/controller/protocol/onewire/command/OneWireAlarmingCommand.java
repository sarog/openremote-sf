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
import org.openremote.controller.protocol.onewire.OneWireLogger;
import org.openremote.controller.protocol.onewire.sensor.OneWireInputSensor;
import org.owfs.jowfsclient.OwfsConnectionFactory;
import org.owfs.jowfsclient.alarm.AlarmingDevicesScanner;
import org.owfs.jowfsclient.device.SwitchAlarmingDeviceEvent;
import org.owfs.jowfsclient.device.SwitchAlarmingDeviceListener;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireAlarmingCommand extends OneWireCommand<SwitchAlarmingDeviceEvent> implements EventListener {

	/**
	 * Sensor related to this command
	 */
	private OneWireInputSensor oneWireInputSensor;

	/**
	 * According to {@link org.openremote.controller.protocol.EventListener} this method is called for each sensor associated with this command
	 * As a result you can install several sensor to one reading command, i.e. DS2408 contains 8 inputs numbered 0...7
	 *
	 * @param sensor sensor this event listener is bound to this command
	 */
	@Override
	public void setSensor(final Sensor sensor) {
		oneWireInputSensor = new OneWireInputSensor(sensor);
		getDevice().addListener(oneWireInputSensor);
		checkIfInstallAlarmingScanner(owfsConnectorFactory);
	}

	/**
	 * Presumably according to {@link org.openremote.controller.protocol.EventListener#setSensor(org.openremote.controller.model.sensor.Sensor)} this method will be
	 * called for each sensor associated with this command.
	 *
	 * @param sensor sensor this event listener is bound to
	 */
	@Override
	public void stop(Sensor sensor) {
		getDevice().removeListener(oneWireInputSensor);
		checkIfStopAlarmingScanner(owfsConnectorFactory);
	}

	/**
	 * It gets alarming scanner installed in owserver connection and checks if device related to this command is already registered
	 *
	 * @param owfsConnectionFactory owserver connection
	 */
	private void checkIfInstallAlarmingScanner(OwfsConnectionFactory owfsConnectionFactory) {
		AlarmingDevicesScanner alarmingScanner = owfsConnectionFactory.getAlarmingScanner();
		try {
			if (!alarmingScanner.isAlarmingDeviceOnList(getDevice().getAddress())) {
				OneWireLogger.info("Installing alarm on device: " + getDevice().getAddress());
				alarmingScanner.addAlarmingDeviceHandler(createSwitchAlarmingDeviceListener());
			}
		} catch (Exception e) {
			OneWireLogger.error("Cannot register alarm listener for device: " + getDevice().getAddress(), e);
		}
	}

	private SwitchAlarmingDeviceListener createSwitchAlarmingDeviceListener() {
		return new SwitchAlarmingDeviceListener(getDevice().getAddress(), SwitchAlarmingDeviceListener.ALARMING_MASK_8_SWITCHES) {
			@Override
			public void handleAlarm(SwitchAlarmingDeviceEvent event) {
				OneWireLogger.info("OneWire device: " + getDevice().getAddress() + " alarming. " + event);
				getDevice().setValue(event);
			}
		};
	}

	private void checkIfStopAlarmingScanner(OwfsConnectionFactory owfsConnectionFactory) {
		if (!getDevice().isAnyListenerRegistered()) {
			OneWireLogger.info("Uninstalling alarm on device: " + getDevice().getAddress());
			AlarmingDevicesScanner alarmingScanner = owfsConnectionFactory.getAlarmingScanner();
			alarmingScanner.removeAlarmingDeviceHandler(getDevice().getAddress());
		}
	}

}
