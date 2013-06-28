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
public class SliderWithInfoDTOTest {

  @Test
  public void testSliderWithInfoDTOsEqual() {
    SliderWithInfoDTO sliderDTO1 = new SliderWithInfoDTO();
    SliderWithInfoDTO sliderDTO2 = new SliderWithInfoDTO();
    
    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
    
    sliderDTO1.setOid(IDUtil.nextID());
    sliderDTO2.setOid(sliderDTO1.getOid());

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");

    sliderDTO1.setDisplayName("Name");
    sliderDTO2.setDisplayName("Name");

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");

    sliderDTO1.setCommandName("Command name");
    sliderDTO2.setCommandName("Command name");

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");

    sliderDTO1.setSensorName("Sensor name");
    sliderDTO2.setSensorName("Sensor name");

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");

    sliderDTO1.setDeviceName("Device name");
    sliderDTO2.setDeviceName("Device name");

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
  }
  
  @Test
  public void testSliderWithInfoDTOsNotEqual() {
    SliderWithInfoDTO sliderDTO1 = new SliderWithInfoDTO();
    sliderDTO1.setOid(IDUtil.nextID());
    sliderDTO1.setDisplayName("Name");
    sliderDTO1.setCommandName("Command name");
    sliderDTO1.setSensorName("Sensor name");
    sliderDTO1.setDeviceName("Device name");
    
    SliderWithInfoDTO sliderDTO2 = new SliderWithInfoDTO();
    sliderDTO2.setOid(sliderDTO1.getOid());
    sliderDTO2.setDisplayName("Name");
    sliderDTO2.setCommandName("Command name");
    sliderDTO2.setSensorName("Sensor name");
    sliderDTO2.setDeviceName("Device name");

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
    
    sliderDTO2.setOid(null);
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, second id is not set");

    sliderDTO2.setOid(IDUtil.nextID());
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, id is different");

    sliderDTO2.setOid(sliderDTO1.getOid());
    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
    
    sliderDTO2.setDisplayName(null);
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, second displayName is not set");

    sliderDTO2.setDisplayName("Name 2");
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, displayName is different");

    sliderDTO2.setDisplayName("Name");
    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
    
    sliderDTO2.setCommandName(null);
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, second commandName is not set");

    sliderDTO2.setCommandName("Command name 2");
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, commandName is different");

    sliderDTO2.setCommandName("Command name");
    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
    
    sliderDTO2.setSensorName(null);
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, second sensorName is not set");

    sliderDTO2.setSensorName("Sensor name 2");
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, sensorName is different");

    sliderDTO2.setSensorName("Sensor name");
    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
    
    sliderDTO2.setDeviceName(null);
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, second deviceName is not set");

    sliderDTO2.setDeviceName("Device name 2");
    Assert.assertFalse(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be different, deviceName is different");
  }

  @Test
  public void testConstructor() {
    SliderWithInfoDTO sliderDTO1 = new SliderWithInfoDTO();
    sliderDTO1.setOid(IDUtil.nextID());
    sliderDTO1.setDisplayName("Name");
    sliderDTO1.setCommandName("Command name");
    sliderDTO1.setSensorName("Sensor name");
    sliderDTO1.setDeviceName("Device name");
    
    SliderWithInfoDTO sliderDTO2 = new SliderWithInfoDTO(sliderDTO1.getOid(), "Name", "Command name", "Sensor name", "Device name");

    Assert.assertTrue(sliderDTO1.equalityEquals(sliderDTO2), "Expected the SliderWithInfoDTO to be equal");
  }

}
