/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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
import org.openremote.android.console.util.ImageUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * This class is responsible for rendering the switch in screen with the switch data.
 * It has control command and sensory.
 * 
s */
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
         button = new Button(context, null, android.R.attr.buttonStyleSmall);
         button.setTextSize(Constants.DEFAULT_FONT_SIZE);
         initSwitch(switchComponent);
         if (switchComponent.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
      setLayoutParams(new ViewGroup.LayoutParams(switchComponent.getFrameWidth(),
            switchComponent.getFrameHeight()));
   }

   /**
    * Sets switch's on/off state(image or text).
    * Sets touch listener on the switch view for change switch state and send command.
    * 
    * If images are used, have the size of the Button match the image currently
    * displayed and center it within the RelativeLayout.  Otherwise, make
    * the button's dimensions the same as the RelativeLayout.
    *
    * @param switchComponent the switch component
    */
   private void initSwitch(Switch switchComponent) {
      int width = switchComponent.getFrameWidth();
      int height = switchComponent.getFrameHeight();

      RelativeLayout.LayoutParams buttonLayoutParams = null;
      if (switchComponent.getOnImage() != null && switchComponent.getOffImage() != null) {
         canUseImage = true;
         onImage = ImageUtil.createFromPathQuietly(context, Constants.FILE_FOLDER_PATH +
               switchComponent.getOnImage().getSrc());
         offImage = ImageUtil.createFromPathQuietly(context, Constants.FILE_FOLDER_PATH +
               switchComponent.getOffImage().getSrc());

         buttonLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
               LayoutParams.WRAP_CONTENT);

         button.setText(null);
         if (isOn) {
            button.setBackgroundDrawable(onImage);
         } else {
            button.setBackgroundDrawable(offImage);
         }
      } else {
         buttonLayoutParams = new RelativeLayout.LayoutParams(width, height);

         if (isOn) {
            button.setText("ON");
         } else {
            button.setText("OFF");
         }
      }
      buttonLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
      button.setLayoutParams(buttonLayoutParams);

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
   
   /** The handler is for updating switch state by polling result. */
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
