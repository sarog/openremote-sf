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
public class UIImageTest {

  @Test
  public void testImagesEqual() {
    ImageSource imageSource1 = new ImageSource("Image");
   
    UIImage image1 = new UIImage(IDUtil.nextID());
    UIImage image2 = new UIImage(image1.getOid());

    Assert.assertEquals(image1, image2, "Expected the Images to be equal");

    image1.setImageSource(imageSource1);
    image2.setImageSource(imageSource1);

    Assert.assertEquals(image1, image2, "Expected the Images to be equal");
   
    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    label.setColor("red");
    label.setFontSize(12);

    image1.setLabel(label);
    image2.setLabel(label);

    Assert.assertEquals(image1, image2, "Expected the Images to be equal");

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

    image1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    image2.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());

    Assert.assertEquals(image1, image2, "Expected the Images to be equal");
  }
  
  @Test
  public void testImagesNotEqual() {
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");
   
    UIImage image1 = new UIImage(IDUtil.nextID());
    image1.setImageSource(imageSource1);

    UIImage image2 = new UIImage(image1.getOid());
    image2.setImageSource(imageSource1);

    Assert.assertEquals(image1, image2, "Expected the Images to be equal");
    
    image2.setOid(IDUtil.nextID());
    Assert.assertFalse(image1.equals(image2), "Expected the Images to be different, id is different");

    image2.setOid(image1.getOid());
    Assert.assertEquals(image1, image2, "Expected the Images to be equal");
    
    image2.setImageSource(imageSource2);
    Assert.assertFalse(image1.equals(image2), "Expected the Images to be different, imageSource is different");
    
    image2.setImageSource(imageSource1);
    Assert.assertEquals(image1, image2, "Expected the Images to be equal");

    UILabel label1 = new UILabel(IDUtil.nextID());
    label1.setText("Label 1");
    label1.setColor("red");
    label1.setFontSize(12);

    UILabel label2 = new UILabel(IDUtil.nextID());
    label2.setText("Label 2");
    label2.setColor("red");
    label2.setFontSize(12);

    image1.setLabel(label1);

    Assert.assertFalse(image1.equals(image2), "Expected the Images to be different, second label is not set");

    image2.setLabel(label2);
    
    Assert.assertFalse(image1.equals(image2), "Expected the Images to be different, label is different");
    
    image2.setLabel(label1);
    Assert.assertEquals(image1, image2, "Expected the Images to be equal");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor1 = new Sensor(SensorType.SWITCH);
    sensor1.setOid(IDUtil.nextID());
    sensor1.setName("Sensor 1");
    sensor1.setDevice(device);

    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setSensor(sensor1);
    sensorCommandRef.setDeviceCommand(deviceCommand);
    sensor1.setSensorCommandRef(sensorCommandRef);

    image1.setSensorDTOAndInitSensorLink(sensor1.getSensorWithInfoDTO());
    image2.setSensorDTOAndInitSensorLink(sensor1.getSensorWithInfoDTO());

    Assert.assertEquals(image1, image2, "Expected the Images to be equal");
    
    image2.setSensorDTOAndInitSensorLink(null);
    Assert.assertFalse(image1.equals(image2), "Expected the Images to be different, sensor is different");

    Sensor sensor2 = new Sensor(SensorType.SWITCH);
    sensor2.setOid(IDUtil.nextID());
    sensor2.setName("Sensor 2");
    sensor2.setDevice(device);

    SensorCommandRef sensorCommandRef2 = new SensorCommandRef();
    sensorCommandRef2.setSensor(sensor2);
    sensorCommandRef2.setDeviceCommand(deviceCommand);
    sensor2.setSensorCommandRef(sensorCommandRef2);

    image2.setSensorDTOAndInitSensorLink(sensor2.getSensorWithInfoDTO());
    Assert.assertFalse(image1.equals(image2), "Expected the Images to be different, sensor is different");
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

    UILabel label = new UILabel(IDUtil.nextID());
    label.setText("Label");
    label.setColor("red");
    label.setFontSize(12);

    ImageSource imageSource = new ImageSource("Image");
    
    UIImage image1 = new UIImage(IDUtil.nextID());
    image1.setImageSource(imageSource);
    image1.setLabel(label);
    image1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());

    UIImage image2 = new UIImage(image1);
    Assert.assertEquals(image1, image2, "Expected the Images to be equal");
  }
  
}
