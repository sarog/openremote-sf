package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorRefItem;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SensorService;

public class SensorServiceImpl extends BaseAbstractService<Sensor> implements SensorService {

   public Boolean deleteSensor(long id) {
      Sensor sensor = super.loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(SensorRefItem.class);
      List<SensorRefItem> sensorRefItems = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("sensor", sensor)));
      if (sensorRefItems.size() > 0) {
         return false;
      } else {
         genericDAO.delete(sensor);
      }
      return true;
   }

   public List<Sensor> loadAll(Account account) {
      return account.getSensors();
   }

   public Sensor saveSensor(Sensor sensor) {
      genericDAO.save(sensor);
      return sensor;
   }

   public Sensor updateSensor(Sensor sensor) {
      Sensor old = genericDAO.loadById(Sensor.class, sensor.getOid());
//      genericDAO.delete(old.getDeviceCommandRef());
//      old.setDeviceCommandRef(sensor.getDeviceCommandRef());`
      old.setName(sensor.getName());
       return old;
   }

   public Sensor loadById(long id) {
      Sensor sensor = genericDAO.getById(Sensor.class, id);
      if(sensor instanceof CustomSensor) {
         Hibernate.initialize(((CustomSensor)sensor).getStates());
      }
      return sensor;
   }

}
