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
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.controller.service.FileService;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.ZipUtil;

/**
 * A implementation for FileService class.
 * 
 * @author Dan 2009-5-14
 */
public class FileServiceImpl implements FileService {
   
   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(FileServiceImpl.class);
   
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
      unzip(inputStream, PathUtil.webappsLocation());
      File lircdConfFile = new File(PathUtil.webappsLocation() + "lircd.conf");
      File etcDir = new File("/etc");
      try {
         if(etcDir.exists() && lircdConfFile.exists()){
            //this needs root user to put lircd.conf into /etc.
            //because it's readonly, or it won't be modified.
            FileUtils.copyFileToDirectory(lircdConfFile, etcDir);
         }
      } catch (IOException e) {
         logger.error("Can't copy lircd.conf to /etc.", e);
      }
   }

}
