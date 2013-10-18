package org.openremote.controller.protocol.onewire.command;

import org.owfs.jowfsclient.OwfsConnection;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireSwitchableCommand extends OneWireExecutableCommand {

	public static final String OFF = "0";
	public static final String ON = "1";

	@Override
	public void setDynamicValue(String dynamicValue) {
		log.debug(this.toString()+" new dynamicValue:'"+dynamicValue+"'");
	}

	@Override
	public void send() {
		if (isItFirstExecution()) {
			tryToReadInitialValue();
		}
		negateDynamicValue();
		super.send();
	}

	/**
	 * Rollback current state value as command was not sent to owfs server
	 * @param e exception thrown during send method
	 */
	@Override
	protected void handleException(Exception e) {
		negateDynamicValue();
		super.handleException(e);
	}

	public boolean isItFirstExecution() {
		return dynamicValue == null;
	}

	private void tryToReadInitialValue() {
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			String value = connection.read(deviceName + "/" + devicePropertyName);
			setState(value);
		} catch (Exception e) {
			log.warn("OneWire cannot read initial value for command: "+this);
			setState(OFF);
		}
	}

	public void setState(String value) {
		if (ON.equals(value)) {
			dynamicValue = ON;
		} else {
			dynamicValue = OFF;
		}
	}

	public void negateDynamicValue() {
		if (ON.equals(dynamicValue)) {
			dynamicValue = OFF;
		} else {
			dynamicValue = ON;
		}
	}
}
