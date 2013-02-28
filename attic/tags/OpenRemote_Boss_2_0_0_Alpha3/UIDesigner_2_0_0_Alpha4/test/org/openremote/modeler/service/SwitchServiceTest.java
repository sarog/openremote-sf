package org.openremote.modeler.service;

import java.util.Collection;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchCommandOffRef;
import org.openremote.modeler.domain.SwitchCommandOnRef;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SwitchServiceTest {
   private SwitchService service = null;
   private DeviceCommandService deviceCommandService;
   @BeforeClass
   public void setUp(){
      service = (SwitchService) SpringTestContext.getInstance().getBean("switchService");
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
      Authentication authentication = new UsernamePasswordAuthenticationToken("javen","javen");
      SecurityContextHolder.getContext().setAuthentication(authentication);
   }
   
   @Test
   public void testSaveSwitch(){
      Switch swh = new Switch();
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      deviceCommandService.save(cmd);
      
      swh.setName("testName");
      
      SwitchCommandOnRef swhOnCmdRef = new SwitchCommandOnRef();
      swhOnCmdRef.setDeviceCommand(cmd);
      swhOnCmdRef.setOnSwitch(swh);
      
      SwitchCommandOffRef swhOffCmdRef = new SwitchCommandOffRef();
      swhOffCmdRef.setDeviceCommand(cmd);
      swhOffCmdRef.setOffSwitch(swh);
      
      swh.setSwitchCommandOnRef(swhOnCmdRef);
      swh.setSwitchCommandOffRef(swhOffCmdRef);
      
      
//      swh.setOffDeviceCommandRef(cmdRef);
      
      Switch swh2 = new Switch();
      swh.setName("testName2");
      
      service.save(swh);
      service.save(swh2);
      
      Switch switchFromTable = service.loadAll().get(0);
      Assert.assertEquals(swh.getOid(),1);
      Assert.assertEquals(swh2.getOid(),2);
      Assert.assertEquals(switchFromTable.getSwitchCommandOnRef().getDeviceCommand().getName(), "testLirc");
      Assert.assertEquals(switchFromTable.getSwitchCommandOffRef().getDeviceCommand().getName(), "testLirc");
   }
   @Test(dependsOnMethods="testSaveSwitch")
   public void testUpdate(){
      Switch swh = new Switch();
      swh.setName("testUpdate");
      swh.setOid(1);
      service.update(swh);
      
      Collection<Switch> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 2);
      for(Switch s: switchs){
         if(s.getOid() == 1){
            Assert.assertEquals(s.getName(), "testUpdate");
            break;
         }
      }
   }
   
   @Test(dependsOnMethods="testSaveSwitch")
   public void testLoadAll(){
      Collection<Switch> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 2);
   }
   
   @Test(dependsOnMethods="testUpdate")
   public void testDelte(){
      Switch swh = new Switch();
      swh.setOid(1);
      service.delete(1);
      Collection<Switch> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 1);
   }
}
