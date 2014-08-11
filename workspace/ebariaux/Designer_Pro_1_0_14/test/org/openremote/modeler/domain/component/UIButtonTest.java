package org.openremote.modeler.domain.component;

import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UIButtonTest {

  @Test
  public void testButtonsEquals() {
	Device device = new Device("Test device", "Test brand", "Test model");
	device.setOid(IDUtil.nextID());
	  
	DeviceCommand dc = new DeviceCommand();
	dc.createProtocol("http");
	dc.setDevice(device);
	device.getDeviceCommands().add(dc);

	ImageSource imgSrc1 = new ImageSource("Image 1");
	ImageSource imgSrc2 = new ImageSource("Image 2");
	
	UIButton button1 = new UIButton(IDUtil.nextID());
	button1.setName("Button");
	button1.setRepeate(true);
	button1.setImage(imgSrc1);
	button1.setPressImage(imgSrc2);
	button1.setUiCommandDTO(dc.getDeviceCommandDTO());
	
	UIButton button2 = new UIButton(button1.getOid());
	button2.setName("Button");
	button2.setRepeate(true);
	button2.setImage(imgSrc1);
	button2.setPressImage(imgSrc2);
	button2.setUiCommandDTO(dc.getDeviceCommandDTO());
	
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  }
  
  @Test
  public void testButtonsWithNavigateEquals() {
	Navigate nav = new Navigate();
	nav.setOid(IDUtil.nextID());
	nav.setToGroup(1L);
	nav.setToScreen(2L);
	
	UIButton button1 = new UIButton(IDUtil.nextID());
	button1.setName("Button");
	button1.setRepeate(true);
	button1.setNavigate(nav);
	
	UIButton button2 = new UIButton(button1.getOid());
	button2.setName("Button");
	button2.setRepeate(true);
	button2.setNavigate(nav);
	
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
	
	nav = new Navigate();
	nav.setOid(IDUtil.nextID());
	nav.setToLogical(ToLogicalType.login);
	
	button1.setNavigate(nav);
	button2.setNavigate(nav);
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  }
  
  @Test
  public void testButtonsNotEqual() {
	Device device = new Device("Test device", "Test brand", "Test model");
	device.setOid(IDUtil.nextID());
	  
	DeviceCommand dc = new DeviceCommand();
	dc.createProtocol("http");
	dc.setDevice(device);
	device.getDeviceCommands().add(dc);
	
	DeviceCommand dc2 = new DeviceCommand();
	dc2.createProtocol("http");
	dc2.setDevice(device);
	device.getDeviceCommands().add(dc2);

	ImageSource imgSrc1 = new ImageSource("Image 1");
	ImageSource imgSrc2 = new ImageSource("Image 2");
	
	UIButton button1 = new UIButton(IDUtil.nextID());
	button1.setName("Button");
	button1.setRepeate(true);
	button1.setImage(imgSrc1);
	button1.setPressImage(imgSrc2);
	button1.setUiCommandDTO(dc.getDeviceCommandDTO());
	
	UIButton button2 = new UIButton(button1.getOid());
	button2.setName("Button");
	button2.setRepeate(true);
	button2.setImage(imgSrc1);
	button2.setPressImage(imgSrc2);
	button2.setUiCommandDTO(dc.getDeviceCommandDTO());
	
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");

	button2.setName("Button 2");
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different");
	
	button2.setName("Button");
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
	
	button2.setOid(IDUtil.nextID());
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different");
	
	button2.setOid(button1.getOid());
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
	
	button2.setRepeate(false);
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different");

	button2.setRepeate(true);
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
	
	button2.setImage(null);
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different");
	
	button2.setImage(imgSrc1);
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
	
	button2.setPressImage(null);
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different");
	
	button2.setPressImage(imgSrc1);
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different");
	
	button2.setPressImage(imgSrc2);
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
	
	// TODO: test fails for now, as DTO is not used in compare, only command itself
	button2.setUiCommandDTO(dc2.getDeviceCommandDTO());
	Assert.assertFalse(button1.equals(button2), "Expected the buttons to be different, they have different commands");
  }
  
  @Test
  public void testCopyConstructor() {
	Device device = new Device("Test device", "Test brand", "Test model");
	device.setOid(IDUtil.nextID());
	  
	DeviceCommand dc = new DeviceCommand();
	dc.createProtocol("http");
	dc.setDevice(device);
	device.getDeviceCommands().add(dc);
	
	ImageSource imgSrc1 = new ImageSource("Image 1");
	ImageSource imgSrc2 = new ImageSource("Image 2");

	Navigate nav = new Navigate();
	nav.setOid(IDUtil.nextID());
	nav.setToGroup(1L);
	nav.setToScreen(2L);

	UIButton button1 = new UIButton(IDUtil.nextID());
	button1.setName("Button 1");
	button1.setRepeate(true);
	button1.setImage(imgSrc1);
	button1.setPressImage(imgSrc2);
	button1.setNavigate(nav);
	button1.setUiCommandDTO(dc.getDeviceCommandDTO());

	UIButton button2 = new UIButton(button1);
	
	Assert.assertEquals(button1, button2, "Expected the buttons to be equal");
  }
  
}
