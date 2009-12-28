package org.openremote.modeler.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.SliderService;

public class SliderServiceImpl extends BaseAbstractService<Slider>implements SliderService{
   
   @Override
   public void delete(Slider slider) {
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
