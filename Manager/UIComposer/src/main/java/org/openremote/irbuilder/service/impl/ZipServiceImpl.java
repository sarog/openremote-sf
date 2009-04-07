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

import org.openremote.irbuilder.service.ZipService;
import org.openremote.irbuilder.exception.FileOperationException;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@Repository
public class ZipServiceImpl implements ZipService {
   static final int BUFFER = 2048;

   /**
    * {@inheritDoc}
    */
   public File compress(String outputFilePath, File... files) {
      BufferedInputStream bufferedInputStream;
      File outputFile = new File(outputFilePath);
      if (!outputFile.getParentFile().exists()) {
         outputFile.getParentFile().mkdirs();
      }
      if (!outputFile.exists()) {
         try {
            outputFile.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Can't access to the output file in path " + outputFilePath, e);
         }
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
            System.out.println("add file " + file.getAbsolutePath());

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

   /**
    * {@inheritDoc}
    */
   public File writeStringToFile(String filePath, String str) {
      File file = new File(filePath);
      FileWriter fileWriter;
      try {
         if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
         }
         if (!file.exists()) {
            file.createNewFile();
         }
         fileWriter = new FileWriter(file);
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("Writes file " + filePath + " occurred IOException", e);
      }
      BufferedWriter buffreader = new BufferedWriter(fileWriter);
      PrintWriter printWriter = new PrintWriter(buffreader);
      printWriter.write(str);
      printWriter.flush();

      printWriter.close();

      try {
         buffreader.close();
         fileWriter.close();
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileOperationException("Close resource occured IOException", e);
      }
      return file;
   }

}
