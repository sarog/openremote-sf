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

import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.bindings.ORButton;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.util.ImageUtil;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class ButtonView extends ControlView {

   private Button uiButton;
   private BitmapDrawable defaultImage;
   private BitmapDrawable pressedImage;
   
   private boolean longPress; // Indicates that the current press is considered long
   private Timer longPressTimer;
   private Timer buttonRepeatTimer;
      
   public ButtonView(Context context, ORButton button) {
      super(context);
      setComponent(button);
      if (button != null) {
         uiButton  = new Button(context, null, android.R.attr.buttonStyleSmall);
         initButton(button);
      }
   }
   
   /**
    * Inits the button.
    * Add listener to make the button send command and navigate to.
    * If pressed, send command; if up, navigate to. It also can 
    * repeat send command if pressed and not up.  
    * 
    * @param button the button
    */
   private void initButton(final ORButton button) {
      int width = button.getFrameWidth();
      int height = button.getFrameHeight();
      uiButton.setId(button.getComponentId());
      uiButton.setText(button.getName());
      uiButton.setTextSize(Constants.DEFAULT_FONT_SIZE);
      uiButton.setLayoutParams(new FrameLayout.LayoutParams(width, height));
      if (button.getDefaultImage() != null) {
         defaultImage = ImageUtil.createClipedDrawableFromPath(context, Constants.FILE_FOLDER_PATH + button.getDefaultImage().getSrc(), width, height);
         if (defaultImage != null) {
            uiButton.setBackgroundDrawable(defaultImage);
         }
      }
      if (button.getPressedImage() != null) {
         pressedImage = ImageUtil.createClipedDrawableFromPath(context, Constants.FILE_FOLDER_PATH + button.getPressedImage().getSrc(), width, height);
      }
      View.OnTouchListener touchListener = new OnTouchListener() {
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
              cancelTimers();
               longPress = false;
               
               if (pressedImage != null) {
                  uiButton.setBackgroundDrawable(pressedImage);
               } else if (defaultImage != null) {
                  defaultImage.setAlpha(200);
                  uiButton.setBackgroundDrawable(defaultImage);
               }
               if (button.isHasPressCommand()) {
                  sendPressCommand();
                  if (button.isRepeat()) {
                     buttonRepeatTimer = new Timer();
                     buttonRepeatTimer.schedule(new TimerTask() {
                        public void run() {
                           sendPressCommand();
                        }
                     }, button.getRepeatDelay(), button.getRepeatDelay());
                  }
               }
               if (button.isHasLongPressCommand() || button.isHasLongReleaseCommand()) {
                 // Set-up timer to detect when this becomes a long press
                 longPressTimer = new Timer();
                 longPressTimer.schedule(new TimerTask() {
                   public void run() {
                     longPress = true;
                     sendLongPressCommand();
                   }
                 }, button.getLongPressDelay());
               }
               
               
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
              cancelTimers();
               if (defaultImage != null) {
                  defaultImage.setAlpha(255);
                  uiButton.setBackgroundDrawable(defaultImage);
               }
               if (button.isHasShortReleaseCommand() && !longPress) {
                 sendShortReleaseCommand();
               }
               if (button.isHasLongReleaseCommand() && longPress) {
                 sendLongReleaseCommand();
               }
               if (button.getNavigate() != null) {
                  uiButton.setPressed(false);
                  ORListenerManager.getInstance().notifyOREventListener(ListenerConstant.ListenerNavigateTo, button.getNavigate());
               }
            }
            return false;
         }
      };
      uiButton.setOnTouchListener(touchListener);
      addView(uiButton);
   }
   
   private void cancelButtonRepeatTimer() {
     if (buttonRepeatTimer != null) {
       buttonRepeatTimer.cancel();
       buttonRepeatTimer = null;
     }
   }

   private void cancelLongPressTimer() {
     if (longPressTimer != null) {
       longPressTimer.cancel();
       longPressTimer = null;
     }
   }
   
   private void cancelTimers() {
     cancelButtonRepeatTimer();
     cancelLongPressTimer();
   }
   
   private void sendPressCommand() {
      sendCommandRequest("press");
   }
   
   private void sendLongPressCommand() {
     sendCommandRequest("longPress");
   }
   
   private void sendShortReleaseCommand() {
     sendCommandRequest("shortRelease");
   }

   private void sendLongReleaseCommand() {
     sendCommandRequest("longRelease");
   }
   
   @Override
   public void handleServerErrorWithStatusCode(int statusCode) {
     if (statusCode != 200) {
       cancelTimers();
     }
     super.handleServerErrorWithStatusCode(statusCode);
  }

   @Override
   public void urlConnectionDidFailWithException(Exception e) {
      cancelTimers();
      super.urlConnectionDidFailWithException(e);
   }

   @Override
   public void urlConnectionDidReceiveResponse(HttpResponse httpResponse) {
      int responseCode = httpResponse.getStatusLine().getStatusCode();
      if (responseCode != 200) {
         cancelTimers();
      }
      super.urlConnectionDidReceiveResponse(httpResponse);
   }

}
