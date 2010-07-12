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
package org.openremote.web.console.client.widget;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.event.OREvent;
import org.openremote.web.console.client.gxtextends.ORSlider;
import org.openremote.web.console.client.listener.OREventListener;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.domain.Slider;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

/**
 * The Class ScreenSlider.
 */
public class ScreenSlider extends ScreenControl implements SensoryDelegate {

   private ORSlider imageSlider;
   
   public ScreenSlider(Slider slider) {
      setComponent(slider);
      setSize(slider.getFrameWidth(), slider.getFrameHeight());
      setLayout(new CenterLayout());
      initImageSlider(slider);
      if (slider.getSensor() != null) {
         addPollingSensoryListener();
      }
   }
   
   private void initImageSlider(final Slider slider) {
      imageSlider = new ORSlider(slider);
      add(imageSlider);
   }
   
   public void addPollingSensoryListener() {
      final Integer sensorId = ((Slider)getComponent()).getSensor().getSensorId();
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(Constants.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String value = ClientDataBase.statusMap.get(sensorId.toString()).toLowerCase();
               try {
                  int valueInt = Integer.parseInt(value);
                  imageSlider.setValue(valueInt);
               } catch (NumberFormatException e) {
                  MessageBox.alert("ERROR", "The returned format of polling value " + value + " for slider is wrong", null);
                  return;
               }
            }
         });
      }

   }

   public void sendCommand(String value) {
      super.sendCommand(value, new AsyncSuccessCallback<Void>() {
         public void onSuccess(Void result) {
            // do nothing.
         }
      });
   }

   
}
