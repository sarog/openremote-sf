/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.statuscache;

import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.service.StatusCacheService;

/**
 * 
 * @author Handy.Wang 2009-03-17
 */
public class PollingMachineThread extends Thread { 
	private String sensorID;
	private StatusCommand statusCommand;
	private StatusCacheService statusCacheService;
	private static final long INTERVAL = 500;	
	/** milliseconds */
	private long pollingMachineInterval;
	
   public PollingMachineThread(String sensorID, StatusCommand statusCommand, StatusCacheService statusCacheService) {
      this(INTERVAL, sensorID, statusCommand, statusCacheService);
   }
	
	public PollingMachineThread(long pollingMachineInterval, String sensorID, StatusCommand statusCommand, StatusCacheService statusCacheService) {
      this.pollingMachineInterval = pollingMachineInterval;
      this.sensorID = sensorID;
      this.statusCommand = statusCommand;
      this.statusCacheService = statusCacheService;
   }
	
	@Override
	public void run() {
		while (true) {
			statusCacheService.saveOrUpdateStatus(Integer.parseInt(this.sensorID), statusCommand.read());
			try {
				Thread.sleep(pollingMachineInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
