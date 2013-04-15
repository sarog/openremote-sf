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

import javax.transaction.TransactionManager;
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
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
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
import org.openremote.modeler.domain.ProtocolAttr;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchCommandOffRef;
import org.openremote.modeler.domain.SwitchCommandOnRef;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.Gesture.GestureType;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UITabbar;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.server.lutron.importmodel.Project;
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
      Set<Panel> emptyPanels = new HashSet<Panel>();
      
      cache.replace(emptyPanels, IDUtil.nextID());
  
      SAXReader reader = new SAXReader();
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);
      factory.setNamespaceAware(true);
  
      Document panelXmlDocument = reader.read(cache.getPanelXmlFile());
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

      Document controllerXmlDocument = reader.read(cache.getControllerXmlFile());
      topElement = controllerXmlDocument.getRootElement();
      Assert.assertEquals(1, topElement.elements("components").size());
      Element componentsElement = topElement.element("components");
      Assert.assertEquals(0, componentsElement.elements().size());
  }
   
   @Test
   public void testPanelHasGroupScreenControl()throws Exception {
      List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
      List<GroupRef> groupRefs = new ArrayList<GroupRef>();
      Set<Panel> panels = new HashSet<Panel>();
      
      /*---------------widget-------------------*/
      UIButton absBtn = new UIButton();            //UIButton
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      ImageSource defaultImage = new ImageSource("default.jpg");
      ImageSource pressedImage = new ImageSource("pressed.jpg");
      absBtn.setImage(defaultImage);
      absBtn.setPressImage(pressedImage);
      
      UILabel label = new UILabel(IDUtil.nextID());    // UILabel
      label.setText("testLabel");
      label.setColor("000fff000");
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      
      Switch switchToggle = new Switch();
      switchToggle.setOid(IDUtil.nextID());
      Sensor sensor = new Sensor();
      sensor.setType(SensorType.SWITCH);
      sensor.setOid(IDUtil.nextID());
      sensor.setName("testSensro");
      SwitchSensorRef sensorRef = new SwitchSensorRef(switchToggle);
      sensorRef.setOid(IDUtil.nextID());
      sensorRef.setSensor(sensor);
      switchToggle.setSwitchSensorRef(sensorRef);
      label.setSensorAndInitSensorLink(sensor);
      
      UISwitch absSwitch = new UISwitch();      //UISwitch
      absSwitch.setOid(IDUtil.nextID());
      ImageSource onImage = new ImageSource("on.jpg");
      ImageSource offImage = new ImageSource("off.jpg");
      absSwitch.setOnImage(onImage);
      absSwitch.setOffImage(offImage);
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle); 
      
      UIImage uiImage = new UIImage(IDUtil.nextID()); //UIImage
      uiImage.setSensorAndInitSensorLink(sensor);
      uiImage.setLabel(label);
         
      /*---------------widget-------------------*/
      
      
      /*---------------screen-------------------*/
      Screen screen1 = new Screen();
      screen1.setOid(IDUtil.nextID());
      screen1.setName("screen1");
      
      Screen screen2 = new Screen();
      screen2.setOid(IDUtil.nextID());
      screen2.setName("screen1");
      
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
      
      screen1.addAbsolute(abs1);
      screen2.addAbsolute(abs2);
      
      screen1.addGrid(grid1);
      screen2.addGrid(grid2);
      
      ScreenPair screenPair1 = new ScreenPair();
      screenPair1.setOid(IDUtil.nextID());
      screenPair1.setPortraitScreen(screen1);
      
      ScreenPair screenPair2 = new ScreenPair();
      screenPair2.setOid(IDUtil.nextID());
      screenPair2.setPortraitScreen(screen2);
      
      screenRefs.add(new ScreenPairRef(screenPair1));
      screenRefs.add(new ScreenPairRef(screenPair2));
      /*---------------group-------------------*/
      Group group1 = new Group();
      group1.setOid(IDUtil.nextID());
      group1.setName("group1");
      group1.setScreenRefs(screenRefs);
      
      Group group2 = new Group();
      group2.setOid(IDUtil.nextID());
      group2.setName("group1");
      group2.setScreenRefs(screenRefs);
      
      groupRefs.add(new GroupRef(group1));
      groupRefs.add(new GroupRef(group2));
      /*---------------panel------------------*/
      Panel panel1 = new Panel();
      panel1.setOid(IDUtil.nextID());
      panel1.setGroupRefs(groupRefs);
      panel1.setGroupRefs(groupRefs);
      panel1.setName("panel1");
      
      Panel panel2 = new Panel();
      panel2.setOid(IDUtil.nextID());
      panel2.setGroupRefs(groupRefs);
      panel2.setGroupRefs(groupRefs);
      panel2.setName("panel2");
      
      panels.add(panel1);
      panels.add(panel2);
      
