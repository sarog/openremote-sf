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
import org.openremote.beehive.domain.Model;
import org.openremote.beehive.exception.SVNException;

// TODO: Auto-generated Javadoc
/**
 * Business service for <code>ModelDTO</code>.
 * 
 * @author allen.wei 2009-2-17
 */
public interface ModelService {
   
   /**
    * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's id.
    * 
    * @param vendorName vendor name
    * 
    * @return list of ModelDTOs
    */
   List<ModelDTO> findModelsByVendorName(String vendorName);

   /**
    * Gets all <code>ModelDTOs</code> belongs to certain <code>VendorDTO</code> according to it's name.
    * 
    * @param vendorId vendor id
    * 
    * @return list of ModelDTOs
    */
   List<ModelDTO> findModelsByVendorId(long vendorId);

   /**
    * loads <code>ModelDTO</code> by <code>VendorDTO</code> name and <code>ModelDTO</code> name.
    * 
    * @param vendorName name of VendorDTO
    * @param modelName name of ModelDTO
    * 
    * @return ModelDTO
    */
   ModelDTO loadByVendorNameAndModelName(String vendorName, String modelName);

   /**
    * loads <code>ModelDTO</code> by id.
    * 
    * @param modelId the model id
    * 
    * @return the model dto
    */
   ModelDTO loadModelById(long modelId);

   /**
    * Allows to import a LIRC Configuration file.
    * 
    * @param fis FileInputStream of the LIRC Configuration file
    * @param vendorName its vendor name
    * @param modelName its model name
    */
   void add(FileInputStream fis, String vendorName, String modelName);

   /**
    * Allows to export the content text of a LIRC Configuration. This will NOT lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the content text
    */
   String exportText(long id);

   /**
    * Allows to export the <code>File</code> of a LIRC Configuration. This will lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the file
    */
   File exportFile(long id);

   /**
    * Allows to export the file of a LIRC Configuration. This will lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the file URL to be downloaded
    */
   String downloadFile(long id);

   /**
    * Allows to export the file of a LIRC Configuration. This will NOT lead to disk writes.
    * 
    * @param id the target LIRC Configuration id
    * 
    * @return the file OutputStream to be downloaded
    */
   InputStream exportStream(long id);

   /**
    * Update database and svn repo sync.
    * 
    * @param paths the file path of the workCopy
    * @param message the comments
    * @param username the username
    * 
    * @throws SVNException the SVN exception
    */
   void update(String[] paths, String message, String username) throws SVNException;

   /**
    * Rollback svn repo to a specify revision.
    * 
    * @param path the path
    * @param revision the revision
    * @param username the username
    * 
    * @throws SVNException the SVN exception
    */
   void rollback(String path, long revision, String username) throws SVNException;
   
   /**
    * Count.
    * 
    * @return the models amount
    */
   int count();
   

   /**
    * Delete by name.
    * 
    * @param modelName the model name
    */
   void deleteByName(String modelName);
   
   /**
    * Merge.
    * 
    * @param fis the fis
    * @param model the model
    */
   void merge(FileInputStream fis, Model model);
   
   boolean isFile(String path);
   
   
}