/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
import org.openremote.android.console.bindings.Image;
import org.openremote.android.console.bindings.Label;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.PollingStatusParser;
import org.openremote.android.console.util.ImageUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ORImageView extends ComponentView implements SensoryDelegate {

   private ImageView imageView;
   private TextView textView;
   private String newStatus;
   private int width;
   private int height;
   public ORImageView(Context context, Image image) {
      super(context);
      setComponent(image);
      if (image != null) {
         image.setLinkedLabel();// read label from cache.
         width = image.getFrameWidth();
         height = image.getFrameHeight();
         imageView = new ImageView(context);
         textView = new TextView(getContext());
         addImageView(image.getSrc());
         if (image.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
   }

   private void addImageView(String imageSrc) {
      BitmapDrawable bd = ImageUtil.createClipedDrawableFromPath(Constants.FILE_FOLDER_PATH + imageSrc, width, height);
      if (bd == null) {
         return;
      }
      imageView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
      imageView.setBackgroundDrawable(bd);
      addView(imageView);
   }
   
   @Override
   public void addPollingSensoryListener() {
      Image image = (Image)getComponent();
      Integer id = image.getSensor().getSensorId();
      if (id <= 0) {
         if (image.getLabel() != null && image.getLabel().getSensor() != null) {
            id = image.getLabel().getSensor().getSensorId();
         }
      }
      final Integer sensorId = id;
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               newStatus = PollingStatusParser.statusMap.get(sensorId.toString());
               handler.sendEmptyMessage(0);
            }
         });
      }

   }
   
   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         String newValue = ((Image)getComponent()).getSensor().getStateValue(newStatus);
         if (newValue != null) {
            removeAllViews();
            addImageView(newValue);
         } else if (((Image)getComponent()).getLabel() != null) {
            Label label = ((Image)getComponent()).getLabel();
            if (label.getSensor() != null) {
               if (label.getText() != null) {
                  textView.setText(label.getText());
               }
               if (label.getFontSize() > 0) {
                  textView.setTextSize(label.getFontSize());
               }
               if (label.getColor() != null) {
                  textView.setTextColor(Color.parseColor(label.getColor()));
               }
               
               newValue = label.getSensor().getStateValue(newStatus);
               if (newValue != null) {
                  removeAllViews();
                  addView(textView);
               }
            }
            
         }
         super.handleMessage(msg);
      }
  };

}
