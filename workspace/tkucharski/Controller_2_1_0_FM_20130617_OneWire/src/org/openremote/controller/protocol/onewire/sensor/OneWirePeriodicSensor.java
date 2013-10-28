package org.openremote.controller.protocol.onewire.sensor;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.onewire.OneWireLogger;
import org.openremote.controller.protocol.onewire.command.OneWireExecutableCommand;
import org.owfs.jowfsclient.OwfsConnection;
import org.owfs.jowfsclient.PeriodicJob;

/**
 * @author Tom Kucharski <tomasz.kucharski@gmail.com>
 * @since 26.10.13 11:18
 */
public class OneWirePeriodicSensor implements OneWireSensor<String> {

	private final OneWireExecutableCommand executableCommand;

	private final Sensor sensor;

	private PeriodicJob periodicJob;

	public OneWirePeriodicSensor(Sensor sensor, OneWireExecutableCommand command, Integer pollingIntervalInMilliseconds) {
		this.sensor = sensor;
		this.executableCommand = command;
		if (pollingIntervalInMilliseconds != null) {
			startPeriodicJob(pollingIntervalInMilliseconds);
		}
	}

	private void startPeriodicJob(final Integer pollingIntervalInMiliseconds) {
		periodicJob = new PeriodicJob(pollingIntervalInMiliseconds) {
			@Override
			public void run(OwfsConnection connection) {
				try {
					executableCommand.execute(connection);
				} catch (Exception e) {
					OneWireLogger.error("Unable to send command to owfs server. Command: " + executableCommand, e);
				}
			}
		};
		executableCommand.getOwfsConnectorFactory().addPeriodicJob(periodicJob);
	}

	public void stop() {
		if (periodicJob != null) {
			periodicJob.cancel();
		}
	}

	public void update(String oldValue, String newValue) {
		OneWireLogger.info(sensor.getName()+": "+newValue);
		sensor.update(newValue);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OneWireSensorWrapper{");
		sb.append(", sensor=").append(sensor.getName());
		sb.append(", periodicJobInterval=").append(periodicJob == null ? "NONE" : periodicJob.getIntervalInMiliseconds());
		sb.append('}');
		return sb.toString();
	}
}
