/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.beehive.api.dto.modeler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.openremote.beehive.domain.modeler.CustomSensor;
import org.openremote.beehive.domain.modeler.Sensor;
import org.openremote.beehive.domain.modeler.SensorType;

/**
 * The Class is used for transmitting custom sensor info.
 */
@SuppressWarnings("serial")
@JsonTypeName(value = "CustomSensor")
public class CustomSensorDTO extends SensorDTO {

   private List<StateDTO> states = new ArrayList<StateDTO>();

   public CustomSensorDTO() {
      super(SensorType.CUSTOM);
   }
   
   @XmlElement(name = "states")
   public List<StateDTO> getStates() {
      return states;
   }

   public void setStates(List<StateDTO> states) {
      this.states = states;
   }
   
   public void addState(StateDTO state) {
      this.states.add(state);
   }
   
   @Override
   public Sensor toSensor() {
      CustomSensor sensor = new CustomSensor();
      sensor.setOid(getId());
      sensor.setName(getName());
      sensor.setSensorCommandRef(getSensorCommandRef().toSensorCommandRef(sensor));
      sensor.setDevice(getDevice().toDevice());
      for (StateDTO stateDTO : states) {
         sensor.addState(stateDTO.toState(sensor));
      }
      return sensor;
   }
}
