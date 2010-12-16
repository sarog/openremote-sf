/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.beehive.api.service.impl;

import org.openremote.beehive.api.dto.modeler.SliderDTO;
import org.openremote.beehive.api.service.SliderService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.modeler.Slider;

public class SliderServiceImpl extends BaseAbstractService<Slider> implements SliderService {

   public void deleteById(long sliderId) {
      Slider slider = super.loadById(sliderId);
      genericDAO.delete(slider);
   }

   public SliderDTO save(SliderDTO sliderDTO, long accountId) {
      Account account = genericDAO.loadById(Account.class, accountId);
      Slider slider = sliderDTO.toSlider();
      slider.setAccount(account);
      genericDAO.save(slider);
      
      return slider.toDTO();
   }

   public Slider update(SliderDTO sliderDTO) {
      Slider oldSlider = genericDAO.loadById(Slider.class, sliderDTO.getId());
      oldSlider.setName(sliderDTO.getName());
      
      if (oldSlider.getSliderSensorRef() != null) {
         genericDAO.delete(oldSlider.getSliderSensorRef());
      }
      if (sliderDTO.getSliderSensorRef() == null) {
         oldSlider.setSliderSensorRef(null);
      } else {
         oldSlider.setSliderSensorRef(sliderDTO.getSliderSensorRef().toSliderSensorRef(oldSlider));
      }

      if (oldSlider.getSetValueCmd() != null) {
         genericDAO.delete(oldSlider.getSetValueCmd());
      }
      if (sliderDTO.getSetValueCmd() == null) {
         oldSlider.setSetValueCmd(null);
      } else {
         oldSlider.setSetValueCmd(sliderDTO.getSetValueCmd().toSliderCommandRef(oldSlider));
      }
      
      return oldSlider;
   }

}
