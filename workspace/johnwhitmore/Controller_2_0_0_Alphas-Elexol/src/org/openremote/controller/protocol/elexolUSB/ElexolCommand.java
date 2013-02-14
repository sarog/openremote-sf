/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.elexolUSB;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;

import org.apache.log4j.Logger;

public class ElexolCommand implements ExecutableCommand {

    /**
     * Logging. Use common Elexol USB log category.
     */
    private static Logger log = Logger.getLogger(ElexolCommandBuilder.ELEXOL_USB_LOG_CATEGORY);

    private ElexolUsbDevice device = null;
    private CommandType     command;
    private PortType        port;
    private PinType         pin;
    private Integer         duration;

    public ElexolCommand(String usbPort, PortType ioPort, PinType pinNumber, CommandType command, Integer duration)
    {
	this.device = DeviceManager.GetDevice(usbPort);
	this.command = command;
	this.port = ioPort;
	this.pin = pinNumber;
        this.duration = duration;
    }
	
    @Override
    public void send() {

	this.device.Send(this.command, this.port, this.pin, this.duration);

	log.debug("ElexolCommand Sent");
    }
}
