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
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.shared.dto.UICommandDTO;

import flexjson.JSON;

/**
 * The color picker contains a image and a command, if click or touch the image on device,
 * can send the current color to controller.
 */
public class ColorPicker extends UIControl implements ImageSourceOwner {

   private static final long serialVersionUID = 8741499727136660767L;

   public static String DEFAULT_COLORPICKER_URL = "image/color.wheel.220X223.png";
   private ImageSource image = new ImageSource(DEFAULT_COLORPICKER_URL);
   private UICommand uiCommand;
   
   private UICommandDTO uiCommandDTO;
   
   public ColorPicker() {
      super();
   }
   public ColorPicker(ColorPicker colorPicker) {
      this.setOid(colorPicker.getOid());
      this.image = colorPicker.image;
      this.uiCommand = colorPicker.uiCommand;
   }
   
   public ImageSource getImage() {
      return image;
   }
   public UICommand getUiCommand() {
      return uiCommand;
   }
   public void setImage(ImageSource image) {
      this.image = image;
   }
   public void setUiCommand(UICommand uiCommand) {
      this.uiCommand = uiCommand;
   }
   
   public UICommandDTO getUiCommandDTO() {
     return uiCommandDTO;
   }

    public void setUiCommandDTO(UICommandDTO uiCommandDTO) {
     this.uiCommandDTO = uiCommandDTO;
   }

   @Override
   public int getPreferredWidth() {
      return 220;
   }

   @Override
   public int getPreferredHeight() {
      return 223;
   }
   
   @Override
   public String getName() {
      return "Color Picker";
   }
   
   @Override
   @JSON(include=false)
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if (uiCommand != null) {
         commands.add(uiCommand);
      }
      return commands;
   }

   @Transient
   @JSON(include = false)
   @Override
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("<colorpicker id=\"" + getOid() + "\" >\n");
      if (this.image != null && ! this.image.isEmpty()) {
         xmlContent.append("<image src=\"" + image.getImageFileName() + "\" />\n");
      }
      xmlContent.append("</colorpicker>\n");
      return xmlContent.toString();
   }

   @Override
   @JSON(include = false)
   public Collection<ImageSource> getImageSources() {
      Collection<ImageSource> imageSources = new ArrayList<ImageSource>(2);
      
      if (this.image != null && ! this.image.isEmpty()) {
         imageSources.add(image);
      }
      return imageSources;
   }

}
