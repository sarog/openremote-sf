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
import org.openremote.android.console.bindings.Control;
import org.openremote.android.console.bindings.XButton;
import org.openremote.android.console.model.AppSettingsModel;

import android.content.Context;
import android.util.Log;

public class ControlView extends ComponentView {

   private Timer timer;
   
   protected ControlView(Context context) {
      super(context);
   }
   
   public static ControlView buildWithControl(Context context, Control control) {
      ControlView controlView = null;
      if (control instanceof XButton) {
         controlView = new ButtonView(context, (XButton)control);
      }
      return controlView;
   }
   
   public void sendCommandRequest(String commandType) {
      try {
         int responseCode = HTTPUtil.sendCommand(AppSettingsModel.getCurrentServer(getContext()), getComponent().getComponentId(), commandType);
         Log.e("response code", ""+responseCode);
      } catch (ClientProtocolException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
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
   
}
