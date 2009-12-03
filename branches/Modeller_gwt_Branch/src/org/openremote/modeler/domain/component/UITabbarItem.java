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
package org.openremote.modeler.domain.component;

import javax.persistence.Transient;

@SuppressWarnings("serial")
public class UITabbarItem extends UIComponent {

   private String name = "TabbarItem";
   private UImage image;
   private Navigate navigate = new Navigate();
   
   public String getName() {
      return name;
   }

   public UImage getImage() {
      return image;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setImage(UImage image) {
      this.image = image;
   }

   public void setNavigate(Navigate navigate) {
      this.navigate = navigate;
   }

   @Transient
   public String getDisplayName() {
      return name;
   }
   
   @Override
   public String getPanelXml() {
      return null;
   }

   @Override
   public void transImagePathToRelative(String relativeSessionFolderPath) {

   }

}
