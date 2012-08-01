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

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.UISwitch;
import org.openremote.modeler.shared.PropertyChangeEvent;
import org.openremote.modeler.shared.PropertyChangeListener;

import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * ScreenSwitch is the switch widget in screen.
 */
public class ScreenSwitch extends ScreenComponent {
   private FlexTable switchTable = new FlexStyleBox();

   /** The switchTable center text. */
   private Text center = new Text("Switch");
   private UISwitch uiSwitch;
   protected Image image = new Image();
   private boolean isOn = true;

   public ScreenSwitch(ScreenCanvas canvas, UISwitch uiSwitch, WidgetSelectionUtil widgetSelectionUtil) {
      super(canvas, widgetSelectionUtil);
      this.uiSwitch = uiSwitch;
      initial();
      
      final PropertyChangeListener imageSourceSrcListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          if (evt.getNewValue() != null) {
            setIcon((String)evt.getNewValue());
          } else {
            removeIcon();
          }
        }
      };
      // We listen to changes on both on and off image, but always display using the on image
      // The means we reset to on image whenever we change the off image
      final PropertyChangeListener imageListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          if (ScreenSwitch.this.uiSwitch.getOnImage() != null) {
            setIcon(ScreenSwitch.this.uiSwitch.getOnImage().getSrc());
          } else {
            removeIcon();
          }
          
          // ImageSource change, should "move" our src listener to the new one
          if (evt.getOldValue() != null) {
            ((ImageSource)evt.getOldValue()).removePropertyChangeListener("src", imageSourceSrcListener);
          }
          ImageSource newSource = (ImageSource) evt.getNewValue();
          if (newSource != null) {
            newSource.addPropertyChangeListener("src", imageSourceSrcListener);
          }
        }
      };
      this.uiSwitch.addPropertyChangeListener("onImage", imageListener);
      this.uiSwitch.addPropertyChangeListener("offImage", imageListener);
      
      if (this.uiSwitch.getOnImage() != null) {
        this.uiSwitch.getOnImage().addPropertyChangeListener("src", imageSourceSrcListener);
        setIcon(uiSwitch.getOnImage().getSrc());
      }
   }

   /**
    * Initial the switch as a style box.
    */
   private void initial() {
      addStyleName("screen-btn");
      switchTable.setWidget(1, 1, center);
      add(switchTable);
   }

   @Override
   public String getName() {
      return center.getText();
   }

   private void setIcon(String icon) {
      image.setUrl(icon);
      switchTable.removeStyleName("screen-btn-cont");
      switchTable.setWidget(1, 1, image);
   }
   
   public void onStateChange() {
      if (uiSwitch.canUseImage()) {
         if (isOn) {
            isOn = false;
            image.setUrl(uiSwitch.getOffImage().getSrc());
         } else {
            isOn = true;
            image.setUrl(uiSwitch.getOnImage().getSrc());
         }
      }
   }
   
   private void removeIcon() {
      center = new Text("Switch");
      switchTable.setWidget(1, 1, center);
      switchTable.addStyleName("screen-btn-cont");
      image.setUrl("");
      isOn = true;
   }

  public UISwitch getUiSwitch() {
    return uiSwitch;
  }

}
