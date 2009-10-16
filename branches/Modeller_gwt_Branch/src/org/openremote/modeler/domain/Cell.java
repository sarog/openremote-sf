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

/**
 * The Class Cell.
 */
@SuppressWarnings("serial")
public class Cell extends BusinessEntity {

   /** The pos x. */
   private int posX;
   
   /** The pos y. */
   private int posY;
   
   /** The rowspan. */
   private int rowspan;
   
   /** The colspan. */
   private int colspan;
   
   /** The screen widget. */
   private ScreenWidget screenWidget;
   
   /**
    * Gets the pos x.
    * 
    * @return the pos x
    */
   public int getPosX() {
      return posX;
   }
   
   /**
    * Gets the pos y.
    * 
    * @return the pos y
    */
   public int getPosY() {
      return posY;
   }
   
   /**
    * Gets the rowspan.
    * 
    * @return the rowspan
    */
   public int getRowspan() {
      return rowspan;
   }
   
   /**
    * Gets the colspan.
    * 
    * @return the colspan
    */
   public int getColspan() {
      return colspan;
   }
   
   /**
    * Sets the pos x.
    * 
    * @param posX the new pos x
    */
   public void setPosX(int posX) {
      this.posX = posX;
   }
   
   /**
    * Sets the pos y.
    * 
    * @param posY the new pos y
    */
   public void setPosY(int posY) {
      this.posY = posY;
   }
   
   /**
    * Sets the rowspan.
    * 
    * @param rowspan the new rowspan
    */
   public void setRowspan(int rowspan) {
      this.rowspan = rowspan;
   }
   
   /**
    * Sets the colspan.
    * 
    * @param colspan the new colspan
    */
   public void setColspan(int colspan) {
      this.colspan = colspan;
   }

   /**
    * Gets the screen widget.
    * 
    * @return the screen widget
    */
   public ScreenWidget getScreenWidget() {
      return screenWidget;
   }

   /**
    * Sets the screen widget.
    * 
    * @param screenWidget the new screen widget
    */
   public void setScreenWidget(ScreenWidget screenWidget) {
      this.screenWidget = screenWidget;
   }
   
}
