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

import org.openremote.controller.exception.NoSuchCommandException;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

import org.apache.log4j.Logger;

/**
 * This class will simply maintain a list of Elexol USB Devices that 
 * are being used by the OpenRemote Controller. 
 *
 * The class has a single static method which gets the Elexol Device
 * Structure.
 *
 * When a request is received for a specific USB Port for example
 * /dev/ttyUSB0 this class will search for it in its list of devices.
 * If the Device is not in the current list it will be created,
 * Initialised and returned to the caller.
 * If the Device is already in the list it is simply found and returned.
 *
 * Throws NoSuchCommandException If the USB Port is not valid or the
 * device connected is not recognised as an Elexol USB Device.
 *
 * @author <a href="mailto:johnfwhitmore@gmail.com">John Whitmore</a>
 */

public class DeviceManager{

  private static List<ElexolUsbDevice> devices = new ArrayList<ElexolUsbDevice>();

  public static ElexolUsbDevice GetDevice(String usbPort)
  {
    CommPortIdentifier comPortID;

    try 
    {
      comPortID = CommPortIdentifier.getPortIdentifier(usbPort);
    }
    catch (NoSuchPortException e)
    {
      throw new NoSuchCommandException("USB port '" + usbPort + "' is not recognized.");
    }

    /*
     * Check have we already got this Comm Port in out list
     */
    Iterator<ElexolUsbDevice> iterator = devices.iterator();

    ElexolUsbDevice device;
    while (iterator.hasNext())
    {
      device = iterator.next();
      if(device.getComPortID() == comPortID)
      {
	return(device);
      }
    }

    /*
     * Can't find the device in our current list so have to add it
     * to the list.
     *
     * Creating a new device for a USB Port can throw NoSuchCommandException
     * if the device is not recognised as an Elexol USB Deivce
     */
    device = new ElexolUsbDevice(comPortID);

    /*
     * Add the new device to our list of devices.
     */
    devices.add(device);

    return(device);
  }
}
