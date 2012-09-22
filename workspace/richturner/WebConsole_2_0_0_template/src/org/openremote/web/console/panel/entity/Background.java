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
package org.openremote.web.console.panel.entity;

/**
 * The background of screen, which contains background position in screen.
 * The position include absolute position and relative position.
 * 
 */
public interface Background {

   public void setFillScreen(Boolean fillScreen);

	public void setImage(Image backgroundImage);
	
	public void setRelative(String relative);
	
	public void setAbsolute(AbsolutePosition absPos);

   public Boolean getFillScreen();
   
   public String getRelative();
   
   public Image getImage();
   
   public AbsolutePosition getAbsolute();
}
