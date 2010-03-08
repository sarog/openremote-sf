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

import org.openremote.android.console.Constants;
import org.openremote.android.console.bindings.Switch;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.PollingStatusParser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SwitchView extends SensoryControlView {

   private ToggleButton button;
   private Drawable onImage;
   private Drawable offImage;
   private boolean isOn;
   private boolean canUseImage;
   public SwitchView(Context context, Switch switchComponent) {
      super(context);
      setComponent(switchComponent);
      if (switchComponent != null) {
         button = new ToggleButton(context);
         initSwitch(switchComponent);
         if (switchComponent.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
   }

   private void initSwitch(Switch switchComponent) {
      if (switchComponent.getOnImage() != null) {
         onImage = Drawable.createFromPath(Constants.FILE_FOLDER_PATH + switchComponent.getOnImage().getSrc());
      }
      if (switchComponent.getOffImage() != null) {
         offImage = Drawable.createFromPath(Constants.FILE_FOLDER_PATH + switchComponent.getOffImage().getSrc());
      }
      if (onImage != null && offImage != null) {
         canUseImage = true;
         button.setTextOn(null);
         button.setTextOff(null);
         button.setText(null);
         if (isOn) {
            button.setBackgroundDrawable(onImage);
         } else {
            button.setBackgroundDrawable(offImage);
         }
         button.setLayoutParams(new FrameLayout.LayoutParams(onImage.getIntrinsicWidth(), onImage
               .getIntrinsicHeight()));
      }
      button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               if (!sendCommandRequest("on")) {
                  return;
               }
               isOn = true;
               if (canUseImage) {
                  button.setBackgroundDrawable(onImage);
               }
            } else {
               if (!sendCommandRequest("off")) {
                  return;
               }
               isOn = false;
               if (canUseImage) {
                  button.setBackgroundDrawable(offImage);
               }
            }
         }
         
      });
      button.setChecked(isOn);
      addView(button);
   }
   
   @Override
   public void addPollingSensoryListener() {
      final Integer sensorId = ((Switch)getComponent()).getSensor().getSensorId();
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               Log.e("polling", "sensorid:"+sensorId);
               String value = PollingStatusParser.statusMap.get(sensorId.toString()).toLowerCase();
               if (isOn && Switch.OFF.equals(value)) {
                  isOn = false;
               } else if (!isOn && Switch.ON.equals(value)) {
                  isOn = true;
               }
               button.setChecked(isOn);
            }
            
         });
      }
   }
}
