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
package org.openremote.controller.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 * The Utility for Zip.
 * 
 * @author Dan 2009-5-14
 */
public class ZipUtil {
   
   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(ZipUtil.class);
   
   /**
    * Unzip a zip.
    * 
    * @param inputStream the input stream
    * @param targetDir the target dir
    */
   public static void unzip(InputStream inputStream, String targetDir){
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry;
      FileOutputStream fileOutputStream = null;
      try {
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
               targetDir = targetDir.endsWith("/") || targetDir.endsWith("\\") ? targetDir : targetDir + "/";
               File zippedFile = new File(targetDir + zipEntry.getName());
               FileUtils.deleteQuietly(zippedFile);
               FileUtils.touch(zippedFile);
               fileOutputStream = new FileOutputStream(zippedFile);
               int b;
               while ((b = zipInputStream.read()) != -1) {
                  fileOutputStream.write(b);
               }
               fileOutputStream.close();
            }
         }
      } catch (IOException e) {
         logger.error("Can't unzip to " + targetDir, e);
      } finally {
         try {
            zipInputStream.closeEntry();
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            logger.error("Error while closing stream.", e);
         }

      }
   }

}
