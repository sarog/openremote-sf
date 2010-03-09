/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.domain.component;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.UICommand;

import flexjson.JSON;

@SuppressWarnings("serial")
public class UISwitch extends UIControl implements SensorOwner {

   private ImageSource onImage;
   private ImageSource offImage;
   private Switch switchCommand;

   public UISwitch() {
      super();
   }

   public UISwitch(UISwitch swh) {
      this.setOid(swh.getOid());
      this.onImage = swh.onImage;
      this.offImage = swh.offImage;
      this.switchCommand = swh.switchCommand;
   }

   public UISwitch(long id) {
      super(id);
   }

   public ImageSource getOnImage() {
      return onImage;
   }

   public ImageSource getOffImage() {
      return offImage;
   }

   public void setOnImage(ImageSource onImage) {
      this.onImage = onImage;
   }

   public void setOffImage(ImageSource offImage) {
      this.offImage = offImage;
   }

   public Switch getSwitchCommand() {
      return switchCommand;
   }

   public void setSwitchCommand(Switch switchCommand) {
      this.switchCommand = switchCommand;
   }

   @Override
   public String getName() {
      return "Switch";
   }

   @JSON(include=false)
   @Override
   public List<UICommand> getCommands() {
      List<UICommand> commands = new ArrayList<UICommand>();
      if (switchCommand != null) {
         if (switchCommand.getSwitchCommandOnRef() != null) {
            commands.add(switchCommand.getSwitchCommandOnRef());
         }
         if (switchCommand.getSwitchCommandOffRef() != null) {
            commands.add(switchCommand.getSwitchCommandOffRef());
         }
      }
      return commands;
   }

   @Transient
   @Override
   @JSON(include=false)
   public String getPanelXml() {
      StringBuffer xmlContent = new StringBuffer();
      xmlContent.append("        <switch id=\"" + getOid() + "\">\n");
      if (getSensor() != null) {
         xmlContent.append("<link type=\"sensor\" ref=\"" + getSensor().getOid() + "\">");
         if (onImage != null && onImage.getSrc() != null) {
            xmlContent.append("          <state name=\"on\" value=\"" + onImage.getImageFileName() + "\"/>\n");
         }
         if (offImage != null && offImage.getSrc() != null) {
            xmlContent.append("          <state name=\"off\" value=\"" + offImage.getImageFileName() + "\"/>\n");
         }
         xmlContent.append("</link>");
      }
      xmlContent.append("        </switch>\n");
      return xmlContent.toString();
   }

   @Override
   public Sensor getSensor() {
      if (switchCommand != null && switchCommand.getSwitchSensorRef() != null) {
         return switchCommand.getSwitchSensorRef().getSensor();
      }
      return null;
   }
}
