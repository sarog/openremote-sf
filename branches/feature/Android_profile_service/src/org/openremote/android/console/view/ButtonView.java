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

import java.util.Timer;
import java.util.TimerTask;

import org.openremote.android.console.Constants;
import org.openremote.android.console.bindings.ORButton;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.ORListenerManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class ButtonView extends ControlView {

   private Button uiButton;
   private Drawable defaultImage;
   private Drawable pressedImage;
   public final static long REPEAT_CMD_INTERVAL = 300;
   public ButtonView(Context context, ORButton button) {
      super(context);
      setComponent(button);
      if (button != null) {
         uiButton  = new Button(context);
         initButton(button);
      }
   }
   
   private void initButton(final ORButton button) {
      uiButton.setId(button.getComponentId());
      uiButton.setText(button.getName());
      uiButton.setTextSize(Constants.DEFAULT_FONT_SIZE);
      if (button.getDefaultImage() != null) {
         defaultImage = Drawable.createFromPath(Constants.FILE_FOLDER_PATH + button.getDefaultImage().getSrc());
         if (defaultImage != null) {
            uiButton.setText(null);
            uiButton.setBackgroundDrawable(defaultImage);
            uiButton.setLayoutParams(new FrameLayout.LayoutParams(defaultImage.getIntrinsicWidth(), defaultImage
                  .getIntrinsicHeight()));
         }
      }
      if (button.getPressedImage() != null) {
         pressedImage = Drawable.createFromPath(Constants.FILE_FOLDER_PATH + button.getPressedImage().getSrc());
      }
      View.OnTouchListener touchListener = new OnTouchListener() {
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
               cancelTimer();
               if (pressedImage != null) {
                  uiButton.setBackgroundDrawable(pressedImage);
               } else if (defaultImage != null) {
                  defaultImage.setAlpha(200);
                  uiButton.setBackgroundDrawable(defaultImage);
               }
               if (button.isHasControlCommand()) {
                  sendCommand();
                  if (button.isRepeat()) {
                     Timer timer = new Timer();
                     timer.schedule(new TimerTask() {
                        public void run() {
                           sendCommand();
                        }
                     }, REPEAT_CMD_INTERVAL, REPEAT_CMD_INTERVAL);
                     setTimer(timer);
                  }
               }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
               cancelTimer();
               if (defaultImage != null) {
                  defaultImage.setAlpha(255);
                  uiButton.setBackgroundDrawable(defaultImage);
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

   private void sendCommand() {
      sendCommandRequest("click");
   }
}
