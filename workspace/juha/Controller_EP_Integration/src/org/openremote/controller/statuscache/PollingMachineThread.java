/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.Constants;

/**
 * TODO : Should move to sensor implementation, as per ORCJAVA-116
 *
 * @author Handy.Wang 2009-03-17
 */
@Deprecated public class PollingMachineThread extends Thread
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Constants.RUNTIME_SENSORS_LOG_CATEGORY);


  private volatile boolean alive = true;

  private Sensor sensor;
  //private StatusCacheService statusCacheService;
  private String lastStatus = StatusCommand.UNKNOWN_STATUS;
  private static final long INTERVAL = 500;

  /** milliseconds */
  private long pollingMachineInterval = INTERVAL;

  public PollingMachineThread(Sensor sensor)
  {
    this.sensor = sensor;
  }

//	public PollingMachineThread(long pollingMachineInterval, Sensor sensor)
//  {
//    this.pollingMachineInterval = pollingMachineInterval;
//    this.sensor = sensor;
//  }


  @Override public void run()
  {
    log.info("Started sensor (ID = {0}, type = {1}).", sensor.getSensorID(), sensor.getSensorType());

    while (alive)
    {
      lastStatus = sensor.read();

      sensor.update(lastStatus);

      try
      {
        Thread.sleep(pollingMachineInterval);
      }
      catch (InterruptedException e)
      {
        alive = false;

        log.info("Shutting down sensor (ID = {0}, type = {1}).", sensor.getSensorID(), sensor.getSensorType());

        // Allow the container to handle thread cleanup if it wants to...

        Thread.currentThread().interrupt();
      }
    }
  }

  public void kill()
  {
     this.alive = false;
  }

  public String getLastStatus()
  {
    return lastStatus;
  }

  public Sensor getSensor()
  {
    return sensor;
  }
	
}
