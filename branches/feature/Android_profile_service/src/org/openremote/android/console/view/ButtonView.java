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
import org.openremote.android.console.bindings.XButton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class ButtonView extends ControlView {

   private Button uiButton;
   public ButtonView(Context context, XButton button) {
      super(context);
      if (button != null) {
         uiButton  = new Button(context);
         initButton(button);
      }
   }
   
   private void initButton(final XButton button) {
      uiButton.setId(button.getButtonId());
      uiButton.setText(button.getName());
      uiButton.setTextSize(10);
      if (button.getDefaultImage() != null) {
         final Drawable defaultImage = Drawable.createFromPath(Constants.FILE_FOLDER_PATH
               + button.getDefaultImage().getSrc());
         if (defaultImage != null) {
            uiButton.setText(null);
            uiButton.setBackgroundDrawable(defaultImage);
            uiButton.setLayoutParams(new FrameLayout.LayoutParams(defaultImage.getIntrinsicWidth(), defaultImage
                  .getIntrinsicHeight()));
         }
         View.OnTouchListener touchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                  if (button.getPressedImage() != null) {
                     Drawable pressedImage = Drawable.createFromPath(Constants.FILE_FOLDER_PATH
                           + button.getPressedImage().getSrc());
                     if (pressedImage != null) {
                        uiButton.setBackgroundDrawable(pressedImage);
                     } else {
                        defaultImage.setAlpha(200);
                        uiButton.setBackgroundDrawable(defaultImage);
                     }
                  } else {
                     defaultImage.setAlpha(200);
                     uiButton.setBackgroundDrawable(defaultImage);
                  }
               } else if (event.getAction() == MotionEvent.ACTION_UP) {
                  defaultImage.setAlpha(255);
                  uiButton.setBackgroundDrawable(defaultImage);
               }
               return false;
            }
         };
         uiButton.setOnTouchListener(touchListener);
      }
      addView(uiButton);
   }

}
