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
package org.openremote.modeler.domain.component;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ColorPickerTest {

  @Test
  public void testColorPickersEqual() {
    ColorPicker colorPicker1 = new ColorPicker();
    colorPicker1.setOid(IDUtil.nextID());
  
    ColorPicker colorPicker2 = new ColorPicker();
    colorPicker2.setOid(colorPicker1.getOid());

    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");

    ImageSource imageSource = new ImageSource("Image");

    colorPicker1.setImage(imageSource);
    colorPicker2.setImage(imageSource);

    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    colorPicker1.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
    colorPicker2.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
    
    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");
  }
  
  @Test
  public void testColorPickersNotEqual() {
    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand1 = new DeviceCommand();
    deviceCommand1.setOid(IDUtil.nextID());
    deviceCommand1.createProtocol("http");
    deviceCommand1.setDevice(device);
    device.getDeviceCommands().add(deviceCommand1);
    
    DeviceCommand deviceCommand2 = new DeviceCommand();
    deviceCommand2.setOid(IDUtil.nextID());
    deviceCommand2.createProtocol("http");
    deviceCommand2.setDevice(device);
    device.getDeviceCommands().add(deviceCommand2);
  
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");
    
    ColorPicker colorPicker1 = new ColorPicker();
    colorPicker1.setOid(IDUtil.nextID());
    colorPicker1.setImage(imageSource1);
    colorPicker1.setUiCommandDTO(deviceCommand1.getDeviceCommandDTO());
  
    ColorPicker colorPicker2 = new ColorPicker();
    colorPicker2.setOid(colorPicker1.getOid());
    colorPicker2.setImage(imageSource1);
    colorPicker2.setUiCommandDTO(deviceCommand1.getDeviceCommandDTO());
    
    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");
  
    colorPicker2.setOid(IDUtil.nextID());
    Assert.assertFalse(colorPicker1.equals(colorPicker2), "Expected the ColorPickers to be different, id is different");
    
    colorPicker2.setOid(colorPicker1.getOid());
    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");

    colorPicker2.setImage(null);
    Assert.assertFalse(colorPicker1.equals(colorPicker2), "Expected the ColorPickers to be different, second one has no image");
    
    colorPicker2.setImage(imageSource2);
    Assert.assertFalse(colorPicker1.equals(colorPicker2), "Expected the ColorPickers to be different, image is different");

    colorPicker2.setImage(imageSource1);
    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");
    
    colorPicker2.setUiCommandDTO(deviceCommand2.getDeviceCommandDTO());
    Assert.assertFalse(colorPicker1.equals(colorPicker2), "Expected the ColorPickers to be different, command is different");
  }
  
  @Test
  public void testCopyConstructor() {
    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);
    
    ImageSource imageSource = new ImageSource("Image");
    
    ColorPicker colorPicker1 = new ColorPicker();
    colorPicker1.setOid(IDUtil.nextID());
    colorPicker1.setImage(imageSource);
    colorPicker1.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
  
    ColorPicker colorPicker2 = new ColorPicker(colorPicker1);

    Assert.assertEquals(colorPicker1, colorPicker2, "Expected the ColorPickers to be equal");
  }
}
