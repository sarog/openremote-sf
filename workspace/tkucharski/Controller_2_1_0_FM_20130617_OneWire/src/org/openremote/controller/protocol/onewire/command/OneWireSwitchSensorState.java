package org.openremote.controller.protocol.onewire.command;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 * @since 22.10.13 22:46
 */
public enum OneWireSwitchSensorState {
	off("0"),
	on("1");

	private String state;

	OneWireSwitchSensorState(String oneWireState) {
		this.state = oneWireState;
	}

	public static String onOffValue(String value) {
		OneWireSwitchSensorState convert = convert(value);
		return convert.name();
	}

	public static String onOffValue(boolean value) {
		OneWireSwitchSensorState convert = valueOf(value);
		return convert.name();
	}

	public static String numericalValue(String value) {
		OneWireSwitchSensorState convert = convert(value);
		return convert.state;
	}

	public static String negateToNumerical(String value) {
		OneWireSwitchSensorState convert = convert(value);
		return convert.negate().state;
	}

	private static OneWireSwitchSensorState convert(String s) {
		for (OneWireSwitchSensorState state : values()) {
			if (state.state.equals(s) || state.name().equals(s)) {
				return state;
			}
		}
		return off;
	}

	private static OneWireSwitchSensorState valueOf(boolean state) {
		if (state) {
			return on;
		} else {
			return off;
		}
	}

	private OneWireSwitchSensorState negate() {
		if (this == on) {
			return off;
		} else {
			return on;
		}
	}
}
