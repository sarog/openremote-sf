package org.openremote.controller.protocol.onewire.container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Kucharski <tomasz.kucharski@gmail.com>
 * @since 26.10.13 00:00
 */
public class OneWireDeviceRepository {

	Map<OneWireDeviceConfiguration, OneWireDevice> devices = new HashMap<OneWireDeviceConfiguration, OneWireDevice>();

	public OneWireDevice getDevice(OneWireDeviceConfiguration configuration) {
		OneWireDevice oneWireDevice = devices.get(configuration);
		if (oneWireDevice == null) {
			oneWireDevice = new OneWireDevice(configuration);
			devices.put(configuration, oneWireDevice);
		}
		return oneWireDevice;
	}
}
