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

import org.openremote.android.console.bindings.Label;
import org.openremote.android.console.bindings.Sensor;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.PollingStatusParser;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * The Class LabelView.
 */
public class LabelView extends ComponentView implements SensoryDelegate {

   /** The text view. */
   private TextView textView;
   
   /** The text. */
   private String text;
   
   /**
    * Instantiates a new label view.
    * {@link #setGravity(int)} make the text be in center.
    * 
    * @param label the label entity
    */
   public LabelView(Context context, Label label) {
      super(context);
      setComponent(label);
      setGravity(Gravity.CENTER);
      if (label != null) {
         textView = new TextView(context);
         initLabel(label);
         if (label.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
   }

   /**
    * Inits the label.
    * 
    */
   private void initLabel(Label label) {
      textView.setId(label.getComponentId());
      textView.setGravity(Gravity.CENTER);
      textView.setLayoutParams(new FrameLayout.LayoutParams(label.getFrameWidth(), label.getFrameHeight()));
      text = label.getText();
      if (text != null) {
         textView.setText(text);
      }
      if (label.getFontSize() > 0) {
         textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,label.getFontSize());
      }
      if (label.getColor() != null) {
         textView.setTextColor(Color.parseColor(label.getColor()));
      }
      addView(textView);
      setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
   }
   
   /* (non-Javadoc)
    * @see org.openremote.android.console.view.SensoryDelegate#addPollingSensoryListener()
    */
   @Override
   public void addPollingSensoryListener() {
      final Sensor sensor = ((Label)getComponent()).getSensor();
      final Integer sensorId = sensor.getSensorId();
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String newState = PollingStatusParser.statusMap.get(sensorId.toString());
               text = sensor.getStateValue(newState);
               if (text == null) {
                  text = newState;
               }
               handler.sendEmptyMessage(0);
            }
         });
      }
   }
   
   /** The handler. */
   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         if (text != null) {
            textView.setText(text);
         }
         super.handleMessage(msg);
      }
  };

}
