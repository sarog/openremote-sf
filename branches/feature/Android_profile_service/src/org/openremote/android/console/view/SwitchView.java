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
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class SwitchView extends SensoryControlView {

   private Button button;
   private Drawable onImage;
   private Drawable offImage;
   private boolean isOn;
   private boolean canUseImage;
   public SwitchView(Context context, Switch switchComponent) {
      super(context);
      setComponent(switchComponent);
      if (switchComponent != null) {
         button = new Button(context);
         button.setTextSize(18);
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
         button.setText(null);
         if (isOn) {
            button.setBackgroundDrawable(onImage);
         } else {
            button.setBackgroundDrawable(offImage);
         }
         button.setLayoutParams(new FrameLayout.LayoutParams(onImage.getIntrinsicWidth(), onImage
               .getIntrinsicHeight()));
      } else {
         if (isOn) {
            button.setText("ON");
         } else {
            button.setText("OFF");
         }
      }
      button.setOnTouchListener(new OnTouchListener() {
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
               if (canUseImage) {
                  if (isOn) {
                     onImage.setAlpha(200);
                     button.setBackgroundDrawable(onImage);
                  } else {
                     offImage.setAlpha(200);
                     button.setBackgroundDrawable(offImage);
                  }
               }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
               if (canUseImage) {
                  if (isOn) {
                     onImage.setAlpha(255);
                     button.setBackgroundDrawable(onImage);
                  } else {
                     offImage.setAlpha(255);
                     button.setBackgroundDrawable(offImage);
                  }
               }
               if (isOn) {
                  sendCommandRequest(Switch.OFF);
               } else {
                  sendCommandRequest(Switch.ON);
               }
            }
            return false;
         }
        
     });
      addView(button);
   }
   
   @Override
   public void addPollingSensoryListener() {
      final Integer sensorId = ((Switch)getComponent()).getSensor().getSensorId();
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String value = PollingStatusParser.statusMap.get(sensorId.toString()).toLowerCase();
               if (isOn && Switch.OFF.equals(value)) {
                  isOn = false;
               } else if (!isOn && Switch.ON.equals(value)) {
                  isOn = true;
               }
               handler.sendEmptyMessage(0);
            }
         });
      }
   }
   
   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         if (canUseImage) {
            if (isOn) {
               button.setBackgroundDrawable(onImage);
            } else {
               button.setBackgroundDrawable(offImage);
            }
         } else {
            if (isOn) {
               button.setText("ON");
            } else {
               button.setText("OFF");
            }
         }
         super.handleMessage(msg);
      }
  };
  
}
