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

/**
 * 
 * @author javen
 *
 */
public class ScreenSlider extends ScreenComponent {
   
   private static final String DEFAULT_HORIZONTAL_MIN_IMAGE = "./resources/images/custom/slider/min.png";
   private static final String DEFAULT_HORIZONTAL_MINTRACK_IMAGE = "./resources/images/custom/slider/minTrack.png";
   private static final String DEFAULT_HORIZONTAL_THUMB_IMAGE = "./resources/images/custom/slider/thumb.png";
   private static final String DEFAULT_HORIZONTAL_MAXTRACK_IMAGE = "./resources/images/custom/slider/maxTrack.png";
   private static final String DEFAULT_HORIZONTAL_MAX_IMAGE = "./resources/images/custom/slider/max.png";
   
   private static final String DEFAULT_VERTICAL_MIN_IMAGE = "./resources/images/custom/slider/vmin.png";
   private static final String DEFAULT_VERTICAL_MINTRACK_IMAGE = "./resources/images/custom/slider/vminTrack.png";
   private static final String DEFAULT_VERTICAL_THUMB_IMAGE = "./resources/images/custom/slider/vthumb.png";
   private static final String DEFAULT_VERTICAL_MAXTRACK_IMAGE = "./resources/images/custom/slider/vmaxTrack.png";
   private static final String DEFAULT_VERTICAL_MAX_IMAGE = "./resources/images/custom/slider/vmax.png";
   

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
      
