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
package org.openremote.web.console.client.gxtextends;

import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.widget.ScreenSlider;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;

/**
 * The Class ORSlider.
 */
public class ORSlider extends ImageSlider {

   private org.openremote.web.console.domain.Slider slider;
   
   public ORSlider(org.openremote.web.console.domain.Slider slider) {
      super();
      this.slider = slider;
      setMinValue(slider.getMinValue());
      setMaxValue(slider.getMaxValue());
      setIncrement(1);
      if (slider.isVertical()) {
         setVertical(true);
         setHeight(slider.getFrameHeight());
      } else {
         setWidth(slider.getFrameWidth());
      }
      if (slider.isPassive()) {
         setDraggable(false);
         setClickToChange(false);
      }
      String resourcePath = ClientDataBase.appSetting.getResourceRootPath();
      if (slider.getMinImage() != null) {
         setMinImage(resourcePath + slider.getMinImage().getSrc());
      }
      if (slider.getMinTrackImage() != null) {
         setMinTrackImage(resourcePath + slider.getMinTrackImage().getSrc());
      }
      if (slider.getThumbImage() != null) {
         setThumbImage(resourcePath + slider.getThumbImage().getSrc());
      }
      if (slider.getMaxTrackImage() != null) {
         setMaxTrackImage(resourcePath + slider.getMaxTrackImage().getSrc());
      }
      if (slider.getMaxImage() != null) {
         setMaxImage(resourcePath + slider.getMaxImage().getSrc());
      }
   }
   
   @Override
   protected void onDragEnd(DragEvent de) {
      super.onDragEnd(de);
      if (!slider.isPassive()) {
         ((ScreenSlider) getParent()).sendCommand("" + getValue());
      }
   }

   @Override
   protected void onClick(ComponentEvent ce) {
      super.onClick(ce);
      if (!slider.isPassive()) {
         ((ScreenSlider) getParent()).sendCommand("" + getValue());
      }
   }
}
