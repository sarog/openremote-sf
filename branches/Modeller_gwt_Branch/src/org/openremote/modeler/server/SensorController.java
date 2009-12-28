package org.openremote.modeler.server;

import java.util.List;

import org.openremote.modeler.client.rpc.SensorRPCService;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.UserService;

public class SensorController extends BaseGWTSpringControllerWithHibernateSupport implements SensorRPCService {

   private static final long serialVersionUID = 7122839354773238989L;

   private SensorService sensorService;
   
   private UserService userService;
   
   public Boolean deleteSensor(long id) {
      return sensorService.deleteSensor(id);
   }

   public List<Sensor> loadAll() {
      return sensorService.loadAll(userService.getAccount());
   }

   public Sensor saveSensor(Sensor sensor) {
      sensor.setAccount(userService.getAccount());
      return sensorService.saveSensor(sensor);
   }

   public Sensor updateSensor(Sensor sensor) {
      return sensorService.updateSensor(sensor);
   }

   public void setSensorService(SensorService sensorService) {
      this.sensorService = sensorService;
   }
   
   public void setUserService(UserService userService) {
      this.userService = userService;
   }

   public Sensor getById(long id) {
      return sensorService.loadById(id);
   }
}
