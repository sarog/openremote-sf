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
package org.openremote.modeler.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.SensorDetailsDTO;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

/**
 * Make user to define custom sensor states.
 * It include a list of states.
 */
@Entity
@DiscriminatorValue("CUSTOM_SENSOR")
public class CustomSensor extends Sensor {

   private static final long serialVersionUID = 8005062021509479312L;
   
   private List<State> states = new ArrayList<State>();
   
   public CustomSensor() {
      super(SensorType.CUSTOM);
   }
   @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
   @OrderBy(value = "oid")
   public List<State> getStates() {
      return states;
   }

   public void setStates(List<State> states) {
      this.states = states;
   }
   
   public void addState(State state) {
      states.add(state);
      state.setSensor(this);
   }
   
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("<link type=\"sensor\" ref=\"" + getOid() + "\">\n");
      for (State state : states) {
         sb.append("\t<state name=\"" + state.getName() + "\"/>\n");
      }
      sb.append("</link>");
      return sb.toString();
   }
   
  @Override
  @Transient
  public SensorDetailsDTO getSensorDetailsDTO() {
    SensorDetailsDTO dto;

    HashMap<String, String> states = new HashMap<String, String>();
    for (State state : getStates()) {
      states.put(state.getName(), state.getValue());
    }
    dto = new SensorDetailsDTO(getOid(), getName(), getType(), getSensorCommandRef().getDisplayName(), null, null, states);
    if (getSensorCommandRef() != null) {
      dto.setCommand(new DTOReference(getSensorCommandRef().getDeviceCommand().getOid()));
    }
    return dto;
  }

  @Override
  @Transient
  public SensorWithInfoDTO getSensorWithInfoDTO() {
    ArrayList<String> states = new ArrayList<String>();
    for (State state : getStates()) {
      states.add(state.getName());
    }
    return new SensorWithInfoDTO(getOid(), getDisplayName(), getType(), getSensorCommandRef().getDisplayName(), null, null, states);
  }

}
