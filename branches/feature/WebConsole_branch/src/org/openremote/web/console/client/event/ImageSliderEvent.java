package org.openremote.web.console.client.event;

import org.openremote.web.console.client.gxtextends.ImageSlider;

import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.google.gwt.user.client.Event;

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
