package org.openremote.modeler.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Cell;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.domain.component.UITabbarItem;
import org.openremote.modeler.domain.component.UImage;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.openremote.modeler.service.impl.ResourceServiceImpl;
import org.openremote.modeler.service.impl.UserServiceImpl;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ResourceServiceImplTest {
   
   Configuration configuration;
   private ResourceServiceImpl resourceServiceImpl = null;
   private DeviceCommandService deviceCommandService;
   private DeviceMacroService deviceMacroService;
   private UserServiceImpl userServiceImpl;
   @BeforeClass
   public void setUp(){
      resourceServiceImpl = (ResourceServiceImpl) SpringTestContext.getInstance().getBean("resourceService");
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
      deviceMacroService = (DeviceMacroService) SpringTestContext.getInstance().getBean("deviceMacroService");
      userServiceImpl = (UserServiceImpl) SpringTestContext.getInstance().getBean("userService");
      /*------------xml validation-------------*/
      configuration = (Configuration) SpringTestContext.getInstance().getBean("configuration");
   }
   @Test
   public void testNopanel(){
      Collection<Panel> emptyPanel = new ArrayList<Panel>();
     outputPanelXML(emptyPanel);
   }
   @Test
   public void testPanelHasGroupScreenControl()throws Exception{
      List<ScreenRef> screenRefs = new ArrayList<ScreenRef>();
      List<GroupRef> groupRefs = new ArrayList<GroupRef> ();
      List<Panel> panels = new ArrayList<Panel>();
      
      /*---------------widget-------------------*/
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      UImage defaultImage = new UImage("default.jpg");
      UImage pressedImage = new UImage("pressed.jpg");
      absBtn.setImage(defaultImage);
      absBtn.setPressImage(pressedImage);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      UImage onImage = new UImage("on.jpg");
      UImage offImage = new UImage("off.jpg");
      absSwitch.setOnImage(onImage);
      absSwitch.setOffImage(offImage);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
         
         
      /*---------------widget-------------------*/
      
      
      /*---------------screen-------------------*/
      Screen screen1 = new Screen();
      screen1.setOid(IDUtil.nextID());
      screen1.setName("screen1");
      
      Screen screen2 = new Screen();
      screen2.setOid(IDUtil.nextID());
      screen2.setName("screen1");
      
      Absolute abs1 = new Absolute();
      abs1.setUIComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUIComponent(absSwitch);
      
      UIGrid grid1 = new UIGrid(10,10,20,20,4,4);
      Cell c1 = new Cell();
      c1.setUiComponent(gridBtn);
      grid1.addCell(c1);
      UIGrid grid2 = new UIGrid(10,10,34,20,5,4);
      Cell c2 = new Cell();
      c2.setUiComponent(gridSwitch);
      grid2.addCell(c2);
      
      
      screen1.addAbsolute(abs1);
      screen2.addAbsolute(abs2);
      
      screen1.addGrid(grid1);
      screen2.addGrid(grid2);
      
      screenRefs.add(new ScreenRef(screen1));
      screenRefs.add(new ScreenRef(screen2));
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
      outputPanelXML(panels);
   }
   @Test
   public void testPanelTabbarWithNavigateToGroupAndScreen(){
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToGroup(1L);
      nav.setToScreen(2L);
      UITabbarItem item = new UITabbarItem();
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      p.setTabbarItems(items);
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
   @Test
   public void testPanelTabbarWithNavigateToLogical(){
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      UITabbarItem item = new UITabbarItem();
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      p.setTabbarItems(items);
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
   @Test
   public void testPanelNavigateHasImage(){
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      
      UImage image = new UImage();
      image.setBorder(14);
      image.setSrc("http://finalist.cn/logo.ico");
      
      UITabbarItem item = new UITabbarItem();
      item.setImage(image);
      
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      p.setTabbarItems(items);
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
   @Test
   public void testGroupNavigateHasImage(){
      Collection<Panel> panelWithJustOneNavigate = new ArrayList<Panel>();
      Navigate nav = new Navigate();
      nav.setOid(IDUtil.nextID());
      nav.setToLogical(ToLogicalType.back);
      
      UImage image = new UImage();
      image.setBorder(14);
      image.setSrc("http://finalist.cn/logo.ico");
      
      UITabbarItem item = new UITabbarItem();
      item.setImage(image);
      
      item.setNavigate(nav);
      item.setName("navigate name");
      Panel p = new Panel();
      p.setName("panel has a navigate");
      List<UITabbarItem> items = new ArrayList<UITabbarItem>();
      items.add(item);
      
      Group group = new Group();
      group.setName("groupName");
      group.setOid(IDUtil.nextID());
      group.setTabbarItems(items);
      
      p.addGroupRef(new GroupRef(group));
      panelWithJustOneNavigate.add(p);
      outputPanelXML(panelWithJustOneNavigate);
   }
   
 @Test
   public void testScreenHasBackgrouond(){
      Collection<Panel> panel = new ArrayList<Panel>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      
      screen.getBackground().setSrc("http://finalist.cn/logo.jpg");
      
      Panel p = new Panel();
      p.setName("panel has a navigate");
      
      Group group = new Group();
      group.setName("groupName");
      group.setOid(IDUtil.nextID());
      
      group.addScreenRef(new ScreenRef(screen));
      
      p.addGroupRef(new GroupRef(group));
      panel.add(p);
      outputPanelXML(panel);
   }
   @Test
   public void testgetControllXMWithEmptyScreen(){
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("EmptyScreen");
      
      screens.add(screen);
      outputControllerXML(screens);
   }
   
   @Test
   public void testGetControllerXMLWithButtonAndSwitchButNoCmd(){
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
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
      
      Absolute abs1 = new Absolute();
      abs1.setUIComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUIComponent(absSwitch);
      
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
      outputControllerXML(screens);
   }
   
   @Test
   public void testGetControllerXMLWithButtonAndSwitchButHaveOnlyDelayCmd(){
      
      CommandDelay delayCmd = new CommandDelay("100");
      
      List<Screen> screens = new ArrayList<Screen>();
      Screen screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setName("screenWithButtonAndSwitch");
      
      UIButton absBtn = new UIButton();
      absBtn.setOid(IDUtil.nextID());
      absBtn.setName("abs_btn1");
      absBtn.setUiCommand(delayCmd);
      
      UIButton gridBtn = new UIButton();
      gridBtn.setOid(IDUtil.nextID());
      gridBtn.setName("grid_btn1");
      gridBtn.setUiCommand(delayCmd);
      
      UISwitch absSwitch = new UISwitch();
      absSwitch.setOid(IDUtil.nextID());
//      absSwitch.setOnCommand(delayCmd);
//      absSwitch.setOffCommand(delayCmd);
//      absSwitch.setStatusCommand(delayCmd);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
//      gridSwitch.setOnCommand(delayCmd);
//      gridSwitch.setOffCommand(delayCmd);
//      gridSwitch.setStatusCommand(delayCmd);
      
      Absolute abs1 = new Absolute();
      abs1.setUIComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUIComponent(absSwitch);
      
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
      outputControllerXML(screens);
   }
   
   @Test
   public void testGetControllerXMLWithButtonAndSwitchButHaveDeviceCommand(){
      
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      //cmd.setOid(IDUtil.nextID());
      deviceCommandService.save(cmd);
      DeviceCommandRef cmdRef = new DeviceCommandRef(cmd);
      resourceServiceImpl.setEventId(1);
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
//      absSwitch.setOnCommand(cmdRef);
//      absSwitch.setOffCommand(cmdRef);
//      absSwitch.setStatusCommand(cmdRef);
      
      UISwitch gridSwitch = new UISwitch();
      gridSwitch.setOid(IDUtil.nextID());
//      gridSwitch.setOnCommand(cmdRef);
//      gridSwitch.setOffCommand(cmdRef);
//      gridSwitch.setStatusCommand(cmdRef);
      
      Absolute abs1 = new Absolute();
      abs1.setUIComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUIComponent(absSwitch);
      
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
      outputControllerXML(screens);
   }
   
   /*
    * The case has some problem because of LazyInitializationException 
    */
//   @Test
   public void testGetControllerXMLWithButtonAndSwitchButHaveMacro(){
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
      
      userServiceImpl.createAccount("testMacro", "testMacro", "role_bm");
      
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
      resourceServiceImpl.setEventId(1);
      
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
      abs1.setUIComponent(absBtn);
      Absolute abs2 = new Absolute();
      abs2.setUIComponent(absSwitch);
      
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
      outputControllerXML(screens);
   }
   private  void outputPanelXML(Collection<Panel> panels){
      try {
         System.out.println( XmlParser.validateAndOutputXML(new File(getClass().getResource(
               configuration.getPanelXsdPath()).getPath()),resourceServiceImpl.getPanelXML(panels)));
      } catch (Exception e) {
         e.printStackTrace();
         fail();
      }
   }
   
   private  void outputControllerXML(Collection<Screen> screens){
      try {
         System.out.println( XmlParser.validateAndOutputXML(new File(getClass().getResource(
               configuration.getControllerXsdPath()).getPath()),resourceServiceImpl.getControllerXML(screens)));
      } catch (Exception e) {
         e.printStackTrace();
         fail();
      }
   }
}
