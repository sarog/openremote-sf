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
package org.openremote.web.console.client.event;

import org.openremote.web.console.client.gxtextends.ImageSlider;

import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.google.gwt.user.client.Event;

/**
 * The Class ImageSliderEvent is used by {@link ImageSlider}.
 */
public class ImageSliderEvent extends BoxComponentEvent {


   private int newValue = -1;
   private int oldValue = -1;
   private ImageSlider slider;

   public ImageSliderEvent(ImageSlider slider) {
     super(slider);
     this.slider = slider;
   }
   
   public ImageSliderEvent(ImageSlider slider, Event event) {
     super(slider, event);
     this.slider = slider;
   }

   /**
    * Returns the new value.
    * 
    * @return the new value
    */
   public int getNewValue() {
     return newValue;
   }

   /**
    * Returns the old value.
    * 
    * @return the old value
    */
   public int getOldValue() {
     return oldValue;
   }

   /**
    * Returns the source slider.
    * 
    * @return the slider
    */
   public ImageSlider getSlider() {
     return slider;
   }

   /**
    * Sets the new value.
    * 
    * @param newValue the new value
    */
   public void setNewValue(int newValue) {
     this.newValue = newValue;
   }

   /**
    * Sets the old value.
    * 
    * @param oldValue the old value
    */
   public void setOldValue(int oldValue) {
     this.oldValue = oldValue;
   }

   /**
    * Sets the source slider.
    * 
    * @param slider the slider
    */
   public void setSlider(ImageSlider slider) {
     this.slider = slider;
   }


}
