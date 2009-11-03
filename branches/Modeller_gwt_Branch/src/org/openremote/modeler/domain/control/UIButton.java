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
package org.openremote.modeler.domain.control;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.UICommand;

@SuppressWarnings("serial")
public class UIButton extends UIControl {

   /** The label. */
   private String name = "Button";

   private boolean repeate;
   
   private UImage image;
   
   private UImage pressImage;
   
   private Navigate navigate;

   /** The ui command. */
   private UICommand uiCommand;

   /**
    * Instantiates a new uI button.
    */
   public UIButton() {
      super();
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

   public UImage getImage() {
      return image;
   }

   public UImage getPressImage() {
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

   public void setImage(UImage image) {
      this.image = image;
   }

   public void setPressImage(UImage pressImage) {
      pressImage.setState("onPress");
      this.pressImage = pressImage;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }

   @Override
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if(uiCommand != null) {
         commands.add(uiCommand);
      }
      return commands;
   }

   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      if (image != null && image.getSrc() != null) {
         String imageSrc = image.getSrc();
         image.setSrc(relativeSessionFolderPath + imageSrc.substring(imageSrc.lastIndexOf("/") + 1));
      }
      if (pressImage != null && pressImage.getSrc() != null) {
         String pressImageSrc = pressImage.getSrc();
         pressImage.setSrc(relativeSessionFolderPath + pressImageSrc.substring(pressImageSrc.lastIndexOf("/") + 1));
      }
   }

   @Transient
   @Override
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <button id=\"" + getOid() + "\" name=\"" + getName() + "\"");
      if (repeate) {
         xmlContent.append(" repeat=\"" + repeate + "\"");
      }
      xmlContent.append(">\n");
      if (image != null && image.getSrc() != null) {
         xmlContent.append("          <image src=\"" + image.getSrc() + "\" />\n");
      }
      if (pressImage != null && pressImage.getSrc() != null) {
         xmlContent.append("          <image src=\"" + pressImage.getSrc() + "\" state=\"onPress\" />\n");
      }
      if (navigate != null) {
         xmlContent.append("          <navigate");
         if (navigate.getToGroup() != -1) {
            xmlContent.append(" toGroup=\"" + navigate.getToGroup() + "\"");
         }
         if (navigate.getToScreen() != -1) {
            xmlContent.append(" toScreen=\"" + navigate.getToScreen() + "\"");
         }
         xmlContent.append(" />\n");
      }
      xmlContent.append("        </button>\n");
      return xmlContent.toString();
   }

}
