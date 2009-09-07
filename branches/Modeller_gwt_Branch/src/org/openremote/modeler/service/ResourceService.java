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
package org.openremote.modeler.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.openremote.modeler.domain.Activity;

/**
 * The Interface ResourceService.
 * 
 * @author Allen, Handy
 */

public interface ResourceService {
   
   /**
    * Download zip resource.
    * 
    * @param maxId the max id
    * @param activities the activities
    * 
    * @return the URL string
    */
   public String downloadZipResource(long maxId, List<Activity> activities);

   /**
    * Gets the irb file from zip.
    * 
    * @param inputStream the input stream
    * @param sessionId the session id
    * 
    * @return the irb file from zip
    */
   public String getIrbFileFromZip(InputStream inputStream,String sessionId);
   
   /**
    * Upload image.
    * 
    * @param inputStream the input stream
    * @param fileName the file name
    * @param sessionId the session id
    * 
    * @return the file
    */
   public File uploadImage(InputStream inputStream,String fileName,String sessionId);
}