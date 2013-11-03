package org.openremote.controller.protocol.onewire.container;

import java.util.HashSet;
import java.util.Set;
import org.openremote.controller.protocol.onewire.OneWireLogger;
import org.openremote.controller.protocol.onewire.sensor.OneWireSensor;

/**
 * Represents device and its current value. Devices are stored in {@link OneWireDeviceRepository} and shared across commands related to this device. Device
 * is defined by {@link OneWireDeviceConfiguration}
 * @author Tom Kucharski <tomasz.kucharski@gmail.com>
 * @since 26.10.13 00:02
 * @see OneWireDeviceRepository
 * @see OneWireDeviceConfiguration
 */
public class OneWireDevice<T extends Object> {

	/**
	 * Device configuration, uniquely determined device endpoint (like property of the device). All commands sharing same device uses the same configuration.
	 */
	private final OneWireDeviceConfiguration configuration;

	/**
	 * Current value of property in device. Used to notify all sensors connected to device or synchronize value between commands connected to the same device
	 * property (in most cases used in switches like DS2408)
	 */
	private T value;

	/**
	 * Listeners (sensors) connected to this device
	 */
	private Set<OneWireSensor<T>> listeners = new HashSet<OneWireSensor<T>>();

	public OneWireDevice(OneWireDeviceConfiguration configuration) {
		this.configuration = configuration;
	}

	public OneWireDeviceConfiguration getConfiguration() {
		return configuration;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		for (OneWireSensor<T> listener : listeners) {
			listener.update(oldValue, value);
		}
	}

	public void addListener(OneWireSensor<T> listener) {
		OneWireLogger.info("Installing listener on device: " + this + ". Listener: " + listener);
		listeners.add(listener);
	}

	public void removeListener(OneWireSensor<T> listener) {
		OneWireLogger.info("Uninstalling listener on device: " + this + ". Listener: " + listener);
		listeners.remove(listener);
	}

	public boolean isAnyListenerRegistered() {
		return listeners.size() > 0;
	}

	public String getAddress() {
		return configuration.getDeviceAddress();
	}

	public String getProperty() {
		return configuration.getDeviceProperty();
	}

	public String getPath() {
		return getAddress() + "/" + getProperty();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OneWireDevice{");
		sb.append("configuration=").append(configuration);
		sb.append(", NoOfListeners=").append(listeners.size());
		sb.append(", value=").append(value);
		sb.append('}');
		return sb.toString();
	}
}
