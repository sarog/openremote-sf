package org.openremote.controller.protocol.elexolUSB;

import org.openremote.controller.exception.NoSuchCommandException;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

import org.apache.log4j.Logger;

public class DeviceManager{

    private static List<ElexolUsbDevice> devices = new ArrayList<ElexolUsbDevice>();

    public static ElexolUsbDevice GetDevice(String usbPort)
    {
	CommPortIdentifier comPortID;

	try {
	    comPortID = CommPortIdentifier.getPortIdentifier(usbPort);
	}
	catch (NoSuchPortException e) {
	    throw new NoSuchCommandException("USB port '" + usbPort + "' is not recognized.");
	}

	/*
	 * Check have we already got this Comm Port in out list
	 */
	Iterator<ElexolUsbDevice> iterator = devices.iterator();

	ElexolUsbDevice device;
	while (iterator.hasNext()) {
	    device = iterator.next();
	    if(device.getComPortID() == comPortID){
		return(device);
	    }
	}

	/*
	 * Can't find the device in our current list so have to add it
	 * to the list.
	 */
	device = new ElexolUsbDevice(comPortID);
	devices.add(device);

	return(device);
    }
}
