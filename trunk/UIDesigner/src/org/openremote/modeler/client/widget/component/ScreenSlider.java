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
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISlider;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;


public class ScreenSlider extends ScreenComponent {
   private boolean imagesUpdated = false;

   private UISlider uiSlider;

   protected LayoutContainer minImage = new LayoutContainer();
   protected LayoutContainer minTrackImage = new LayoutContainer();
   protected LayoutContainer thumbImage = new LayoutContainer();
   protected LayoutContainer maxTrackImage = new LayoutContainer();
   protected LayoutContainer maxImage = new LayoutContainer();

   public ScreenSlider(ScreenCanvas screenCanvas) {
      super(screenCanvas);
      setLayout(new FlowLayout());
   }

   public ScreenSlider(ScreenCanvas screenCanvas, UISlider uiSlider) {
      this(screenCanvas);
      this.uiSlider = uiSlider;
//      setDefault();
      toHorizontal();
      if(!imagesUpdated) {
         setHorizontalDefaultImages();
      }
      add(minImage);
      add(minTrackImage);
      add(thumbImage);
      add(maxTrackImage);
      add(maxImage);
   }

   public UISlider getUiSlider() {
      return uiSlider;
   }

   public void setUiSlider(UISlider uiSlider) {
      this.uiSlider = uiSlider;
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
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new SliderPropertyForm(this);
   }

   public void setMinImage(String imageURL) {
      ImageSource minImageSource = new ImageSource();
      minImageSource.setSrc(imageURL);
      uiSlider.setMinImage(minImageSource);
      minImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
      imagesUpdated = true;
   }

   public void setMinTrackImage(String imageURL) {
      uiSlider.setMinTrackImage(new ImageSource(imageURL));
      minTrackImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
      imagesUpdated = true;
   }

   public void setThumbImage(String imageURL) {
      uiSlider.setThumbImage(new ImageSource(imageURL));
      thumbImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
      imagesUpdated = true;
   }

   public void setMaxTrackImage(String imageURL) {
      uiSlider.setMaxTrackImage(new ImageSource(imageURL));
      maxTrackImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
      imagesUpdated = true;
   }

   public void setMaxImage(String imageURL) {
      uiSlider.setMaxImage(new ImageSource(imageURL));
      maxImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
      imagesUpdated = true;
   }

   public void setSlider(Slider slider) {
      uiSlider.setSlider(slider);
   }

   public Slider getSlider() {
      return uiSlider.getSlider();
   }
   
   public void setVertical(boolean isVertical) {
      uiSlider.setVertical(isVertical);
      if(isVertical) {
         toVertical();
         if(!imagesUpdated) {
            setVerticalDefaultImages();
         }
      } else {
        toHorizontal();
         if(!imagesUpdated) {
            setHorizontalDefaultImages();
         }
      }
//      setWidth(uiSlider.getPreferredWidth());
//      setHeight(uiSlider.getPreferredHeight());
//      setSize(uiSlider.getPreferredWidth(),uiSlider.getPreferredHeight());
      this.getScreenCanvas().setSizeToDefault(uiSlider);
      this.layout();
   }
   
   private void toVertical() {
      //1, remove the style for horizontal slider. 
      minImage.removeStyleName("sliderMinImage");
      minTrackImage.removeStyleName("sliderMinTrackImage");
      thumbImage.removeStyleName("sliderThumbImage");
      maxTrackImage.removeStyleName("sliderMaxTrackImage");
      maxImage.removeStyleName("sliderMaxImage");
      //2, add the style for vertical slider. 
      minImage.addStyleName("vsliderMinImage");
      minTrackImage.addStyleName("vsliderMinTrackImage");
      thumbImage.addStyleName("vsliderThumbImage");
      maxTrackImage.addStyleName("vsliderMaxTrackImage");
      maxImage.addStyleName("vsliderMaxImage");
   }
   
   private void toHorizontal() {
      //1, remove the style for vertical slider.
      minImage.removeStyleName("vsliderMinImage");
      minTrackImage.removeStyleName("vsliderMinTrackImage");
      thumbImage.removeStyleName("vsliderThumbImage");
      maxTrackImage.removeStyleName("vsliderMaxTrackImage");
      maxImage.removeStyleName("vsliderMaxImage");
      //2, add the style for horizontal slider.
      minImage.addStyleName("sliderMinImage");
      minTrackImage.addStyleName("sliderMinTrackImage");
      thumbImage.addStyleName("sliderThumbImage");
      maxTrackImage.addStyleName("sliderMaxTrackImage");
      maxImage.addStyleName("sliderMaxImage");
   }
   
   private void setVerticalDefaultImages() {
      setMinImage("./resources/images/custom/slider/vmin.png");
      setMinTrackImage("./resources/images/custom/slider/vminTrack.png");
      setThumbImage("./resources/images/custom/slider/vthumb.png");
      setMaxTrackImage("./resources/images/custom/slider/vmaxTrack.png");
      setMaxImage("./resources/images/custom/slider/vmax.png");
      imagesUpdated = false;
   }
   
   private void setHorizontalDefaultImages() {
      setMinImage("./resources/images/custom/slider/min.png");
      setMinTrackImage("./resources/images/custom/slider/minTrack.png");
      setThumbImage("./resources/images/custom/slider/thumb.png");
      setMaxTrackImage("./resources/images/custom/slider/maxTrack.png");
      setMaxImage("./resources/images/custom/slider/max.png");
      imagesUpdated = false;
   }
}
