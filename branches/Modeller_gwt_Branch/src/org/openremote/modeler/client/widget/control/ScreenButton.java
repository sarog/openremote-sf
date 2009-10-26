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

import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class ScreenButton. It display as a style box, can be adjust size.
 */
public class ScreenButton extends ScreenControl {

   /** The Constant DATA_BUTTON. */
   public static final String DATA_BUTTON = "button";

   /** The Constant LABEL. */
   private static final String LABEL = "Label: ";
   
   /** The button table. */
   private FlexTable btnTable = new FlexTable();

   /** The center. */
   private Text center = new Text();

   /** The image. */
   private Image image = new Image();

   /**
    * Instantiates a new screen button.
    */
   public ScreenButton() {
      initial();
   }
   
   public ScreenButton(String text) {
      this();
      center.setText(text);
   }
   
   /**
    * Instantiates a new screen button.
    * 
    * @param button the button
    * @param width the width
    * @param height the height
    */
   public ScreenButton(int width, int height) {
      this();
      setSize(width, height);
   }


   /**
    * Initial.
    * 
    * @param width the width
    * @param height the height
    */
   private void initial() {
//      setData(DATA_BUTTON, button);
//      UICommand uiCommand = button.getUiCommand();
//      String displayName = uiCommand.getDisplayName();
//      originalName = originalName + displayName;
//      setToolTip(LABEL + button.getLabel() + originalName);
//      setSize(30, 20);
      center.setText("Button");
      
      addStyleName("screen-btn");
      btnTable.addStyleName("screen-btn-cont");
      btnTable.setCellPadding(0);
      btnTable.setCellSpacing(0);
      
//      adjustTextLength(width);
      btnTable.setWidget(0, 0, null);
      btnTable.setWidget(0, 1, null);
      btnTable.setWidget(0, 2, null);

      btnTable.setWidget(1, 0, null);
      btnTable.setWidget(1, 1, center);
      btnTable.setWidget(1, 2, null);

      btnTable.setWidget(2, 0, null);
      btnTable.setWidget(2, 1, null);
      btnTable.setWidget(2, 2, null);

      btnTable.getCellFormatter().addStyleName(0, 0, "tl-c");
      btnTable.getCellFormatter().addStyleName(0, 1, "top");
      btnTable.getCellFormatter().addStyleName(0, 2, "tr-c");

      btnTable.getCellFormatter().addStyleName(1, 0, "ml");
      btnTable.getCellFormatter().addStyleName(1, 1, "middle");
      btnTable.getCellFormatter().addStyleName(1, 2, "mr");

      btnTable.getCellFormatter().addStyleName(2, 0, "bl-c");
      btnTable.getCellFormatter().addStyleName(2, 1, "bottom");
      btnTable.getCellFormatter().addStyleName(2, 2, "br-c");

      btnTable.getRowFormatter().addStyleName(0, "screen-top");
      btnTable.getRowFormatter().addStyleName(2, "screen-bottom");

      add(btnTable);
   }
   
   @Override
   public void setName(String name) {
      center.setText(name);
   }
   
   public String getName() {
      return center.getText();
   }
}
