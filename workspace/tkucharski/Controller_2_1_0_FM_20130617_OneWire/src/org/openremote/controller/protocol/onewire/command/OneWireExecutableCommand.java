package org.openremote.controller.protocol.onewire.command;

import org.openremote.controller.command.ExecutableCommand;
import org.owfs.jowfsclient.OwfsConnection;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireExecutableCommand extends OneWireCommand implements ExecutableCommand {


	@Override
	public void send() {
		try {
			OwfsConnection connection = owfsConnectorFactory.createNewConnection();
			connection.write(deviceName + "/" + devicePropertyName, dynamicValue);
		} catch (Exception e) {
			handleException(e);
		}
	}

	protected void handleException(Exception e) {
		log.error("Unable to send command to owfs server. Command: "+this, e);
	}
}
