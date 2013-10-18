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
