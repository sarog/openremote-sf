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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
         String SectionIds, String sessionId) {
      
      File sessionFolder = new File(PathConfig.getInstance().sessionFolder(sessionId));
      if(!sessionFolder.exists()){
         sessionFolder.mkdirs();
      }
      File iphoneXMLFile = new File(PathConfig.getInstance().iPhoneXmlFilePath(sessionId));
      File controllerXMLFile = new File(PathConfig.getInstance().controllerXmlFilePath(sessionId));
      File panelDescFile = new File(PathConfig.getInstance().panelDescFilePath(sessionId));
      File lircdFile = new File(PathConfig.getInstance().lircFilePath(sessionId));
      File zipFile = new File(PathConfig.getInstance().openremoteZipFilePath(sessionId));
      
      String newIphoneXML = IphoneXmlParser.parserXML(iphoneXML, sessionFolder);
      List<File> files = new ArrayList<File>();
      for (File file : sessionFolder.listFiles()) {
         files.add(file);
      }
      
      try {
         FileUtils.writeStringToFile(iphoneXMLFile, newIphoneXML);
         files.add(iphoneXMLFile);
         FileUtils.writeStringToFile(controllerXMLFile, controllerXML);
         files.add(controllerXMLFile);
         FileUtils.writeStringToFile(panelDescFile, panelDesc);
         files.add(panelDescFile);         
         FileUtils.copyURLToFile(buildLircRESTUrl(RESTAPIUrl, SectionIds), lircdFile);
         files.add(lircdFile);
         ZipUtils.compress(zipFile.getAbsolutePath(), files);
      } catch (IOException e) {
         logger.error("Compress zip file occur IOException", e);
         throw new FileOperationException("Compress zip file occur IOException", e);
      } finally {
         FileUtils.deleteQuietly(iphoneXMLFile);
         FileUtils.deleteQuietly(controllerXMLFile);
         FileUtils.deleteQuietly(panelDescFile);
         FileUtils.deleteQuietly(lircdFile);         
      }
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

   public String getIrbFileFromZip(InputStream inputStream) {
      ZipInputStream zipInputStream = new ZipInputStream(inputStream);
      ZipEntry zipEntry;
      try {
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
               if (Constants.PANEL_DESC_FILE_EXT.equalsIgnoreCase(StringUtils.getFileExt(zipEntry.getName()))) {
                  return IOUtils.toString(zipInputStream);
               }
            }
         }
      } catch (IOException e) {
         logger.error("Get Irb file from zip file Occur IOException", e);
         throw new FileOperationException("Get Irb file from zip file Occur IOException", e);
      } finally {
         try {
            zipInputStream.closeEntry();
         } catch (IOException e) {
            logger.error("Close zipInputStream Occur IOException while get Irb file from zip", e);
            throw new FileOperationException("Close zipInputStream Occur IOException while get Irb file from zip", e);
         }
      }
      return "";
   }

}
