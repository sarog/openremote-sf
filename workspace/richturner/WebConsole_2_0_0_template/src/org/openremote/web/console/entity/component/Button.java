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
package org.openremote.web.console.entity.component;

import org.openremote.web.console.entity.Link;
import org.openremote.web.console.entity.Navigate;

/**
 * The button can send command to controller and navigate to, has default image and pressed image.
 * If it has set repeat, it would repeat send command.
 */
@SuppressWarnings("serial")
public class Button extends Component {

   private String name;
   private boolean hasControlCommand;
   private boolean repeat;
   private Image defaultImage;
   private Image pressedImage;
   private Navigate navigate;
   
   public Button() {
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
   	this.name = name;
   }
   
   public boolean getHasControlCommand() {
      return hasControlCommand;
   }
   
   public void setHasControlCommand(boolean hasControlCommand) {
   	this.hasControlCommand = hasControlCommand;
   }
   
   public boolean getRepeat() {
      return repeat;
   }

   public void setRepeat(boolean repeat) {
   	this.repeat = repeat;
   }
   
   public Image getDefaultImage() {
      return defaultImage;
   }
   
   public void setDefaultImage(Image defaultImage) {
   	this.defaultImage = defaultImage;
   }

   public Image getPressedImage() {
      return pressedImage;
   }

   public void setPressedImage(Image pressedImage) {
   	this.pressedImage = pressedImage;
   }
   
   public Navigate getNavigate() {
      return navigate;
   }
   
   public void setNavigate(Navigate navigate) {
   	this.navigate = navigate;
   }
}
