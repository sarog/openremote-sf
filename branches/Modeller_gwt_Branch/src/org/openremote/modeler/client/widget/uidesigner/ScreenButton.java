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

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The <Code>Button</Code> to be used in ScreenPanel.
 */
public class ScreenButton extends LayoutContainer {
   
   /** The ui designer images. */
   private UIDesignerImages uiDesignerImages = GWT.create(UIDesignerImages.class);
   
   public static final String DATA_BUTTON = "button"; 
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
      setData(DATA_BUTTON, button);
//      setToolTip(button.getName());
//      setLayout(new BorderLayout());
      setSize(width, height);
      addStyleName("absolute");
      addStyleName("cursor-move");
      nameLabel = new Label(button.getLabel());
//      nameLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//      nameLabel.addStyleName("label-font");
//      Image image = uiDesignerImages.iphoneBtn().createImage();
//      add(nameLabel, new BorderLayoutData(LayoutRegion.CENTER)); // add label first to make the image as background.
//      add(image, new BorderLayoutData(LayoutRegion.CENTER));
      
      
//      setBorders(true);
      FlexTable btnTable = new FlexTable();
      btnTable.addStyleName("button-table");
      btnTable.setHeight("100%");
      btnTable.setWidth("100%");
      btnTable.setCellPadding(0);
      btnTable.setCellSpacing(0);
      
      Text center = new Text("center");
      center.setTagName("span");
      center.addStyleName("label-font");
      center.setSize("100%", "100%");
      
      btnTable.setWidget(0, 0, null);
      btnTable.setWidget(0, 1, null);
      btnTable.setWidget(0, 2, null);
      
      btnTable.setWidget(1, 0, null);
      btnTable.setWidget(1, 1, center);
      btnTable.setWidget(1, 2, null);
      
      btnTable.setWidget(2, 0, null);
      btnTable.setWidget(2, 1, null);
      btnTable.setWidget(2, 2, null);
      
      btnTable.getCellFormatter().addStyleName(0, 0, "tlc-image");
      btnTable.getCellFormatter().addStyleName(0, 1, "top-image");
      btnTable.getCellFormatter().addStyleName(0, 2, "trc-image");
      
      btnTable.getCellFormatter().addStyleName(1, 0, "ml-image");
      btnTable.getCellFormatter().addStyleName(1, 1, "text-center");
      btnTable.getCellFormatter().addStyleName(1, 2, "mr-image");
      
      btnTable.getCellFormatter().addStyleName(2, 0, "blc-image");
      btnTable.getCellFormatter().addStyleName(2, 1, "bottom-image");
      btnTable.getCellFormatter().addStyleName(2, 2, "brc-image");
      
      btnTable.getRowFormatter().addStyleName(0, "top");
      btnTable.getRowFormatter().addStyleName(2, "bottom");
      add(btnTable);
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
