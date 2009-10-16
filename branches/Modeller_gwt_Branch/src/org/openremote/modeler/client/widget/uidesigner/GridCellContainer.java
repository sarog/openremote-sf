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

import org.openremote.modeler.client.model.Position;
import org.openremote.modeler.domain.UIButton;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.control.UIControl;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class GridCellContainer.
 */
public class GridCellContainer extends LayoutContainer {

   /** The ui control. */
   private UIControl uiControl;

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

   /** The button. */
   private UIButton button;
   
   /** The original name. */
   private String originalName = "<br />OriginalName: ";

   /**
    * Instantiates a new screen button.
    * 
    * @param button the button
    * @param width the width
    * @param height the height
    */
   public GridCellContainer(UIButton button, int width, int height) {
      this.button = button;
      initial(width, height);
   }

   /**
    * Instantiates a new screen button.
    */
   public GridCellContainer() {
   }

   /**
    * Initial.
    * 
    * @param width the width
    * @param height the height
    */
   private void initial(int width, int height) {
      setData(DATA_BUTTON, button);
      UICommand uiCommand = button.getUiCommand();
      String displayName = uiCommand.getDisplayName();
      originalName = originalName + displayName;
      setToolTip(LABEL + button.getLabel() + originalName);
      setSize(width, height);

      addStyleName("screen-btn");
      btnTable.addStyleName("screen-btn-cont");
      btnTable.setCellPadding(0);
      btnTable.setCellSpacing(0);

      adjustTextLength(width);
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

   /**
    * Sets the label.
    * 
    * @param label the new label
    */
   public void setLabel(String label) {
      button.setLabel(label);
      setToolTip(LABEL + label + originalName);
      adjust();
   }

   /**
    * Sets the icon.
    * 
    * @param icon the new icon
    */
   public void setIcon(String icon) {
      image.setUrl(icon);
      button.setIcon(icon);
      btnTable.removeStyleName("screen-btn-cont");
      btnTable.setWidget(1, 1, image);
   }

   /**
    * Sets the button position.
    * 
    * @param position the new button position
    */
   public void setButtonPosition(Position position) {
      button.setPosition(position);
   }

   /**
    * Gets the button position.
    * 
    * @return the button position
    */
   public Position getButtonPosition() {
      return button.getPosition();
   }

   /**
    * Sets the button width.
    * 
    * @param width the new button width
    */
   public void setButtonWidth(int width) {
      button.setWidth(width);
   }

   /**
    * Sets the button height.
    * 
    * @param height the new button height
    */
   public void setButtonHeight(int height) {
      button.setHeight(height);
   }

   /**
    * Gets the button width.
    * 
    * @return the button width
    */
   public int getButtonWidth() {
      return button.getWidth();
   }

   /**
    * Gets the button height.
    * 
    * @return the button height
    */
   public int getButtonHeight() {
      return button.getHeight();
   }

   /**
    * Fill area.
    * 
    * @param btnArea the btn area
    */
   public void fillArea(boolean[][] btnArea) {
      Position position = button.getPosition();
      for (int i = 0; i < button.getWidth(); i++) {
         int x = position.getPosX() + i;
         for (int j = 0; j < button.getHeight(); j++) {
            int y = position.getPosY() + j;
            btnArea[x][y] = true;
         }
      }
   }

   /**
    * Clear area.
    * 
    * @param btnArea the button area
    */
   public void clearArea(boolean[][] btnArea) {
      Position position = button.getPosition();
      for (int i = 0; i < button.getWidth(); i++) {
         int x = position.getPosX() + i;
         for (int j = 0; j < button.getHeight(); j++) {
            int y = position.getPosY() + j;
            btnArea[x][y] = false;
         }
      }
   }

   /**
    * Adjust layout.
    */
   public void adjust() {
      if (center.isVisible()) {
         adjustTextLength(getWidth());
      }
   }

   /**
    * Adjust text length.
    * 
    * @param length the length
    */
   private void adjustTextLength(int length) {
      int ajustLength = (length - 8) / 8;
      if (ajustLength < button.getLabel().length()) {
         center.setText(button.getLabel().substring(0, ajustLength) + "...");
      } else {
         center.setText(button.getLabel());
      }
   }
   
   /**
    * In IE if the button width is 2 cell width, it can't be resize to 1 cell width.
    * 
    * @param width the width
    */
   public void adjustCenter(int width) {
      if (center.isVisible()) {
         adjustTextLength(width - 2);
      }
   }
   
   /**
    * Gets the button's icon.
    * 
    * @return the button icon
    */
   public String getButtonIcon() {
      return button.getIcon();
   }
   
   
}
