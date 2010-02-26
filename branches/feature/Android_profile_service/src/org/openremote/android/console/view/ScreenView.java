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

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.openremote.android.console.Constants;
import org.openremote.android.console.HTTPUtil;
import org.openremote.android.console.bindings.Background;
import org.openremote.android.console.bindings.LayoutContainer;
import org.openremote.android.console.bindings.XScreen;
import org.openremote.android.console.model.AppSettingsModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ScreenView extends AbsoluteLayout {

   private XScreen screen;
   
   /**
    * Instantiates a new screen view.
    * 
    * @param context the context
    * @param screen the screen
    */
   @SuppressWarnings("deprecation")
   public ScreenView(Context context, XScreen screen) {
      super(context);
      this.screen = screen;
      setBackgroundColor(0);
      setTag(screen.getName());
      if (screen.getBackground() != null) {
         addBackground(screen);
         
      }
      
      ArrayList<LayoutContainer> layouts = screen.getLayouts();
      for (int i = 0; i < layouts.size(); i++) {
         LayoutContainerView la = LayoutContainerView.buildWithLayoutContainer(context, layouts.get(i));
         if (la != null) {
            LayoutContainer layout = layouts.get(i);
            addView(la, new AbsoluteLayout.LayoutParams(layout.getWidth(), layout.getHeight(), layout.getLeft(), layout.getTop()));
         }
      }
   }

   /**
    * @param screen
    */
   private void addBackground(XScreen screen) {
      ImageView backgroudView = new ImageView(this.getContext());
      int left = 0;
      int top = 0;
      int screenWidth = Constants.SCREEN_WIDTH;
      int screenHeight = Constants.SCREEN_HEIGHT - Constants.SCREEN_STATUS_BAR_HEIGHT;
      try {
         Bitmap backgroudBitMap = BitmapFactory.decodeStream(this.getContext().openFileInput(screen.getBackgroundSrc()));
         backgroudView.setImageBitmap(backgroudBitMap);
         int imageWidth = backgroudBitMap.getWidth();
         int imageHeight = backgroudBitMap.getHeight();
         Background background = screen.getBackground();
         if (!background.isFillScreen()) {
            if (background.isBackgroundImageAbsolutePosition()) {
               left = background.getBackgroundImageAbsolutePositionLeft();
               top = background.getBackgroundImageAbsolutePositionTop();
            } else {
               String backgroundImageRelativePosition = background.getBackgroundImageRelativePosition();
               if ("top-left".equals(backgroundImageRelativePosition)) {
               } else if("top".equals(backgroundImageRelativePosition)) {
                  left = (screenWidth - imageWidth) / 2;
               } else if("top-right".equals(backgroundImageRelativePosition)) {
                  left = screenWidth - imageWidth;
               } else if("left".equals(backgroundImageRelativePosition)) {
                  top = (screenHeight - imageHeight) / 2;
               } else if("center".equals(backgroundImageRelativePosition)) {
                  left = (screenWidth - imageWidth) / 2;
                  top = (screenHeight - imageHeight) / 2;
               } else if("right".equals(backgroundImageRelativePosition)) {
                  left = screenWidth - imageWidth;
                  top = (screenHeight - imageHeight) / 2;
               } else if("bottom".equals(backgroundImageRelativePosition)) {
                  left = (screenWidth - imageWidth) / 2;
                  top = screenHeight - imageHeight;
               } else if("bottom-left".equals(backgroundImageRelativePosition)) {
                  top = screenHeight - imageHeight;
               } else if("bottom-right".equals(backgroundImageRelativePosition)) {
                  left = screenWidth - imageWidth;
                  top = screenHeight - imageHeight;
               }
            }
         }
         addView(backgroudView, new AbsoluteLayout.LayoutParams(imageWidth, imageHeight ,left , top));
      } catch (FileNotFoundException e) {
         Log.e("ScreenView", "screen background file" + screen.getBackgroundSrc() + " not found.", e);
      }
   }
   

}
