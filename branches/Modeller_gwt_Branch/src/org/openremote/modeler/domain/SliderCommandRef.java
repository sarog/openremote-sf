package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("SLIDER_CMD_REF")
public class SliderCommandRef extends CommandRefItem {

   private Slider slider;
   
   public SliderCommandRef(){}
   
   public SliderCommandRef(Slider slider){
      this.slider = slider;
   }
   @OneToOne
   @JoinColumn(name = "slider_oid")
   public Slider getSlider() {
      return slider;
   }

   public void setSlider(Slider slider) {
      this.slider = slider;
   }
   
   
}
