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
package org.openremote.modeler.client.widget.control;

import org.openremote.modeler.client.widget.uidesigner.SwitchPropertyForm;
import org.openremote.modeler.domain.control.UISwitch;

import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * ScreenSwitch is the switch widget in screen.
 */
public class ScreenSwitch extends ScreenControl {
   private FlexTable switchTable = new FlexTableBox();
   
   /** The switchTable center text. */
   private Text center = new Text("Switch");
   private UISwitch uiSwitch;
   protected Image image = new Image();
   /**
    * Instantiates a new screen button.
    */
   public ScreenSwitch() {
      initial();
   }

   public ScreenSwitch(UISwitch uiSwitch) {
      this();
      this.uiSwitch = uiSwitch;
      if (uiSwitch.getOnImage() != null) {
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
   
   @Override
   public FormPanel buildPropertiesForm() {
      return new SwitchPropertyForm(this, uiSwitch);
   }

   @Override
   public void setName(String name) {
      center.setText(name);
   }
   
   public void setIcon(String icon) {
      image.setUrl(icon);
      switchTable.removeStyleName("screen-btn-cont");
      switchTable.setWidget(1, 1, image);
   }
}
