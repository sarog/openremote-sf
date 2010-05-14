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
import org.openremote.android.console.bindings.Slider;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.PollingStatusParser;
import org.openremote.android.console.util.ImageUtil;
import org.openremote.android.console.util.NumberFormat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * This class is responsible for rendering the slider in screen with the slider data.
 * 
 * @author handy 2010-05-12
 *
 */
public class SliderView extends SensoryControlView implements OnSeekBarChangeListener {
   private static final int SEEK_BAR_PROGRESS_INIT_VALUE = 0;
   private static final int SEEK_BAR_PROGRESS_MAX = 100;
   
   private Context context;
   private Slider slider;
   private SeekBar seekBar;
   private int slideToBusinessValue = 0;

   protected SliderView(Context context, Slider slider) {
      super(context);
      if (slider != null) {
         setComponent(slider);
         this.context = context;
         this.slider = slider;
         initSeekBar();
         
         // If the sensor is null, the sliderview is one-way.
         if (slider.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
   }

   private void initSeekBar() {
      setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
      
      // isVertical
//      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      if (slider.isVertical()) {
//         seekBar = (SeekBar) inflater.inflate(R.layout.seek_bar_vertical, (ViewGroup) findViewById(R.id.seek_bar_vertical));
//      } else {
//         seekBar = (SeekBar) inflater.inflate(R.layout.seek_bar_horizontal, (ViewGroup) findViewById(R.id.seek_bar_horizontal));
//      }
      seekBar = new SeekBar(context);
      seekBar.setMax(SEEK_BAR_PROGRESS_MAX);
      
      //maxTrackImage
//      seekBar.setBackgroundResource(R.drawable.progress_bg);
      
      seekBar.setMax(SEEK_BAR_PROGRESS_MAX);
      seekBar.setProgress(getProgressOfBusinessValue(SEEK_BAR_PROGRESS_INIT_VALUE));
      
      // isPassive
      if (slider.isPassive()) {
         seekBar.setEnabled(false);
      }
      
      // thumbImage
      Image thumbImage = slider.getThumbImage();
      if (thumbImage != null) {
         Drawable thumb = ImageUtil.createFromPathQuietly(Constants.FILE_FOLDER_PATH + thumbImage.getSrc());
         seekBar.setThumb(thumb);
      }
      
      // minImage
      // maxImage
      // minTrackImage
      // maxTrackImage
      seekBar.setOnSeekBarChangeListener(this);
      this.addView(seekBar);
   }
   
   private int getProgressOfBusinessValue(int businessValue) {
      if (slider.getMaxValue() == 0 || slider.getMaxValue() == slider.getMinValue()) {
         return 0;
      }
      double progress = SEEK_BAR_PROGRESS_MAX * ((float)(businessValue - slider.getMinValue()) / (slider.getMaxValue() - slider.getMinValue()));
      return (int) NumberFormat.format(0, progress);
   }
   
   @Override
   public void addPollingSensoryListener() {
      final Integer sensorId = ((Slider)getComponent()).getSensor().getSensorId();
      Log.e("INFO", "sensor id is " + sensorId);
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String value = PollingStatusParser.statusMap.get(sensorId.toString()).toLowerCase();
               int valueInt = 0;
               try {
                  valueInt = Integer.parseInt(value);
               } catch (NumberFormatException e) {
                  Log.e("ERROR", "The returned format of polling value " + value + " for slider is wrong", e);
                  return;
               }
               pollingValueUIHandler.sendEmptyMessage(valueInt);
            }
         });
      }
   }
   
   private Handler pollingValueUIHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         int businessValue = msg.what;
         int progress = getProgressOfBusinessValue(businessValue);
         seekBar.setProgress(progress);
         super.handleMessage(msg);
      }
  };
   
   // The following three overided methods are for onSeekBarChangeListener
   @Override
   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      slideToBusinessValue = (int)(((float)progress/SEEK_BAR_PROGRESS_MAX) * (slider.getMaxValue() - slider.getMinValue()) + slider.getMinValue());
   }

   @Override
   public void onStartTrackingTouch(SeekBar seekBar) {
   }

   @Override
   public void onStopTrackingTouch(SeekBar seekBar) {
      Log.e("INFO onStopTrackingTouch", "Slide to business value " + slideToBusinessValue);
      sendCommandRequest(String.valueOf(slideToBusinessValue));
   }

}
