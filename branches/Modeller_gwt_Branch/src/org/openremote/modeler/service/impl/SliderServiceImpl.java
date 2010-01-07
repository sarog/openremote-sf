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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderSensorRef;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SliderService;

public class SliderServiceImpl extends BaseAbstractService<Slider>implements SliderService{
   
   @Override
   public void delete(long id) {
      Slider slider = super.loadById(id);
      DetachedCriteria criteria = DetachedCriteria.forClass(SliderSensorRef.class);
      List<SliderSensorRef> sliderSensorRefs = genericDAO.findByDetachedCriteria(criteria.add(Restrictions.eq("slider", slider)));
      genericDAO.deleteAll(sliderSensorRefs);
      genericDAO.delete(slider);
   }

   @Override
   public List<Slider> loadAll() {
      List<Slider> sliders = genericDAO.loadAll(Slider.class);
      Hibernate.initialize(sliders);
      return sliders;
   }

   @Override
   public void save(Slider slider) {
      genericDAO.save(slider);
   }

   @Override
   public void update(Slider slider) {
      Slider oldSlider = genericDAO.loadById(Slider.class, slider.getOid());
      oldSlider.setName(slider.getName());
   }
}
