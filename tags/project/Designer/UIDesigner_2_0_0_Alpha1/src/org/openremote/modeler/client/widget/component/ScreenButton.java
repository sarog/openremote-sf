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

import org.openremote.modeler.client.widget.propertyform.ButtonPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.component.UIButton;

import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class ScreenButton. It display as a style box, can be adjust size.
 */
public class ScreenButton extends ScreenComponent {

   private FlexTable btnTable = new FlexStyleBox();

   /** The btnTable center text. */
   protected Text center = new Text("Button");

   /** The btnTable center image. */
   protected Image image = new Image();

   private UIButton uiButton = new UIButton();

   /**
    * Instantiates a new screen button.
    */
   public ScreenButton(ScreenCanvas canvas) {
      super(canvas);
      initial();
   }

   public ScreenButton(ScreenCanvas canvas, String text) {
      super(canvas);
      center.setText(text);
   }

   /**
    * Instantiates a new screen button.
    * 
    * @param width
    *           the width
    * @param height
    *           the height
    */
   public ScreenButton(ScreenCanvas canvas, int width, int height) {
      this(canvas);
      setSize(width, height);
   }

   public ScreenButton(ScreenCanvas canvas, UIButton uiButton) {
      this(canvas);
      this.uiButton = uiButton;
      center.setText(uiButton.getName());
      adjustTextLength();
      if (uiButton.getImage() != null) {
         setIcon(uiButton.getImage().getSrc());
      }
   }

   /**
    * Initial.
    * 
    */
   protected void initial() {
      addStyleName("screen-btn");
      btnTable.setWidget(1, 1, center);
      add(btnTable);
   }

   @Override
   public void setName(String name) {
      uiButton.setName(name);
      adjustTextLength();
   }

   @Override
   public String getName() {
      return uiButton.getName();
   }

   /**
    * Sets the center icon url.
    * 
    */
   public void setIcon(String icon) {
      image.setUrl(icon);
      btnTable.removeStyleName("screen-btn-cont");
      btnTable.setWidget(1, 1, image);
   }

   @Override
   public PropertyForm getPropertiesForm() {
      return new ButtonPropertyForm(this, uiButton);
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
         int ajustLength = (width - 6) / 7;
         if (ajustLength < uiButton.getName().length()) {
            center.setText(uiButton.getName().substring(0, ajustLength) + "..");
         } else {
            center.setText(uiButton.getName());
         }
      }
   }
}
