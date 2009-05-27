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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.openremote.irbuilder.Constants;
import org.openremote.irbuilder.configuration.PathConfig;
import org.openremote.irbuilder.exception.FileOperationException;
import org.openremote.irbuilder.service.ResourceService;
import org.openremote.irbuilder.utils.IphoneXmlParser;
import org.openremote.irbuilder.utils.StringUtils;
import org.openremote.irbuilder.utils.ZipUtils;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
@Service(value = "resourceService")
public class ResourceServiceImpl implements ResourceService {
   private static final Logger logger = Logger.getLogger(ResourceServiceImpl.class);

   public File downloadZipResource(String controllerXML, String iphoneXML, String panelDesc, String RESTAPIUrl,
                                   String sectionIds, String sessionId) {

      File sessionFolder = new File(PathConfig.getInstance().sessionFolder(sessionId));
      if (!sessionFolder.exists()) {
         sessionFolder.mkdirs();
      }
      File iphoneXMLFile = new File(PathConfig.getInstance().iPhoneXmlFilePath(sessionId));
      File controllerXMLFile = new File(PathConfig.getInstance().controllerXmlFilePath(sessionId));
      File panelDescFile = new File(PathConfig.getInstance().panelDescFilePath(sessionId));
      File lircdFile = new File(PathConfig.getInstance().lircFilePath(sessionId));
      File zipFile = new File(PathConfig.getInstance().openremoteZipFilePath(sessionId));

      String newIphoneXML = IphoneXmlParser.parserXML(iphoneXML, sessionFolder);
      
       try {
          
         FileUtils.deleteQuietly(iphoneXMLFile);
         FileUtils.deleteQuietly(controllerXMLFile);
         FileUtils.deleteQuietly(panelDescFile);
         FileUtils.deleteQuietly(lircdFile);

         FileUtils.writeStringToFile(iphoneXMLFile, newIphoneXML);
         FileUtils.writeStringToFile(controllerXMLFile, controllerXML);
         FileUtils.writeStringToFile(panelDescFile, panelDesc);

         if (sectionIds != "") {
            FileUtils.copyURLToFile(buildLircRESTUrl(RESTAPIUrl, sectionIds), lircdFile);
         } else {
            FileUtils.writeStringToFile(lircdFile, "");
         }

      } catch (IOException e) {
         logger.error("Compress zip file occur IOException", e);
         throw new FileOperationException("Compress zip file occur IOException", e);
      }

      List<File> files = new ArrayList<File>();
      for (File file : sessionFolder.listFiles()) {
         if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("zip")) {
            continue;
         }
         files.add(file);
      }

      ZipUtils.compress(zipFile.getAbsolutePath(), files);
      return zipFile;
   }

   private URL buildLircRESTUrl(String RESTAPIUrl, String ids) {
      URL lircUrl;
      try {
         lircUrl = new URL(RESTAPIUrl + "?ids=" + ids);
      } catch (MalformedURLException e) {
         logger.error("Lirc file url is invalid", e);
         throw new IllegalArgumentException("Lirc file url is invalid", e);
      }
      return lircUrl;
   }

   public String getIrbFileFromZip(InputStream inputStream, String sessionId) {
      File tmpDir = new File(PathConfig.getInstance().sessionFolder(sessionId));
      if (tmpDir.exists() && tmpDir.isDirectory()) {
         try {
            FileUtils.deleteDirectory(tmpDir);
         } catch (IOException e) {
            logger.error("Delete temp dir Occur IOException", e);
            throw new FileOperationException("Delete temp dir Occur IOException", e);
         }
      }
      String irbFileContent = "";
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry;
      FileOutputStream fileOutputStream = null;
      try {
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
               if (Constants.PANEL_DESC_FILE_EXT.equalsIgnoreCase(StringUtils.getFileExt(zipEntry.getName()))) {
                  irbFileContent = IOUtils.toString(zipInputStream);
               }
               //TODO extract constant
               if (!FilenameUtils.getExtension(zipEntry.getName()).matches("(xml|irb)")) {
                  File file = new File(PathConfig.getInstance().sessionFolder(sessionId) + zipEntry.getName());
                  FileUtils.touch(file);

                  fileOutputStream = new FileOutputStream(file);
                  int b;
                  while ((b = zipInputStream.read()) != -1) {
                     fileOutputStream.write(b);
                  }
                  fileOutputStream.close();
               }
            }

         }
      } catch (IOException e) {
         logger.error("Get Irb file from zip file Occur IOException", e);
         throw new FileOperationException("Get Irb file from zip file Occur IOException", e);
      } finally {
         try {
            zipInputStream.closeEntry();
             if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            logger.error("Clean Resource used occured IOException when import a file", e);
         }

      }
      return irbFileContent;
   }

   public File uploadImage(InputStream inputStream, String fileName, String sessionId) {
      File file = new File(PathConfig.getInstance().sessionFolder(sessionId) + File.separator + fileName);
      FileOutputStream fileOutputStream = null;
      try {
         FileUtils.touch(file);
         fileOutputStream = new FileOutputStream(file);
         IOUtils.copy(inputStream, fileOutputStream);
      } catch (IOException e) {
         logger.error("Save uploaded image to file occur IOException.", e);
         throw new FileOperationException("Save uploaded image to file occur IOException.", e);
      } finally {
         try {
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         } catch (IOException e) {
            logger.error("Close FileOutputStream Occur IOException while save a uploaded image.", e);
         }

      }

      return file;
   }
}
