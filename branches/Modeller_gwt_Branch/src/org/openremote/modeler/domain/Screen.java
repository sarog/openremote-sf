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

import javax.persistence.Transient;

import org.openremote.modeler.touchpanel.TouchPanelDefinition;

/**
 * The Class UIScreen.
 */
@SuppressWarnings("serial")
public class Screen extends BusinessEntity {

   /** The default name index. */
   private static int defaultNameIndex = 1;
   
   /** The name. */
   private String name;
   
   /** The absolute. */
   private boolean absoluteLayout;
   
   /** The grid. */
   private Grid grid;
   
   /** The absolutes. */
   private List<Absolute> absolutes = new ArrayList<Absolute>();
   
   /** The touch panel definition. */
   private TouchPanelDefinition touchPanelDefinition;
   
   /** The background. */
   private String background = "";

   /**
    * Gets the name.
    * 
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the grid.
    * 
    * @return the grid
    */
   public Grid getGrid() {
      return grid;
   }

   /**
    * Gets the absolutes.
    * 
    * @return the absolutes
    */
   public List<Absolute> getAbsolutes() {
      return absolutes;
   }

   /**
    * Gets the touch panel definition.
    * 
    * @return the touch panel definition
    */
   public TouchPanelDefinition getTouchPanelDefinition() {
      return touchPanelDefinition;
   }

   /**
    * Gets the background.
    * 
    * @return the background
    */
   public String getBackground() {
      return background;
   }

   public String getCSSBackground() {
      return background.replaceAll(" ", "%20");
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
    * Sets the grid.
    * 
    * @param grid the new grid
    */
   public void setGrid(Grid grid) {
      if (!absoluteLayout) {
         this.grid = grid;
      }
   }

   /**
    * Sets the absolutes.
    * 
    * @param absolutes the new absolutes
    */
   public void setAbsolutes(List<Absolute> absolutes) {
      if (absoluteLayout) {
         this.absolutes = absolutes;
      }
   }

   /**
    * Adds the absolute.
    * 
    * @param absolute the absolute
    */
   public void addAbsolute(Absolute absolute) {
      if (absoluteLayout) {
         this.absolutes.add(absolute);
      }
   }
   
   /**
    * Sets the touch panel definition.
    * 
    * @param touchPanelDefinition the new touch panel definition
    */
   public void setTouchPanelDefinition(TouchPanelDefinition touchPanelDefinition) {
      this.touchPanelDefinition = touchPanelDefinition;
   }

   /**
    * Sets the background.
    * 
    * @param background the new background
    */
   public void setBackground(String background) {
      this.background = background;
   }

   /**
    * Checks if is absolute layout.
    * 
    * @return true, if is absolute layout
    */
   public boolean isAbsoluteLayout() {
      return absoluteLayout;
   }

   /**
    * Sets the absolute layout.
    * 
    * @param absoluteLayout the new absolute layout
    */
   public void setAbsoluteLayout(boolean absoluteLayout) {
      this.absoluteLayout = absoluteLayout;
   }
   
   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Transient
   public String getDisplayName() {
      return name;
   }
   
   /**
    * Gets the new default name when you want a new name. such as screen1.
    * 
    * @return the new default name
    */
   @Transient
   public static String getNewDefaultName() {
      return "screen" + defaultNameIndex;
   }
   
   @Transient
   public static void increaseDefaultNameIndex() {
      defaultNameIndex++;
   }
   
   public void removeAbsolute(Absolute absolute) {
      if (absoluteLayout && this.absolutes.size() > 0) {
         this.absolutes.remove(absolute);
      }
   }
}
