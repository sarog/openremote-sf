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
package org.openremote.modeler.client.widget.uidesigner;

import org.openremote.modeler.client.icon.uidesigner.UIDesignerImages;
import org.openremote.modeler.domain.UIButton;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * The Class ScreenButton.
 */
public class ScreenButton extends LayoutContainer {
   
   /** The ui designer images. */
   private UIDesignerImages uiDesignerImages = GWT.create(UIDesignerImages.class);
   
   /** The name label. */
   private Label nameLabel;
   
   /**
    * Instantiates a new screen button.
    * 
    * @param button the button
    * @param width the width
    * @param height the height
    */
   public ScreenButton(UIButton button, int width, int height) {
      initial(button, width, height);
   }
   
   /**
    * Initial.
    * 
    * @param button the button
    * @param width the width
    * @param height the height
    */
   private void initial(UIButton button, int width, int height){
      setData("button", button);
      setToolTip(button.getName());
      setLayout(new BorderLayout());
      setSize(width, height);
      addStyleName("absolute");
      addStyleName("cursor-move");
      nameLabel = new Label(button.getLabel());
      nameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      nameLabel.addStyleName("label-font");
      Image image = uiDesignerImages.iphoneBtn().createImage();
      add(nameLabel, new BorderLayoutData(LayoutRegion.CENTER)); // add label first to make the image as background.
      add(image, new BorderLayoutData(LayoutRegion.CENTER));
   }
   
   /**
    * Sets the label.
    * 
    * @param label the new label
    */
   public void setLabel(String label){
      nameLabel.setText(label);
   }
}
