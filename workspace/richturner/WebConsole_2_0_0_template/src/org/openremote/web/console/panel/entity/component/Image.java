/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Label;

/**
 * The image component can has sensor and can change status.
 * It can also has a linked label to display as a label. 
 */
public interface Image {

   public void setSrc(String src);

	public void setStyle(String style);
   
   public String getSrc();
   
   public String getStyle();
   
   public Label getLabel();

   public void setLabel(Label label);
   
   public void setLabelRefId(int labelRefId);
   
   public int getLabelRefId();
}
