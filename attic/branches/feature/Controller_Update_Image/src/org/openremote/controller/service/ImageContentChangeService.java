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
package org.openremote.controller.service;

/**
 * @author tomsky.wang 2010-11-3
 *
 */
public interface ImageContentChangeService {

   /**
    * Refresh image content timeStamps.
    * 
    * Get image file timeStamps of each panel, and refresh them to ImageContentTimeStampData.  
    */
   public void refreshImageContentTimeStamps();
   
   /**
    * Gets the changed panel console images as a xml.
    * 
    * @param panel
    *           the panel name
    * @param console
    *           the device id
    * 
    * @return the changed panel console images
    */
   public String getChangedPanelConsoleImages(String panel, String console);
}
