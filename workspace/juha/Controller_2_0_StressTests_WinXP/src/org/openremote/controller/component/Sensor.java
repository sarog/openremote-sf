/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.component;

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.command.NoStatusCommand;
import org.openremote.controller.command.StatusCommand;

/**
 * TODO : Sensor class for referencing status command
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Handy.Wang 2010-01-04
 *
 */
public class Sensor
{

  // Private Instance Fields ----------------------------------------------------------------------

  private int sensorID;

  private String sensorType;

  private StatusCommand statusCommand;

  private Map<String, String> stateMap;


  // Constructors ---------------------------------------------------------------------------------

  public Sensor()
  {
    super();
    this.statusCommand = new NoStatusCommand();
    stateMap = new HashMap<String, String>();
  }

  public Sensor(int sensorID, String sensorType, StatusCommand statusCommand, Map<String, String> stateMap)
  {
    super();
    this.sensorID = sensorID;
    this.sensorType = sensorType;
    this.statusCommand = statusCommand;
    this.stateMap = stateMap;
  }

  public Sensor(StatusCommand statusCommand)
  {
    super();
    this.statusCommand = statusCommand;
  }


  // Public Instance Fields -----------------------------------------------------------------------

  public int getSensorID()
  {
    return sensorID;
  }

  public void setSensorID(int sensorID)
  {
    this.sensorID = sensorID;
  }

  public StatusCommand getStatusCommand()
  {
    return statusCommand;
  }

  public void setStatusCommand(StatusCommand statusCommand)
  {
    this.statusCommand = statusCommand;
  }

  public String getSensorType()
  {
    return sensorType;
  }

  public void setSensorType(String sensorType)
  {
    this.sensorType = sensorType;
  }

  public Map<String, String> getStateMap()
  {
    return stateMap;
  }

  public void setStateMap(Map<String, String> stateMap)
  {
    this.stateMap = stateMap;
  }

  /**
   * Read status using status command.
   *
   * @return status
   */
  public String readStatus()
  {
    return statusCommand.read(EnumSensorType.enumValueOf(sensorType), stateMap);
  }


  // Object Overrides -----------------------------------------------------------------------------

  @Override public String toString()
  {
    return sensorID + " " + sensorType + " " + statusCommand;
  }
}
