/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.controller.protocol.onewire.command;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.onewire.OneWireConfigurationReader;
import org.openremote.controller.protocol.onewire.OneWireLogger;
import org.openremote.controller.protocol.onewire.sensor.OneWirePeriodicSensor;
import org.owfs.jowfsclient.OwfsConnection;

/**
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public abstract class OneWireExecutableCommand extends OneWireCommand<String> implements ExecutableCommand, EventListener {

	private OneWirePeriodicSensor oneWirePeriodicSensor;

	private Integer pollingIntervalInMiliseconds;

	public void setPollingIntervalInMiliseconds(Integer pollingIntervalInMiliseconds) {
		this.pollingIntervalInMiliseconds = pollingIntervalInMiliseconds;
	}

	@Override
	public void setSensor(final Sensor sensor) {
		oneWirePeriodicSensor = new OneWirePeriodicSensor(sensor, this, pollingIntervalInMiliseconds);
		getDevice().addListener(oneWirePeriodicSensor);
	}

	@Override
	public void stop(Sensor sensor) {
		oneWirePeriodicSensor.stop();
		getDevice().removeListener(oneWirePeriodicSensor);
	}

	@Override
	public final void send() {
		OwfsConnection newConnection = getOwfsConnectorFactory().createNewConnection();
		try {
			execute(newConnection);
		} catch (Exception e) {
			handleException(e);
		}
	}

	public abstract void execute(OwfsConnection connection) throws Exception;

	public void handleException(Exception e) {
		OneWireLogger.error("Unable to send command to owfs server. Command: " + this, e);
	}

	@Override
	public StringBuilder toStringParameterOnly() {
		return super.toStringParameterOnly()
				.append(", intervalInMiliseconds='").append(pollingIntervalInMiliseconds).append("'");
	}

	@Override
	public void configure(OneWireConfigurationReader configuration) {
 		super.configure(configuration);
		if (configuration.getPollingInterval() != null) {
			setPollingIntervalInMiliseconds(configuration.getPollingInterval());
		}
	}

}
