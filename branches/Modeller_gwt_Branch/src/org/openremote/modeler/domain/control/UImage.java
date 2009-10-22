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
package org.openremote.modeler.domain.control;

import org.openremote.modeler.domain.BusinessEntity;

/**
 * The Class UImage.
 */
@SuppressWarnings("serial")
public class UImage extends BusinessEntity {

   private String src;
   private String state;
   private int border;
   public String getSrc() {
      return src;
   }
   public String getState() {
      return state;
   }
   public int getBorder() {
      return border;
   }
   public void setSrc(String src) {
      this.src = src;
   }
   public void setState(String state) {
      this.state = state;
   }
   public void setBorder(int border) {
      this.border = border;
   }
   
   
}
