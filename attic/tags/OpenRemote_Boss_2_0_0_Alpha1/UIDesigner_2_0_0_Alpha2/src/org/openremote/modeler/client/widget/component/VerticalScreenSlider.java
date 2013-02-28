package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UISlider;

import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

public class VerticalScreenSlider extends ScreenSlider {

   public VerticalScreenSlider(ScreenCanvas screenCanvas) {
      super(screenCanvas);
   }

   public VerticalScreenSlider(ScreenCanvas canvas, UISlider uiComponent) {
      super(canvas,uiComponent);
   }

   @Override
   public void setDefault() {
      super.setMinImage("./resources/images/custom/slider/vmin.png");
      super.setMinTrackImage("./resources/images/custom/slider/vminTrack.png");
      super.setThumbImage("./resources/images/custom/slider/vthumb.png");
      super.setMaxTrackImage("./resources/images/custom/slider/vmaxTrack.png");
      super.setMaxImage("./resources/images/custom/slider/vmax.png");
      
      setLayout(new FlowLayout());
      minImage.addStyleName("vsliderMinImage");
      minTrackImage.addStyleName("vsliderMinTrackImage");
      thumbImage.addStyleName("vsliderThumbImage");
      maxTrackImage.addStyleName("vsliderMaxTrackImage");
      maxImage.addStyleName("vsliderMaxImage");
   }
   
   
}
