package org.openremote.modeler.service;

import java.util.Collection;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.Slider;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SliderServiceTest {
   private SliderService service = null;
   private DeviceCommandService deviceCommandService;
   @BeforeClass
   public void setUp(){
      
      service = (SliderService) SpringTestContext.getInstance().getBean("sliderService");
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
   }
   
   @Test
   public void testSaveSlider(){
      Slider slider = new Slider();
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      deviceCommandService.save(cmd);
      
      
      slider.setName("testName");
      DeviceCommandRef cmdRef = new DeviceCommandRef(deviceCommandService.loadById(1));
      Assert.assertEquals(cmdRef.getDeviceCommand().getName(), "testLirc");
//      slider.setSetValueCmd(cmdRef);
      
      Slider slider2 = new Slider();
      slider.setName("testName2");
      
      service.save(slider);
      service.save(slider2);
      
      Slider sliderFromTable = service.loadAll().get(0);
      Assert.assertEquals(slider.getOid(),1);
      Assert.assertEquals(slider2.getOid(),2);
      Assert.assertEquals(sliderFromTable.getSetValueCmd().getDeviceCommand().getName(), "testLirc");
   }
   @Test(dependsOnMethods="testSaveSlider")
   public void testUpdate(){
      Slider slider = new Slider();
      slider.setName("testUpdate");
      slider.setOid(1);
      service.update(slider);
      
      Collection<Slider> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 2);
      for(Slider s: switchs){
         if(s.getOid() == 1){
            Assert.assertEquals(s.getName(), "testUpdate");
            break;
         }
      }
   }
   
   @Test(dependsOnMethods="testSaveSlider")
   public void testLoadAll(){
      Collection<Slider> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 2);
   }
   
   @Test(dependsOnMethods="testUpdate")
   public void testDelte(){
      Slider slider = new Slider();
      slider.setOid(1);
      service.delete(1);
      Collection<Slider> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 1);
   }
}
