package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SensorService;

public class SensorServiceImpl extends BaseAbstractService<Sensor> implements SensorService {

   public void deleteSensor(long id) {
      Sensor sensor = loadById(id);
      genericDAO.delete(sensor);
   }

   public List<Sensor> loadAll(Account account) {
      List<Sensor> sensors = account.getSensors();
      for (Sensor sensor : sensors) {
         if(sensor instanceof CustomSensor) {
            Hibernate.initialize(((CustomSensor)sensor).getStates());
         }
      }
      return sensors;
   }

   public Sensor saveSensor(Sensor sensor) {
      genericDAO.save(sensor);
      return sensor;
   }

   public Sensor updateSensor(Sensor sensor) {
      Sensor old = genericDAO.loadById(Sensor.class, sensor.getOid());
      genericDAO.delete(old.getDeviceCommandRef());
      old.setDeviceCommandRef(sensor.getDeviceCommandRef());
      old.setName(sensor.getName());
      return (Sensor)genericDAO.merge(old);
   }

   public Sensor loadById(long id) {
      return super.loadById(id);
   }

}
