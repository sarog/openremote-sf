package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UISlider;

import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public class HorizontalScreenSlider extends ScreenSlider {

   public HorizontalScreenSlider(ScreenCanvas screenCanvas) {
      super(screenCanvas);
   }

   public HorizontalScreenSlider(ScreenCanvas canvas, UISlider uiSlider) {
      super(canvas,uiSlider);
   }

   @Override
   public void setDefault() {
      super.setMinImage("./resources/images/custom/slider/min.png");
      super.setMinTrackImage("./resources/images/custom/slider/minTrack.png");
      super.setThumbImage("./resources/images/custom/slider/thumb.png");
      super.setMaxTrackImage("./resources/images/custom/slider/maxTrack.png");
      super.setMaxImage("./resources/images/custom/slider/max.png");
      
      setLayout(new FlowLayout());
      minImage.addStyleName("sliderMinImage");
      minTrackImage.addStyleName("sliderMinTrackImage");
      thumbImage.addStyleName("sliderThumbImage");
      maxTrackImage.addStyleName("sliderMaxTrackImage");
      maxImage.addStyleName("sliderMaxImage");
   }
   
   
}
