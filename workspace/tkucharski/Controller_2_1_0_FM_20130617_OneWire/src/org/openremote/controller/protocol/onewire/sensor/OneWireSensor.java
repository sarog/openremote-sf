package org.openremote.controller.protocol.onewire.sensor;

/**
 * @author Tom Kucharski <tomasz.kucharski@gmail.com>
 * @since 27.10.13 23:02
 */
public interface OneWireSensor<T extends Object> {

	void update(T oldValue, T value);
}
