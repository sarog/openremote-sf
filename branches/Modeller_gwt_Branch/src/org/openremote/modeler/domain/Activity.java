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


/**
 * The Class Activity.
 * @author allen.wei
 */
@SuppressWarnings("serial")
public class Activity extends BusinessEntity {
   
   
   /** The default name index. */
   private static int defaultNameIndex = 1;
   
   /** The name of the activity. It is the  display name on the device */
   private String name;
   
   /** The screens. */
   private List<Screen> screens = new ArrayList<Screen>();
   
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
    * Gets the screens.
    * 
    * @return the screens
    */
   public List<Screen> getScreens() {
      return screens;
   }
   
   /**
    * Sets the screens.
    * 
    * @param screens the new screens
    */
   public void setScreens(List<Screen> screens) {
      this.screens = screens;
   }
   
   /**
    * Adds the screen.
    * 
    * @param screen the screen
    */
   public void addScreen(Screen screen) {
      screens.add(screen);
   }
   
   /**
    * Delete screen.
    * 
    * @param screen the screen
    */
   public void deleteScreen(Screen screen) {
      screens.remove(screen);
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
    * Gets the new default name when you want a new name. such as activity1.
    * 
    * @return the new default name
    */
   @Transient
   public static String getNewDefaultName() {
      return "activity" + defaultNameIndex++;
   }
}
