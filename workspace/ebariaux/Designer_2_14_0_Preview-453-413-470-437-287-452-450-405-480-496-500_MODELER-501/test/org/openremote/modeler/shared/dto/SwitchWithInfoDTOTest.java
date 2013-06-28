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

import org.openremote.modeler.client.utils.IDUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class SwitchWithInfoDTOTest {
  
  @Test
  public void testSwitchWithInfoDTOsEqual() {
    SwitchWithInfoDTO switchDTO1 = new SwitchWithInfoDTO();
    SwitchWithInfoDTO switchDTO2 = new SwitchWithInfoDTO();
    
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO1.setOid(IDUtil.nextID());
    switchDTO2.setOid(switchDTO1.getOid());
    
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");

    switchDTO1.setDisplayName("Name");
    switchDTO2.setDisplayName("Name");

    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");

    switchDTO1.setOnCommandName("On command");
    switchDTO2.setOnCommandName("On command");

    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");

    switchDTO1.setOffCommandName("Off command");
    switchDTO2.setOffCommandName("Off command");
    
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");

    switchDTO1.setSensorName("Sensor");
    switchDTO2.setSensorName("Sensor");

    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO1.setDeviceName("Device name");
    switchDTO2.setDeviceName("Device name");

    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
  }
  
  @Test
  public void testSwitchWithInfoDTOsNotEqual() {
    SwitchWithInfoDTO switchDTO1 = new SwitchWithInfoDTO();
    switchDTO1.setOid(IDUtil.nextID());
    switchDTO1.setDisplayName("Name");
    switchDTO1.setOnCommandName("On command");
    switchDTO1.setOffCommandName("Off command");
    switchDTO1.setSensorName("Sensor");
    switchDTO1.setDeviceName("Device name");

    SwitchWithInfoDTO switchDTO2 = new SwitchWithInfoDTO();
    switchDTO2.setOid(switchDTO1.getOid());
    switchDTO2.setDisplayName("Name");
    switchDTO2.setOnCommandName("On command");
    switchDTO2.setOffCommandName("Off command");
    switchDTO2.setSensorName("Sensor");
    switchDTO2.setDeviceName("Device name");

    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO2.setOid(null);
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, second id is not set");
    
    switchDTO2.setOid(IDUtil.nextID());
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, id is different");

    switchDTO2.setOid(switchDTO1.getOid());
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");

    switchDTO2.setDisplayName(null);
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, second displayName is not set");

    switchDTO2.setDisplayName("Name 2");
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, displayName is different");

    switchDTO2.setDisplayName("Name");
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO2.setOnCommandName(null);
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, second onCommand is not set");

    switchDTO2.setOnCommandName("On command 2");
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, onCommand is different");

    switchDTO2.setOnCommandName("On command");
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO2.setOffCommandName(null);
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, second offCommand is not set");

    switchDTO2.setOffCommandName("Off command 2");
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, offCommand is different");

    switchDTO2.setOffCommandName("Off command");
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO2.setSensorName(null);
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, second sensorName is not set");

    switchDTO2.setSensorName("Sensor 2");
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, sensorName is different");

    switchDTO2.setSensorName("Sensor");
    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
    
    switchDTO2.setDeviceName(null);
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, second deviceName is not set");
    
    switchDTO2.setDeviceName("Device name 2");
    Assert.assertFalse(switchDTO1.equals(switchDTO2), "Expected the SwitchWithInfoDTO to be different, deviceName is different");
  }
  
  @Test
  public void testConstructor() {
    SwitchWithInfoDTO switchDTO1 = new SwitchWithInfoDTO();
    switchDTO1.setOid(IDUtil.nextID());
    switchDTO1.setDisplayName("Name");
    switchDTO1.setOnCommandName("On command");
    switchDTO1.setOffCommandName("Off command");
    switchDTO1.setSensorName("Sensor");
    switchDTO1.setDeviceName("Device name");

    SwitchWithInfoDTO switchDTO2 = new SwitchWithInfoDTO(switchDTO1.getOid(), "Name", "On command", "Off command", "Sensor", "Device name");

    Assert.assertEquals(switchDTO1, switchDTO2, "Expected the SwitchWithInfoDTO to be equal");
  }

}