      if(uiSlider.isVertical()) {
         this.addVerticalStyle();
         updateVerticalImages();
      } else {
        this.addHorizontalStyle();
        updateHorizontaoImages();
      }
      add(minImage);
      add(minTrackImage);
      add(thumbImage);
      add(maxTrackImage);
      add(maxImage);
      this.layout();
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
   }

   public void setMinTrackImage(String imageURL) {
      uiSlider.setMinTrackImage(new ImageSource(imageURL));
      minTrackImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setThumbImage(String imageURL) {
      uiSlider.setThumbImage(new ImageSource(imageURL));
      thumbImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setMaxTrackImage(String imageURL) {
      uiSlider.setMaxTrackImage(new ImageSource(imageURL));
      maxTrackImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
   }

   public void setMaxImage(String imageURL) {
      uiSlider.setMaxImage(new ImageSource(imageURL));
      maxImage.setStyleAttribute("backgroundImage", "url(" + imageURL + ")");
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
         toVertical(true);
         updateVerticalImages();
      } else {
        toHorizontal(true);
        updateHorizontaoImages();
      }
      this.getScreenCanvas().setSizeToDefault(uiSlider);
      this.layout();
   }
   
   private void toVertical(boolean initialized) {
      //1, remove the style for horizontal slider. 
      this.removeHorizontalStyle();
      //2, add the style for vertical slider. 
      this.addVerticalStyle();
   }
   
   
   private void toHorizontal(boolean initialized) {
      //1, remove the style for vertical slider.
      this.removeVerticalStyle();
      //2, add the style for horizontal slider.
      this.addHorizontalStyle();
   }
   
   private void removeHorizontalStyle() {
      minImage.removeStyleName("sliderMinImage");
      minTrackImage.removeStyleName("sliderMinTrackImage");
      thumbImage.removeStyleName("sliderThumbImage");
      maxTrackImage.removeStyleName("sliderMaxTrackImage");
      maxImage.removeStyleName("sliderMaxImage");
   }
   
   private void addHorizontalStyle () {
      minImage.addStyleName("sliderMinImage");
      minTrackImage.addStyleName("sliderMinTrackImage");
      thumbImage.addStyleName("sliderThumbImage");
      maxTrackImage.addStyleName("sliderMaxTrackImage");
      maxImage.addStyleName("sliderMaxImage");
   }
   
   private void removeVerticalStyle() {
      minImage.removeStyleName("vsliderMinImage");
      minTrackImage.removeStyleName("vsliderMinTrackImage");
      thumbImage.removeStyleName("vsliderThumbImage");
      maxTrackImage.removeStyleName("vsliderMaxTrackImage");
      maxImage.removeStyleName("vsliderMaxImage");
   }
   
   private void addVerticalStyle() {
      minImage.addStyleName("vsliderMinImage");
      minTrackImage.addStyleName("vsliderMinTrackImage");
      thumbImage.addStyleName("vsliderThumbImage");
      maxTrackImage.addStyleName("vsliderMaxTrackImage");
      maxImage.addStyleName("vsliderMaxImage");
   }
   
   private void updateVerticalImages() {
      if(!isMinImageUploaded()) {
         setMinImage(DEFAULT_VERTICAL_MIN_IMAGE);
      }
      if(!isMinTrackImageUploaded()) {
         setMinTrackImage(DEFAULT_VERTICAL_MINTRACK_IMAGE);
      }
      if(!isThumbUploaded()) {
         setThumbImage(DEFAULT_VERTICAL_THUMB_IMAGE);
      }
      if(!isMaxTrackImageUploaded()) {
         setMaxTrackImage(DEFAULT_VERTICAL_MAXTRACK_IMAGE);
      }
      if(!isMaxImageUploaded()) {
         setMaxImage(DEFAULT_VERTICAL_MAX_IMAGE);
      }
   }
   
   private void updateHorizontaoImages() {
      if(!isMinImageUploaded()) {
         setMinImage(DEFAULT_HORIZONTAL_MIN_IMAGE);
      }
      if(!isMinTrackImageUploaded()) {
         setMinTrackImage(DEFAULT_HORIZONTAL_MINTRACK_IMAGE);
      }
      if(!isThumbUploaded()) {
         setThumbImage(DEFAULT_HORIZONTAL_THUMB_IMAGE);
      }
      if(!isMaxTrackImageUploaded()) {
         setMaxTrackImage(DEFAULT_HORIZONTAL_MAXTRACK_IMAGE);
      }
      if(!isMaxImageUploaded()) {
         setMaxImage(DEFAULT_HORIZONTAL_MAX_IMAGE);
      }
   }
   
   
   private boolean isMinImageUploaded() {
      ImageSource minImageSource = uiSlider.getMinImage();
      if (minImageSource != null && minImageSource.getSrc() != null) {
         String imageURL = uiSlider.getMinImage().getSrc();
         return !(DEFAULT_HORIZONTAL_MIN_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MIN_IMAGE.equals(imageURL)); 
      }
      return false;
   }
   
   private boolean isMinTrackImageUploaded() {
      ImageSource minTrackImageSource = uiSlider.getMinTrackImage();
         if (minTrackImageSource != null && minTrackImageSource.getSrc() != null) {
         String imageURL = uiSlider.getMinTrackImage().getSrc();
         return !(DEFAULT_HORIZONTAL_MINTRACK_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MINTRACK_IMAGE.equals(imageURL));
      }
      return false;
   }
   
   private boolean isThumbUploaded() {
      ImageSource thumbImageSource = uiSlider.getThumbImage();
      if (thumbImageSource !=null && thumbImageSource.getSrc() != null) {
         String imageURL = uiSlider.getThumbImage().getSrc();
         return !(DEFAULT_HORIZONTAL_THUMB_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_THUMB_IMAGE.equals(imageURL)); 
      } 
      return false;
   }
   
   private boolean isMaxTrackImageUploaded() {
      ImageSource maxTrackImageSource = uiSlider.getMaxTrackImage();
      if (maxTrackImageSource != null && maxTrackImageSource.getSrc() != null) {
         String imageURL = uiSlider.getMaxTrackImage().getSrc();
         return !(DEFAULT_HORIZONTAL_MAXTRACK_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MAXTRACK_IMAGE.equals(imageURL)); 
      }
      return false;
   }
   
   private boolean isMaxImageUploaded() {
      ImageSource maxImageSource = uiSlider.getMaxImage();
      if (maxImageSource != null && maxImageSource.getSrc() != null) {
         String imageURL = uiSlider.getMaxImage().getSrc();
         return !(DEFAULT_HORIZONTAL_MAX_IMAGE.equals(imageURL) || DEFAULT_VERTICAL_MAX_IMAGE.equals(imageURL)); 
      }
      return false;
   }

   public Boolean isVertical() {
      return uiSlider.isVertical();
   }
}
