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

import org.openremote.modeler.domain.UICommand;

@SuppressWarnings("serial")
public class UISwitch extends UIControl {

   private UImage onImage;
   private UImage offImage;
   private UICommand onCommand;
   private UICommand offCommand;
   private UICommand statusCommand;
   
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
   
   
}
