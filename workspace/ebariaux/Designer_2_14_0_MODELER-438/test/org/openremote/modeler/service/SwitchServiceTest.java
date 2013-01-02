package org.openremote.modeler.service;

import java.util.Collection;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Protocol;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorCommandRef;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchCommandOffRef;
import org.openremote.modeler.domain.SwitchCommandOnRef;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SwitchServiceTest {
   private SwitchService service = null;
   private DeviceCommandService deviceCommandService;

   private SensorService sensorService = null;
   private UserService userService = null;
   
   private long createdSwitchId;
   
   @BeforeClass
   public void setUp() {
      userService = (UserService)SpringTestContext.getInstance().getBean("userService");
      service = (SwitchService) SpringTestContext.getInstance().getBean("switchService");
      deviceCommandService = (DeviceCommandService) SpringTestContext.getInstance().getBean("deviceCommandService");
      sensorService = (SensorService)SpringTestContext.getInstance().getBean("sensorService");
      
      userService.createUserAccount("test", "test", "test@email.com");
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test", "test"));
   }

   @Test
   public void testSaveSwitch() {
      
      Switch swh = new Switch();
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);

      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc");
      deviceCommandService.save(cmd);

      Sensor sensor = new Sensor();
      sensor.setName("Test");
      SensorCommandRef sensorCmdRef = new SensorCommandRef();
      sensorCmdRef.setDeviceCommand(cmd);
      sensor.setSensorCommandRef(sensorCmdRef);
      
      sensorCmdRef.setSensor(sensor);
      sensorService.saveSensor(sensor);
      SwitchSensorRef sensorRef = new SwitchSensorRef();
      sensorRef.setSensor(sensor);
      sensorRef.setSwitchToggle(swh);
      
      swh.setSwitchSensorRef(sensorRef);
      
      swh.setName("testName");

      SwitchCommandOnRef swhOnCmdRef = new SwitchCommandOnRef();
      swhOnCmdRef.setDeviceCommand(cmd);
      swhOnCmdRef.setOnSwitch(swh);

      SwitchCommandOffRef swhOffCmdRef = new SwitchCommandOffRef();
      swhOffCmdRef.setDeviceCommand(cmd);
      swhOffCmdRef.setOffSwitch(swh);

      swh.setSwitchCommandOnRef(swhOnCmdRef);
      swh.setSwitchCommandOffRef(swhOffCmdRef);
      swh.setAccount(userService.getAccount());
      
      service.save(swh);
      Assert.assertTrue(swh.getOid() != 0);
      createdSwitchId = swh.getOid();
      
      Switch switchFromTable = null;
      for (Switch s : service.loadAll()) {
      	if (s.getOid() == swh.getOid()) {
      		switchFromTable = s;
      	}
      }

      Assert.assertEquals(switchFromTable.getName(), "testName");
      Assert.assertEquals(switchFromTable.getSwitchSensorRef().getSensor().getName(), "Test");
      Assert.assertEquals(switchFromTable.getSwitchCommandOnRef().getDeviceCommand().getName(), "testLirc");
      Assert.assertEquals(switchFromTable.getSwitchCommandOffRef().getDeviceCommand().getName(), "testLirc");
   }

   @Test(dependsOnMethods = "testSaveSwitch")
   public void testUpdate() {
      Switch swh = service.loadById(createdSwitchId);
      Protocol protocol = new Protocol();
      protocol.setType(Constants.INFRARED_TYPE);
      
      DeviceCommand cmd = new DeviceCommand();
      cmd.setProtocol(protocol);
      cmd.setName("testLirc2");
      cmd.setDevice(swh.getDevice());
      deviceCommandService.save(cmd);
      
      Sensor sensor = new Sensor();
      sensor.setName("SensorAfterUpdate");      
      sensor.setDevice(swh.getDevice());
      sensor.setAccount(swh.getAccount());      
      SensorCommandRef sensorCmdRef = new SensorCommandRef();
      sensorCmdRef.setDeviceCommand(cmd);
      sensor.setSensorCommandRef(sensorCmdRef);
      sensorCmdRef.setSensor(sensor);
      sensorService.saveSensor(sensor);
      
      SwitchSensorRef sensorRef = new SwitchSensorRef();
      sensorRef.setSensor(sensor);
      sensorRef.setSwitchToggle(swh);      
      swh.setSwitchSensorRef(sensorRef);
      
      SwitchCommandOnRef swhOnCmdRef = new SwitchCommandOnRef();
      swhOnCmdRef.setDeviceCommand(cmd);
      swhOnCmdRef.setOnSwitch(swh);
      swh.setSwitchCommandOnRef(swhOnCmdRef);
      
      SwitchCommandOffRef swhOffCmdRef = new SwitchCommandOffRef();
      swhOffCmdRef.setDeviceCommand(cmd);
      swhOffCmdRef.setOffSwitch(swh);      
      swh.setSwitchCommandOffRef(swhOffCmdRef);

      swh.setName("testUpdate");
      Collection<Switch> switchs = service.loadAll();
      long originalNumberOfSwitches = switchs.size();
      
      service.update(swh);
      
      switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), originalNumberOfSwitches);
      for (Switch s : switchs) {
         if (s.getOid() == createdSwitchId) {
            Assert.assertEquals(s.getName(), "testUpdate");
            Assert.assertTrue(s.getSwitchCommandOffRef().getDeviceCommand().getName().equals("testLirc2"));
            Assert.assertTrue(s.getSwitchCommandOnRef().getDeviceCommand().getName().equals("testLirc2"));
            Assert.assertTrue(s.getSwitchSensorRef().getSensor().getName().equals("SensorAfterUpdate"));
            break;
         }
      }
   }

   @Test(dependsOnMethods = "testSaveSwitch")
   public void testLoadAll() {
      Collection<Switch> switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), 2);
   }

   @Test(dependsOnMethods = "testUpdate")
   public void testDelete() {
     Collection<Switch> switchs = service.loadAll();
     long originalNumberOfSwitches = switchs.size();

      Switch swh = new Switch();
      swh.setOid(createdSwitchId);
      service.delete(1);
      
      switchs = service.loadAll();
      Assert.assertEquals(switchs.size(), originalNumberOfSwitches - 1);
   }
}
