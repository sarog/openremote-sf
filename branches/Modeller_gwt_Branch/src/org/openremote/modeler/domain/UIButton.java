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
package org.openremote.modeler.domain;

import org.openremote.modeler.client.model.Position;

/**
 * The Class UIButton.
 */
@SuppressWarnings("serial")
public class UIButton extends BusinessEntity {

   /** The label. */
   private String label;

   /** The position. */
   private Position position;
   
   /** The icon. */
   private String icon;

   /** The width. */
   private int width;

   /** The height. */
   private int height;

   /** The ui command. */
   private UICommand uiCommand;

   /**
    * Instantiates a new uI button.
    * 
    * @param id the id
    */
   public UIButton(long id) {
      super(id);
   }

   /**
    * Gets the height.
    * 
    * @return the height
    */
   public int getHeight() {
      return height;
   }

   /**
    * Sets the height.
    * 
    * @param height the new height
    */
   public void setHeight(int height) {
      this.height = height;
   }

   /**
    * Gets the position.
    * 
    * @return the position
    */
   public Position getPosition() {
      return position;
   }

   /**
    * Sets the position.
    * 
    * @param position the new position
    */
   public void setPosition(Position position) {
      this.position = position;
   }

   /**
    * Gets the icon.
    * 
    * @return the icon
    */
   public String getIcon() {
      return icon;
   }

   /**
    * Sets the icon.
    * 
    * @param icon the new icon
    */
   public void setIcon(String icon) {
      this.icon = icon;
   }

   /**
    * Gets the label.
    * 
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the label.
    * 
    * @param label the new label
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * Gets the width.
    * 
    * @return the width
    */
   public int getWidth() {
      return width;
   }

   /**
    * Sets the width.
    * 
    * @param width the new width
    */
   public void setWidth(int width) {
      this.width = width;
   }

   /**
    * Gets the ui command.
    * 
    * @return the ui command
    */
   public UICommand getUiCommand() {
      return uiCommand;
   }

   /**
    * Sets the ui command.
    * 
    * @param uiCommand the new ui command
    */
   public void setUiCommand(UICommand uiCommand) {
      this.uiCommand = uiCommand;
   }

}
