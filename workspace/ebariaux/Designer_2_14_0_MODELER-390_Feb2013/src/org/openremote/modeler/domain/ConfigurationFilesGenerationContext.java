/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.modeler.domain;

import java.util.HashMap;

import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;

/**
 * This context allows the controller and panel XML generation process to pass information around.
 * 
 * In particular, the UIComponents do not reference DTOs with all the required information to generate the XML files.
 * This context provides lookup tables with the required information.
 * It has to be filled-in as part of the XML generation process "bootstrap".
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ConfigurationFilesGenerationContext {

  private HashMap<Long, SwitchDetailsDTO> switches = new HashMap<Long, SwitchDetailsDTO>();
  private HashMap<Long, SensorDetailsDTO> sensors = new HashMap<Long, SensorDetailsDTO>();
  
  public void putSwitch(Long id, SwitchDetailsDTO aSwitch) {
    switches.put(id, aSwitch);
  }
  
  public SwitchDetailsDTO getSwitch(Long id) {
    return switches.get(id);
  }
  
  public void putSensor(Long id, SensorDetailsDTO sensor) {
    sensors.put(id, sensor);
  }
  
  public SensorDetailsDTO getSensor(Long id) {
    return sensors.get(id);
  }
}
