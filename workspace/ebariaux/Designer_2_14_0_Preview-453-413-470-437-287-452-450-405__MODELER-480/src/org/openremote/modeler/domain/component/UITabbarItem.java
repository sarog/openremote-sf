/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

import org.openremote.modeler.domain.ConfigurationFilesGenerationContext;

/**
 * UITabbarItem defines a image, a name and a navigation.
 * It can navigates to screen or logical targets.
 */
public class UITabbarItem extends UIComponent {

   private static final long serialVersionUID = -3815544266807672929L;
   
   private String name = "Tab Bar Item";
   private ImageSource image;
   private Navigate navigate = new Navigate();
   
   public String getName() {
      return name;
   }

   public ImageSource getImage() {
      return image;
   }

   public Navigate getNavigate() {
      return navigate;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setImage(ImageSource image) {
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
   public String getPanelXml(ConfigurationFilesGenerationContext context) {
      return null;
   }
   
   @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((image == null) ? 0 : image.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((navigate == null) ? 0 : navigate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null) {
      return false;
    }
    /*
     TODO: this can be used instead of above test once equals on super implementation has been cleaned.
    if (!super.equals(obj))
      return false;
    */
    if (getClass() != obj.getClass())
      return false;
    UITabbarItem other = (UITabbarItem) obj;
    if (image == null) {
      if (other.image != null)
        return false;
    } else if (!image.equals(other.image))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (navigate == null) {
      if (other.navigate != null)
        return false;
    } else if (!navigate.equals(other.navigate))
      return false;
    return true;
  }

}
