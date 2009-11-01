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
package org.openremote.controller.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.controller.Configuration;
import org.openremote.controller.Constants;
import org.openremote.controller.service.FileService;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.ZipUtil;

/**
 * The implementation for FileService interface.
 * 
 * @author Dan 2009-5-14
 */
public class FileServiceImpl implements FileService {
   
   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(FileServiceImpl.class);
   
   /** The configuration. */
   private Configuration configuration;
   
   /**
    * {@inheritDoc}
    */
   public boolean unzip(InputStream inputStream, String targetDir) {
      return ZipUtil.unzip(inputStream, targetDir);
   }

   /**
    * {@inheritDoc}
    */
   public boolean uploadConfigZip(InputStream inputStream) {
      String resourcePath = configuration.getResourcePath();
      try {
         FileUtils.forceDeleteOnExit(new File(resourcePath));
      } catch (IOException e1) {
         logger.error("Can't delete" + resourcePath, e1);
      }
      if (!unzip(inputStream, resourcePath)){
         return false; 
      }
      File lircdConfFile = new File(resourcePath + Constants.LIRCD_CONF);
      File lircdconfDir = new File(configuration.getLircdconfPath().replaceAll(Constants.LIRCD_CONF, ""));
      try {
         if(lircdconfDir.exists() && lircdConfFile.exists()){
            //this needs root user to put lircd.conf into /etc.
            //because it's readonly, or it won't be modified.
            if (configuration.isCopyLircdconf()) {
               FileUtils.copyFileToDirectory(lircdConfFile, lircdconfDir);
            }
         }
         logger.info("copy lircd.conf to" + configuration.getLircdconfPath());
      } catch (IOException e) {
         logger.error("Can't copy lircd.conf to " + configuration.getLircdconfPath(), e);
         return false;
      }
      logger.info("uploaded config zip to " + resourcePath);
      return true;
   }


   /* (non-Javadoc)
    * @see org.openremote.controller.service.FileService#findResource(java.lang.String)
    */
   public InputStream findResource(String relativePath) {
      File file = new File(PathUtil.removeSlashSuffix(configuration.getResourcePath()) + relativePath);
      if (file.exists() && file.isFile()) {
         try {
            return new FileInputStream(file);
         } catch (FileNotFoundException e) {
            return null;
         }
      }
      return null;
   }
   
   /**
    * Sets the configuration.
    * 
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

}
