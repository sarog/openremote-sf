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
    
    sensor2.setOid(null);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, second id is not set");

    sensor2.setOid(IDUtil.nextID());
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, id is different");

    sensor2.setOid(sensor1.getOid());
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    sensor2.setDisplayName(null);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, second displayName is not set");
    
    sensor2.setDisplayName("Name 2");
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, displayName is different");

    sensor2.setDisplayName("Name");
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    sensor2.setType(null);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, second type is not set");

    sensor2.setType(SensorType.CUSTOM);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, type is different");

    sensor2.setType(SensorType.SWITCH);
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    sensor2.setMinValue(null);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, second minValue is not set");

    sensor2.setMinValue("1");
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, minValue is different");

    sensor2.setMinValue("0");
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    sensor2.setMaxValue(null);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, second maxValue is not set");

    sensor2.setMaxValue("1");
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, maxValue is different");

    sensor2.setMaxValue("100");
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
    
    sensor2.setStateNames(null);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, second stateNames is not set");

    sensor2.setStateNames(new ArrayList<String>());
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, stateNames is different");
    
    ArrayList<String> states2 = new ArrayList<String>();
    states2.add("Foo");

    sensor2.setStateNames(states2);
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, stateNames is different");
    
    states2.add("On");
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, stateNames is different");

    states2.add("Off");
    Assert.assertFalse(sensor1.equals(sensor2), "Expected the SensorWithInfoDTO to be different, stateNames is different");

    states2.remove("Foo");
    Assert.assertEquals(sensor1, sensor2, "Expected the SensorWithInfoDTO to be equal");
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
