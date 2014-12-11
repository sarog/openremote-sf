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
import org.openremote.modeler.domain.Slider;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class UISliderTest {

  @Test
  public void testSlidersEqual() {
    UISlider slider1 = new UISlider();
    UISlider slider2 = new UISlider();
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
    
    slider1.setOid(IDUtil.nextID());
    slider2.setOid(slider1.getOid());
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider1.setVertical(true);
    slider2.setVertical(true);
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
    
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");
    ImageSource imageSource3 = new ImageSource("Image 3");
    ImageSource imageSource4 = new ImageSource("Image 4");
    ImageSource imageSource5 = new ImageSource("Image 5");

    slider1.setThumbImage(imageSource1);
    slider2.setThumbImage(imageSource1);
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider1.setMinImage(imageSource2);
    slider2.setMinImage(imageSource2);
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider1.setMinTrackImage(imageSource3);
    slider2.setMinTrackImage(imageSource3);

    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider1.setMaxImage(imageSource4);
    slider2.setMaxImage(imageSource4);
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider1.setMaxTrackImage(imageSource5);
    slider2.setMaxTrackImage(imageSource5);
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor = new Sensor(SensorType.LEVEL);
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

    Slider buildingSlider = new Slider("Slider", deviceCommand2, sensor);
    
    slider1.setSliderDTO(buildingSlider.getSliderWithInfoDTO());
    slider2.setSliderDTO(buildingSlider.getSliderWithInfoDTO());
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
  }
  
  @Test
  public void testSlidersNotEqual() {
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");
    ImageSource imageSource3 = new ImageSource("Image 3");
    ImageSource imageSource4 = new ImageSource("Image 4");
    ImageSource imageSource5 = new ImageSource("Image 5");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor = new Sensor(SensorType.LEVEL);
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

    Slider buildingSlider1 = new Slider("Slider 1", deviceCommand2, sensor);
    
    UISlider slider1 = new UISlider();
    slider1.setOid(IDUtil.nextID());
    slider1.setVertical(true);
    slider1.setThumbImage(imageSource1);
    slider1.setMinImage(imageSource2);
    slider1.setMinTrackImage(imageSource3);
    slider1.setMaxImage(imageSource4);
    slider1.setMaxTrackImage(imageSource5);
    slider1.setSliderDTO(buildingSlider1.getSliderWithInfoDTO());

    UISlider slider2 = new UISlider();
    slider2.setOid(slider1.getOid());
    slider2.setVertical(true);
    slider2.setThumbImage(imageSource1);
    slider2.setMinImage(imageSource2);
    slider2.setMinTrackImage(imageSource3);
    slider2.setMaxImage(imageSource4);
    slider2.setMaxTrackImage(imageSource5);
    slider2.setSliderDTO(buildingSlider1.getSliderWithInfoDTO());
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
    
    slider2.setOid(IDUtil.nextID());
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, id is different");

    slider2.setOid(slider1.getOid());
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
    
    slider2.setVertical(false);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, vertical is different");
    
    slider2.setVertical(true);
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider2.setThumbImage(null);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, second thumbImage is not set");

    slider2.setThumbImage(imageSource2);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, thumbImage is different");

    slider2.setThumbImage(imageSource1);
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider2.setMinImage(null);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, second minImage is not set");

    slider2.setMinImage(imageSource1);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, minImage is different");

    slider2.setMinImage(imageSource2);
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider2.setMinTrackImage(null);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, second minTrackImage is not set");

    slider2.setMinTrackImage(imageSource1);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, minTrackImage is different");

    slider2.setMinTrackImage(imageSource3);
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
    
    slider2.setMaxImage(null);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, second maxImage is not set");

    slider2.setMaxImage(imageSource1);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, maxImage is different");

    slider2.setMaxImage(imageSource4);
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");

    slider2.setMaxTrackImage(null);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, second maxTrackImage is not set");

    slider2.setMaxTrackImage(imageSource1);
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, maxTrackImage is different");

    slider2.setMaxTrackImage(imageSource5);
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
    
    DeviceCommand deviceCommand3 = new DeviceCommand();
    deviceCommand3.createProtocol("http");
    deviceCommand3.setDevice(device);
    device.getDeviceCommands().add(deviceCommand3);

    Slider buildingSlider2 = new Slider("Slider 2", deviceCommand3, sensor);
    
    slider2.setSliderDTO(buildingSlider2.getSliderWithInfoDTO());
    Assert.assertFalse(slider1.equals(slider2), "Expected the Sliders to be different, building slider is different");
  }
  
  @Test
  public void testCopyConstructor() {
    ImageSource imageSource1 = new ImageSource("Image 1");
    ImageSource imageSource2 = new ImageSource("Image 2");
    ImageSource imageSource3 = new ImageSource("Image 3");
    ImageSource imageSource4 = new ImageSource("Image 4");
    ImageSource imageSource5 = new ImageSource("Image 5");

    Device device = new Device("Test device", "Test brand", "Test model");
    device.setOid(IDUtil.nextID());
      
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.createProtocol("http");
    deviceCommand.setDevice(device);
    device.getDeviceCommands().add(deviceCommand);

    Sensor sensor = new Sensor(SensorType.LEVEL);
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

    Slider buildingSlider1 = new Slider("Slider 1", deviceCommand2, sensor);
    
    UISlider slider1 = new UISlider();
    slider1.setOid(IDUtil.nextID());
    slider1.setVertical(true);
    slider1.setThumbImage(imageSource1);
    slider1.setMinImage(imageSource2);
    slider1.setMinTrackImage(imageSource3);
    slider1.setMaxImage(imageSource4);
    slider1.setMaxTrackImage(imageSource5);
    slider1.setSliderDTO(buildingSlider1.getSliderWithInfoDTO());

    UISlider slider2 = new UISlider(slider1);
    
    Assert.assertEquals(slider1, slider2, "Expected the Sliders to be equal");
  }
}
