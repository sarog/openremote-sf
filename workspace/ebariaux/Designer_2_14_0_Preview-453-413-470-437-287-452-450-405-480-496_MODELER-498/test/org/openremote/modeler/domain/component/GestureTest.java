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
import org.openremote.modeler.domain.component.Gesture.GestureType;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class GestureTest {

  @Test
  public void testGesturesEqual() {
    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);
  
    Gesture gesture1 = new Gesture();
    gesture1.setOid(IDUtil.nextID());
    gesture1.setType(GestureType.swipe_bottom_to_top);
  
    Gesture gesture2 = new Gesture();
    gesture2.setOid(gesture1.getOid());
    gesture2.setType(GestureType.swipe_bottom_to_top);

    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");

    gesture1.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
    gesture2.setUiCommandDTO(deviceCommand.getDeviceCommandDTO());
    
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");
  }
  
  @Test
  public void testGesturesWithNavigateEqual() {
    Navigate navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToGroup(1L);
    navigate.setToScreen(2L);
  
    Gesture gesture1 = new Gesture();
    gesture1.setOid(IDUtil.nextID());
    gesture1.setType(GestureType.swipe_bottom_to_top);
    gesture1.setNavigate(navigate);
  
    Gesture gesture2 = new Gesture();
    gesture2.setOid(gesture1.getOid());
    gesture2.setType(GestureType.swipe_bottom_to_top);
    gesture2.setNavigate(navigate);
    
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");
  
    navigate = new Navigate();
    navigate.setOid(IDUtil.nextID());
    navigate.setToLogical(ToLogicalType.login);
  
    gesture1.setNavigate(navigate);
    gesture2.setNavigate(navigate);
    
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");
  }
  
  @Test
  public void testGesturesNotEqual() {
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

    Navigate navigate1 = new Navigate();
    navigate1.setOid(IDUtil.nextID());
    navigate1.setToGroup(1L);
    navigate1.setToScreen(2L);

    Gesture gesture1 = new Gesture();
    gesture1.setOid(IDUtil.nextID());
    gesture1.setType(GestureType.swipe_bottom_to_top);
    gesture1.setNavigate(navigate1);
  
    Gesture gesture2 = new Gesture();
    gesture2.setOid(gesture1.getOid());
    gesture2.setType(GestureType.swipe_bottom_to_top);
    gesture2.setNavigate(navigate1);
    
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");

    gesture2.setOid(IDUtil.nextID());
    Assert.assertFalse(gesture1.equals(gesture2), "Expected the Gestures to be different, id is different");

    gesture2.setOid(gesture1.getOid());
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");
  
    gesture2.setType(GestureType.swipe_left_to_right);
    Assert.assertFalse(gesture1.equals(gesture2), "Expected the Gestures to be different, gesture type is different");
    
    gesture2.setType(gesture1.getType());
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");
    
    Navigate navigate2 = new Navigate();
    navigate2.setOid(IDUtil.nextID());
    navigate2.setToGroup(2L);
    navigate2.setToScreen(3L);
    
    gesture2.setNavigate(navigate2);
    Assert.assertFalse(gesture1.equals(gesture2), "Expected the Gestures to be different, navigation is different");

    gesture1.setNavigate(null);
    gesture2.setNavigate(null);
    gesture1.setUiCommandDTO(deviceCommand1.getDeviceCommandDTO());
    gesture2.setUiCommandDTO(deviceCommand1.getDeviceCommandDTO());
    Assert.assertEquals(gesture1, gesture2, "Expected the Gestures to be equal");
    
    gesture2.setUiCommandDTO(deviceCommand2.getDeviceCommandDTO());
    Assert.assertFalse(gesture1.equals(gesture2), "Expected the Gestures to be different, command is different");
  }

}
