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
   public void unzip(InputStream inputStream, String targetDir) {
      ZipUtil.unzip(inputStream, targetDir);
   }

   /**
    * {@inheritDoc}
    */
   public void uploadConfigZip(InputStream inputStream) {
      String resourcePath = configuration.getResourcePath();
      try {
         FileUtils.forceDeleteOnExit(new File(resourcePath));
      } catch (IOException e1) {
         logger.error("Can't delete" + resourcePath, e1);
      }
      unzip(inputStream, resourcePath);
      File lircdConfFile = new File(resourcePath + Constants.LIRCD_CONF);
      File lircdconfDir = new File(configuration.getLircdconfPath().replaceAll(Constants.LIRCD_CONF, ""));
      try {
         if(lircdconfDir.exists() && lircdConfFile.exists()){
            //this needs root user to put lircd.conf into /etc.
            //because it's readonly, or it won't be modified.
            if ("true".equalsIgnoreCase(configuration.getCopyLircdconf())) {
               FileUtils.copyFileToDirectory(lircdConfFile, lircdconfDir);
            }
         }
      } catch (IOException e) {
         logger.error("Can't copy lircd.conf to " + configuration.getLircdconfPath(), e);
      }
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
