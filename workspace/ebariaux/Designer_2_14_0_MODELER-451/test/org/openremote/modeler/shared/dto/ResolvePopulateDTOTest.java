/* OpenRemote, the Home of the Digital Home.
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.service.ControllerConfigService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.DeviceMacroService;
import org.openremote.modeler.service.DeviceService;
import org.openremote.modeler.service.ResourceService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.service.impl.ResourceServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class does not match a specific existing class to test
 * but tests a specific feature related to DTOs.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class ResolvePopulateDTOTest {
  
  private ResourceService resourceService;
  private DeviceService deviceService;
  private DeviceCommandService deviceCommandService;
  
  @BeforeClass
  public void setUp() {

    deviceService = (DeviceService) SpringTestContext.getInstance().getBean("deviceService");
    deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
    /*
    // Getting the ResourceService from Spring causes issue with reflection call, as we're getting a proxy and not the actual class.
    // Instantiate the class and inject dependencies manually solves that issue.
    
    
    // TODO EBR : creating bean like that bypasses transaction management -> issue with Hibernate session not opened on called methods
    
     ResourceServiceImpl rsi = new ResourceServiceImpl();
     rsi.setConfiguration((Configuration)SpringTestContext.getInstance().getBean("configuration"));
     rsi.setDeviceCommandService(deviceCommandService);
     rsi.setDeviceMacroService((DeviceMacroService) SpringTestContext.getInstance().getBean("deviceMacroService"));
     rsi.setSensorService((SensorService) SpringTestContext.getInstance().getBean("sensorService"));
     rsi.setSliderService((SliderService) SpringTestContext.getInstance().getBean("sliderService"));
     rsi.setSwitchService((SwitchService) SpringTestContext.getInstance().getBean("switchService"));
     rsi.setVelocity((VelocityEngine) SpringTestContext.getInstance().getBean("velocity"));
     rsi.setUserService((UserService) SpringTestContext.getInstance().getBean("userService"));
     rsi.setControllerConfigService((ControllerConfigService) SpringTestContext.getInstance().getBean("controllerConfigService"));
     resourceService = rsi;
     
     */

    resourceService = (ResourceService) SpringTestContext.getInstance().getBean("resourceService");
  }
  
  @Test @Transactional
  public void testPopulateDTOReferences() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    List<Panel> panels = new ArrayList<Panel>();   
    Panel panel = new Panel();
    panel.setOid(IDUtil.nextID());
    panel.setName("panel");  
    panels.add(panel);

    List<GroupRef> groupRefs = new ArrayList<GroupRef>();
    Group group = new Group();
    group.setOid(IDUtil.nextID());
    group.setName("group");
    groupRefs.add(new GroupRef(group));
    panel.setGroupRefs(groupRefs);
    
    Screen screen = new Screen();
    screen.setOid(IDUtil.nextID());
    screen.setName("screen1");
    ScreenPair screenPair = new ScreenPair();
    screenPair.setOid(IDUtil.nextID());
    screenPair.setPortraitScreen(screen);
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    screenRefs.add(new ScreenPairRef(screenPair));
    group.setScreenRefs(screenRefs);
    
    Device device = new Device();
    device.setVendor("Vendor");
    device.setModel("Model");
    device.setName("Device");
    deviceService.saveDevice(device);
    
    DeviceCommand deviceCommand = new DeviceCommand();
    deviceCommand.setName("Command1");
    deviceCommand.setDevice(device);
    deviceCommand.setOid(IDUtil.nextID());
    
    Protocol protocol = new Protocol();
    protocol.setType("http");
    protocol.setDeviceCommand(deviceCommand);
    ProtocolAttr protocolAttr = new ProtocolAttr();
    protocolAttr.setName("url");
    protocolAttr.setValue("http://www.sina.com");
    protocolAttr.setProtocol(protocol);
    protocol.getAttributes().add(protocolAttr);
    deviceCommand.setProtocol(protocol);

    deviceCommandService.save(deviceCommand);
    
    Absolute abs = new Absolute();
    screen.addAbsolute(abs);
    
    UIButton button = new UIButton();
    button.setName("Button");
    abs.setUiComponent(button);

    DeviceCommandRef uiCommand = new DeviceCommandRef();
    uiCommand.setDeviceCommand(deviceCommand);
    uiCommand.setDeviceName(device.getName());
    uiCommand.setOid(IDUtil.nextID());
    button.setUiCommand(uiCommand);
    
    //ÊMethod is private, using reflection to test
    /*
    Method method = resourceService.getClass().getDeclaredMethod("populateDTOReferences", Collection.class);
    Assert.assertNotNull(method);
    method.setAccessible(true);
    method.invoke(resourceService, panels);
    */
    resourceService.populateDTOReferences(panels);
    
    Assert.assertEquals("Panels count should stay identical", 1, panels.size());
    Assert.assertEquals("Groups count should stay identical", 1, panels.get(0).getGroups().size());
    Assert.assertEquals("Screens count should stay identical", 1, panels.get(0).getGroups().get(0).getScreenRefs().size());


/*    
    Absolute abs1 = new Absolute();
    abs1.setUiComponent(absBtn);
    Absolute abs2 = new Absolute();
    abs2.setUiComponent(absSwitch);
    
    UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
    Cell c1 = new Cell();
    c1.setUiComponent(gridBtn);
    grid1.addCell(c1);
    UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
    Cell c2 = new Cell();
    c2.setUiComponent(gridSwitch);
    grid2.addCell(c2);
    Cell uiImageCell = new Cell();
    uiImageCell.setUiComponent(uiImage);
    grid2.addCell(uiImageCell);
    
    Cell labelCell = new Cell();
    labelCell.setUiComponent(label);
    grid2.addCell(labelCell);
    
    screen.addAbsolute(abs1);
    screen2.addAbsolute(abs2);
    
    screen.addGrid(grid1);
    screen2.addGrid(grid2);
    
    ScreenPair screenPair1 = new ScreenPair();
    screenPair1.setOid(IDUtil.nextID());
    screenPair1.setPortraitScreen(screen);
    
    ScreenPair screenPair2 = new ScreenPair();
    screenPair2.setOid(IDUtil.nextID());
    screenPair2.setPortraitScreen(screen2);
    
    screenRefs.add(new ScreenPairRef(screenPair1));
    screenRefs.add(new ScreenPairRef(screenPair2));
  */

  }

}
