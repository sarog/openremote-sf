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
package org.openremote.web.console.client.gxtextends;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.event.OREvent;
import org.openremote.web.console.client.listener.OREventListener;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.client.widget.ScreenSwitch;
import org.openremote.web.console.domain.Switch;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;

/**
 * Generate a switch component by {@link org.openremote.web.console.domain.Switch}.
 */
public class SwitchButton extends Button {

   private Switch uiSwitch;
   private boolean isOn; 
   private boolean hasIcon;
   private String onImageUrl;
   private String offImageUrl;
   private El imageEL;
   
   public SwitchButton(Switch uiSwitch) {
      this.uiSwitch = uiSwitch;
      setSize(uiSwitch.getFrameWidth(), uiSwitch.getFrameHeight());
      hasIcon = uiSwitch.getOnImage() != null && uiSwitch.getOffImage() != null;
      if (hasIcon) {
         onImageUrl = ClientDataBase.getResourceRootPath() + URL.encode(uiSwitch.getOnImage().getSrc());
         offImageUrl = ClientDataBase.getResourceRootPath() + URL.encode(uiSwitch.getOffImage().getSrc());
      } else {
         setText("OFF");
      }
      setSize(uiSwitch.getFrameWidth(), uiSwitch.getFrameHeight());
   }
   
   @Override
   protected void onRender(Element target, int index) {
      // default set off state.
      if (hasIcon) {
         buttonSelector = "div";
         template = new Template("<div style=\"height:100%;\"><img src=\"" + offImageUrl + "\" /><div style=\"outline:none;\"></div></div>");
      }
      super.onRender(target, index);
      if (hasIcon) {
         imageEL = el().selectNode("img");
      }
   }
   
   @Override
   protected void onMouseDown(ComponentEvent ce) {
      super.onMouseDown(ce);
      if (hasIcon) {
         addStyleName("pressed");
      }
   }

   @Override
   protected void onMouseUp(ComponentEvent ce) {
      super.onMouseUp(ce);
      if (hasIcon) {
         removeStyleName("pressed");
      }
      if (isOn) {
         sendCommand(Switch.OFF);
      } else {
         sendCommand(Switch.ON);
      }
   }
   
   /**
    * Send command by command type.
    * 
    * @param commandType 
    * the command type support "on" and "off"
    */
   private void sendCommand(String commandType) {
      ((ScreenSwitch) getParent()).sendCommand(commandType, new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            // do nothing.
         }
      });
   }
   
   /**
    * Adds the polling sensory listener.
    */
   public void addPollingSensoryListener() {
      final Integer sensorId = uiSwitch.getSensor().getSensorId();
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(Constants.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String value = ClientDataBase.statusMap.get(sensorId.toString()).toLowerCase();
               if (isOn && Switch.OFF.equals(value)) {
                  isOn = false;
                  if (hasIcon) {
                     updateImage(offImageUrl);
                  } else {
                     setText("OFF");
                  }
               } else if (!isOn && Switch.ON.equals(value)) {
                  isOn = true;
                  if (hasIcon) {
                     updateImage(onImageUrl);
                  } else {
                     setText("ON");
                  }
               }
               
            }
         });
      }
   }
   
   private void updateImage(String imageUrl) {
      if (imageEL != null) {
         imageEL.setElementAttribute("src", imageUrl);
      }
   }
}
