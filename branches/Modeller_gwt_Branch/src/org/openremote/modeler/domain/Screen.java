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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Screen.
 */
@SuppressWarnings("serial")
public class Screen extends BusinessEntity {

   /** The name. */
   private String name;

   /** The activity. */
   private Activity activity;
   
   /** The row count. */
   private Integer rowCount;

   /** The column count. */
   private Integer columnCount;

   /** The buttons. */
   private List<UIButton> buttons = new ArrayList<UIButton>();
   
   /**
    * Gets the row count.
    * 
    * @return the row count
    */
   public Integer getRowCount() {
      return rowCount;
   }

   /**
    * Sets the row count.
    * 
    * @param rowCount the new row count
    */
   public void setRowCount(Integer rowCount) {
      this.rowCount = rowCount;
   }

   /**
    * Gets the column count.
    * 
    * @return the column count
    */
   public Integer getColumnCount() {
      return columnCount;
   }

   /**
    * Sets the column count.
    * 
    * @param columnCount the new column count
    */
   public void setColumnCount(Integer columnCount) {
      this.columnCount = columnCount;
   }

   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the buttons.
    * 
    * @return the buttons
    */
   public List<UIButton> getButtons() {
      return buttons;
   }

   /**
    * Sets the buttons.
    * 
    * @param buttons the new buttons
    */
   public void setButtons(List<UIButton> buttons) {
      this.buttons = buttons;
   }
   
   /**
    * Adds the button.
    * 
    * @param button the button
    */
   public void addButton(UIButton button) {
      this.buttons.add(button);
   }
   
   /**
    * Delete button.
    * 
    * @param button the button
    */
   public void deleteButton(UIButton button) {
      if(this.buttons.contains(button)){
         this.buttons.remove(button);
      }
   }

   public Activity getActivity() {
      return activity;
   }

   public void setActivity(Activity activity) {
      this.activity = activity;
   }
   
}
