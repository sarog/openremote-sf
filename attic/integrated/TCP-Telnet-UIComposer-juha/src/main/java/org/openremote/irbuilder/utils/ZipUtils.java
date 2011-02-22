/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.openremote.irbuilder.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.irbuilder.exception.FileOperationException;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class ZipUtils {
   private static final Logger logger = Logger.getLogger(ZipUtils.class);

   private ZipUtils() {
   }

   public static File compress(String outputFilePath, List<File> files) {
      final int BUFFER = 2048;
      BufferedInputStream bufferedInputStream;
      File outputFile = new File(outputFilePath);
      try {
         FileUtils.touch(outputFile);
      } catch (IOException e) {
         logger.error("create zip file fail.", e);
         throw new FileOperationException("create zip file fail.", e);
      }
      FileOutputStream fileOutputStream = null;
      ZipOutputStream zipOutputStream = null;
      FileInputStream fileInputStream;
      try {
         fileOutputStream = new FileOutputStream(outputFilePath);
         zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
         byte data[] = new byte[BUFFER];
         for (File file : files) {
            if (!file.exists()) {
               continue;
            }
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream, BUFFER);
            ZipEntry entry = new ZipEntry(file.getName());
            entry.setSize(file.length());
            entry.setTime(file.lastModified());
            zipOutputStream.putNextEntry(entry);

            int count;
            while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
               zipOutputStream.write(data, 0, count);
            }
            zipOutputStream.closeEntry();
            if (fileInputStream != null) fileInputStream.close();
            if (bufferedInputStream != null) bufferedInputStream.close();
         }
      } catch (FileNotFoundException e) {
         logger.error("Can't find the output file.", e);
         throw new FileOperationException("Can't find the output file.", e);
      } catch (IOException e) {
         logger.error("Can't compress file to zip archive, occured IOException", e);
         throw new FileOperationException("Can't compress file to zip archive, occured IOException", e);
      } finally {
         try {
            if (zipOutputStream != null) zipOutputStream.close();
            if (fileOutputStream != null) fileOutputStream.close();
         } catch (IOException e) {
            logger.error("Close zipOutputStream and fileOutputStream occur IOException", e);
         }
      }
      return outputFile;
   }

}
