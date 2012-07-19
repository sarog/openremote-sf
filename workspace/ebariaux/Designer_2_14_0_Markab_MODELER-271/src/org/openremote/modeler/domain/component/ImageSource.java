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

import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.shared.PropertyChangeListener;
import org.openremote.modeler.shared.PropertyChangeSupport;


/**
 * Define image source path. The path can be a url or a file path.
 */
public class ImageSource extends BusinessEntity {

   private static final long serialVersionUID = 641025600256733725L;
   
   protected transient PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

   private String src;
   public ImageSource() {
   }
   
   public ImageSource(String src) {
     String oldSrc = this.src;
     this.src = src;
     this.pcSupport.firePropertyChange("src", oldSrc, this.src);
   }
   
   public String getSrc() {
      return src;
   }
   public void setSrc(String src) {
      this.src = src;
   }
   public String getImageFileName() {
      String result = "";
      if (src != null && src.trim().length() != 0) {
         result = src.substring(src.lastIndexOf("/") + 1);
      }
      return result;
   }
   
   public boolean isEmpty () {
      if (src == null || src.trim().length() == 0) return true;
      return false;
   }
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((src == null) ? 0 : src.hashCode());
      return result;
   }
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ImageSource other = (ImageSource) obj;
      if (src == null) {
         if (other.src != null) return false;
      } else if (!src.equals(other.src)) return false;
      return true;
   }
   
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
     this.pcSupport.addPropertyChangeListener(propertyName, listener);
   }
   
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
     this.pcSupport.removePropertyChangeListener(propertyName, listener);
   }

}
