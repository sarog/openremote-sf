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
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.UICommand;

import flexjson.JSON;

@SuppressWarnings("serial")
public class UIButton extends UIControl {

   /** The label. */
   private String name = "Button";

   private boolean repeate;
   
   private ImageSource image;
   
   private ImageSource pressImage;
   
   private Navigate navigate = new Navigate();

   /** The ui command. */
   private UICommand uiCommand;

   /**
    * Instantiates a new uI button.
    */
   public UIButton() {
      super();
   }
   public UIButton(UIButton btn) {
      this.setOid(btn.getOid());
      this.name = btn.name;
      this.repeate = btn.repeate;
      this.image = btn.image;
      this.navigate = btn.navigate;
      this.pressImage = btn.pressImage;
      this.uiCommand = btn.uiCommand;
   }
   /**
    * Instantiates a new uI button.
    * 
    * @param id the id
    */
   public UIButton(long id) {
      super(id);
   }

   /**
    * Gets the ui command.
    * 
    * @return the ui command
    */
   public UICommand getUiCommand() {
      return uiCommand;
   }

   /**
    * Sets the ui command.
    * 
    * @param uiCommand the new ui command
    */
   public void setUiCommand(UICommand uiCommand) {
      this.uiCommand = uiCommand;
   }
   @Override
   public String getName() {
      return name;
   }

   public boolean isRepeate() {
      return repeate;
   }

   public ImageSource getImage() {
      return image;
   }

   public ImageSource getPressImage() {
      return pressImage;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setRepeate(boolean repeate) {
      this.repeate = repeate;
   }

   public void setImage(ImageSource image) {
      this.image = image;
   }

   public void setPressImage(ImageSource pressImage) {
      this.pressImage = pressImage;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }

   @Override
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
      xmlContent.append("        <button id=\"" + getOid() + "\" name=\"" + getName() + "\"");
      if (uiCommand != null) {
         xmlContent.append(" hasControlCommand=\"true\"");
      }
      if (repeate) {
         xmlContent.append(" repeat=\"" + repeate + "\"");
      }
      xmlContent.append(">\n");
      if (image != null && image.getImageFileName() != null) {
         xmlContent.append("          <default>\n");
         xmlContent.append("          <image src=\"" + image.getImageFileName() + "\" />\n");
         xmlContent.append("          </default>\n");
      }
      if (pressImage != null && pressImage.getImageFileName() != null) {
         xmlContent.append("          <pressed>\n");
         xmlContent.append("          <image src=\"" + pressImage.getImageFileName() + "\" />\n");
         xmlContent.append("          </pressed>\n");
      }
      if (navigate.isSet()) {
         xmlContent.append("          <navigate");
         if (navigate.getToGroup() != -1) {
            xmlContent.append(" toGroup=\"" + navigate.getToGroup() + "\"");
            if (navigate.getToScreen() != -1) {
               xmlContent.append(" toScreen=\"" + navigate.getToScreen() + "\"");
            }
         } else {
            xmlContent.append(" to=\"" + navigate.getToLogical() + "\"");
         }
         xmlContent.append(" />\n");
      }
      xmlContent.append("        </button>\n");
      return xmlContent.toString();
   }

}
