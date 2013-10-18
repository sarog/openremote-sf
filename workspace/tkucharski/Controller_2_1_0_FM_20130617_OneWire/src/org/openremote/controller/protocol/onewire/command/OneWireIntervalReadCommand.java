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

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.onewire.OneWireLoggerFactory;
import org.openremote.controller.utils.Logger;
import org.owfs.jowfsclient.OwfsConnection;
import org.owfs.jowfsclient.PeriodicJob;

/**
 * Command that periodically reads data from owfs server and updates to sensor
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireIntervalReadCommand extends OneWireCommand implements EventListener {

	public static final String NOT_A_NUMBER = "N/A";

	private static final Logger log = OneWireLoggerFactory.getLogger();

	private Sensor sensor;

	private ReadCommandPeriodicJob periodicJob;

	public void setPollingIntervalInMiliseconds(Integer pollingIntervalInMiliseconds) {
		periodicJob = new ReadCommandPeriodicJob(pollingIntervalInMiliseconds);
	}

	@Override
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
		log.info("Installing periodic scheduler on sensor: " + sensor.getName() + " using command: " + this.toString());
		owfsConnectorFactory.addPeriodicJob(periodicJob);
	}

	@Override
	public void stop(Sensor sensor) {
		this.sensor = null;
		log.info("Uninstalling periodic scheduler on sensor: " + sensor.getName() + " using command: " + this.toString());
		periodicJob.cancel();
	}

	class ReadCommandPeriodicJob extends PeriodicJob {

		protected ReadCommandPeriodicJob(int intervalInMiliseconds) {
			super(intervalInMiliseconds);
		}

		@Override
		public void run(OwfsConnection connection) {
			String read = readValue(connection);
			log.info("Sensor: '"+sensor.getName()+"', value: '"+read+"'");
			sensor.update(read);
		}

		private String readValue(OwfsConnection connection) {
			try {
				return connection.read(deviceName + "/" + devicePropertyName);
			} catch (Exception e) {
				return NOT_A_NUMBER;
			}
		}
	}
}
