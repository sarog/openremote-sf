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
public class UISwitch extends UIControl {

   private UImage onImage;
   private UImage offImage;
   private UICommand onCommand;
   private UICommand offCommand;
   private UICommand statusCommand;
   public UISwitch() {
      super();
   }
   
   public UISwitch(long id) {
      super(id);
   }
   
   public UImage getOnImage() {
      return onImage;
   }
   public UImage getOffImage() {
      return offImage;
   }
   public UICommand getOnCommand() {
      return onCommand;
   }
   public UICommand getOffCommand() {
      return offCommand;
   }
   public UICommand getStatusCommand() {
      return statusCommand;
   }
   public void setOnImage(UImage onImage) {
      onImage.setState("ON");
      this.onImage = onImage;
   }
   public void setOffImage(UImage offImage) {
      offImage.setState("OFF");
      this.offImage = offImage;
   }
   public void setOnCommand(UICommand onCommand) {
      this.onCommand = onCommand;
   }
   public void setOffCommand(UICommand offCommand) {
      this.offCommand = offCommand;
   }
   public void setStatusCommand(UICommand statusCommand) {
      this.statusCommand = statusCommand;
   }
   
   @Override
   public String getName() {
      return "Switch";
   }

   @Override
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if (onCommand != null) {
         commands.add(onCommand);
      }
      if (offCommand != null) {
         commands.add(offCommand);
      }
      if (statusCommand != null) {
         commands.add(statusCommand);
      }
      return commands;
   }
   
   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {
      if (onImage != null && onImage.getSrc() != null) {
         String onImageSrc = onImage.getSrc();
         onImage.setSrc(relativeSessionFolderPath + onImageSrc.substring(onImageSrc.lastIndexOf("/") + 1));
      }
      if (offImage != null && offImage.getSrc() != null) {
         String offImageSrc = offImage.getSrc();
         offImage.setSrc(relativeSessionFolderPath + offImageSrc.substring(offImageSrc.lastIndexOf("/") + 1));
      }
   }

   @Transient
   @Override
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <switch id=\"" + getOid() + "\">\n");
      if (onImage != null && onImage.getSrc() != null) {
         xmlContent.append("          <image src=\"" + onImage.getSrc() + "\" state=\"" + onImage.getState() + "\" />\n");
      }
      if (offImage != null && offImage.getSrc() != null) {
         xmlContent.append("          <image src=\"" + offImage.getSrc() + "\" state=\"" + offImage.getState() + "\" />\n");
      }
      xmlContent.append("        </switch>\n");
      return xmlContent.toString();
   }
}
