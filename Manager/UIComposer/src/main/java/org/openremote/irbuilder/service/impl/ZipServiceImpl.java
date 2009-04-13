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

package org.openremote.irbuilder.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openremote.irbuilder.exception.FileOperationException;
import org.openremote.irbuilder.service.FilePathService;
import org.openremote.irbuilder.service.ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@Repository
public class ZipServiceImpl implements ZipService {

   static final int BUFFER = 2048;

   @Autowired
   private FilePathService filePathService;

   /**
    * {@inheritDoc}
    */
   public File compress(String outputFilePath, File... files) {
      BufferedInputStream bufferedInputStream;
      File outputFile = new File(outputFilePath);
      try {
         FileUtils.touch(outputFile);
      } catch (IOException e) {
         e.printStackTrace();
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
            ZipEntry entry = new ZipEntry(fileNameRemoveUUID(file.getName()));
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
         e.printStackTrace();
         throw new FileOperationException("Can't find the output file.", e);
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("Can't compress file to zip archive, occured IOException", e);
      } finally {
         try {
            if (zipOutputStream != null) zipOutputStream.close();
            if (fileOutputStream != null) fileOutputStream.close();
         } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Can't close resource occurr IOException", e);
         }

      }
      return outputFile;
   }

   private String fileNameRemoveUUID(String fileName) {
      return fileName.substring(0, fileName.lastIndexOf("_"));
   }

   /**
    * {@inheritDoc}
    */
   public File writeStringToFile(String filePath, String str) {
      File file = new File(filePath);
      try {
         FileUtils.touch(file);
         FileUtils.writeStringToFile(file, str);
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("write String to file fail. file path is " + filePath, e);
      }
      return file;
   }

   public String getIrbFileFromZip(InputStream inputStream) {
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry;
      try {
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
               if ("irb".equalsIgnoreCase(getFileExt(zipEntry.getName()))) {
                  return IOUtils.toString(zipInputStream);
               }
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            zipInputStream.closeEntry();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return "";
   }

   private String getFileExt(String fileName) {
      String[] parts = fileName.split("\\.");
      return parts[parts.length - 1];
   }

}
