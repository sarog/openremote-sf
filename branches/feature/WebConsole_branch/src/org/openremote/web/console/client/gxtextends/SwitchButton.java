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

import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.widget.ScreenSwitch;
import org.openremote.web.console.domain.BusinessEntity;
import org.openremote.web.console.domain.Switch;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Element;

/**
 * The Class SwitchButton.
 */
public class SwitchButton extends Button {

   private Switch uiSwitch;
   private boolean isOn; 
   private boolean hasIcon;
   private String onImageUrl;
   private String offImageUrl;
   
   public SwitchButton(Switch uiSwitch) {
      this.uiSwitch = uiSwitch;
      setSize(uiSwitch.getFrameWidth(), uiSwitch.getFrameHeight());
      hasIcon = uiSwitch.getOnImage() != null && uiSwitch.getOffImage() != null;
      if (hasIcon) {
         onImageUrl = ClientDataBase.appSetting.getResourceRootPath() + URL.encode(uiSwitch.getOnImage().getSrc());
         offImageUrl = ClientDataBase.appSetting.getResourceRootPath() + URL.encode(uiSwitch.getOffImage().getSrc());
      }
      setSize(uiSwitch.getFrameWidth(), uiSwitch.getFrameHeight());
//      String jSessionId = Cookies.getCookie("JSESSIONID");
   }
   
   @Override
   protected void onRender(Element target, int index) {
      // default set off state.
      if (hasIcon) {
         buttonSelector = "div";
         template = new Template("<div style=\"height:100%; background: url(" + offImageUrl
               + ") no-repeat 0 0;\"><div></div></div>");
      } else {
         setText("OFF");
      }
      super.onRender(target, index);
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
      isOn = !isOn;
   }
   
   private void sendCommand(String commandType) {
      ((ScreenSwitch) getParent()).sendCommand(commandType, new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            // do nothing.
         }
      });
   }
}
