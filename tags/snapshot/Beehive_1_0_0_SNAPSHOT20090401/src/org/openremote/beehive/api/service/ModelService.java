/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.api.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.openremote.beehive.api.dto.ModelDTO;

/**
 * Business service for <code>ModelDTO</code>
 * 
 * @author allen.wei 2009-2-17
 */
public interface ModelService {
   /**
    * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's id
    * 
    * @param vendorName
    *           vendor name
    * @return list of ModelDTOs
    */
   List<ModelDTO> findModelsByVendorName(String vendorName);

   /**
    * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's name
    * 
    * @param vendorId
    *           vendor id
    * @return list of ModelDTOs
    */
   List<ModelDTO> findModelsByVendorId(long vendorId);

   /**
    * loads <code>ModelDTO</code> by <code>VendorDTO</code> name and <code>ModelDTO</code> name
    * 
    * @param vendorName
    *           name of VendorDTO
    * @param modelName
    *           name of ModelDTO
    * @return ModelDTO
    */
   ModelDTO loadByVendorNameAndModelName(String vendorName, String modelName);

   /**
    * loads <code>ModelDTO</code> by id
    * 
    * @param modelId
    * @return
    */
   ModelDTO loadModelById(long modelId);

   /**
    * Allows to import a LIRC Configuration file
    * 
    * @param fis
    *           FileInputStream of the LIRC Configuration file
    * @param vendorName
    *           its vendor name
    * @param modelName
    *           its model name
    */
   void add(FileInputStream fis, String vendorName, String modelName);

   /**
    * Allows to export the content text of a LIRC Configuration. This will NOT lead to disk writes.
    * 
    * @param id
    *           the target LIRC Configuration id
    * @return the content text
    */
   String exportText(long id);

   /**
    * Allows to export the <code>File</code> of a LIRC Configuration. This will lead to disk writes.
    * 
    * @param id
    *           the target LIRC Configuration id
    * @return the file
    */
   File exportFile(long id);

   /**
    * Allows to export the file of a LIRC Configuration. This will lead to disk writes.
    * 
    * @param id
    *           the target LIRC Configuration id
    * @return the file URL to be downloaded
    */
   String downloadFile(long id);

   /**
    * Allows to export the file of a LIRC Configuration. This will NOT lead to disk writes.
    * 
    * @param id
    *           the target LIRC Configuration id
    * @return the file OutputStream to be downloaded
    */
   InputStream exportStream(long id);

   void update(String[] paths, String message, String username);

   void rollback(String path, int revision, String username);

}