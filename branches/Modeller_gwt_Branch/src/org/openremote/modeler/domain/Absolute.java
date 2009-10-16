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
 * The Class Absolute.
 */
@SuppressWarnings("serial")
public class Absolute extends BusinessEntity {

   /** The left. */
   private int left;
   
   /** The top. */
   private int top;
   
   /** The width. */
   private int width;
   
   /** The height. */
   private int height;
   
   /** The screen widget. */
   private ScreenWidget screenWidget;
   
   /**
    * Gets the left.
    * 
    * @return the left
    */
   public int getLeft() {
      return left;
   }
   
   /**
    * Gets the top.
    * 
    * @return the top
    */
   public int getTop() {
      return top;
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
    * Gets the height.
    * 
    * @return the height
    */
   public int getHeight() {
      return height;
   }
   
   /**
    * Sets the left.
    * 
    * @param left the new left
    */
   public void setLeft(int left) {
      this.left = left;
   }
   
   /**
    * Sets the top.
    * 
    * @param top the new top
    */
   public void setTop(int top) {
      this.top = top;
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
    * Sets the height.
    * 
    * @param height the new height
    */
   public void setHeight(int height) {
      this.height = height;
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
