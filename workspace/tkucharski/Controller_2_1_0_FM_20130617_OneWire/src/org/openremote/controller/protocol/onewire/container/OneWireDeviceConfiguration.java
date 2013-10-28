package org.openremote.controller.protocol.onewire.container;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 * @since 25.10.13 23:48
 */
public class OneWireDeviceConfiguration {

	/**
	 * owserver connection parameters
	 */
	private OneWireHost oneWireHost;

	/**
	 * address of the 1-wire device, such as /1F.E9E803000000/main/28.25E9E3010000
	 */
	private String deviceAddress;

	/**
	 * device attribute - is filename in owfs that holds values, such as "temperature", "temperature9", "humidity" or similar
	 */
	private String deviceProperty;

	public OneWireHost getOneWireHost() {
		return oneWireHost;
	}

	public void setOneWireHost(OneWireHost oneWireHost) {
		this.oneWireHost = oneWireHost;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public String getDeviceProperty() {
		return deviceProperty;
	}

	public void setDeviceProperty(String deviceProperty) {
		this.deviceProperty = deviceProperty;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OneWireDeviceConfiguration that = (OneWireDeviceConfiguration) o;

		if (!deviceAddress.equals(that.deviceAddress)) return false;
		if (!deviceProperty.equals(that.deviceProperty)) return false;
		if (!oneWireHost.equals(that.oneWireHost)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = oneWireHost.hashCode();
		result = 31 * result + deviceAddress.hashCode();
		result = 31 * result + deviceProperty.hashCode();
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OneWireDeviceConfiguration{");
		sb.append("oneWireHost=").append(oneWireHost);
		sb.append(", deviceAddress='").append(deviceAddress).append('\'');
		sb.append(", deviceProperty='").append(deviceProperty).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
