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
package org.openremote.modeler.client.widget.component;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.SensorLink;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.WebViewPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.CustomSensor;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.SensorType;
import org.openremote.modeler.domain.State;
import org.openremote.modeler.domain.component.UIWebView;

import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

/**
 * WebView on Screen.
 * 
 * @author Selvakkumar Palaniyappan
 *
 */
public class ScreenWebView extends ScreenComponent {


   protected Text center = new Text();

   private UIWebView uiWebView = new UIWebView();

   private int stateIndex = -1;
   
   private List<String> states = new ArrayList<String>();
   /** The color use the web format, default is white. */
   private String color = "FFFFFF";
   
   /** The default font size is 14. */
   private int fontSize = 14;
    
   public ScreenWebView(ScreenCanvas canvas, UIWebView uiWebView, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.uiWebView = uiWebView;
      center.setText(uiWebView.getURL());
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
      center.setStyleAttribute("color", color);
      center.setStyleAttribute("fontSize", fontSize + "px");
      center.setStyleAttribute("fontFamily", Constants.DEFAULT_FONT_FAMILY);
      add(center);
      layout();
   }

   public void setText(String text) {
      uiWebView.setURL(text);
      adjustTextLength();
   }

   public UIWebView getUIWebView() {
      return uiWebView;
   }

   public void setUIWebView(UIWebView uiWebView) {
      this.uiWebView = uiWebView;
   }

  
   @Override
   public String getName() {
      return uiWebView.getName();
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new WebViewPropertyForm(this, widgetSelectionUtil);
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
         if (ajustLength < uiWebView.getURL().length()) {
            center.setText(uiWebView.getURL().substring(0, ajustLength) + "..");
         } else {
            center.setText(uiWebView.getURL());
         }
      }
   }
   
   public void clearSensorStates() {
      stateIndex = -1;
      states.clear();
      setText(uiWebView.getURL());
   }
   public void onStateChange() {
      Sensor sensor = uiWebView.getSensor();
      if (sensor != null && states.isEmpty()) {
         if (sensor.getType() == SensorType.SWITCH) {
            if (!"".equals(uiWebView.getSensorLink().getStateValueByStateName("on"))) {
               states.add(uiWebView.getSensorLink().getStateValueByStateName("on"));
            }
            if (!"".equals(uiWebView.getSensorLink().getStateValueByStateName("off"))) {
               states.add(uiWebView.getSensorLink().getStateValueByStateName("off"));
            }
         } else if (sensor.getType() == SensorType.CUSTOM) {
            SensorLink sensorLink = uiWebView.getSensorLink();
            for (State state : ((CustomSensor)sensor).getStates()) {
               if (!"".equals(uiWebView.getSensorLink().getStateValueByStateName(state.getName()))) {
                  states.add(sensorLink.getStateValueByStateName(state.getName()));
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

