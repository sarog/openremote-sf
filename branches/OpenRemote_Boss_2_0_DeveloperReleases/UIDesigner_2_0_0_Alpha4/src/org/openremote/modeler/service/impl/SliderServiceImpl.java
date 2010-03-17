/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SliderService;

public class SliderServiceImpl extends BaseAbstractService<Slider>implements SliderService {
   
   @Override
   public void delete(long id) {
      Slider slider = super.loadById(id);
      genericDAO.delete(slider);
   }

   @Override
   public List<Slider> loadAll() {
      List<Slider> sliders = genericDAO.loadAll(Slider.class);
      Hibernate.initialize(sliders);
      return sliders;
   }

   @Override
   public Slider save(Slider slider) {
      genericDAO.save(slider);
      return slider;
   }

   @Override
   public Slider update(Slider slider) {
      Slider oldSlider = genericDAO.loadById(Slider.class, slider.getOid());
      genericDAO.delete(oldSlider.getSliderSensorRef());
      genericDAO.delete(oldSlider.getSetValueCmd());
      oldSlider.setName(slider.getName());
      if(slider.getSetValueCmd().getOid()!=oldSlider.getSetValueCmd().getOid()){
         slider.getSetValueCmd().setSlider(oldSlider);
         oldSlider.setSetValueCmd(slider.getSetValueCmd());
      }
      if(slider.getSliderSensorRef().getOid()!=oldSlider.getSliderSensorRef().getOid()){
         slider.getSliderSensorRef().setSlider(oldSlider);
         oldSlider.setSliderSensorRef(slider.getSliderSensorRef());
      }
      return oldSlider;
   }
}
