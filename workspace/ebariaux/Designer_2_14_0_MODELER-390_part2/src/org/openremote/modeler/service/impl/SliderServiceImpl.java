/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SliderService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.SliderDetailsDTO;
import org.springframework.transaction.annotation.Transactional;

public class SliderServiceImpl extends BaseAbstractService<Slider> implements SliderService {

   private UserService userService = null;
   private DeviceCommandService deviceCommandService;
   private SensorService sensorService;

   @Override
   public Slider loadById(long id) {
     return genericDAO.getById(Slider.class, id);
   }

   @Override
   @Transactional public void delete(long id) {
      Slider slider = super.loadById(id);
      genericDAO.delete(slider);
   }

   @Override
   public List<Slider> loadAll() {
     DetachedCriteria criteria = DetachedCriteria.forClass(Slider.class);
     criteria.add(Restrictions.eq("account", userService.getAccount()));
     @SuppressWarnings("unchecked")
    List<Slider> result = genericDAO.getHibernateTemplate().findByCriteria(criteria);
      if (result == null || result.size() == 0) {
         return new ArrayList<Slider> ();
      }
      return result;
   }

   @Override
   @Transactional public Slider save(Slider slider) {
      genericDAO.save(slider);
      return slider;
   }

   @Override
   @Transactional public void updateSliderWithDTO(SliderDetailsDTO sliderDTO) {
     Slider slider = loadById(sliderDTO.getOid());
     slider.setName(sliderDTO.getName());

     if (slider.getSliderSensorRef().getSensor().getOid() != sliderDTO.getSensor().getId()) {
       Sensor sensor = sensorService.loadById(sliderDTO.getSensor().getId());
       slider.getSliderSensorRef().setSensor(sensor);
     }
     /*
      * EBR : this is the old code. TODO: have unit tests to validate new works same as old.
      * Note, old allows possibility that existing slider had no sensor or that update wants to remove sensor;
      * this is not allowed and not foreseen by new code.
     if (oldSlider.getSliderSensorRef() != null) {
        genericDAO.delete(oldSlider.getSliderSensorRef());
     }
     if (slider.getSliderSensorRef() == null) {
        oldSlider.setSliderSensorRef(null);
     } else if (slider.getSliderSensorRef() != null) {
        slider.getSliderSensorRef().setSlider(oldSlider);
        oldSlider.setSliderSensorRef(slider.getSliderSensorRef());
     }
     */

     
     if (sliderDTO.getCommand() != null) {
       if (slider.getSetValueCmd() == null || (slider.getSetValueCmd().getDeviceCommand().getOid() != sliderDTO.getCommand().getId())) {
         DeviceCommand dc = deviceCommandService.loadById(sliderDTO.getCommand().getId());
         if (slider.getSetValueCmd() != null) { 
           slider.getSetValueCmd().setDeviceCommand(dc);
         } else {
           slider.setSetValueCmd(dc);
         }
       }
     } else {
       if (slider.getSetValueCmd() != null) {
         genericDAO.delete(slider.getSetValueCmd());
         slider.setSetValueCmd((SliderCommandRef)null);
       }
     }
     /*
      * EBR : this is the old code. TODO: have unit tests to validate new works same as old.
      * Old code seems to replace reference altogether, where new code updates it. TODO: validate which behaviour is appropriate.
      if (slider.getSetValueCmd() == null) {
         if (oldSlider.getSetValueCmd() != null) {
            genericDAO.delete(oldSlider.getSetValueCmd());
         }
         oldSlider.setSetValueCmd(null);
      } else if (slider.getSetValueCmd() != null && oldSlider.getSetValueCmd() != null
            && slider.getSetValueCmd().getOid() != oldSlider.getSetValueCmd().getOid()) {
         genericDAO.delete(oldSlider.getSetValueCmd());
         slider.getSetValueCmd().setSlider(oldSlider);
         oldSlider.setSetValueCmd(slider.getSetValueCmd());
      }
      */

     genericDAO.saveOrUpdate(slider);
   }
   
   public List<Slider> loadSameSliders(Slider slider) {
      List<Slider> result = null;
      DetachedCriteria critera = DetachedCriteria.forClass(Slider.class);
      critera.add(Restrictions.eq("device.oid", slider.getDevice().getOid()));
      critera.add(Restrictions.eq("name", slider.getName()));
      result = genericDAO.findByDetachedCriteria(critera);
      if (result != null) {
         for(Iterator<Slider> iterator = result.iterator();iterator.hasNext();) {
            Slider sld = iterator.next();
            if (!sld.equalsWithoutCompareOid(slider)) {
               iterator.remove();
            }
         }
      }
      return result;
   }

   @Transactional
   public List<Slider> saveAllSliders(List<Slider> sliderList, Account account) {
       for (Slider slider : sliderList) {
           slider.setAccount(account);
           genericDAO.save(slider);
       }
       return sliderList;
   }

   public void setUserService(UserService userService) {
      this.userService = userService;
   }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }
   
}
