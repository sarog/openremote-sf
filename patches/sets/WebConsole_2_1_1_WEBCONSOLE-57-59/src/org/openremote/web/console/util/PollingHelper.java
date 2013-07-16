/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.web.console.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.service.AsyncControllerCallback;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class PollingHelper {
	private Set<Integer> monitoredSensorIds = new HashSet<Integer>();
	private boolean monitorActive = false;
	private boolean monitorRunning = false;
	AsyncControllerCallback<Map<Integer, String>> callback;
	
	public PollingHelper(Set<Integer> sensorIds, AsyncControllerCallback<Map<Integer, String>> callback) {
		monitoredSensorIds = sensorIds;
		this.callback = callback;
	}
	
	public void addMonitoredSensor(Integer sensorId) {
		if (!monitoredSensorIds.contains(sensorId)) {
			monitoredSensorIds.add(sensorId);
		}
	}
	
	// Send current values to callback and then monitor sensors until stopped
	public void startSensorMonitoring() {
		monitorActive = true;
		
		// Monitor may be running if we switched back to this screen before it timed out or a sensor change occurred
		if (!monitorRunning) {
			// Get current values
			WebConsole.getConsoleUnit().getControllerService().getSensorValues(monitoredSensorIds.toArray(new Integer[0]), new AsyncControllerCallback<Map<Integer, String>>() {
				@Override
				public void onSuccess(Map<Integer, String> result) {
					callback.onSuccess(result);	
				}
			});
			
			monitorSensors();
		}
	}
	
	// Monitor all the sensors
	private void monitorSensors() {
		monitorRunning = true;
		WebConsole.getConsoleUnit().getControllerService().monitorSensors(monitoredSensorIds.toArray(new Integer[0]), new AsyncControllerCallback<Map<Integer, String>>() {
			@Override
			public void onFailure(Throwable exception) {
				monitorRunning = false;
				if (monitorActive) {
					// Restart monitoring
					monitorSensors();
				}
			}
			@Override
			public void onSuccess(Map<Integer, String> result) {
				monitorRunning = false;
				if (monitorActive) {
					if (result != null) {
						callback.onSuccess(result);
					}
					// Restart monitoring
					monitorSensors();
				}
			}
		});
	}
	
	public void stopMonitoring() {
		monitorActive = false;
	}
}
