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

import org.openremote.modeler.domain.component.UITabbarItem;

/**
 * The Class Group.
 */
@SuppressWarnings("serial")
public class Group extends RefedEntity {

   /** The default name index. */
   private static int defaultNameIndex = 1;
   
   /** The name. */
   private String name;
   
   /** The screen refs. */
   private List<ScreenRef> screenRefs = new ArrayList<ScreenRef>();

   private List<UITabbarItem> tabbarItems = new ArrayList<UITabbarItem>();
   
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
    * Gets the screen refs.
    * 
    * @return the screen refs
    */
   public List<ScreenRef> getScreenRefs() {
      return screenRefs;
   }

   /**
    * Sets the screen refs.
    * 
    * @param screenRefs the new screen refs
    */
   public void setScreenRefs(List<ScreenRef> screenRefs) {
      this.screenRefs = screenRefs;
   }

   /**
    * Adds the screen ref.
    * 
    * @param screen the screen
    */
   public void addScreenRef(ScreenRef screenRef) {
      screenRefs.add(screenRef);
   }
   
   public void removeScreenRef(ScreenRef screenRef) {
      screenRefs.remove(screenRef);
   }
   
   public void insertScreenRef(ScreenRef before, ScreenRef target) {
      int index = screenRefs.indexOf(before);
      screenRefs.add(index, target);
   }
   public List<UITabbarItem> getTabbarItems() {
      return tabbarItems;
   }

   public void setTabbarItems(List<UITabbarItem> tabbarItems) {
      this.tabbarItems = tabbarItems;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Transient
   public String getDisplayName() {
      return name;
   }
   
   /**
    * Gets the new default name.
    * 
    * @return the new default name
    */
   @Transient
   public static String getNewDefaultName() {
      return "group" + defaultNameIndex;
   }
   
   public void clearScreenRefs() {
      screenRefs.clear();
   }
   
   @Transient
   public static void increaseDefaultNameIndex() {
      defaultNameIndex++;
   }
}
