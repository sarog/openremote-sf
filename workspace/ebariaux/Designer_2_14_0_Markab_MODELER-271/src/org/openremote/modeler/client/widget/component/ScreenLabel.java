/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.shared.PropertyChangeEvent;
import org.openremote.modeler.shared.PropertyChangeListener;
import org.openremote.modeler.shared.dto.SensorWithInfoDTO;

import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

/**
 * Label on Screen.
 * 
 * @author Javen Zhang, Dan Cong
 *
 */
public class ScreenLabel extends ScreenComponent {


   protected Text center = new Text();

   private UILabel uiLabel = new UILabel();

   private int stateIndex = -1;
   
   private List<String> states = new ArrayList<String>();
   
   public ScreenLabel(ScreenCanvas canvas, UILabel uiLabel, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.uiLabel = uiLabel;
      uiLabel.addPropertyChangeListener("text", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          adjustTextLength();
        }
      });
      uiLabel.addPropertyChangeListener("color", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          center.setStyleAttribute("color", "#" + evt.getNewValue());
        }
      });
      uiLabel.addPropertyChangeListener("fontSize", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          center.setStyleAttribute("fontSize", evt.getNewValue() + "px");
        }
      });
      
      final PropertyChangeListener linkerChildrenListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          clearSensorStates();
        }
      };      
      uiLabel.addPropertyChangeListener("sensorLink", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          clearSensorStates();

          // SensorLink change, should "move" our linkerChildren listener to the new one
          if (evt.getOldValue() != null) {
            ((SensorLink)evt.getOldValue()).removePropertyChangeListener("linkerChildren", linkerChildrenListener);
          }
          if (evt.getNewValue() != null) {
            ((SensorLink)evt.getNewValue()).addPropertyChangeListener("linkerChildren", linkerChildrenListener);
          }
        }
      });
      if (uiLabel.getSensorLink() != null) {
        uiLabel.getSensorLink().addPropertyChangeListener("linkerChildren", linkerChildrenListener);
      }
      
      center.setText(uiLabel.getText());
      initial();
      adjustTextLength();
   }

   /**
    * Initial.
    * 
    */
   protected void initial() {
      setLayout(new CenterLayout());
      center.setStyleAttribute("textAlign", "center");
      center.setStyleAttribute("color", "#" + uiLabel.getColor());
      center.setStyleAttribute("fontSize", uiLabel.getFontSize() + "px");
      center.setStyleAttribute("fontFamily", Constants.DEFAULT_FONT_FAMILY);
      add(center);
      layout();
   }

   public UILabel getUiLabel() {
      return uiLabel;
   }

   @Override
   public String getName() {
      return uiLabel.getName();
   }

   @Override
   public void setSize(int width, int height) {
      super.setSize(width, height);
      if (getWidth() == 0) {
         adjustTextLength(width);
      } else {
         adjustTextLength();
      }
   }

   /**
    * Adjust text length.
    * 
    * @param length
    *           the length
    */
   private void adjustTextLength() {
      if (stateIndex == -1) {
         adjustTextLength(getWidth());
      } else {
         setState(states.get(stateIndex));
      }
   }

   private void adjustTextLength(int width) {
      if (center.isVisible()) {
         int ajustLength = (width - 6) / 6;
         if (ajustLength < uiLabel.getText().length()) {
            center.setText(uiLabel.getText().substring(0, ajustLength) + "..");
         } else {
            center.setText(uiLabel.getText());
         }
      }
   }
   
   private void clearSensorStates() {
      stateIndex = -1;
      states.clear();
      adjustTextLength();
   }
   
   public void onStateChange() {
      SensorWithInfoDTO sensor = uiLabel.getSensorDTO();
      if (sensor != null && states.isEmpty()) {
         if (sensor.getType() == SensorType.SWITCH) {
            if (!"".equals(uiLabel.getSensorLink().getStateValueByStateName("on"))) {
               states.add(uiLabel.getSensorLink().getStateValueByStateName("on"));
            }
            if (!"".equals(uiLabel.getSensorLink().getStateValueByStateName("off"))) {
               states.add(uiLabel.getSensorLink().getStateValueByStateName("off"));
            }
         } else if (sensor.getType() == SensorType.CUSTOM) {
            SensorLink sensorLink = uiLabel.getSensorLink();
            for (String stateName : sensor.getStateNames()) {
               if (!"".equals(uiLabel.getSensorLink().getStateValueByStateName(stateName))) {
                  states.add(sensorLink.getStateValueByStateName(stateName));
               }
            }
         }
      }
      
      if (!states.isEmpty()) {
         if (stateIndex < states.size() - 1) {
            stateIndex = stateIndex + 1;
         } else if (stateIndex == states.size() - 1) {
            stateIndex = 0;
         }
         setState(states.get(stateIndex));
      }
   }
   
   private void setState(String state) {
      if (center.isVisible()) {
         int ajustLength = (getWidth() - 6) / 6;
         if (ajustLength < state.length()) {
            center.setText(state.substring(0, ajustLength) + "..");
         } else {
            center.setText(state);
         }
      }
   }
}
