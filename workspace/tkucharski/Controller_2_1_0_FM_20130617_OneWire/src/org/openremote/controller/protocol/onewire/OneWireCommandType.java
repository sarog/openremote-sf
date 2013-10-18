package org.openremote.controller.protocol.onewire;

/**
 * Represents types of commands supported by OneWire adapter
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public enum OneWireCommandType {
	/**
	 * Devices that alerts on 1-wire alarm
	 */
	ALARMING,

	/**
	 * Switchable devices, mostly used in DS2408 or in similar devices;
	 */
	SWITCHABLE,

	/**
	 * Simple command that reads its value periodically
	 */
	INTERVAL,

	/**
	 * Simple passive command
	 */
	EXECUTABLE;

}
