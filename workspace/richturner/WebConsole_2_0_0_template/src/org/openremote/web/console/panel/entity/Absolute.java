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
package org.openremote.web.console.panel.entity;


/**
 * The subclass of LayoutContainer which includes a component.
 * It parse the absolute node, contains size and position info.
 * 
 */
public interface Absolute {
   public Label getLabel();
   
   public int getWidth();
   
   public int getHeight();
   
   public int getLeft();
   
   public int getTop();
   
   public void setLabel(Label label);
   
   public void setWidth(int width);
   
   public void setHeight(int height);
   
   public void setLeft(int left);
   
   public void setTop(int top);
}
