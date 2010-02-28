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

import org.openremote.modeler.client.widget.propertyform.ImagePropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UIImage;

import com.google.gwt.user.client.ui.Image;

/**
 * The Class ScreenButton. It display as a style box, can be adjust size.
 */
public class ScreenImage extends ScreenComponent {


   /** The btnTable center text. */
   protected Image image = new Image();

   private UIImage uiImage = new UIImage();

   public ScreenImage(ScreenCanvas canvas, UIImage uiImage) {
      super(canvas);
      this.uiImage = uiImage;
      initial();
   }

   /**
    * Initial.
    * 
    */
   protected void initial() {
      image.setStyleName("screen-image");
      if (!"".equals(uiImage.getImageSource().getSrc().trim())) {
         image.setUrl(uiImage.getImageSource().getSrc());
      }
      add(image);
      layout();
   }

   public UIImage getUiImage() {
      return uiImage;
   }

   public void setUiImage(UIImage uiImage) {
      this.uiImage = uiImage;
   }

   public void setImageSource(ImageSource imageURL) {
      uiImage.setImageSource(imageURL);
      image.setUrl(imageURL.getSrc());
   }

   @Override
   public void setName(String name) {
      return;
   }

   @Override
   public String getName() {
      return uiImage.getName();
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new ImagePropertyForm(this);
   }

}
