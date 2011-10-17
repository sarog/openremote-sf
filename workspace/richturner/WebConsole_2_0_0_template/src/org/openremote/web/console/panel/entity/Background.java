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

import org.openremote.web.console.panel.entity.TabImage;

/**
 * The background of screen, which contains background position in screen.
 * The position include absolute position and relative position.
 * 
 */
public interface Background {

   public void setFillScreen(String fillScreen);

	public void setImage(TabImage backgroundImage);
	
	public void setRelative(String relative);

   public String getFillScreen();
   
   public String getRelative();
   
   public TabImage getImage();
}
