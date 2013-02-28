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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.widget.propertyform.LabelPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UILabel;

import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

/**
 * The Class ScreenButton. It display as a style box, can be adjust size.
 */
public class ScreenLabel extends ScreenComponent {


   /** The btnTable center text. */
   protected Text center = new Text("The text of the label");


   private UILabel uiLabel = new UILabel();

   
   public ScreenLabel(ScreenCanvas canvas, UILabel uiLabel) {
      super(canvas);
      this.uiLabel = uiLabel;
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
      center.setStyleAttribute("color", uiLabel.getColor());
      center.setStyleAttribute("fontSize", uiLabel.getFontSize()+"");
      add(center);
      layout();
   }

   public void setText(String text){
      uiLabel.setText(text);
      adjustTextLength();
   }
   @Override
   public void setName(String name) {
      return;
   }
   
   public UILabel getUiLabel() {
      return uiLabel;
   }

   public void setUiLabel(UILabel uiLabel) {
      this.uiLabel = uiLabel;
   }
   
   public void setColor(String color){
      uiLabel.setColor(color);
      center.setStyleAttribute("color", color);
   }
   
   public void setFontSize(int size){
      uiLabel.setFontSize(size);
      center.setStyleAttribute("fontSize", size+"");
   }
   @Override
   public String getName() {
      return uiLabel.getName();
   }


   @Override
   public PropertyForm getPropertiesForm() {
      return new LabelPropertyForm(this);
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
      adjustTextLength(getWidth());
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
}
