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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.ColorPickerPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.ColorPicker;
import org.openremote.modeler.domain.component.ImageSource;

import com.google.gwt.user.client.ui.Image;

/**
 * It display as a image,can upload image and set command in its property form.
 */
public class ScreenColorPicker extends ScreenComponent {

   /** Display the image. */
   private Image image = new Image();
   
   /** The colorpicker entity to configure. */
   private ColorPicker colorPicker = new ColorPicker();
   
   public ScreenColorPicker(ScreenCanvas screenCanvas, ColorPicker colorPicker, WidgetSelectionUtil widgetSelectionUtil) {
      super(screenCanvas, widgetSelectionUtil);
      this.colorPicker = colorPicker;
      initial();
   }

   private void initial() {
      image.setStyleName("screen-image");
      if (!"".equals(colorPicker.getImage().getSrc().trim())) {
         image.setUrl(colorPicker.getImage().getSrc());
      }
      add(image);
      setStyleAttribute("overflow", "hidden");
      layout();
   }
   
   public ColorPicker getColorPicker() {
      return colorPicker;
   }

   public void setColorPicker(ColorPicker colorPicker) {
      this.colorPicker = colorPicker;
   }

   public void setImageSource(ImageSource imageSource) {
      colorPicker.setImage(imageSource);
      if (imageSource != null) {
         image.setUrl(imageSource.getSrc());
      }
   }
   
   public void setUICommand(UICommand uiCommand) {
      if (colorPicker != null) {
         colorPicker.setUiCommand(uiCommand);
      }
   }
   
   public UICommand getUICommand() {
      if (colorPicker != null) {
         return colorPicker.getUiCommand();
      }
      return null;
   }
   
   @Override
   public String getName() {
      return colorPicker.getName();
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new ColorPickerPropertyForm(this, colorPicker, widgetSelectionUtil);
   }
}
