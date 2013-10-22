package org.openremote.controller.protocol.onewire.command;

/**
 * @author Tomasz Kucharski <tomasz.kucharski@decerto.pl>
 * @since 22.10.13 22:46
 */
public enum OneWireSwitchSensorState {
	on("1"),
	off("2");

	private String state;

	OneWireSwitchSensorState(String oneWireState) {
		this.state = oneWireState;
	}

	public static OneWireSwitchSensorState convert(String s) {
		for (OneWireSwitchSensorState state : values()) {
			if (state.state.equals(s) || state.name().equals(s)) {
				return state;
			}
		}
		return off;
	}

	public static OneWireSwitchSensorState valueOf(boolean state) {
		if (state) {
			return on;
		} else {
			return off;
		}
	}

	public OneWireSwitchSensorState negate() {
		if (this == on) {
			return off;
		} else {
			return on;
		}
	}

	public boolean isOn() {
		return OneWireSwitchSensorState.on.equals(this);
	}
}
