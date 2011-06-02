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
import org.openremote.web.console.client.listener.OREventListener;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.domain.Label;
import org.openremote.web.console.domain.Sensor;

import com.extjs.gxt.ui.client.widget.Text;

/**
 * The ScreenLabel is for displaying the label according to label model.
 * It support sensor.
 */
public class ScreenLabel extends ScreenComponent implements SensoryDelegate {

   private Text text;
   private String labelText;
   
   /**
    * Instantiates a new screen label.
    * 
    * @param label the label
    */
   public ScreenLabel(Label label) {
      setComponent(label);
      if (label != null) {
         initText(label);
         if (label.getSensor() != null) {
            addPollingSensoryListener();
         }
      }
   }
   
   /**
    * Inits the label text.
    * 
    * @param label the label
    */
   private void initText(Label label) {
      text = new Text();
      text.setSize(label.getFrameWidth(), label.getFrameHeight());
      text.setStyleName("label-style");
      labelText = label.getText();
      if (labelText != null) {
         text.setText(labelText);
      }
      if (label.getFontSize() > 0) {
         text.setStyleAttribute("fontSize", label.getFontSize() + "px");
      }
      if (label.getColor() != null) {
         text.setStyleAttribute("color", label.getColor());
      }
      add(text);
   }
   
   /* (non-Javadoc)
    * @see org.openremote.web.console.client.widget.SensoryDelegate#addPollingSensoryListener()
    */
   public void addPollingSensoryListener() {
      final Sensor sensor = ((Label)getComponent()).getSensor();
      final Integer sensorId = sensor.getSensorId();
      if (sensorId > 0) {
         ORListenerManager.getInstance().addOREventListener(Constants.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
            public void handleEvent(OREvent event) {
               String newState = ClientDataBase.statusMap.get(sensorId.toString());
               labelText = sensor.getStateValue(newState);
               if (labelText == null) {
                  labelText = newState;
               }
               if (labelText != null) {
                  text.setText(labelText);
               }
            }
         });
      }
   }

}
