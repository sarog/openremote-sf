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
package org.openremote.android.console.view;

import java.io.IOException;
import java.util.Timer;

import org.apache.http.client.ClientProtocolException;
import org.openremote.android.console.HTTPUtil;
import org.openremote.android.console.bindings.Component;
import org.openremote.android.console.bindings.Switch;
import org.openremote.android.console.bindings.XButton;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ViewHelper;

import android.content.Context;
import android.util.Log;

public class ControlView extends ComponentView {

   private Timer timer;
   private Context context;
   
   protected ControlView(Context context) {
      super(context);
      this.context = context;
   }
   
   public static ControlView buildWithControl(Context context, Component control) {
      ControlView controlView = null;
      if (control instanceof XButton) {
         controlView = new ButtonView(context, (XButton)control);
      } else if (control instanceof Switch) {
         controlView = new SwitchView(context, (Switch)control);
      }
      return controlView;
   }
   
   public boolean sendCommandRequest(String commandType) {
      try {
         int responseCode = HTTPUtil.sendCommand(this.context, AppSettingsModel.getCurrentServer(getContext()), getComponent().getComponentId(), commandType);
         if (responseCode != 200) {
            handleServerErrorWithStatusCode(responseCode);
            return false;
         }
      } catch (ClientProtocolException e) {
         cancelTimer();
         return false;
      } catch (IOException e) {
         cancelTimer();
         return false;
      }
      return true;
   }
   
   public void cancelTimer() {
      if(timer != null) {
         timer.cancel();
      }
      timer = null;
   }

   public void setTimer(Timer timer) {
      this.timer = timer;
   }
   
   public void handleServerErrorWithStatusCode(int statusCode) {
      if (statusCode != 200) {
//         if (statusCode != 401) {
//            [[DataBaseService sharedDataBaseService] saveCurrentUser];
//         } else {
//            [Definition sharedDefinition].password = nil;
//         }

         cancelTimer();
         ViewHelper.showAlertViewWithTitle(getContext(), "Send Request Error", ControllerException.exceptionMessageOfCode(statusCode));
      }
   }
}
