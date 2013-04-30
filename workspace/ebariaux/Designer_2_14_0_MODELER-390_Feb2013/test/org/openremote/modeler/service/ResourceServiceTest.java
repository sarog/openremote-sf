/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.cache.LocalFileCache;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Background;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.RangeSensor;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.domain.Background.RelativeType;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.Gesture.GestureType;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIComponent;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.domain.component.UIWebView;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class ResourceServiceTest {
   
   private static final Logger log = Logger.getLogger(ResourceServiceTest.class);
   
   private Configuration configuration;
   private ResourceService resourceService;
   private DeviceCommandService deviceCommandService;
   private DeviceMacroService deviceMacroService;
   private SensorService sensorService;
   private SwitchService switchService;
   private SliderService sliderService;
   private UserService userService;
   
   private DeviceService deviceService;
   private ControllerConfigService controllerConfigService;
   private VelocityEngine velocity;
   
   private Account account;
   private LocalFileCache cache;
     
   private TransactionTemplate transactionTemplate;
   
   @BeforeClass
   public void setUp() {
	    PathConfig.WEBROOTPATH = System.getProperty("java.io.tmpdir");

	    PlatformTransactionManager transactionManager = (HibernateTransactionManager)SpringTestContext.getInstance().getBean("transactionManager");
	    System.out.println("Transaction manager is " + transactionManager);
	    transactionTemplate = new TransactionTemplate(transactionManager);
	    
      resourceService = (ResourceService)SpringTestContext.getInstance().getBean("resourceService");
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
      deviceMacroService = (DeviceMacroService) SpringTestContext.getInstance().getBean("deviceMacroService");
      sensorService = (SensorService) SpringTestContext.getInstance().getBean("sensorService");
      switchService = (SwitchService) SpringTestContext.getInstance().getBean("switchService");
      sliderService = (SliderService) SpringTestContext.getInstance().getBean("sliderService");
      
      deviceService = (DeviceService) SpringTestContext.getInstance().getBean("deviceService");
      controllerConfigService = (ControllerConfigService) SpringTestContext.getInstance().getBean("controllerConfigService");
      velocity = (VelocityEngine) SpringTestContext.getInstance().getBean("velocity");
      
      userService = (UserService) SpringTestContext.getInstance().getBean("userService");
      userService.createUserAccount("ResourceServiceTest", "ResourceServiceTest", "ResourceServiceTest");
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("ResourceServiceTest", "ResourceServiceTest"));
      
      configuration = (Configuration) SpringTestContext.getInstance().getBean("configuration");

      // This must execute in a transaction to get access to DB
      // As we're not using a JUnit test integrated with Spring,
      // there is no default transaction provided and we need to manage it ourself.
      transactionTemplate.execute(new TransactionCallbackWithoutResult() {
        @Override
        protected void doInTransactionWithoutResult(TransactionStatus arg0) {
          account = userService.getCurrentUser().getAccount();
          Hibernate.initialize(account.getDevices());
          Hibernate.initialize(account.getSensors());
          Hibernate.initialize(account.getSliders());
          Hibernate.initialize(account.getSwitches());
          Hibernate.initialize(account.getDeviceMacros());

          cache = new LocalFileCache(configuration, userService.getCurrentUser());

          cache.setDeviceService(deviceService);
          cache.setSwitchService(switchService);
          cache.setSliderService(sliderService);
          cache.setSensorService(sensorService);
          cache.setDeviceMacroService(deviceMacroService);
          cache.setDeviceCommandService(deviceCommandService);
          cache.setControllerConfigService(controllerConfigService);
          cache.setVelocity(velocity);
         
          // Make sure required folder structure exists
          cache.getPanelXmlFile().getParentFile().mkdirs();
        }
      });
   }
   
  /**
   * Tests that an empty configuration generates valid panel and controller XML files.
   * 
   * @throws DocumentException
   */
  @Test
  public void testEmptyConfiguration() throws DocumentException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> emptyPanels = new HashSet<Panel>();
      
        cache.replace(emptyPanels, IDUtil.nextID());
  
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
  
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("panels").size());
        Element panelsElement = topElement.element("panels");
        Assert.assertEquals(0, panelsElement.elements().size());
        Assert.assertEquals(1, topElement.elements("screens").size());
        Element screensElement = topElement.element("screens");
        Assert.assertEquals(0, screensElement.elements().size());
        Assert.assertEquals(1, topElement.elements("groups").size());
        Element groupsElement = topElement.element("groups");
        Assert.assertEquals(0, groupsElement.elements().size());
  
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals(0, componentsElement.elements().size());
        
        status.setRollbackOnly();
      }
    });
  }
   
  @Test
  public void testEmptyScreen() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
        Set<Panel> panels = new HashSet<Panel>();
        
        /*---------------group-------------------*/
        Group group = new Group();
        group.setOid(IDUtil.nextID());
        group.setName("group1");
        group.setScreenRefs(screenRefs);    
        groupRefs.add(new GroupRef(group));
        
        Panel panel = new Panel();
        panel.setOid(IDUtil.nextID());
        panel.setGroupRefs(groupRefs);
        panel.setGroupRefs(groupRefs);
        panel.setName("panel1");
        panels.add(panel);
        
        Screen screen = new Screen();
        screen.setOid(IDUtil.nextID());
        screen.setName("EmptyScreen");
         
        ScreenPair screenPair1 = new ScreenPair();
        screenPair1.setOid(IDUtil.nextID());
        screenPair1.setPortraitScreen(screen);      
        screenRefs.add(new ScreenPairRef(screenPair1));
     
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
  
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, panel);
        assertPanelHasOneGroupChild(panelElement, group);
        
        Element groupElement = assertOneGroup(topElement, group);
        assertGroupHasOneScreenChild(groupElement, screen);
        
        Element screenElement  = assertOneScreen(topElement, screen);
        Assert.assertEquals("Expecting no child for screen element", 0, screenElement.elements().size());
  
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals(0, componentsElement.elements().size());
        
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithBackgroundImage() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        Screen screen = new Screen();
        screen.setOid(IDUtil.nextID());
        screen.setName("Screen");
     
        Background background = screen.getBackground();
        background.setImageSource(new ImageSource("Image"));
        background.setAbsolute(false);
        background.setFillScreen(false);
        background.setRelatedType(RelativeType.TOP_RIGHT);
     
        Panel panel = new Panel();
        panel.setOid(IDUtil.nextID());
        panel.setName("Panel");
       
        Group group = new Group();
        group.setName("Group");
        group.setOid(IDUtil.nextID());
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen);
       
        group.addScreenRef(new ScreenPairRef(screenPair));
       
        panel.addGroupRef(new GroupRef(group));
        panels.add(panel);
     
        cache.replace(panels, IDUtil.nextID());
  
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
  
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, panel);
        assertPanelHasOneGroupChild(panelElement, group);
        
        Element groupElement = assertOneGroup(topElement, group);
        assertGroupHasOneScreenChild(groupElement, screen);
        
        Element screenElement  = assertOneScreen(topElement, screen);
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 background child for screen element", 1, screenElement.elements("background").size());
        
        Element backgroundElement = screenElement.element("background");
  
        Assert.assertNull("Not expecting a fillScreen attribute on screen element", screenElement.attribute("fillScreen"));
        Assert.assertNull("Not expecting a absolute attribute on screen element", screenElement.attribute("absolute"));
        assertAttribute(backgroundElement, "relative", "TOP_RIGHT");

        Assert.assertEquals("Expecting 1 child for background element", 1, backgroundElement.elements().size());
        Assert.assertEquals("Expecting 1 image child for background element", 1, backgroundElement.elements("image").size());
        
        Element imageElement = backgroundElement.element("image");
        assertAttribute(imageElement, "src", "Image");
  
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals(0, componentsElement.elements().size());
        
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testAbsoluteWidgets() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        Sensor sensor = new Sensor(SensorType.SWITCH);
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        DeviceCommand onCommand = new DeviceCommand();
        onCommand.setProtocol(protocol);
        onCommand.setName("onCommand");
        
        onCommand.setDevice(dev);
        dev.getDeviceCommands().add(onCommand);
        
        onCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(onCommand);
        
        DeviceCommand offCommand = new DeviceCommand();
        offCommand.setProtocol(protocol);
        offCommand.setName("offCommand");
        
        offCommand.setDevice(dev);
        dev.getDeviceCommands().add(offCommand);
        
        offCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(offCommand);
    
        Switch buildingSwitch = new Switch(onCommand, offCommand, sensor);
        buildingSwitch.setOid(IDUtil.nextID());
        buildingSwitch.setAccount(account);    
        account.getSwitches().add(buildingSwitch);
        buildingSwitch.setDevice(dev);
        dev.getSwitchs().add(buildingSwitch);
        switchService.save(buildingSwitch);
        
        Slider buildingSlider = new Slider("Slider", onCommand, sensor);
        buildingSlider.setOid(IDUtil.nextID());
        buildingSlider.setAccount(account);    
        account.getSliders().add(buildingSlider);
        buildingSlider.setDevice(dev);
        dev.getSliders().add(buildingSlider);
        sliderService.save(buildingSlider);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);

        UIButton button = new UIButton(IDUtil.nextID());
        button.setName("Button 1");
    
        Absolute buttonAbsolute = createAbsolute(10,  button);
        screen1.addAbsolute(buttonAbsolute);
        
        UISwitch aSwitch = new UISwitch(IDUtil.nextID());
        aSwitch.setOnImage(new ImageSource("On image"));
        aSwitch.setOffImage(new ImageSource("Off image"));
        aSwitch.setSwitchDTO(buildingSwitch.getSwitchWithInfoDTO());
        
        Absolute switchAbsolute = createAbsolute(20,  aSwitch);
        screen1.addAbsolute(switchAbsolute);
        
        UISlider slider = new UISlider(IDUtil.nextID());
        slider.setVertical(true);
        slider.setSliderDTO(buildingSlider.getSliderWithInfoDTO());
        
        Absolute sliderAbsolute = createAbsolute(30, slider);
        screen1.addAbsolute(sliderAbsolute);
        
        UILabel label = new UILabel(IDUtil.nextID());
        label.setText("Label");
        
        Absolute labelAbsolute = createAbsolute(40, label);
        screen1.addAbsolute(labelAbsolute);
        
        UIImage image = new UIImage(IDUtil.nextID());
        image.setImageSource(new ImageSource("Image"));
        
        Absolute imageAbsolute = createAbsolute(50, image);
        screen1.addAbsolute(imageAbsolute);
        
        UIWebView webView = new UIWebView(IDUtil.nextID());
        webView.setURL("http://www.openremote.org");
        
        Absolute webAbsolute = createAbsolute(60, webView);
        screen1.addAbsolute(webAbsolute);
        
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOid(IDUtil.nextID());
        
        Absolute colorPickerAbsolute = createAbsolute(70, colorPicker);
        screen1.addAbsolute(colorPickerAbsolute);
        
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
         
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
        
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
        
        Element screenElement  = assertOneScreen(topElement, screen1);
        Assert.assertEquals("Expecting 7 children for screen element", 7, screenElement.elements().size());
        Assert.assertEquals("Expecting 7 absolute children for screen element", 7, screenElement.elements("absolute").size());

        for (Element absoluteElement : (Collection<Element>)screenElement.elements("absolute")) {
          String childName = assertElementWithOneChild(absoluteElement);
          if ("button".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "button", "10", "11", "12", "13");
          } else if ("switch".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "switch", "20", "21", "22", "23");
          } else if ("slider".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "slider", "30", "31", "32", "33");
          } else if ("label".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "label", "40", "41", "42", "43");
          } else if ("image".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "image", "50", "51", "52", "53");
          } else if ("web".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "web", "60", "61", "62", "63");
          } else if ("colorpicker".equals(childName)) {
            assertAbsoluteElementChild(absoluteElement, "colorpicker", "70", "71", "72", "73");
          } else {
            Assert.fail("Unknown child element for absolute");
          }
        }
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 6 children for components element", 6, componentsElement.elements().size());
        
        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSwitches().remove(buildingSwitch);
        account.getSliders().remove(buildingSlider);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testInGridWidgets() {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        Sensor sensor = new Sensor(SensorType.SWITCH);
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        DeviceCommand onCommand = new DeviceCommand();
        onCommand.setProtocol(protocol);
        onCommand.setName("onCommand");
        
        onCommand.setDevice(dev);
        dev.getDeviceCommands().add(onCommand);
        
        onCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(onCommand);
        
        DeviceCommand offCommand = new DeviceCommand();
        offCommand.setProtocol(protocol);
        offCommand.setName("offCommand");
        
        offCommand.setDevice(dev);
        dev.getDeviceCommands().add(offCommand);
        
        offCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(offCommand);
    
        Switch buildingSwitch = new Switch(onCommand, offCommand, sensor);
        buildingSwitch.setOid(IDUtil.nextID());
        buildingSwitch.setAccount(account);    
        account.getSwitches().add(buildingSwitch);
        buildingSwitch.setDevice(dev);
        dev.getSwitchs().add(buildingSwitch);
        switchService.save(buildingSwitch);
        
        Slider buildingSlider = new Slider("Slider", onCommand, sensor);
        buildingSlider.setOid(IDUtil.nextID());
        buildingSlider.setAccount(account);    
        account.getSliders().add(buildingSlider);
        buildingSlider.setDevice(dev);
        dev.getSliders().add(buildingSlider);
        sliderService.save(buildingSlider);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);

        UIGrid grid = new UIGrid(10, 11, 12, 13, 4, 2);
        screen1.addGrid(grid);
        
        UIButton button = new UIButton(IDUtil.nextID());
        button.setName("Button 1");
    
        Cell buttonCell = createCell(0,  0, button);
        grid.addCell(buttonCell);
        
        UISwitch aSwitch = new UISwitch(IDUtil.nextID());
        aSwitch.setOnImage(new ImageSource("On image"));
        aSwitch.setOffImage(new ImageSource("Off image"));
        aSwitch.setSwitchDTO(buildingSwitch.getSwitchWithInfoDTO());

        Cell switchCell = createCell(1, 0, aSwitch);
        grid.addCell(switchCell);
        
        UISlider slider = new UISlider(IDUtil.nextID());
        slider.setVertical(true);
        slider.setSliderDTO(buildingSlider.getSliderWithInfoDTO());
        
        Cell sliderCell = createCell(2, 0, slider);
        grid.addCell(sliderCell);
        
        UILabel label = new UILabel(IDUtil.nextID());
        label.setText("Label");
        
        Cell labelCell = createCell(3, 0, label);
        grid.addCell(labelCell);
        
        UIImage image = new UIImage(IDUtil.nextID());
        image.setImageSource(new ImageSource("Image"));
        
        Cell imageCell = createCell(0, 1, image);
        grid.addCell(imageCell);
        
        UIWebView webView = new UIWebView(IDUtil.nextID());
        webView.setURL("http://www.openremote.org");

        Cell webCell = createCell(1, 1, webView);
        grid.addCell(webCell);
        
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOid(IDUtil.nextID());
        
        Cell colorPickerCell = createCell(2, 1, colorPicker);
        grid.addCell(colorPickerCell);
        
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
         
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
        
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
        
        Element screenElement  = assertOneScreen(topElement, screen1);
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 grid children for screen element", 1, screenElement.elements("grid").size());
        
        Element gridElement = screenElement.element("grid");
        assertAttribute(gridElement, "left", "10");
        assertAttribute(gridElement, "top", "11");
        assertAttribute(gridElement, "width", "12");
        assertAttribute(gridElement, "height", "13");
        assertAttribute(gridElement, "rows", "4");
        assertAttribute(gridElement, "cols", "2");
        
        Assert.assertEquals("Expecting 7 children for grid element", 7, gridElement.elements().size());
        Assert.assertEquals("Expecting 7 cell children for grid element", 7, gridElement.elements("cell").size());

        for (Element cellElement : (Collection<Element>)gridElement.elements("cell")) {
          String childName = assertElementWithOneChild(cellElement);
          if ("button".equals(childName)) {
            assertCellElementChild(cellElement, "button", "0", "0");
          } else if ("switch".equals(childName)) {
            assertCellElementChild(cellElement, "switch", "1", "0");
          } else if ("slider".equals(childName)) {
            assertCellElementChild(cellElement, "slider", "2", "0");
          } else if ("label".equals(childName)) {
            assertCellElementChild(cellElement, "label", "3", "0");
          } else if ("image".equals(childName)) {
            assertCellElementChild(cellElement, "image", "0", "1");
          } else if ("web".equals(childName)) {
            assertCellElementChild(cellElement, "web", "1", "1");
          } else if ("colorpicker".equals(childName)) {
            assertCellElementChild(cellElement, "colorpicker", "2", "1");
          } else {
            Assert.fail("Unknown child element for absolute");
          }
        }
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 6 children for components element", 6, componentsElement.elements().size());
        
        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSwitches().remove(buildingSwitch);
        account.getSliders().remove(buildingSlider);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }

  /**
   * Tests generated panel and controller.xml for a configuration 1 panel (no screens),
   * having a panel level tab bar with 1 item doing a navigation to a specific screen.
   * 
   * The group and screen being navigated to do not exist in the configuration,
   * but this is never validated by the schema, so generated XML is still valid.
   * 
   * @throws DocumentException
   */
  @Test
  public void testPanelTabbarWithNavigateToGroupAndScreen() throws DocumentException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        Navigate nav = new Navigate();
        nav.setOid(IDUtil.nextID());
        nav.setToGroup(1L);
        nav.setToScreen(2L);
        UITabbarItem item = new UITabbarItem();
        item.setNavigate(nav);
        item.setName("navigate name");
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel has a navigate");
        List<UITabbarItem> items = new ArrayList<UITabbarItem>();
        items.add(item);
        UITabbar tabbar = new UITabbar();
        tabbar.setTabbarItems(items);
        p.setTabbar(tabbar);
        panels.add(p);
  
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
    
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        Element panelElement = assertOnePanel(topElement, p);
        Assert.assertEquals(1, panelElement.elements().size());
        Assert.assertEquals(1, panelElement.elements("tabbar").size());
        Element tabbarElement = panelElement.element("tabbar");
        Assert.assertEquals(1, tabbarElement.elements().size());
        Assert.assertEquals(1, tabbarElement.elements("item").size());
        Element itemElement = tabbarElement.element("item");
        Assert.assertEquals(item.getName(), itemElement.attribute("name").getText());
        Assert.assertEquals(1, itemElement.elements().size());
        Assert.assertEquals(1, itemElement.elements("navigate").size());
        Element navigateElement = itemElement.element("navigate");
        Assert.assertEquals(Long.toString(nav.getToGroup()), navigateElement.attribute("toGroup").getText());
        Assert.assertEquals(Long.toString(nav.getToScreen()), navigateElement.attribute("toScreen").getText());
  
        Assert.assertEquals(1, topElement.elements("screens").size());
        Element screensElement = topElement.element("screens");
        Assert.assertEquals(0, screensElement.elements().size());
        Assert.assertEquals(1, topElement.elements("groups").size());
        Element groupsElement = topElement.element("groups");
        Assert.assertEquals(0, groupsElement.elements().size());
  
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals(0, componentsElement.elements().size());
        
        status.setRollbackOnly();
      }
    });
  }
  
  /**
   * Tests generated panel and controller.xml for a configuration 1 panel (no screens),
   * having a panel level tab bar with 1 item doing a navigation using a logical action.
   * Tab bar item is also associated an image.
   * 
   * The group and screen being navigated to do not exist in the configuration,
   * but this is never validated by the schema, so generated XML is still valid.
   * 
   * @throws DocumentException
   */
  @Test
  public void testPanelTabbarWithLogicalNavigateAndImage() throws DocumentException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        Navigate nav = new Navigate();
        nav.setOid(IDUtil.nextID());
        nav.setToLogical(ToLogicalType.login);
        ImageSource imageSource = new ImageSource("Image");
        UITabbarItem item = new UITabbarItem();
        item.setNavigate(nav);
        item.setImage(imageSource);
        item.setName("navigate name");
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel has a navigate");
        List<UITabbarItem> items = new ArrayList<UITabbarItem>();
        items.add(item);
        UITabbar tabbar = new UITabbar();
        tabbar.setTabbarItems(items);
        p.setTabbar(tabbar);
        panels.add(p);
  
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
    
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        Element panelElement = assertOnePanel(topElement, p);
        Assert.assertEquals(1, panelElement.elements().size());
        Assert.assertEquals(1, panelElement.elements("tabbar").size());
        Element tabbarElement = panelElement.element("tabbar");
        Assert.assertEquals(1, tabbarElement.elements().size());
        Assert.assertEquals(1, tabbarElement.elements("item").size());
        Element itemElement = tabbarElement.element("item");
        assertAttribute(itemElement, "name", item.getName());
        Assert.assertEquals("Expecting 2 children on item element", 2, itemElement.elements().size());
        Assert.assertEquals("Expecting 1 navigate child on item element", 1, itemElement.elements("navigate").size());
        Element navigateElement = itemElement.element("navigate");
        assertAttribute(navigateElement,  "to",  "login");
        Assert.assertNull("Not expecting a toGroup attribute on navigate", navigateElement.attribute("toGroup"));
        Assert.assertNull("Not expecting a toScreen attribute on navigate", navigateElement.attribute("toScreen"));
        
        Assert.assertEquals("Expecting 1 image child on item element", 1, itemElement.elements("image").size());
        Element imageElement = itemElement.element("image");
        assertAttribute(imageElement, "src", "Image");
        
        Assert.assertEquals(1, topElement.elements("screens").size());
        Element screensElement = topElement.element("screens");
        Assert.assertEquals(0, screensElement.elements().size());
        Assert.assertEquals(1, topElement.elements("groups").size());
        Element groupsElement = topElement.element("groups");
        Assert.assertEquals(0, groupsElement.elements().size());
  
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals(0, componentsElement.elements().size());
        
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testGroupTabbarWithLogicalNavigateAndImage() throws DocumentException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
         
        Navigate nav = new Navigate();
        nav.setOid(IDUtil.nextID());
        nav.setToLogical(ToLogicalType.login);
        ImageSource imageSource = new ImageSource("Image");
        UITabbarItem item = new UITabbarItem();
        item.setNavigate(nav);
        item.setImage(imageSource);
        item.setName("navigate name");
         
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel has a navigate");

        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
         
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);

        List<UITabbarItem> items = new ArrayList<UITabbarItem>();
        items.add(item);
        UITabbar tabbar = new UITabbar();
        tabbar.setTabbarItems(items);
        group1.setTabbar(tabbar);

        panels.add(p);
        
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
         
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
        
        Element groupElement = assertOneGroup(topElement, group1);
        
        Assert.assertEquals("Expecting 2 children for group element", 2, groupElement.elements().size());
        Assert.assertEquals("Expecting 1 include element", 1, groupElement.elements("include").size());
        Element includeElement = groupElement.element("include");
        Assert.assertEquals("screen", includeElement.attribute("type").getText());
        Assert.assertEquals(Long.toString(screen1.getOid()), includeElement.attribute("ref").getText());
        
        Assert.assertEquals("Expecting 1 tabbar element", 1, groupElement.elements("tabbar").size());
        Element tabbarElement = groupElement.element("tabbar");
        Assert.assertEquals(1, tabbarElement.elements().size());
        Assert.assertEquals(1, tabbarElement.elements("item").size());
        Element itemElement = tabbarElement.element("item");
        assertAttribute(itemElement, "name", item.getName());
        Assert.assertEquals("Expecting 2 children on item element", 2, itemElement.elements().size());
        Assert.assertEquals("Expecting 1 navigate child on item element", 1, itemElement.elements("navigate").size());
        Element navigateElement = itemElement.element("navigate");
        assertAttribute(navigateElement,  "to",  "login");
        Assert.assertNull("Not expecting a toGroup attribute on navigate", navigateElement.attribute("toGroup"));
        Assert.assertNull("Not expecting a toScreen attribute on navigate", navigateElement.attribute("toScreen"));
        
        Assert.assertEquals("Expecting 1 image child on item element", 1, itemElement.elements("image").size());
        Element imageElement = itemElement.element("image");
        assertAttribute(imageElement, "src", "Image");
        
        Element screenElement  = assertOneScreen(topElement, screen1);
        Assert.assertEquals("Expecting no child for screen element", 0, screenElement.elements().size());
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals(1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals(0, componentsElement.elements().size());
        
        status.setRollbackOnly();
      }
    });
  }
  
  /**
   * Tests generated panel and controller.xml for a configuration with
   * 1 panel including 1 screen in 1 group.
   * One gesture with a navigation is associated with the screen.
   * 
   * The group and screen being navigated to do not exist in the configuration,
   * but this is never validated by the schema, so generated XML is still valid.
   * 
   * @throws DocumentException
   */
  @Test
  public void testScreenHasGesture() throws DocumentException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panelWithJustOneNavigate = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
         
        List<Gesture> gestures = new ArrayList<Gesture>();
         
        Navigate nav = new Navigate();
        nav.setOid(IDUtil.nextID());
        nav.setToGroup(1L);
        nav.setToScreen(2L);
        Gesture gesture = new Gesture();
        gesture.setNavigate(nav);
        gesture.setOid(IDUtil.nextID());
        gesture.setType(GestureType.swipe_bottom_to_top);
         
        gestures.add(gesture);
         
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel has a navigate");
         
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        screen1.setGestures(gestures);
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
         
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
         
        panelWithJustOneNavigate.add(p);
        
        cache.replace(panelWithJustOneNavigate, IDUtil.nextID());
         
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
         
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
        
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
        
        Element screenElement  = assertOneScreen(topElement, screen1);
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 gesture element", 1, screenElement.elements("gesture").size());
        Element gestureElement = screenElement.element("gesture");
        Assert.assertEquals(Long.toString(gesture.getOid()), gestureElement.attribute("id").getText());
        Assert.assertEquals(gesture.getType().toString(), gestureElement.attribute("type").getText());
        Assert.assertEquals("Expecting 1 child for gesture element", 1, gestureElement.elements().size());
        Assert.assertEquals("Expexting 1 navigate element", 1, gestureElement.elements("navigate").size());
        Element navigateElement = gestureElement.element("navigate");
        Assert.assertEquals(Long.toString(nav.getToGroup()), navigateElement.attribute("toGroup").getText());
        Assert.assertEquals(Long.toString(nav.getToScreen()), navigateElement.attribute("toScreen").getText());
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size()); // Gesture is included in component
        
        status.setRollbackOnly();
      }
    });
  }

  /**
   * Tests generated panel and controller.xml for a configuration with
   * 1 panel including 1 screen in 1 group.
   * One gesture with a device command is associated with the screen.
   * 
   * @throws DocumentException
   */
  @Test
  public void testScreenHasGestureWithCommand() throws DocumentException {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);
        deviceService.saveDevice(dev);
        
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand cmd = new DeviceCommand();
        cmd.setProtocol(protocol);
        cmd.setName("testLirc");
        
        cmd.setDevice(dev);
        dev.getDeviceCommands().add(cmd);
        
        cmd.setOid(IDUtil.nextID());
        deviceCommandService.save(cmd);
        
        Set<Panel> panelWithJustOneNavigate = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
         
        List<Gesture> gestures = new ArrayList<Gesture>();
         
        Gesture gesture = new Gesture();
        gesture.setUiCommandDTO(cmd.getDeviceCommandDTO());
        gesture.setOid(IDUtil.nextID());
        gesture.setType(GestureType.swipe_bottom_to_top);
         
        gestures.add(gesture);
         
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel has a navigate");
         
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        screen1.setGestures(gestures);
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
         
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
         
        panelWithJustOneNavigate.add(p);
        
        cache.replace(panelWithJustOneNavigate, IDUtil.nextID());
         
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
         
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
        
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
        
        Element screenElement  = assertOneScreen(topElement, screen1);
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 gesture element", 1, screenElement.elements("gesture").size());
        Element gestureElement = screenElement.element("gesture");
        assertAttribute(gestureElement, "id", Long.toString(gesture.getOid()));
        assertAttribute(gestureElement, "type", gesture.getType().toString());
        assertAttribute(gestureElement, "hasControlCommand", "true");
        Assert.assertEquals("Expecting no child for gesture element", 0, gestureElement.elements().size());
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Exepcting 1 gesture child for components element", 1, componentsElement.elements("gesture").size());
        gestureElement = componentsElement.element("gesture");
        assertAttribute(gestureElement, "id", Long.toString(gesture.getOid()));
        Assert.assertEquals("Expecting 1 child for gesture element", 1, gestureElement.elements().size());
        Assert.assertEquals("Expecting 1 include element", 1, gestureElement.elements("include").size());
        Element includeElement = gestureElement.element("include");
        assertAttribute(includeElement, "type", "command");
        Assert.assertNotNull("Expecting include to have a ref attribute", includeElement.attribute("ref"));
        
        // Reference is to the command, id is not the id in the database, but should cross reference a command element defined below
        String referencedCommandId = includeElement.attribute("ref").getText();
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command element as child of components", 1, commandsElement.elements("command").size());
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        status.setRollbackOnly();
      }
    });
  }

  @Test
  public void testOneScreenWithOneButtonHavingOneDeviceCommand() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);
        deviceService.saveDevice(dev);
        
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand cmd = new DeviceCommand();
        cmd.setProtocol(protocol);
        cmd.setName("testLirc");
        
        cmd.setDevice(dev);
        dev.getDeviceCommands().add(cmd);
        
        cmd.setOid(IDUtil.nextID());
        deviceCommandService.save(cmd);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        UIButton button = new UIButton(IDUtil.nextID());
        button.setName("Button 1");
        button.setUiCommandDTO(cmd.getDeviceCommandDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(button);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
    
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 button element", 1, absoluteElement.elements("button").size());
        Element buttonElement = absoluteElement.element("button");
        assertAttribute(buttonElement, "id", Long.toString(button.getOid()));
        assertAttribute(buttonElement, "hasControlCommand", "true");
        assertAttribute(buttonElement, "name", button.getName());
       
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Expecting 1 button element as child of components", 1, componentsElement.elements("button").size());
        buttonElement = componentsElement.element("button");
        assertAttribute(buttonElement, "id", Long.toString(button.getOid()));
        Assert.assertEquals("Expecting 1 child for button element", 1, buttonElement.elements().size());
        Assert.assertEquals("Expecting 1 include element", 1, buttonElement.elements("include").size());
        Element includeElement = buttonElement.element("include");
        assertAttribute(includeElement, "type", "command");
        Assert.assertNotNull("Expecting include to have a ref attribute", includeElement.attribute("ref"));
        
        // Reference is to the command, id is not the id in the database, but should cross reference a command element defined below
        String referencedCommandId = includeElement.attribute("ref").getText();
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command element as child of components", 1, commandsElement.elements("command").size());
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneButtonHavingOneMacro() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);
        deviceService.saveDevice(dev);
        
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand command1 = new DeviceCommand();
        command1.setProtocol(protocol);
        command1.setName("Command one");
        
        command1.setDevice(dev);
        dev.getDeviceCommands().add(command1);
        
        command1.setOid(IDUtil.nextID());
        deviceCommandService.save(command1);

        DeviceCommand command2 = new DeviceCommand();
        command2.setProtocol(protocol);
        command2.setName("Command two");
        
        command2.setDevice(dev);
        dev.getDeviceCommands().add(command2);
        
        command2.setOid(IDUtil.nextID());
        deviceCommandService.save(command2);
        
        DeviceMacro deviceMacro = new DeviceMacro();
        deviceMacro.setName("Macro");
        deviceMacro.setOid(IDUtil.nextID());
        
        deviceMacro.setAccount(account);
        account.getDeviceMacros().add(deviceMacro);
        
        DeviceMacroItem item1 = new DeviceCommandRef(command1);
        item1.setOid(IDUtil.nextID());
        item1.setParentDeviceMacro(deviceMacro);
        
        DeviceMacroItem item2 = new CommandDelay("1000");
        item2.setOid(IDUtil.nextID());
        item1.setParentDeviceMacro(deviceMacro);

        DeviceMacroItem item3 = new DeviceCommandRef(command2);
        item3.setOid(IDUtil.nextID());
        item3.setParentDeviceMacro(deviceMacro);
        
        List<DeviceMacroItem> deviceMacroItems = new ArrayList<DeviceMacroItem>();
        deviceMacroItems.add(item1);
        deviceMacroItems.add(item2);
        deviceMacroItems.add(item3);
        deviceMacro.setDeviceMacroItems(deviceMacroItems);
        
        deviceMacroService.saveDeviceMacro(deviceMacro);

        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        UIButton button = new UIButton(IDUtil.nextID());
        button.setName("Button 1");
        button.setUiCommandDTO(deviceMacro.getMacroDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(button);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
    
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 button element", 1, absoluteElement.elements("button").size());
        Element buttonElement = absoluteElement.element("button");
        assertAttribute(buttonElement, "id", Long.toString(button.getOid()));
        assertAttribute(buttonElement, "hasControlCommand", "true");
        assertAttribute(buttonElement, "name", button.getName());
       
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Expecting 1 button element as child of components", 1, componentsElement.elements("button").size());
        buttonElement = componentsElement.element("button");
        assertAttribute(buttonElement, "id", Long.toString(button.getOid()));
        Assert.assertEquals("Expecting 3 children for button element", 3, buttonElement.elements().size());
        Assert.assertEquals("Expecting 2 include elements", 2, buttonElement.elements("include").size());
        Assert.assertEquals("Expecting 1 delay element", 1, buttonElement.elements("delay").size());
        
        Element element = (Element) buttonElement.elements().get(0);
        Assert.assertEquals("Expecting first child to be include", "include", element.getName());
        assertAttribute(element, "type", "command");
        Assert.assertNotNull("Expecting include to have a ref attribute", element.attribute("ref"));
        String referencedCommandId1 = element.attribute("ref").getText();

        element = (Element)buttonElement.elements().get(1);
        Assert.assertEquals("Expecting second child to be delay", "delay", element.getName());
        Assert.assertEquals("Expecting delay value to be 1000", "1000", element.getText());

        element = (Element) buttonElement.elements().get(2);
        Assert.assertEquals("Expecting third child to be include", "include", element.getName());
        assertAttribute(element, "type", "command");
        Assert.assertNotNull("Expecting include to have a ref attribute", element.attribute("ref"));
        String referencedCommandId2 = element.attribute("ref").getText();

        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");

        Assert.assertEquals("Expecting 2 children for commands element", 2, commandsElement.elements().size());
        Assert.assertEquals("Expecting 2 command elements as child of components", 2, commandsElement.elements("command").size());
        
        for (Element commandElement : ((List <Element>)commandsElement.elements("command"))) {
          Assert.assertNotNull("Expecting command element to have id attribute", commandElement.attribute("id"));
          assertAttribute(commandElement, "protocol", "ir");
          Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
          Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
          Element propertyElement = commandElement.element("property");
          assertAttribute(propertyElement, "name", "name");
          Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));
    
          if (commandElement.attribute("id").getText().equals(referencedCommandId1)) {
            Assert.assertEquals("Expecting property value to be onCommand", "Command one", propertyElement.attribute("value").getText());        
          } else if (commandElement.attribute("id").getText().equals(referencedCommandId2)) {
            Assert.assertEquals("Expecting property value to be onCommand", "Command two", propertyElement.attribute("value").getText());        
          } else {
            Assert.fail("Un-expected command found, id: " + commandElement.attribute("id").getText());
          }
        }

        account.getDeviceMacros().remove(deviceMacro);
        status.setRollbackOnly();
      }
    });
  }  
  
  @Test
  public void testOneScreenWithOneSwitch() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        Sensor sensor = new Sensor(SensorType.SWITCH);
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        DeviceCommand onCommand = new DeviceCommand();
        onCommand.setProtocol(protocol);
        onCommand.setName("onCommand");
        
        onCommand.setDevice(dev);
        dev.getDeviceCommands().add(onCommand);
        
        onCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(onCommand);
        
        DeviceCommand offCommand = new DeviceCommand();
        offCommand.setProtocol(protocol);
        offCommand.setName("offCommand");
        
        offCommand.setDevice(dev);
        dev.getDeviceCommands().add(offCommand);
        
        offCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(offCommand);
    
        Switch buildingSwitch = new Switch(onCommand, offCommand, sensor);
        buildingSwitch.setOid(IDUtil.nextID());
        buildingSwitch.setAccount(account);    
        account.getSwitches().add(buildingSwitch);
        buildingSwitch.setDevice(dev);
        dev.getSwitchs().add(buildingSwitch);
        switchService.save(buildingSwitch);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        ImageSource onImageSource = new ImageSource("On image");
        ImageSource offImageSource = new ImageSource("Off image");
        
        UISwitch aSwitch = new UISwitch(IDUtil.nextID());
        aSwitch.setOnImage(onImageSource);
        aSwitch.setOffImage(offImageSource);
        aSwitch.setSwitchDTO(buildingSwitch.getSwitchWithInfoDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(aSwitch);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
    
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 switch element", 1, absoluteElement.elements("switch").size());
        Element switchElement = absoluteElement.element("switch");
        Assert.assertEquals(Long.toString(aSwitch.getOid()), switchElement.attribute("id").getText());
        
        Assert.assertEquals("Expecting 1 child for switch element", 1, switchElement.elements().size());
        Assert.assertEquals("Expecting 1 link element", 1, switchElement.elements("link").size());
        Element linkElement = switchElement.element("link");
        
        String referencedSensorId = assertLinkElement(linkElement, "sensor");
        
        Assert.assertEquals("Expecting 2 children for link element", 2, linkElement.elements().size());
        Assert.assertEquals("Expected link element children to be state elements", 2, linkElement.elements("state").size());
        Element onStateElement = (Element) linkElement.elements("state").get(0);
        assertAttribute(onStateElement, "name", "on");
        assertAttribute(onStateElement, "value", "On image");
        Element offStateElement = (Element) linkElement.elements("state").get(1);
        assertAttribute(offStateElement, "name", "off");
        assertAttribute(offStateElement, "value", "Off image");
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals(1, componentsElement.elements("switch").size());
        switchElement = componentsElement.element("switch");
        Assert.assertEquals(Long.toString(aSwitch.getOid()), switchElement.attribute("id").getText());
        Assert.assertEquals("Expecting 3 children for switch element", 3, switchElement.elements().size());
        Assert.assertEquals("Expecting 1 on child for switch element", 1, switchElement.elements("on").size());
        Assert.assertEquals("Expecting 1 off child for switch element", 1, switchElement.elements("off").size());
        Assert.assertEquals("Expecting 1 include child for switch element", 1, switchElement.elements("include").size());
        Element onElement = switchElement.element("on");
        Assert.assertEquals("Expecting 1 child for on element",  1, onElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for on element", 1, onElement.elements("include").size());
        Element includeElement = onElement.element("include");
        String referencedOnCommandId = assertIncludeElement(includeElement, "command");
        
        Element offElement = switchElement.element("off");
        Assert.assertEquals("Expecting 1 child for off element",  1, offElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for off element", 1, offElement.elements("include").size());
        includeElement = offElement.element("include");
        String referencedOffCommandId = assertIncludeElement(includeElement, "command");
    
        includeElement = switchElement.element("include");
        Assert.assertEquals("Expecting include element to reference appropriate sensor", referencedSensorId, assertIncludeElement(includeElement, "sensor"));
    
        Assert.assertEquals("Expecting 1 sensors element", 1, topElement.elements("sensors").size());
        Element sensorsElement = topElement.element("sensors");
        Assert.assertEquals("Expecting 1 child for sensors element", 1, sensorsElement.elements().size());
        Assert.assertEquals("Expecting 1 sensor child for sensors element", 1, sensorsElement.elements("sensor").size());
        Element sensorElement = sensorsElement.element("sensor");
        assertSensorElement(sensorElement, referencedSensorId, "switch", "Sensor");
    
        Assert.assertEquals("Expecting 3 children for sensor element", 3, sensorElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for sensorElement", 1, sensorElement.elements("include").size());
        Assert.assertEquals("Expecting 2 states children for sensorElement", 2, sensorElement.elements("state").size());
        includeElement = sensorElement.element("include");
        String referencedReadCommandId = assertIncludeElement(includeElement, "command");
        Element stateElement = (Element) sensorElement.elements("state").get(0);
        assertAttribute(stateElement, "name", "on");
        stateElement = (Element) sensorElement.elements("state").get(1);
        assertAttribute(stateElement, "name", "off");
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 3 children for commands element", 3, commandsElement.elements().size());
        Assert.assertEquals("Expecting 3 command children for commands element", 3, commandsElement.elements("command").size());
    
        for (Element commandElement : ((List <Element>)commandsElement.elements("command"))) {
          Assert.assertNotNull("Expecting command element to have id attribute", commandElement.attribute("id"));
          assertAttribute(commandElement, "protocol", "ir");
          Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
          Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
          Element propertyElement = commandElement.element("property");
          assertAttribute(propertyElement, "name", "name");
          Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));
    
          if (commandElement.attribute("id").getText().equals(referencedOnCommandId)) {
            Assert.assertEquals("Expecting property value to be onCommand", "onCommand", propertyElement.attribute("value").getText());        
          } else if (commandElement.attribute("id").getText().equals(referencedOffCommandId)) {
            Assert.assertEquals("Expecting property value to be onCommand", "offCommand", propertyElement.attribute("value").getText());        
          } else if (commandElement.attribute("id").getText().equals(referencedReadCommandId)) {
            Assert.assertEquals("Expecting property value to be onCommand", "readCommand", propertyElement.attribute("value").getText());        
          } else {
            Assert.fail("Un-expected command found, id: " + commandElement.attribute("id").getText());
          }
        }
    
        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSwitches().remove(buildingSwitch);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }

  @Test
  public void testOneScreenWithOneSlider() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        RangeSensor sensor = new RangeSensor(-2,  10);
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        DeviceCommand setCommand = new DeviceCommand();
        setCommand.setProtocol(protocol);
        setCommand.setName("setCommand");
        
        setCommand.setDevice(dev);
        dev.getDeviceCommands().add(setCommand);
        
        setCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(setCommand);
        
        Slider buildingSlider = new Slider("Slider", setCommand, sensor);
        buildingSlider.setOid(IDUtil.nextID());
        buildingSlider.setAccount(account);    
        account.getSliders().add(buildingSlider);
        buildingSlider.setDevice(dev);
        dev.getSliders().add(buildingSlider);
        sliderService.save(buildingSlider);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        ImageSource minImageSource = new ImageSource("Min image");
        ImageSource minTrackImageSource = new ImageSource("Min track image");
        ImageSource thumbImageSource = new ImageSource("Thumb image");
        ImageSource maxTrackImageSource = new ImageSource("Max track image");
        ImageSource maxImageSource = new ImageSource("Max image");
       
        UISlider slider = new UISlider(IDUtil.nextID());
        slider.setMinImage(minImageSource);
        slider.setMinTrackImage(minTrackImageSource);
        slider.setThumbImage(thumbImageSource);
        slider.setMaxTrackImage(maxTrackImageSource);
        slider.setMaxImage(maxImageSource);
        slider.setVertical(true);
        slider.setSliderDTO(buildingSlider.getSliderWithInfoDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(slider);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 slider element", 1, absoluteElement.elements("slider").size());
        Element sliderElement = absoluteElement.element("slider");
        assertAttribute(sliderElement, "id",Long.toString(slider.getOid()));
        assertAttribute(sliderElement, "thumbImage", "Thumb image");
        assertAttribute(sliderElement, "vertical", "true");
        if (sliderElement.attribute("passive") != null) {
          Assert.assertEquals("Expecting passive attribute, if present, to be false", "false", sliderElement.attribute("passive").getText());
        }
        
        Assert.assertEquals("Expecting 3 children for slider element", 3, sliderElement.elements().size());
        Assert.assertEquals("Expecting 1 link element", 1, sliderElement.elements("link").size());
        Element linkElement = sliderElement.element("link");        
        String referencedSensorId = assertLinkElement(linkElement, "sensor");
        Assert.assertEquals("Expecting no child for link element", 0, linkElement.elements().size());
        
        Assert.assertEquals("Expecting 1 min element", 1, sliderElement.elements("min").size());
        Element minElement = sliderElement.element("min");
        assertAttribute(minElement, "value", "-2");
        assertAttribute(minElement, "image", "Min image");
        assertAttribute(minElement, "trackImage", "Min track image");
        Assert.assertEquals("Expecting 1 max element", 1, sliderElement.elements("max").size());
        Element maxElement = sliderElement.element("max");
        assertAttribute(maxElement, "value", "10");
        assertAttribute(maxElement, "image", "Max image");
        assertAttribute(maxElement, "trackImage", "Max track image");

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals(1, componentsElement.elements("slider").size());
        sliderElement = componentsElement.element("slider");
        Assert.assertEquals(Long.toString(slider.getOid()), sliderElement.attribute("id").getText());
        Assert.assertEquals("Expecting 2 children for slider element", 2, sliderElement.elements().size());
        Assert.assertEquals("Expecting 1 setValue child for switch element", 1, sliderElement.elements("setValue").size());
        Assert.assertEquals("Expecting 1 include child for switch element", 1, sliderElement.elements("include").size());
        Element setValueElement = sliderElement.element("setValue");
        Assert.assertEquals("Expecting 1 child for setValue element",  1, setValueElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for setValue element", 1, setValueElement.elements("include").size());
        Element includeElement = setValueElement.element("include");
        String referencedSetCommandId = assertIncludeElement(includeElement, "command");
        
        includeElement = sliderElement.element("include");
        Assert.assertEquals("Expecting include element to reference appropriate sensor", referencedSensorId, assertIncludeElement(includeElement, "sensor"));
    
        Assert.assertEquals("Expecting 1 sensors element", 1, topElement.elements("sensors").size());
        Element sensorsElement = topElement.element("sensors");
        Assert.assertEquals("Expecting 1 child for sensors element", 1, sensorsElement.elements().size());
        Assert.assertEquals("Expecting 1 sensor child for sensors element", 1, sensorsElement.elements("sensor").size());
        Element sensorElement = sensorsElement.element("sensor");
        assertSensorElement(sensorElement, referencedSensorId, "range", "Sensor");
    
        Assert.assertEquals("Expecting 3 children for sensor element", 3, sensorElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for sensorElement", 1, sensorElement.elements("include").size());
        Assert.assertEquals("Expecting 1 min child for sensorElement", 1, sensorElement.elements("min").size());
        Assert.assertEquals("Expecting 1 max child for sensorElement", 1, sensorElement.elements("max").size());
        includeElement = sensorElement.element("include");
        String referencedReadCommandId = assertIncludeElement(includeElement, "command");
        minElement = sensorElement.element("min");
        assertAttribute(minElement, "value", "-2");
        maxElement = sensorElement.element("max");
        assertAttribute(maxElement, "value", "10");
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 2 children for commands element", 2, commandsElement.elements().size());
        Assert.assertEquals("Expecting 2 command children for commands element", 2, commandsElement.elements("command").size());
    
        for (Element commandElement : ((List <Element>)commandsElement.elements("command"))) {
          Assert.assertNotNull("Expecting command element to have id attribute", commandElement.attribute("id"));
          assertAttribute(commandElement, "protocol", "ir");
          Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
          Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
          Element propertyElement = commandElement.element("property");
          assertAttribute(propertyElement, "name", "name");
          Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));
    
          if (commandElement.attribute("id").getText().equals(referencedSetCommandId)) {
            Assert.assertEquals("Expecting property value to be setCommand", "setCommand", propertyElement.attribute("value").getText());        
          } else if (commandElement.attribute("id").getText().equals(referencedReadCommandId)) {
            Assert.assertEquals("Expecting property value to be onCommand", "readCommand", propertyElement.attribute("value").getText());        
          } else {
            Assert.fail("Un-expected command found, id: " + commandElement.attribute("id").getText());
          }
        }
    
        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSliders().remove(buildingSlider);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneLabel() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));

        UILabel label = new UILabel(IDUtil.nextID());
        label.setText("Label");
        label.setColor("ff0000");
        label.setFontSize(12);
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(label);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 label element", 1, absoluteElement.elements("label").size());
        Element labelElement = absoluteElement.element("label");
        assertAttribute(labelElement, "id", Long.toString(label.getOid()));
        assertAttribute(labelElement, "text", "Label");
        assertAttribute(labelElement, "fontSize", "12");
        assertAttribute(labelElement, "color", "#ff0000");       
        Assert.assertEquals("Expecting no child for label element", 0, labelElement.elements().size());

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals(1, componentsElement.elements("label").size());
        labelElement = componentsElement.element("label");
        assertAttribute(labelElement, "id", Long.toString(label.getOid()));
        Assert.assertEquals("Expecting no child for label element", 0, labelElement.elements().size());

        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithLabelWithSensor() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        CustomSensor sensor = new CustomSensor();
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.addState(new State("state name",  "state value"));
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));

        UILabel label = new UILabel(IDUtil.nextID());
        label.setText("Label with sensor");
        label.setColor("ff0000");
        label.setFontSize(12);
        label.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(label);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 label element", 1, absoluteElement.elements("label").size());
        Element labelElement = absoluteElement.element("label");
        assertAttribute(labelElement, "id", Long.toString(label.getOid()));
        assertAttribute(labelElement, "text" , "Label with sensor");
        assertAttribute(labelElement, "fontSize", "12");
        assertAttribute(labelElement, "color", "#ff0000");
        
        Assert.assertEquals("Expecting 1 child for label element", 1, labelElement.elements().size());
        Assert.assertEquals("Expecting 1 link child for label element", 1, labelElement.elements("link").size());
        Element linkElement = labelElement.element("link");
        String referencedSensorId = assertLinkElement(linkElement, "sensor");

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals(1, componentsElement.elements("label").size());
        labelElement = componentsElement.element("label");
        assertAttribute(labelElement, "id", Long.toString(label.getOid()));
        Assert.assertEquals("Expecting 1 child for label element", 1, labelElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for label element", 1, labelElement.elements("include").size());
        Element includeElement = labelElement.element("include");
        Assert.assertEquals("Expecting include element to reference appropriate sensor", referencedSensorId, assertIncludeElement(includeElement, "sensor"));

        Assert.assertEquals("Expecting 1 sensors element", 1, topElement.elements("sensors").size());
        Element sensorsElement = topElement.element("sensors");
        Assert.assertEquals("Expecting 1 child for sensors element", 1, sensorsElement.elements().size());
        Assert.assertEquals("Expecting 1 sensor child for sensors element", 1, sensorsElement.elements("sensor").size());
        Element sensorElement = sensorsElement.element("sensor");
        assertSensorElement(sensorElement, referencedSensorId, "custom", "Sensor");
    
        Assert.assertEquals("Expecting 2 children for sensor element", 2, sensorElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for sensorElement", 1, sensorElement.elements("include").size());
        Assert.assertEquals("Expecting 1 state child for sensorElement", 1, sensorElement.elements("state").size());
        includeElement = sensorElement.element("include");
        String referencedReadCommandId = assertIncludeElement(includeElement, "command");
        Element stateElement = sensorElement.element("state");
        assertAttribute(stateElement, "name", "state name");
        assertAttribute(stateElement, "value", "state value");
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command child for commands element", 1, commandsElement.elements("command").size());
    
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedReadCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneImage() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));

        ImageSource imageSource = new ImageSource("Image");
        UIImage image = new UIImage(IDUtil.nextID());
        image.setImageSource(imageSource);
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(image);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 image element", 1, absoluteElement.elements("image").size());
        Element imageElement = absoluteElement.element("image");
        assertAttribute(imageElement, "id", Long.toString(image.getOid()));
        assertAttribute(imageElement, "src", imageSource.getSrc());
        Assert.assertEquals("Expecting no child for image element", 0, imageElement.elements().size());

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Expecting 1 image child for components element", 1, componentsElement.elements("image").size());
        imageElement = componentsElement.element("image");
        assertAttribute(imageElement, "id", Long.toString(image.getOid()));
        Assert.assertEquals("Expecting no child for image element", 0, imageElement.elements().size());

        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneImageWithSensor() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        CustomSensor sensor = new CustomSensor();
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.addState(new State("state name",  "state value"));
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));

        ImageSource imageSource = new ImageSource("Image");
        UIImage image = new UIImage(IDUtil.nextID());
        image.setImageSource(imageSource);
        image.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(image);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 image element", 1, absoluteElement.elements("image").size());
        Element imageElement = absoluteElement.element("image");
        assertAttribute(imageElement, "id", Long.toString(image.getOid()));
        assertAttribute(imageElement, "src", imageSource.getSrc());

        Assert.assertEquals("Expecting 1 child for image element", 1, imageElement.elements().size());
        Assert.assertEquals("Expecting 1 link child for image element", 1, imageElement.elements("link").size());
        Element linkElement = imageElement.element("link");
        String referencedSensorId = assertLinkElement(linkElement, "sensor");

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Expecting 1 image child for components element", 1, componentsElement.elements("image").size());
        imageElement = componentsElement.element("image");
        assertAttribute(imageElement, "id", Long.toString(image.getOid()));
        Assert.assertEquals("Expecting 1 child for image element", 1, imageElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for image element", 1, imageElement.elements("include").size());
        Element includeElement = imageElement.element("include");
        Assert.assertEquals("Expecting include element to reference appropriate sensor", referencedSensorId, assertIncludeElement(includeElement, "sensor"));

        Assert.assertEquals("Expecting 1 sensors element", 1, topElement.elements("sensors").size());
        Element sensorsElement = topElement.element("sensors");
        Assert.assertEquals("Expecting 1 child for sensors element", 1, sensorsElement.elements().size());
        Assert.assertEquals("Expecting 1 sensor child for sensors element", 1, sensorsElement.elements("sensor").size());
        Element sensorElement = sensorsElement.element("sensor");
        assertSensorElement(sensorElement, referencedSensorId, "custom", "Sensor");
    
        Assert.assertEquals("Expecting 2 children for sensor element", 2, sensorElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for sensorElement", 1, sensorElement.elements("include").size());
        Assert.assertEquals("Expecting 1 state child for sensorElement", 1, sensorElement.elements("state").size());
        includeElement = sensorElement.element("include");
        String referencedReadCommandId = assertIncludeElement(includeElement, "command");
        Element stateElement = sensorElement.element("state");
        assertAttribute(stateElement, "name", "state name");
        assertAttribute(stateElement, "value", "state value");
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command child for commands element", 1, commandsElement.elements("command").size());
    
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedReadCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneImageWithSensorAndLabel() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        CustomSensor sensor = new CustomSensor();
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.addState(new State("state name",  "state value"));
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        UILabel label = new UILabel(IDUtil.nextID());
        label.setText("Label with sensor");
        label.setColor("ff0000");
        label.setFontSize(12);
        label.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());

        ImageSource imageSource = new ImageSource("Image");
        UIImage image = new UIImage(IDUtil.nextID());
        image.setImageSource(imageSource);
        image.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
        image.setLabel(label);
        
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(image);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());
        
        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 image element", 1, absoluteElement.elements("image").size());
        Element imageElement = absoluteElement.element("image");
        assertAttribute(imageElement, "id", Long.toString(image.getOid()));
        assertAttribute(imageElement, "src", imageSource.getSrc());

        Assert.assertEquals("Expecting 2 children for image element", 2, imageElement.elements().size());
        Assert.assertEquals("Expecting 1 link child for image element", 1, imageElement.elements("link").size());
        Element linkElement = imageElement.element("link");
        String referencedSensorId = assertLinkElement(linkElement, "sensor");
        Assert.assertEquals("Expecting 1 include child for image element", 1, imageElement.elements("include").size());
        Element includeElement = imageElement.element("include");
        assertIncludeElement(includeElement, "label");

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Expecting 1 image child for components element", 1, componentsElement.elements("image").size());
        imageElement = componentsElement.element("image");
        assertAttribute(imageElement, "id", Long.toString(image.getOid()));
        Assert.assertEquals("Expecting 1 child for image element", 1, imageElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for image element", 1, imageElement.elements("include").size());
        includeElement = imageElement.element("include");
        Assert.assertEquals("Expecting include element to reference appropriate sensor", referencedSensorId, assertIncludeElement(includeElement, "sensor"));

        Assert.assertEquals("Expecting 1 sensors element", 1, topElement.elements("sensors").size());
        Element sensorsElement = topElement.element("sensors");
        Assert.assertEquals("Expecting 1 child for sensors element", 1, sensorsElement.elements().size());
        Assert.assertEquals("Expecting 1 sensor child for sensors element", 1, sensorsElement.elements("sensor").size());
        Element sensorElement = sensorsElement.element("sensor");
        assertSensorElement(sensorElement, referencedSensorId, "custom", "Sensor");
    
        Assert.assertEquals("Expecting 2 children for sensor element", 2, sensorElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for sensorElement", 1, sensorElement.elements("include").size());
        Assert.assertEquals("Expecting 1 state child for sensorElement", 1, sensorElement.elements("state").size());
        includeElement = sensorElement.element("include");
        String referencedReadCommandId = assertIncludeElement(includeElement, "command");
        Element stateElement = sensorElement.element("state");
        assertAttribute(stateElement, "name", "state name");
        assertAttribute(stateElement, "value", "state value");
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command child for commands element", 1, commandsElement.elements("command").size());
    
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedReadCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneWebview() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));

        UIWebView webView = new UIWebView(IDUtil.nextID());
        webView.setURL("http://www.openremote.org");
        webView.setUserName("username");
        webView.setPassword("password");
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(webView);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 web element", 1, absoluteElement.elements("web").size());
        Element webElement = absoluteElement.element("web");
        assertAttribute(webElement, "id", Long.toString(webView.getOid()));
        assertAttribute(webElement, "src", webView.getURL());
        assertAttribute(webElement, "username", webView.getUserName());
        assertAttribute(webElement, "password", webView.getPassword());       
        Assert.assertEquals("Expecting no child for web element", 0, webElement.elements().size());

        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        
        // Web element does not generate anything in controller.xml
        Assert.assertEquals("Expecting no child for components element", 0, componentsElement.elements().size());

        status.setRollbackOnly();
      }
    });
  }

  @Test
  public void testOneScreenWithOneWebviewWithSensor() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @SuppressWarnings("unchecked")
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);   
        account.addDevice(dev);
        deviceService.saveDevice(dev);
    
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand readCommand = new DeviceCommand();
        readCommand.setProtocol(protocol);
        readCommand.setName("readCommand");
        
        readCommand.setDevice(dev);
        dev.getDeviceCommands().add(readCommand);
        
        readCommand.setOid(IDUtil.nextID());
        deviceCommandService.save(readCommand);
        
        CustomSensor sensor = new CustomSensor();
        sensor.setOid(IDUtil.nextID());
        sensor.setName("Sensor");
        sensor.addState(new State("state name",  "state value"));
        sensor.setDevice(dev);
        sensor.setAccount(account);
        account.getSensors().add(sensor);
    
        SensorCommandRef sensorCommandRef = new SensorCommandRef();
        sensorCommandRef.setSensor(sensor);
        sensorCommandRef.setDeviceCommand(readCommand);
        sensor.setSensorCommandRef(sensorCommandRef);
        
        sensorService.saveSensor(sensor);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));

        UIWebView webView = new UIWebView(IDUtil.nextID());
        webView.setURL("http://www.openremote.org");
        webView.setUserName("username");
        webView.setPassword("password");
        webView.setSensorDTOAndInitSensorLink(sensor.getSensorWithInfoDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(webView);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 web element", 1, absoluteElement.elements("web").size());
        Element webElement = absoluteElement.element("web");
        assertAttribute(webElement, "id", Long.toString(webView.getOid()));
        assertAttribute(webElement, "src", webView.getURL());
        assertAttribute(webElement, "username", webView.getUserName());
        assertAttribute(webElement, "password", webView.getPassword());

        Assert.assertEquals("Expecting 1 child for web element", 1, webElement.elements().size());
        Assert.assertEquals("Expecting 1 link child for web element", 1, webElement.elements("link").size());
        Element linkElement = webElement.element("link");
        String referencedSensorId = assertLinkElement(linkElement, "sensor");
        
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        
        // Web element does not generate anything in controller.xml
        Assert.assertEquals("Expecting no child for components element", 0, componentsElement.elements().size());

        Assert.assertEquals("Expecting 1 sensors element", 1, topElement.elements("sensors").size());
        Element sensorsElement = topElement.element("sensors");
        Assert.assertEquals("Expecting 1 child for sensors element", 1, sensorsElement.elements().size());
        Assert.assertEquals("Expecting 1 sensor child for sensors element", 1, sensorsElement.elements("sensor").size());
        Element sensorElement = sensorsElement.element("sensor");
        assertSensorElement(sensorElement, referencedSensorId, "custom", "Sensor");
    
        Assert.assertEquals("Expecting 2 children for sensor element", 2, sensorElement.elements().size());
        Assert.assertEquals("Expecting 1 include child for sensorElement", 1, sensorElement.elements("include").size());
        Assert.assertEquals("Expecting 1 state child for sensorElement", 1, sensorElement.elements("state").size());
        Element includeElement = sensorElement.element("include");
        String referencedReadCommandId = assertIncludeElement(includeElement, "command");
        Element stateElement = sensorElement.element("state");
        assertAttribute(stateElement, "name", "state name");
        assertAttribute(stateElement, "value", "state value");
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command child for commands element", 1, commandsElement.elements("command").size());
    
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedReadCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        // Must cleanup what we did, explicit remove of device from account is required as account is shared by all tests
        account.getDevices().remove(dev);
        account.getSensors().remove(sensor);
        status.setRollbackOnly();
      }
    });
  }
  
  @Test
  public void testOneScreenWithOneColorPicker() throws DocumentException {
    // Test does require database access, must include in transaction
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Device dev = new Device("Test", "Vendor", "Model");
        dev.setDeviceCommands(new ArrayList<DeviceCommand>());
        dev.setAccount(account);
        deviceService.saveDevice(dev);
        
        Protocol protocol = new Protocol();
        protocol.setType(Constants.INFRARED_TYPE);
        
        DeviceCommand cmd = new DeviceCommand();
        cmd.setProtocol(protocol);
        cmd.setName("testLirc");
        
        cmd.setDevice(dev);
        dev.getDeviceCommands().add(cmd);
        
        cmd.setOid(IDUtil.nextID());
        deviceCommandService.save(cmd);
        
        Set<Panel> panels = new HashSet<Panel>();
        List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
        List<GroupRef> groupRefs = new ArrayList<GroupRef>();
            
        Panel p = new Panel();
        p.setOid(IDUtil.nextID());
        p.setName("panel");
        
        final Screen screen1 = new Screen();
        screen1.setOid(IDUtil.nextID());
        screen1.setName("screen1");
        ScreenPair screenPair = new ScreenPair();
        screenPair.setOid(IDUtil.nextID());
        screenPair.setPortraitScreen(screen1);
        screenRefs.add(new ScreenPairRef(screenPair));
        
        ImageSource imageSource = new ImageSource("Image");
        
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOid(IDUtil.nextID());
        colorPicker.setImage(imageSource);
        colorPicker.setUiCommandDTO(cmd.getDeviceCommandDTO());
    
        Absolute abs = new Absolute(IDUtil.nextID());
        abs.setUiComponent(colorPicker);
        screen1.addAbsolute(abs);
        
        Group group1 = new Group();
        group1.setOid(IDUtil.nextID());
        group1.setName("group1");
        group1.setScreenRefs(screenRefs);
        
        groupRefs.add(new GroupRef(group1));
        p.setGroupRefs(groupRefs);
        
        panels.add(p);
    
        cache.replace(panels, IDUtil.nextID());

        SAXReader reader = new SAXReader();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
    
        Document panelXmlDocument = null;
        try {
          panelXmlDocument = reader.read(cache.getPanelXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        Element topElement = panelXmlDocument.getRootElement();
        
        Element panelElement = assertOnePanel(topElement, p);
        assertPanelHasOneGroupChild(panelElement, group1);
    
        Element groupElement = assertOneGroup(topElement, group1);
        assertGroupHasOneScreenChild(groupElement, screen1);
    
        Element screenElement  = assertOneScreen(topElement, screen1);
    
        Assert.assertEquals("Expecting 1 child for screen element", 1, screenElement.elements().size());
        Assert.assertEquals("Expecting 1 absolute element", 1, screenElement.elements("absolute").size());
        Element absoluteElement = screenElement.element("absolute");
        Assert.assertEquals("Expecting 1 child for absolute element", 1, absoluteElement.elements().size());
        Assert.assertEquals("Expecting 1 colorpicker element", 1, absoluteElement.elements("colorpicker").size());
        Element colorPickerElement = absoluteElement.element("colorpicker");
        assertAttribute(colorPickerElement, "id", Long.toString(colorPicker.getOid()));
        Assert.assertEquals("Expecting 1 child for colorpicker element", 1, colorPickerElement.elements().size());
        Assert.assertEquals("Expecting 1 image child for colorpicker element", 1, colorPickerElement.elements("image").size());
        Element imageElement = colorPickerElement.element("image");
        assertAttribute(imageElement, "src", imageSource.getSrc());
       
        Document controllerXmlDocument = null;
        try {
          controllerXmlDocument = reader.read(cache.getControllerXmlFile());
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        topElement = controllerXmlDocument.getRootElement();
        Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
        Element componentsElement = topElement.element("components");
        Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
        Assert.assertEquals("Expecting 1 colorpicker element as child of components", 1, componentsElement.elements("colorpicker").size());
        colorPickerElement = componentsElement.element("colorpicker");
        assertAttribute(colorPickerElement, "id", Long.toString(colorPicker.getOid()));
        Assert.assertEquals("Expecting 1 child for colorpicker element", 1, colorPickerElement.elements().size());
        Assert.assertEquals("Expecting 1 include element", 1, colorPickerElement.elements("include").size());
        Element includeElement = colorPickerElement.element("include");
        assertAttribute(includeElement, "type", "command");
        Assert.assertNotNull("Expecting include to have a ref attribute", includeElement.attribute("ref"));
        
        // Reference is to the command, id is not the id in the database, but should cross reference a command element defined below
        String referencedCommandId = includeElement.attribute("ref").getText();
        
        Assert.assertEquals("Expecting 1 commands element", 1, topElement.elements("commands").size());
        Element commandsElement = topElement.element("commands");
        Assert.assertEquals("Expecting 1 child for commands element", 1, commandsElement.elements().size());
        Assert.assertEquals("Expecting 1 command element as child of components", 1, commandsElement.elements("command").size());
        Element commandElement = commandsElement.element("command");
        assertAttribute(commandElement, "id", referencedCommandId);
        assertAttribute(commandElement, "protocol", "ir");
        Assert.assertEquals("Expecting command element to have 1 child", 1, commandElement.elements().size());
        Assert.assertEquals("Expecting command element to have 1 property child", 1, commandElement.elements("property").size());
        Element propertyElement = commandElement.element("property");
        assertAttribute(propertyElement, "name", "name");
        Assert.assertNotNull("Expecting property to have a value attribute", propertyElement.attribute("value"));

        status.setRollbackOnly();
      }
    });
  }

   /**
    * Validates that the element has the given attribute and that it has the given value.
    * 
    * @param element
    * @param attributeName
    * @param attributeValue
    */
   private void assertAttribute(Element element, String attributeName, String attributeValue) {
     Assert.assertNotNull("Expecting " + attributeName + " attribute on " + element.getName(), element.attribute(attributeName));
     Assert.assertEquals("Expecting " + attributeName + " attribute to be '" + attributeValue + "'", attributeValue, element.attribute(attributeName).getText());
   }
   
   /**
    * Validates that the element does reference one panel and that it matches the given panel model object.
    * 
    * @param topElement
    * @param p
    * @return
    */
   private Element assertOnePanel(Element topElement, Panel p) {
     Assert.assertEquals("Expecting 1 panels element", 1, topElement.elements("panels").size());
     Element panelsElement = topElement.element("panels");
     Assert.assertEquals("Expecting 1 child for panels element", 1, panelsElement.elements().size());
     Assert.assertEquals("Expecting 1 panel element", 1, panelsElement.elements("panel").size());
     Element panelElement = panelsElement.element("panel");
     Assert.assertEquals("Expecting panel to be named " + p.getName(), p.getName(), panelElement.attribute("name").getText());
     Assert.assertEquals("Expecting panel to have id " + p.getOid(), Long.toString(p.getOid()), panelElement.attribute("id").getText());

     return panelElement;
   }

   private void assertPanelHasOneGroupChild(Element panelElement, Group group) {
     Assert.assertEquals("Expecting 1 child for panel element", 1, panelElement.elements().size());
     Assert.assertEquals("Expecting no tab bar in panel element", 0, panelElement.elements("tabbar").size());
     Assert.assertEquals("Expecting 1 include element", 1, panelElement.elements("include").size());
     Element includeElement = panelElement.element("include");
     Assert.assertEquals("group", includeElement.attribute("type").getText());
     Assert.assertEquals(Long.toString(group.getOid()), includeElement.attribute("ref").getText());
   }
   
   private Element assertOneGroup(Element topElement, Group group) {
     Assert.assertEquals("Expecting 1 groups element", 1, topElement.elements("groups").size());
     Element groupsElement = topElement.element("groups");
     Assert.assertEquals("Expecting 1 child for groups element", 1, groupsElement.elements().size());
     Assert.assertEquals("Expecting 1 group element", 1, groupsElement.elements("group").size());
     Element groupElement = groupsElement.element("group");
     Assert.assertEquals("Expecting group to have id " + group.getOid(), Long.toString(group.getOid()), groupElement.attribute("id").getText());
     Assert.assertEquals("Expecting group to be named " + group.getName(), group.getName(), groupElement.attribute("name").getText());

     return groupElement;
   }

   private void assertGroupHasOneScreenChild(Element groupElement, Screen screen) {  
     Assert.assertEquals("Expecting 1 child for group element", 1, groupElement.elements().size());
     Assert.assertEquals("Expecting 1 include element", 1, groupElement.elements("include").size());
     Element includeElement = groupElement.element("include");
     Assert.assertEquals("screen", includeElement.attribute("type").getText());
     Assert.assertEquals(Long.toString(screen.getOid()), includeElement.attribute("ref").getText());
   }
   
   private Element assertOneScreen(Element topElement, Screen screen) {
     Assert.assertEquals("Expecting 1 screens element", 1, topElement.elements("screens").size());
     Element screensElement = topElement.element("screens");
     Assert.assertEquals("Expecting 1 child for screens element", 1, screensElement.elements().size());
     Assert.assertEquals("Expecting 1 screen element",  1, screensElement.elements("screen").size());
     Element screenElement = screensElement.element("screen");
     Assert.assertEquals("Expecting screen to have id " + screen.getOid(), Long.toString(screen.getOid()), screenElement.attribute("id").getText());
     Assert.assertEquals("Expecting screen to be named " + screen.getName(), screen.getName(), screenElement.attribute("name").getText());

     return screenElement;
   }
   
   private String assertLinkElement(Element linkElement, String expectedType) {
     Assert.assertNotNull("Expecting link to have a type attribute", linkElement.attribute("type"));
     Assert.assertEquals("Expecting link type to be " + expectedType, expectedType, linkElement.attribute("type").getText());
     Assert.assertNotNull("Expecting link to have a ref attribute", linkElement.attribute("ref"));
     return linkElement.attribute("ref").getText();
   }
   
   private String assertIncludeElement(Element includeElement, String expectedType) {
     Assert.assertNotNull("Expecting include to have a type attribute", includeElement.attribute("type"));
     Assert.assertEquals("Expecting include type to be " + expectedType, expectedType, includeElement.attribute("type").getText());
     Assert.assertNotNull("Expecting include to have a ref attribute", includeElement.attribute("ref"));
     return includeElement.attribute("ref").getText();
   }
   
   private void assertSensorElement(Element sensorElement, String sensorId, String sensorType, String sensorName) {
     assertAttribute(sensorElement, "id", sensorId);
     assertAttribute(sensorElement, "type", sensorType);
     assertAttribute(sensorElement, "name", sensorName);
   }
   
   private String assertElementWithOneChild(Element element) {
     Assert.assertEquals("Expecting 1 child for " + element.getName() + " element", 1, element.elements().size());
     return ((Element)element.elements().get(0)).getName();
   }

   private Element assertAbsoluteElementChild(Element absoluteElement, String childName, String left, String top, String width, String height) {
     Assert.assertEquals("Expecting 1 " + childName + " child for absolute element", 1, absoluteElement.elements(childName).size());
     Element element = absoluteElement.element(childName);
     assertAttribute(absoluteElement, "left", left);
     assertAttribute(absoluteElement, "top", top);
     assertAttribute(absoluteElement, "width", width);
     assertAttribute(absoluteElement, "height", height);
     return element;
   }
   
   private Element assertCellElementChild(Element cellElement, String childName, String x, String y) {
     Assert.assertEquals("Expecting 1 " + childName + " child for cell element", 1, cellElement.elements(childName).size());
     Element element = cellElement.element(childName);
     assertAttribute(cellElement, "x", x);
     assertAttribute(cellElement, "y", y);
     assertAttribute(cellElement, "rowspan", "1");
     assertAttribute(cellElement, "colspan", "1");
     return element;
   }

   private Absolute createAbsolute(int baseValue, UIComponent uiComponent) {
     Absolute abs = new Absolute(IDUtil.nextID());
     abs.setPosition(baseValue, baseValue + 1);
     abs.setSize(baseValue + 2, baseValue + 3);
     abs.setUiComponent(uiComponent);
     return abs;
   }
   
   private Cell createCell(int left, int right, UIComponent uiComponent) {
     Cell cell = new Cell();
     cell.setPosX(left);
     cell.setPosY(right);
     cell.setColspan(1);
     cell.setRowspan(1);
     cell.setUiComponent(uiComponent);
     return cell;
   }
}