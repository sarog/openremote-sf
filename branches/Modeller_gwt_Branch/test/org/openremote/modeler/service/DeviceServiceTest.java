package org.openremote.modeler.service;

import java.util.HashMap;
import java.util.Map;

import org.openremote.modeler.SpringContext;
import org.openremote.modeler.TestBase;
import org.openremote.modeler.client.rpc.DeviceService;

public class DeviceServiceTest extends TestBase{
   private DeviceService deviceService = (DeviceService) SpringContext.getInstance().getBean("deviceService");
   

   public void testSaveDevice(){
      Map<String, String> map  = new HashMap<String,String>();
      map.put("name", "tv");
      map.put("vendor", "sony");
      map.put("model", "tv");
      deviceService.saveDevice(map);
//      System.out.println("id:"+device.getOid()+" name:"+device.getName());
   }
}
