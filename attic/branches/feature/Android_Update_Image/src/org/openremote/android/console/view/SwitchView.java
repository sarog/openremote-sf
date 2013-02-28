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
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * This class is responsible for rendering the switch in screen with the switch data.
 * It has control command and sensory.
 * 
 */
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
      setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
   }

   /**
    * Sets switch's on/off state(image or text).
    * Sets touch listener on the switch view for change switch state and send command.
    * 
    * @param switchComponent the switch component
    */
   private void initSwitch(Switch switchComponent) {
      final int width = switchComponent.getFrameWidth();
      final int height = switchComponent.getFrameHeight();
      button.setLayoutParams(new FrameLayout.LayoutParams(width, height));
      if (switchComponent.getOnImage() != null) {
         final String onImageName = switchComponent.getOnImage().getSrc();
         onImage = ImageUtil.createFromPathQuietly(Constants.FILE_FOLDER_PATH + switchComponent.getOnImage().getSrc());
         button.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
         ORListenerManager.getInstance().addOREventListener(ListenerConstant.LISTENER_IMAGE_CHANGE_FORMAT + onImageName, new OREventListener() {
            public void handleEvent(OREvent event) {
               onImage = null;
               onImage = ImageUtil.createFromPathQuietly(Constants.FILE_FOLDER_PATH + onImageName);
               handler.sendEmptyMessage(1);
            }
         });
      }
      if (switchComponent.getOffImage() != null) {
         final String offImageName = switchComponent.getOffImage().getSrc();
         offImage = ImageUtil.createFromPathQuietly(Constants.FILE_FOLDER_PATH + switchComponent.getOffImage().getSrc());

         ORListenerManager.getInstance().addOREventListener(ListenerConstant.LISTENER_IMAGE_CHANGE_FORMAT + offImageName, new OREventListener() {
            public void handleEvent(OREvent event) {
               offImage = null;
               offImage = ImageUtil.createFromPathQuietly(Constants.FILE_FOLDER_PATH + offImageName);
               handler.sendEmptyMessage(2);
            }
         });
      }
      if (onImage != null && offImage != null) {
         canUseImage = true;
         button.setText(null);
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
   
   /** The handler is for updating switch state by polling result or image changed. */
   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         switch (msg.what) {
         case 0: // polling result
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
            break;
         case 1: // on image changed
            if (isOn) {
               button.setBackgroundDrawable(onImage);
            }
            break;
         case 2: // off image changed
            if (!isOn) {
               button.setBackgroundDrawable(offImage);
            }
            break;

         default:
            // do nothing.
            break;
         }
         super.handleMessage(msg);
      }
  };
  
}
