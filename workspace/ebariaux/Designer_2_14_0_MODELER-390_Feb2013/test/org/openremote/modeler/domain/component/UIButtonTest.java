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
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UIButtonTest {

  @Test
  public void testButtonsEquals() {
  	UIButton button1 = new UIButton(IDUtil.nextID());
  	UIButton button2 = new UIButton(button1.getOid());
  	
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  	
    button1.setName("Button");
    button2.setName("Button");

    Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
    
    button1.setRepeate(true);
    button2.setRepeate(true);
    
    Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
    
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");

    button1.setImage(imageSource1);
    button2.setImage(imageSource1);

    Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
    
    button1.setPressImage(imageSource2);
    button2.setPressImage(imageSource2);

    Assert.assertEquals(button1, button2, "Expected the buttons to be equal");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    button1.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
    button2.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());

    Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  }
  
  @Test
  public void testButtonsWithNavigateEquals() {
  	Navigate navigate = new Navigate();
  	navigate.setOid(IDUtil.nextID());
  	navigate.setToGroup(1L);
  	navigate.setToScreen(2L);
  	
  	UIButton button1 = new UIButton(IDUtil.nextID());
  	button1.setName("Button");
  	button1.setRepeate(true);
  	button1.setNavigate(navigate);
  	
  	UIButton button2 = new UIButton(button1.getOid());
  	button2.setName("Button");
  	button2.setRepeate(true);
  	button2.setNavigate(navigate);
  	
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  	
  	navigate = new Navigate();
  	navigate.setOid(IDUtil.nextID());
  	navigate.setToLogical(ToLogicalType.login);
  	
  	button1.setNavigate(navigate);
  	button2.setNavigate(navigate);
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  }
  
  @Test
  public void testButtonsNotEqual() {
  	Device device = new Device("Test device", "Test brand", "Test model");
  	device.setOid(IDUtil.nextID());
  	  
  	DeviceCommand deviceCommand1 = new DeviceCommand();
  	deviceCommand1.createProtocol("http");
  	deviceCommand1.setDevice(device);
  	device.getDeviceCommands().add(deviceCommand1);
  	
  	DeviceCommand deviceCommand2 = new DeviceCommand();
  	deviceCommand2.createProtocol("http");
  	deviceCommand2.setDevice(device);
  	device.getDeviceCommands().add(deviceCommand2);
  
  	ImageSource imageSource1 = new ImageSource("Image 1");
  	ImageSource imageSource2 = new ImageSource("Image 2");
  	
  	UIButton button1 = new UIButton(IDUtil.nextID());
  	button1.setName("Button");
  	button1.setRepeate(true);
  	button1.setImage(imageSource1);
  	button1.setPressImage(imageSource2);
  	button1.setUiCommandDTO(deviceCommand1.getDeviceCommandDTO());
  	
  	UIButton button2 = new UIButton(button1.getOid());
  	button2.setName("Button");
  	button2.setRepeate(true);
  	button2.setImage(imageSource1);
  	button2.setPressImage(imageSource2);
  	button2.setUiCommandDTO(deviceCommand1.getDeviceCommandDTO());
  	
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  
    button2.setName(null);
    Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, second name is not set");

  	button2.setName("Button 2");
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, name is different");
  	
  	button2.setName("Button");
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  	
  	button2.setOid(IDUtil.nextID());
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, id is different");
  	
  	button2.setOid(button1.getOid());
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  	
  	button2.setRepeate(false);
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, repeate is different");
  
  	button2.setRepeate(true);
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");

    button2.setImage(imageSource2);
    Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, image is different");

  	button2.setImage(null);
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, second image is not set");
  	
  	button2.setImage(imageSource1);
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  	
  	button2.setPressImage(null);
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, second press image is not set");
  	
  	button2.setPressImage(imageSource1);
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, press image is different");
  	
  	button2.setPressImage(imageSource2);
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  	
  	// TODO: test fails for now, as DTO is not used in compare, only command itself
  	button2.setUiCommandDTO(deviceCommand2.getDeviceCommandDTO());
  	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, command is different");
  }
  
  @Test
  public void testCopyConstructor() {
  	Device device = new Device("Test device", "Test brand", "Test model");
  	device.setOid(IDUtil.nextID());
  	  
  	DeviceCommand deviceCommand = new DeviceCommand();
  	deviceCommand.createProtocol("http");
  	deviceCommand.setDevice(device);
  	device.getDeviceCommands().add(deviceCommand);
  	
  	ImageSource imageSource1 = new ImageSource("Image 1");
  	ImageSource imageSource2 = new ImageSource("Image 2");
  
  	Navigate navigate = new Navigate();
  	navigate.setOid(IDUtil.nextID());
  	navigate.setToGroup(1L);
  	navigate.setToScreen(2L);
  
  	UIButton button1 = new UIButton(IDUtil.nextID());
  	button1.setName("Button 1");
  	button1.setRepeate(true);
  	button1.setImage(imageSource1);
  	button1.setPressImage(imageSource2);
  	button1.setNavigate(navigate);
  	button1.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
  
  	UIButton button2 = new UIButton(button1);
  	
  	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  }
  
}
