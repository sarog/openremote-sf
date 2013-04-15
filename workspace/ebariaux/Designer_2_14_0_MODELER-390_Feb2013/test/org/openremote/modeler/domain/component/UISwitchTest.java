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
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Switch;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UISwitchTest {

  @Test
  public void testSwitchesEqual() {
    UISwitch switch1 = new UISwitch();
    UISwitch switch2 = new UISwitch();
    
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");
    
    switch1.setOid(IDUtil.nextID());
    switch2.setOid(switch1.getOid());

    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");

    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");

    switch1.setOnImage(imageSource1);
    switch2.setOnImage(imageSource1);

    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");

    switch1.setOffImage(imageSource2);
    switch2.setOffImage(imageSource2);

    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor = new Sensor(SensorType.SWITCH);
    sensor.setOid(IDUtil.nextID());
    sensor.setName("Sensor");
    sensor.setDevice(device);

    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setSensor(sensor);
    sensorCommandRef.setDeviceCommand(deviceCommand);
    sensor.setSensorCommandRef(sensorCommandRef);

    DeviceCommand deviceCommand2 = new DeviceCommand();
    deviceCommand2.createProtocol("http");
    deviceCommand2.setDevice(device);
    device.getDeviceCommands().add(deviceCommand2);

    DeviceCommand deviceCommand3 = new DeviceCommand();
    deviceCommand3.createProtocol("http");
    deviceCommand3.setDevice(device);
    device.getDeviceCommands().add(deviceCommand3);
    
    Switch buildingSwitch = new Switch(deviceCommand2, deviceCommand3, sensor);
    
    switch1.setSwitchDTO(buildingSwitch.getSwitchWithInfoDTO());
    switch2.setSwitchDTO(buildingSwitch.getSwitchWithInfoDTO());
    
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");
  }
 
  @Test
  public void testSwitchesNotEqual() {
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor = new Sensor(SensorType.SWITCH);
    sensor.setOid(IDUtil.nextID());
    sensor.setName("Sensor");
    sensor.setDevice(device);

    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setSensor(sensor);
    sensorCommandRef.setDeviceCommand(deviceCommand);
    sensor.setSensorCommandRef(sensorCommandRef);

    DeviceCommand deviceCommand2 = new DeviceCommand();
    deviceCommand2.createProtocol("http");
    deviceCommand2.setDevice(device);
    device.getDeviceCommands().add(deviceCommand2);

    DeviceCommand deviceCommand3 = new DeviceCommand();
    deviceCommand3.createProtocol("http");
    deviceCommand3.setDevice(device);
    device.getDeviceCommands().add(deviceCommand3);
    
    Switch buildingSwitch1 = new Switch(deviceCommand2, deviceCommand3, sensor);

    UISwitch switch1 = new UISwitch();
    switch1.setOid(IDUtil.nextID());
    switch1.setOnImage(imageSource1);
    switch1.setOffImage(imageSource2);
    switch1.setSwitchDTO(buildingSwitch1.getSwitchWithInfoDTO());

    UISwitch switch2 = new UISwitch();
    switch2.setOid(switch1.getOid());
    switch2.setOnImage(imageSource1);
    switch2.setOffImage(imageSource2);   
    switch2.setSwitchDTO(buildingSwitch1.getSwitchWithInfoDTO());
    
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");
    
    switch2.setOid(IDUtil.nextID());
    Assert.assertFalse(switch1.equals(switch2), "Expected the Switches to be different, id is different");

    switch2.setOid(switch1.getOid());
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");

    switch2.setOnImage(null);
    Assert.assertFalse(switch1.equals(switch2), "Expected the Switches to be different, second onImage is not set");

    switch2.setOnImage(imageSource2);
    Assert.assertFalse(switch1.equals(switch2), "Expected the Switches to be different, onImage is different");
    
    switch2.setOnImage(imageSource1);
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");

    switch2.setOffImage(null);
    Assert.assertFalse(switch1.equals(switch2), "Expected the Switches to be different, second offImage is not set");

    switch2.setOffImage(imageSource1);
    Assert.assertFalse(switch1.equals(switch2), "Expected the Switches to be different, onImage is different");
    
    switch2.setOffImage(imageSource2);
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");

    DeviceCommand deviceCommand4 = new DeviceCommand();
    deviceCommand4.createProtocol("http");
    deviceCommand4.setDevice(device);
    device.getDeviceCommands().add(deviceCommand4);
    
    Switch buildingSwitch2 = new Switch(deviceCommand2, deviceCommand4, sensor);
    switch2.setSwitchDTO(buildingSwitch2.getSwitchWithInfoDTO());
    
    Assert.assertFalse(switch1.equals(switch2), "Expected the Switches to be different, building switch is different");   
  }

  @Test
  public void testCopyConstructor() {
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor = new Sensor(SensorType.SWITCH);
    sensor.setOid(IDUtil.nextID());
    sensor.setName("Sensor");
    sensor.setDevice(device);

    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setSensor(sensor);
    sensorCommandRef.setDeviceCommand(deviceCommand);
    sensor.setSensorCommandRef(sensorCommandRef);

    DeviceCommand deviceCommand2 = new DeviceCommand();
    deviceCommand2.createProtocol("http");
    deviceCommand2.setDevice(device);
    device.getDeviceCommands().add(deviceCommand2);

    DeviceCommand deviceCommand3 = new DeviceCommand();
    deviceCommand3.createProtocol("http");
    deviceCommand3.setDevice(device);
    device.getDeviceCommands().add(deviceCommand3);
    
    Switch buildingSwitch1 = new Switch(deviceCommand2, deviceCommand3, sensor);

    UISwitch switch1 = new UISwitch();
    switch1.setOid(IDUtil.nextID());
    switch1.setOnImage(imageSource1);
    switch1.setOffImage(imageSource2);
    switch1.setSwitchDTO(buildingSwitch1.getSwitchWithInfoDTO());

    UISwitch switch2 = new UISwitch(switch1);
    
    Assert.assertEquals(switch1, switch2, "Expected the Switches to be equal");
  }
}