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
 * This class is used to store the background image information for a screen. 
 * @author Javen
 *
 */
@SuppressWarnings("serial")
public class Background extends BusinessEntity {
   
   private String src = "";
   private boolean fillScreen = true;
   private  boolean absolute = false;
   private int left = 0;
   private int top = 0;
   private int width = 0;
   private int height = 0;
   private RelativeType relatedType = RelativeType.TOP_LEFT;
   
   public Background() {
      this.src = "";
   }
   public Background(String src) {
      this.src = src;
   }

   public String getSrc() {
      return src;
   }
   public void setSrc(String src) {
      this.src = src;
   }
   public boolean isFillScreen() {
      return fillScreen;
   }
   public void setFillScreen(boolean fillScreen) {
      this.fillScreen = fillScreen;
   }
   
   
   public boolean isAbsolute() {
      return absolute;
   }
   public void setAbsolute(boolean absolute) {
      this.absolute = absolute;
   }
   public int getLeft() {
      return left;
   }
   public void setLeft(int left) {
      this.left = left;
   }
   public int getTop() {
      return top;
   }
   public void setTop(int top) {
      this.top = top;
   }
   public int getWidth() {
      return width;
   }
   public void setWidth(int width) {
      this.width = width;
   }
   public int getHeight() {
      return height;
   }
   public void setHeight(int height) {
      this.height = height;
   }

   
   public RelativeType getRelatedType() {
      return relatedType;
   }

   public void setRelatedType(RelativeType relatedType) {
      this.relatedType = relatedType;
   }

   /**
    * Some type for relative Type. 
    * @author Javen
    *
    */
   public static enum RelativeType {
      LEFT, RIGHT, TOP, BOTTOM, TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT, CENTER;

      @Override
      public String toString() {
         return super.toString().replace("_", "-");
      }
      
      
   }
}
