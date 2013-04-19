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
package org.openremote.modeler.shared.dto;

import java.util.ArrayList;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.SensorType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class SensorWithInfoDTOTest {

  @Test
  public void testSensorWithInfoDTOsEqual() {
    SensorWithInfoDTO sensor1 = new SensorWithInfoDTO();
    SensorWithInfoDTO sensor2 = new SensorWithInfoDTO();
    
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    sensor1.setOid(IDUtil.nextID());
    sensor2.setOid(sensor1.getOid());

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");

    sensor1.setDisplayName("Name");
    sensor2.setDisplayName("Name");

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");

    sensor1.setType(SensorType.SWITCH);
    sensor2.setType(SensorType.SWITCH);

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");

    sensor1.setCommandName("Command name");
    sensor2.setCommandName("Command name");

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");

    sensor1.setMinValue("0");
    sensor2.setMinValue("0");

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");

    sensor1.setMaxValue("100");
    sensor2.setMaxValue("100");

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");

    ArrayList<String> states = new ArrayList<String>();
    states.add("On");
    states.add("Off");
    
    sensor1.setStateNames(states);
    sensor2.setStateNames(states);

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
  }
  
  @Test
  public void testSensorWithInfoDTOsNotEqual() {
    ArrayList<String> states = new ArrayList<String>();
    states.add("On");
    states.add("Off");
    
    SensorWithInfoDTO sensor1 = new SensorWithInfoDTO();
    sensor1.setOid(IDUtil.nextID());
    sensor1.setDisplayName("Name");
    sensor1.setType(SensorType.SWITCH);
    sensor1.setCommandName("Command name");
    sensor1.setMinValue("0");
    sensor1.setMaxValue("100");
    sensor1.setStateNames(states);

    SensorWithInfoDTO sensor2 = new SensorWithInfoDTO();
    sensor2.setOid(sensor1.getOid());
    sensor2.setDisplayName("Name");
    sensor2.setType(SensorType.SWITCH);
    sensor2.setCommandName("Command name");
    sensor2.setMinValue("0");
    sensor2.setMaxValue("100");
    sensor2.setStateNames(states);

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    
    // TODO
    
  }
  
  @Test
  public void testConstructor() {
    ArrayList<String> states = new ArrayList<String>();
    states.add("On");
    states.add("Off");
    
    SensorWithInfoDTO sensor1 = new SensorWithInfoDTO();
    sensor1.setOid(IDUtil.nextID());
    sensor1.setDisplayName("Name");
    sensor1.setType(SensorType.SWITCH);
    sensor1.setCommandName("Command name");
    sensor1.setMinValue("0");
    sensor1.setMaxValue("100");
    sensor1.setStateNames(states);

    SensorWithInfoDTO sensor2 = new SensorWithInfoDTO(sensor1.getOid(), "Name", SensorType.SWITCH, "Command name", "0", "100", states);

    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
  }

}
