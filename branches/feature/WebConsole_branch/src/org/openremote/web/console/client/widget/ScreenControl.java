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
package org.openremote.web.console.client.widget;

import org.openremote.web.console.client.rpc.AsyncServiceFactory;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.domain.Button;
import org.openremote.web.console.domain.Component;
import org.openremote.web.console.domain.Slider;
import org.openremote.web.console.domain.Switch;

/**
 * The Class ScreenControl is the superclass of that screen component with control command.
 */
public class ScreenControl extends ScreenComponent {

   /**
    * Builds the with control.
    * 
    * @param control the control
    * 
    * @return the screen control
    */
   public static ScreenControl buildWithControl(Component control) {
      ScreenControl screenControl = null;
      if (control instanceof Button) {
         screenControl = new ScreenButton((Button) control);
      } else if (control instanceof Switch) {
         screenControl = new ScreenSwitch((Switch) control);
      } else if (control instanceof Slider) {
         screenControl = new ScreenSlider((Slider) control);
      }
      return screenControl;
   }
   
   /**
    * Send control command to controller.
    * 
    * @param commandType the command type
    * @param callback the callback
    */
   public void sendCommand(String commandType, AsyncSuccessCallback<Void> callback) {
      AsyncServiceFactory.getCommandServiceAsync().sendCommand(
            ClientDataBase.getControlPath() + getComponent().getComponentId() + "/" + commandType, ClientDataBase.userInfo.getUsername(),
            ClientDataBase.userInfo.getPassword(), callback);
   }
}
