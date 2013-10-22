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
import org.owfs.jowfsclient.OwfsConnection;
import org.owfs.jowfsclient.PeriodicJob;

/**
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public abstract class OneWireExecutableCommand extends OneWireCommand implements ExecutableCommand, EventListener {

	private Sensor sensor;

	private PeriodicJob periodicJob;

	public void setPollingIntervalInMiliseconds(int pollingIntervalInMiliseconds) {
		// if it is not null it means that sensor is currently installed and reconfiguration is not possible
		if (periodicJob == null) {
			periodicJob = new PeriodicJob(pollingIntervalInMiliseconds) {
				@Override
				public void run(OwfsConnection connection) {
					execute(connection);
				}

			};
		}
	}

	@Override
	public void setSensor(Sensor sensor) {
		log.info("Installing periodic scheduler on command: " + this.toString() + ", notification to sensor: " + sensor.getName());
		this.sensor = sensor;
		if (periodicJob != null) {
			owfsConnectorFactory.addPeriodicJob(periodicJob);
		}
	}

	@Override
	public void stop(Sensor sensor) {
		log.info("Uninstalling periodic scheduler on command: " + this.toString() + ", notification to sensor: " + sensor.getName());
		this.sensor = null;
		if (periodicJob != null) {
			periodicJob.cancel();
		}
	}

	@Override
	public final void send() {
		OwfsConnection newConnection = owfsConnectorFactory.createNewConnection();
		execute(newConnection);
	}

	public abstract void execute(OwfsConnection connection);

	public void updateSensor(String value) {
		if (sensor != null) {
			log.info("Sensor: " + sensor.getName() + "=" + value);
			sensor.update(value);
		}
	}

	@Override
	public StringBuilder toStringParameterOnly() {
		return super.toStringParameterOnly()
				.append(", intervalInMiliseconds='").append(periodicJob != null ? periodicJob.getIntervalInMiliseconds() : null).append("'");
	}

	@Override
	public void configure(OneWireConfigurationReader configuration) {
		super.configure(configuration);
		if (configuration.getPollingInterval() != null) {
			setPollingIntervalInMiliseconds(configuration.getPollingInterval());
		}
	}
}
