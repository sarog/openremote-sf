package org.openremote.modeler.service;

import org.openremote.modeler.SpringContext;
import org.openremote.modeler.TestBase;
import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.domain.Device;

public class DeviceServiceTest extends TestBase{
   private DeviceService deviceService = (DeviceService) SpringContext.getInstance().getBean("deviceService");
   
   public void testSaveDevice(){
      Device device = new Device();
      device.setName("tv");
      device.setVendor("sony");
      device.setModel("TV");
      deviceService.saveDevice(device);
      System.out.println("id:"+device.getOid()+" name:"+device.getName());
      
      device.setName("m3");
      device.setVendor("m3");
      device.setModel("MP8640");
      deviceService.saveDevice(device);
      System.out.println("id:"+device.getOid()+" name:"+device.getName());
   }
}
