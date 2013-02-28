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


public abstract class ScreenSlider extends ScreenComponent {

   private UISlider uiSlider;

   protected LayoutContainer minImage = new LayoutContainer();
   protected LayoutContainer minTrackImage = new LayoutContainer();
   protected LayoutContainer thumbImage = new LayoutContainer();
   protected LayoutContainer maxTrackImage = new LayoutContainer();
   protected LayoutContainer maxImage = new LayoutContainer();

   public ScreenSlider(ScreenCanvas screenCanvas) {
      super(screenCanvas);
   }

   public ScreenSlider(ScreenCanvas screenCanvas, UISlider uiSlider) {
      this(screenCanvas);
      this.uiSlider = uiSlider;
      setDefault();
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

   public abstract void setDefault();

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
}