// EBR TEMP      resourceService.initResources(panels, IDUtil.nextID());
      
          cache.replace(panels, IDUtil.nextID());
      
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
  
      Document panelXmlDocument = reader.read(cache.getPanelXmlFile());
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

      Document controllerXmlDocument = reader.read(cache.getControllerXmlFile());
      topElement = controllerXmlDocument.getRootElement();
      Assert.assertEquals(1, topElement.elements("components").size());
      Element componentsElement = topElement.element("components");
      Assert.assertEquals(0, componentsElement.elements().size());
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

   Document panelXmlDocument = reader.read(cache.getPanelXmlFile());
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
   
   Document controllerXmlDocument = reader.read(cache.getControllerXmlFile());
   topElement = controllerXmlDocument.getRootElement();
   Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
   Element componentsElement = topElement.element("components");
   Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size()); // Gesture is included in component
 }
   
   @Test
   public void testPanelTabbarWithNavigateToLogical() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
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
      panelWithJustOneNavigate.add(p);
// EBR TEMP      resourceService.initResources(panelWithJustOneNavigate, IDUtil.nextID());
   }
   
@Test
   public void testPanelNavigateHasImage() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      
      ImageSource image = new ImageSource();
      image.setSrc("http://finalist.cn/logo.ico");
      
      UITabbarItem item = new UITabbarItem();
      item.setImage(image);
      
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setOid(IDUtil.nextID());
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      p.setTabbarItems(items);
      panelWithJustOneNavigate.add(p);
// EBR TEMP      resourceService.initResources(panelWithJustOneNavigate, IDUtil.nextID());
   }
   
@Test
   public void testGroupNavigateHasImage() {
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      
      ImageSource image = new ImageSource();
      image.setSrc("http://finalist.cn/logo.ico");
      
      UITabbarItem item = new UITabbarItem();
      item.setImage(image);
      
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setOid(IDUtil.nextID());
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      
      Group group = new Group();
      group.setName("groupName");
      group.setOid(IDUtil.nextID());
      group.setTabbarItems(items);
      
      p.addGroupRef(new GroupRef(group));
      panelWithJustOneNavigate.add(p);
// EBR TEMP      resourceService.initResources(panelWithJustOneNavigate, IDUtil.nextID());
   }
   
 @Test
   public void testScreenHasBackgrouond() {
      Collection<Panel> panel = new ArrayList<Panel>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      
      screen.getBackground().setImageSource(new ImageSource("http://finalist.cn/logo.jpg"));
      
      Panel p = new Panel();
      p.setOid(IDUtil.nextID());
      p.setName("panel has a navigate");
      
      Group group = new Group();
      group.setName("groupName");
      group.setOid(IDUtil.nextID());
      ScreenPair screenPair = new ScreenPair();
      screenPair.setOid(IDUtil.nextID());
      screenPair.setPortraitScreen(screen);
      
      group.addScreenRef(new ScreenPairRef(screenPair));
      
      p.addGroupRef(new GroupRef(group));
      panel.add(p);
// EBR TEMP      resourceService.initResources(panel, IDUtil.nextID());
   }
 
   @Test
   public void testEmptyScreen() {
    List<ScreenPairRef> screenRefs = new ArrayList<ScreenPairRef>();
    List<GroupRef> groupRefs = new ArrayList<GroupRef>();
    List<Panel> panels = new ArrayList<Panel>();

    /*---------------group-------------------*/
    Group group1 = new Group();
    group1.setOid(IDUtil.nextID());
    group1.setName("group1");
    group1.setScreenRefs(screenRefs);    
    groupRefs.add(new GroupRef(group1));

    Panel panel1 = new Panel();
    panel1.setOid(IDUtil.nextID());
    panel1.setGroupRefs(groupRefs);
    panel1.setGroupRefs(groupRefs);
    panel1.setName("panel1");

      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("EmptyScreen");
      
      ScreenPair screenPair1 = new ScreenPair();
      screenPair1.setOid(IDUtil.nextID());
      screenPair1.setPortraitScreen(screen);      
      screenRefs.add(new ScreenPairRef(screenPair1));
      
// EBR TEMP      resourceService.initResources(panels, IDUtil.nextID());
   }
   
