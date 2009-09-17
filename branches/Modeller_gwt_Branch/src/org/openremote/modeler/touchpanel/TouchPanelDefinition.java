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
package org.openremote.modeler.touchpanel;

import java.io.Serializable;

/**
 * The Class TouchPanelDefinition define the touch panel's properties, which match along with panel xml file.
 */
public class TouchPanelDefinition implements Serializable {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -4187486425514081932L;

   /** The type. */
   private String type;
   
   /** The name. */
   private String name;
   
   /** The bg image. */
   private String bgImage;
   
   /** The width. */
   private int width;
   
   /** The height. */
   private int height;
   
   /** The padding left. */
   private int paddingLeft;
   
   /** The padding top. */
   private int paddingTop;
   
   /** The grid. */
   private TouchPanelGridDefinition grid;
   
   /**
    * Instantiates a new panel definition.
    */
   public TouchPanelDefinition() {
   }
   
   /**
    * Gets the type.
    * 
    * @return the type
    */
   public String getType() {
      return type;
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
    * Gets the bg image.
    * 
    * @return the bg image
    */
   public String getBgImage() {
      return bgImage;
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
    * Sets the type.
    * 
    * @param type the new type
    */
   public void setType(String type) {
      this.type = type;
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
    * Sets the bg image.
    * 
    * @param bgImage the new bg image
    */
   public void setBgImage(String bgImage) {
      this.bgImage = bgImage;
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
    * Gets the padding left.
    * 
    * @return the padding left
    */
   public int getPaddingLeft() {
      return paddingLeft;
   }
   
   /**
    * Gets the padding top.
    * 
    * @return the padding top
    */
   public int getPaddingTop() {
      return paddingTop;
   }
   
   /**
    * Gets the grid.
    * 
    * @return the grid
    */
   public TouchPanelGridDefinition getGrid() {
      return grid;
   }
   
   /**
    * Sets the padding left.
    * 
    * @param paddingLeft the new padding left
    */
   public void setPaddingLeft(int paddingLeft) {
      this.paddingLeft = paddingLeft;
   }
   
   /**
    * Sets the padding top.
    * 
    * @param paddingTop the new padding top
    */
   public void setPaddingTop(int paddingTop) {
      this.paddingTop = paddingTop;
   }
   
   /**
    * Sets the grid.
    * 
    * @param grid the new grid
    */
   public void setGrid(TouchPanelGridDefinition grid) {
      this.grid = grid;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      TouchPanelDefinition that = (TouchPanelDefinition) o;
      if (width != that.width) {
         return false;
      }
      if (height != that.height) {
         return false;
      }
      if (paddingLeft != that.paddingLeft) {
         return false;
      }
      if (paddingTop != that.paddingTop) {
         return false;
      }
      if (type != null ? !type.equals(that.type) : that.type != null) {
         return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null) {
         return false;
      }
      if (bgImage != null ? !bgImage.equals(that.bgImage) : that.bgImage != null) {
         return false;
      }
      if (grid == null && that.grid == null) {
         return true;
      }
      if (grid == null || that.grid == null) {
         return false;
      }
      if (grid.getWidth() != that.grid.getWidth()) {
         return false;
      }
      if (grid.getHeight() != that.grid.getHeight()) {
         return false;
      }
      return true;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      int result = type != null ? type.hashCode() : 0;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (bgImage != null ? bgImage.hashCode() : 0);
      result = 31 * result + (grid != null ? grid.hashCode() : 0);
      return result;
   }
}
