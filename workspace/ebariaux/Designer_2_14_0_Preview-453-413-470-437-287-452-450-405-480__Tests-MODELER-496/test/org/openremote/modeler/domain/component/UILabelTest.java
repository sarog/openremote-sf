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
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UILabelTest {
  
  @Test
  public void testLabelsEqual() {
    UILabel label1 = new UILabel(IDUtil.nextID());
    UILabel label2 = new UILabel(label1.getOid());

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

    label1.setText("Label");
    label2.setText("Label");

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

    label1.setColor("red");
    label2.setColor("red");

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

    label1.setFontSize(12);
    label2.setFontSize(12);

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

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

    label1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    label2.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");
  }

  @Test
  public void testLabelsNotEqual() {
    UILabel label1 = new UILabel(IDUtil.nextID());
    label1.setText("Label");
    label1.setColor("red");
    label1.setFontSize(12);
    
    UILabel label2 = new UILabel(label1.getOid());
    label2.setText("Label");
    label2.setColor("red");
    label2.setFontSize(12);

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

    label2.setOid(IDUtil.nextID());
    Assert.assertFalse(label1.equals(label2), "Expected the Labels to be different, id is different");

    label2.setOid(label1.getOid());
    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

    label2.setText("Label 2");
    Assert.assertFalse(label1.equals(label2), "Expected the Labels to be different, text is different");

    label2.setText("Label");
    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

    label2.setColor("green");
    Assert.assertFalse(label1.equals(label2), "Expected the Labels to be different, color is different");

    label2.setColor("red");
    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");
    
    label2.setFontSize(15);
    Assert.assertFalse(label1.equals(label2), "Expected the Labels to be different, fontSize is different");
    
    label2.setFontSize(12);
    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");

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

    label1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    label2.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");
    
    label2.setSensor(null);
    Assert.assertFalse(label1.equals(label2), "Expected the Labels to be different, sensor is different");

    Sensor sensor2 = new Sensor(SensorType.SWITCH);
    sensor2.setOid(IDUtil.nextID());
    sensor2.setName("Sensor 2");
    sensor2.setDevice(device);

    SensorCommandRef sensorCommandRef2 = new SensorCommandRef();
    sensorCommandRef2.setSensor(sensor2);
    sensorCommandRef2.setDeviceCommand(deviceCommand);
    sensor2.setSensorCommandRef(sensorCommandRef2);

    // TODO: test fails for now, as DTO is not used in compare, only sensor itself
    label2.setSensorDTOAndInitSensorLink(sensor2.getSensorWithInfoDTO());
    Assert.assertFalse(label1.equals(label2), "Expected the Labels to be different, sensor is different");
  }
  
  @Test
  public void testCopyConstructor() {
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

    UILabel label1 = new UILabel(IDUtil.nextID());
    label1.setText("Label");
    label1.setColor("red");
    label1.setFontSize(12);
    label1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    
    UILabel label2 = new UILabel(label1);

    Assert.assertEquals(label1, label2, "Expected the Labels to be equal");
  }
}
