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
public class UIWebViewTest {
  
  @Test
  public void testWebViewsEqual() {
    UIWebView webView1 = new UIWebView(IDUtil.nextID());
    UIWebView webView2 = new UIWebView(webView1.getOid());

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView1.setURL("URL");
    webView2.setURL("URL");

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView1.setUserName("Username");
    webView2.setUserName("Username");

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView1.setPassword("Password");
    webView2.setPassword("Password");

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

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

    webView1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    webView2.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");
  }

  @Test
  public void testWebViewsNotEqual() {
    UIWebView webView1 = new UIWebView(IDUtil.nextID());
    webView1.setURL("URL");
    webView1.setUserName("Username");
    webView1.setPassword("Password");

    UIWebView webView2 = new UIWebView(webView1.getOid());
    webView2.setURL("URL");
    webView2.setUserName("Username");
    webView2.setPassword("Password");

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");
    
    webView2.setOid(IDUtil.nextID());
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, id is different");

    webView2.setOid(webView1.getOid());
    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView2.setURL(null);
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, second URL is not set");

    webView2.setURL("URL2");
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, URL is different");

    webView2.setURL("URL");
    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView2.setUserName(null);
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, second username is not set");
    
    webView2.setUserName("Username2");
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, username is different");

    webView2.setUserName("Username");
    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView2.setPassword(null);
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, second password is not set");
    
    webView2.setPassword("Password2");
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, password is different");

    webView2.setPassword("Password");
    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

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

    webView1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    webView2.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");

    webView2.setSensor(null);
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, sensor is different");

    Sensor sensor2 = new Sensor(SensorType.SWITCH);
    sensor2.setOid(IDUtil.nextID());
    sensor2.setName("Sensor 2");
    sensor2.setDevice(device);

    SensorCommandRef sensorCommandRef2 = new SensorCommandRef();
    sensorCommandRef2.setSensor(sensor2);
    sensorCommandRef2.setDeviceCommand(deviceCommand);
    sensor2.setSensorCommandRef(sensorCommandRef2);

    // TODO: test fails for now, as DTO is not used in compare, only sensor itself
    webView2.setSensorDTOAndInitSensorLink(sensor2.getSensorWithInfoDTO());
    Assert.assertFalse(webView1.equals(webView2), "Expected the WebViews to be different, sensor is different");
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

    UIWebView webView1 = new UIWebView(IDUtil.nextID());
    webView1.setURL("URL");
    webView1.setUserName("Username");
    webView1.setPassword("Password");
    webView1.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    
    UIWebView webView2 = new UIWebView(webView1);

    Assert.assertEquals(webView1, webView2, "Expected the WebViews to be equal");
  }

}
