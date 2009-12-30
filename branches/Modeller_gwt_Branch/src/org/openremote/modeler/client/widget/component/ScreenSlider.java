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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.SliderPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UISlider;

public class ScreenSlider extends ScreenComponent {

   private UISlider uiSlider;
   private FlexSliderTable slider = new FlexSliderTable();
   
   public ScreenSlider(ScreenCanvas screenCanvas) {
      super(screenCanvas);
      initial();
   }
   
   public ScreenSlider(ScreenCanvas screenCanvas, UISlider uiSlider) {
      this(screenCanvas);
      this.uiSlider = uiSlider;
      if (uiSlider.isVertical()) {
         slider.setVertical(true);
      }
      if (uiSlider.getMinImage() != null) {
         slider.setMinImageUrl(uiSlider.getMinImage());
         slider.setMinTrackImageUrl(uiSlider.getMinTrackImage());
         slider.setThumbImageUrl(uiSlider.getThumbImage());
         slider.setMaxImageUrl(uiSlider.getMaxImage());
         slider.setMaxTrackImageUrl(uiSlider.getMaxTrackImage());
      }
   }
   
   private void initial() {
      add(slider);
      setSize(150, 20);
   }
   
   public UISlider getUiSlider() {
      return uiSlider;
   }

   public void setUiSlider(UISlider uiSlider) {
      this.uiSlider = uiSlider;
   }

   public FlexSliderTable getSlider() {
      return slider;
   }
   
   public void setVertical(boolean vertical) {
      if (slider.isVertical() != vertical) {
         if (vertical) {
            setSize(20, 150);
         } else {
            setSize(150, 20);
         }
      }
      slider.setVertical(vertical);
   }
   
   @Override
   public String getName() {
      return "Slider";
   }

   @Override
   public void setName(String name) {
   }

   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      slider.setSize("" + width, "" + height);
   }
   
   @Override
   public PropertyForm getPropertiesForm() {
      return new SliderPropertyForm(this);
   }
   
}
