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

import javax.persistence.Transient;

/**
 * The Class ScreenRef.
 */
@SuppressWarnings("serial")
public class ScreenRef extends BusinessEntity {

   /** The screen. */
   private Screen screen;
   
   /** The group. */
   private Group group;
   
   public ScreenRef() {
   }
   /**
    * Instantiates a new screen ref.
    * 
    * @param screen the screen
    */
   public ScreenRef(Screen screen) {
      super();
      screen.ref();
      this.screen = screen;
   }
   
   
   /**
    * Gets the screen.
    * 
    * @return the screen
    */
   public Screen getScreen() {
      return screen;
   }

   /**
    * Sets the screen.
    * 
    * @param screen the new screen
    */
   public void setScreen(Screen screen) {
      if (this.screen != null) {
         this.screen.releaseRef();
      }
      screen.ref();
      this.screen = screen;
   }

   
   /**
    * Gets the group.
    * 
    * @return the group
    */
   public Group getGroup() {
      return group;
   }

   /**
    * Sets the group.
    * 
    * @param group the new group
    */
   public void setGroup(Group group) {
      this.group = group;
   }


   /**
    * {@inheritDoc}
    * @see org.openremote.modeler.domain.BusinessEntity#getDisplayName()
    */
   @Override
   @Transient
   public String getDisplayName() {
      return screen.getName();
   }
   
   @Transient
   public long getScreenId() {
      return screen.getOid();
   }
}