@Test(enabled=false)
   public void testGetControllerXMLWithButtonAndSwitchButNoCmd() {
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      
      
      Switch switchToggle = new Switch();
      switchToggle.setOid(IDUtil.nextID());
      Sensor sensor = new Sensor();
      sensor.setType(SensorType.SWITCH);
      sensor.setOid(IDUtil.nextID());
      sensor.setName("testSensro");
      SwitchSensorRef sensorRef = new SwitchSensorRef(switchToggle);
      sensorRef.setOid(IDUtil.nextID());
      sensorRef.setSensor(sensor);
      switchToggle.setSwitchSensorRef(sensorRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle);
      Absolute abs1 = new Absolute();
      abs1.setOid(IDUtil.nextID());
      abs1.setUiComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setOid(IDUtil.nextID());
      abs2.setUiComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      grid1.setOid(IDUtil.nextID());
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      grid2.setOid(IDUtil.nextID());
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
//      outputControllerXML(screens);
   }


  @Test
  public void testOneScreenWithOneButtonHavingOneDeviceCommand() throws DocumentException {
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
    
    try {
      System.out.println("Controller file has been written to " + cache.getControllerXmlFile());
      System.out.println("Content is ");
      IOUtils.copy(new FileInputStream(cache.getControllerXmlFile()), System.out);
      System.out.println("Panel file has been written to " + cache.getPanelXmlFile());
      System.out.println("Content is ");
      IOUtils.copy(new FileInputStream(cache.getPanelXmlFile()), System.out);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    
    
    SAXReader reader = new SAXReader();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(true);

    Document panelXmlDocument = reader.read(cache.getPanelXmlFile());
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
    Assert.assertEquals(Long.toString(button.getOid()), buttonElement.attribute("id").getText());
    Assert.assertNotNull("Expecting button to have a hasControlCommand attribute", buttonElement.attribute("hasControlCommand"));
    Assert.assertEquals("true", buttonElement.attribute("hasControlCommand").getText());
    Assert.assertEquals(button.getName(), buttonElement.attribute("name").getText());
   
    Document controllerXmlDocument = reader.read(cache.getControllerXmlFile());
    topElement = controllerXmlDocument.getRootElement();
    Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
    Element componentsElement = topElement.element("components");
    Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
    Assert.assertEquals("Expecting 1 button element as child of components", 1, componentsElement.elements("button").size());
    buttonElement = componentsElement.element("button");
    Assert.assertEquals(Long.toString(button.getOid()), buttonElement.attribute("id").getText());
    Assert.assertEquals("Expecting 1 child for button element", 1, buttonElement.elements().size());
    Assert.assertEquals("Expecting 1 include element", 1, buttonElement.elements("include").size());
    Element includeElement = buttonElement.element("include");
    Assert.assertNotNull("Expecting include to have a type attribute", includeElement.attribute("type"));
    Assert.assertEquals("Expecting include type to be button", "button", includeElement.attribute("type").getText());
    Assert.assertNotNull("Expeting include to have a ref attribute", includeElement.attribute("ref"));
    Assert.assertEquals("Expecting include to reference the right button", Long.toString(button.getOid()), includeElement.attribute("ref").getText());
  }
   
  @Test
  public void testOneScreenWithOneSwitch() throws DocumentException {
    Device dev = new Device("Test", "Vendor", "Model");
    dev.setDeviceCommands(new ArrayList<DeviceCommand>());
    dev.setAccount(account);
    deviceService.saveDevice(dev);
    
    Protocol protocol = new Protocol();
    protocol.setType(Constants.INFRARED_TYPE);
    
    DeviceCommand readCommand = new DeviceCommand();
    readCommand.setProtocol(protocol);
    readCommand.setName("testLirc");
    
    readCommand.setDevice(dev);
    dev.getDeviceCommands().add(readCommand);
    
    readCommand.setOid(IDUtil.nextID());
    deviceCommandService.save(readCommand);
    
    Sensor sensor = new Sensor(SensorType.SWITCH);
    sensor.setOid(IDUtil.nextID());
    sensor.setName("Sensor");
    sensor.setDevice(dev);
    sensor.setAccount(account);

    SensorCommandRef sensorCommandRef = new SensorCommandRef();
    sensorCommandRef.setSensor(sensor);
    sensorCommandRef.setDeviceCommand(readCommand);
    sensor.setSensorCommandRef(sensorCommandRef);
    
    sensorService.saveSensor(sensor);
    
    DeviceCommand onCommand = new DeviceCommand();
    onCommand.setProtocol(protocol);
    onCommand.setName("testLirc");
    
    onCommand.setDevice(dev);
    dev.getDeviceCommands().add(onCommand);
    
    onCommand.setOid(IDUtil.nextID());
    deviceCommandService.save(onCommand);
    
    DeviceCommand offCommand = new DeviceCommand();
    offCommand.setProtocol(protocol);
    offCommand.setName("testLirc");
    
    offCommand.setDevice(dev);
    dev.getDeviceCommands().add(offCommand);
    
    offCommand.setOid(IDUtil.nextID());
    deviceCommandService.save(offCommand);

    Switch buildingSwitch = new Switch(onCommand, offCommand, sensor);
    buildingSwitch.setOid(IDUtil.nextID());
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
    
    UISwitch aSwitch = new UISwitch(IDUtil.nextID());
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
    
    try {
      System.out.println("Controller file has been written to " + cache.getControllerXmlFile());
      System.out.println("Content is ");
      IOUtils.copy(new FileInputStream(cache.getControllerXmlFile()), System.out);
      System.out.println("Panel file has been written to " + cache.getPanelXmlFile());
      System.out.println("Content is ");
      IOUtils.copy(new FileInputStream(cache.getPanelXmlFile()), System.out);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    SAXReader reader = new SAXReader();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(true);

    Document panelXmlDocument = reader.read(cache.getPanelXmlFile());
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
   
    Document controllerXmlDocument = reader.read(cache.getControllerXmlFile());
    topElement = controllerXmlDocument.getRootElement();
    Assert.assertEquals("Expecting 1 components element", 1, topElement.elements("components").size());
    Element componentsElement = topElement.element("components");
    Assert.assertEquals("Expecting 1 child for components element", 1, componentsElement.elements().size());
    Assert.assertEquals(1, componentsElement.elements("switch").size());
    switchElement = componentsElement.element("switch");
    Assert.assertEquals(Long.toString(aSwitch.getOid()), switchElement.attribute("id").getText());
  }
  
  @Test(enabled=false)
   public void testGetControllerXMLWithButtonAndSwitchJustHaveDeviceCommand() {
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      //cmd.setOid(IDUtil.nextID());
      deviceCommandService.save(cmd);
      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(cmdRef);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(cmdRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      
      Switch switchToggle = new Switch();
      
      SwitchCommandOnRef onCommand = new SwitchCommandOnRef();
      onCommand.setOnSwitch(switchToggle);
      onCommand.setDeviceCommand(cmd);
      SwitchCommandOffRef offCommand = new SwitchCommandOffRef();
      offCommand.setOffSwitch(switchToggle);
      offCommand.setDeviceCommand(cmd);
      switchToggle.setSwitchCommandOffRef(offCommand);
      switchToggle.setSwitchCommandOnRef(onCommand);
      
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle);
      
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
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
//      outputControllerXML(screens);
   }
@Test(enabled=false)
public void testGetControllerXMLWithGestureHaveDeviceCommand() {
   
   Protocol protocol = new Protocol();
   protocol.setType(Constants.INFRARED_TYPE);
   
   DeviceCommand cmd = new DeviceCommand();
   cmd.setProtocol(protocol);
   cmd.setName("testLirc");
   deviceCommandService.save(cmd);
   DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
   List<Screen> screens = new ArrayList<Screen>();
   Screen screen = new Screen();
   screen.setOid(IDUtil.nextID());
   screen.setName("screenWithButtonAndSwitch");
   List<Gesture> gestures = new ArrayList<Gesture>();
   Gesture gesture = new Gesture();
   gesture.setOid(IDUtil.nextID());
   gesture.setUiCommand(cmdRef);
   gestures.add(gesture);
   screen.setGestures(gestures);
   screens.add(screen);
//   outputControllerXML(screens);
}
   @Test(enabled=false)
   public void testGetControllerXMLWithButtonAndSwitchHaveSensor() {
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      List<ProtocolAttr> attrs = new ArrayList<ProtocolAttr>();
      ProtocolAttr attr1 = new ProtocolAttr();
      attr1.setName("command");
      attr1.setValue("\"nameValue");
      attr1.setProtocol(protocol);
      attrs.add(attr1);
     
      protocol.setAttributes(attrs);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      deviceCommandService.save(cmd);
      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(cmdRef);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(cmdRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      
      Switch switchToggle = new Switch();
      SwitchCommandOnRef onCommand = new SwitchCommandOnRef();
      onCommand.setOnSwitch(switchToggle);
      onCommand.setDeviceCommand(cmd);
      SwitchCommandOffRef offCommand = new SwitchCommandOffRef();
      offCommand.setOffSwitch(switchToggle);
      offCommand.setDeviceCommand(cmd);
      switchToggle.setSwitchCommandOffRef(offCommand);
      switchToggle.setSwitchCommandOnRef(onCommand);
      
      Sensor sensor = new Sensor();
      sensor.setOid(IDUtil.nextID());
      sensor.setType(SensorType.SWITCH);
      sensor.setName("testSensor");
      SensorCommandRef sensorCmdRef = new SensorCommandRef();
      sensorCmdRef.setDeviceCommand(cmd);
      sensorCmdRef.setSensor(sensor);
      sensor.setSensorCommandRef(sensorCmdRef);
      SwitchSensorRef switchSensorRef = new SwitchSensorRef(switchToggle);
      switchSensorRef.setSensor(sensor);
      switchToggle.setSwitchSensorRef(switchSensorRef);
      
      absSwitch.setSwitchCommand(switchToggle);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      gridSwitch.setSwitchCommand(switchToggle);
      
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
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
//      outputControllerXML(screens);
   }
   
   /*
    * The case has some problem because of LazyInitializationException 
    */
// @Test(enabled=false)
   public void testGetControllerXMLWithButtonAndSwitchHaveMacro() {
/*      
      Account account = new Account();
      account.setOid(5);
      
      User u = new User();
      u.setAccount(account);
      u.setPassword("");
      u.setUsername("sa");
      List<Role> roles = new ArrayList<Role>();
      roles.add(Role.ROLE_DESIGNER);
      roles.add(Role.ROLE_MODELER);
      u.setRoles(Role.ROLE_MODELER);
//      u.setOid(4);
      userService.saveUser(u);*/
      
      userService.createUserAccount("testMacro", "testMacro", "test");
      
      DeviceMacro deviceMacro = new DeviceMacro();
      deviceMacro.setName("testMacro");
//      deviceMacro.setOid(6);
//      deviceMacro.setAccount(account);
      
      DeviceMacroItem item1 = new CommandDelay("1000");
//      item1.setOid(7);
      item1.setParentDeviceMacro(deviceMacro);
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
//      cmd.setOid(4);
      deviceCommandService.save(cmd);
      
      DeviceMacroItem item2 = new DeviceCommandRef(cmd);
//      item2.setOid(8);
      item2.setParentDeviceMacro(deviceMacro);
      
      List<DeviceMacroItem> items = new ArrayList<DeviceMacroItem>();
      items.add(item1);
      items.add(item2);
      
      deviceMacro.setDeviceMacroItems(items);
      
      DeviceMacroRef macroRef = new DeviceMacroRef(deviceMacro);
//      macroRef.setOid(9);
      
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testMacro", "testMacro"));
      deviceMacroService.saveDeviceMacro(deviceMacro);
      
     /* Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd1 = new DeviceCommand();
      cmd1.setProtocol(protocol);
      cmd1.setName("testLirc");
      cmd1.setOid(4);
      
      DeviceCommand cmd2 = new DeviceCommand();
      cmd2.setProtocol(protocol);
      cmd2.setName("testLirc");
      cmd2.setOid(5);
      
      deviceCommandService.save(cmd1);
      deviceCommandService.save(cmd2);*/
      
      
      
//      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd1);
      
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(macroRef);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(macroRef);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
//      absSwitch.setOnCommand(macroRef);
//      absSwitch.setOffCommand(macroRef);
//      absSwitch.setStatusCommand(macroRef);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
//      gridSwitch.setOnCommand(macroRef);
//      gridSwitch.setOffCommand(macroRef);
//      gridSwitch.setStatusCommand(macroRef);
      
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
      
      screen.addAbsolute(abs1);
      screen.addAbsolute(abs2);
      screen.addGrid(grid1);
      screen.addGrid(grid2);
      
      screens.add(screen);
//      outputControllerXML(screens);
   }
   
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
}