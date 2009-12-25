package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.domain.Slider;

public interface SliderService {
   void save(Slider slider);
   void update(Slider slider);
   void delete(Slider slider);
   List<Slider> loadAll();
}
