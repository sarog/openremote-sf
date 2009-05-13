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

package org.openremote.irbuilder.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.openremote.irbuilder.Constants;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 *
 */
public class PathConfig {
   private static final Logger logger = Logger.getLogger(PathConfig.class);

   private static final PathConfig myInstance = new PathConfig();

   private PathConfig() {
   }

   public static PathConfig getInstance() {
      return myInstance;
   }

   /**
    * Gets temp folder
    * 
    * @return folder absolute path
    */
   public String tempFolder() {
      String root = System.getProperty("modeler.root");
      if (root == null) {
         logger.fatal("Can't find modeler.root in system property, please check web.xml.");
         throw new IllegalStateException("Can't find modeler.root in system property, please check web.xml.");
      }
      return  root+"tmp"+File.separator;
   }
   /**
    * Gets iphone xml path
    * 
    * @return file absolute path
    */
   public String iPhoneXmlFilePath(String sessionId) {
      return sessionFolder(sessionId) + "iphone.xml";
   }

   /**
    * Gets controller xml file path
    * 
    * @return file absolute path
    */
   public String controllerXmlFilePath(String sessionId) {
      return sessionFolder(sessionId) + "controller.xml";
   }

   /**
    * Gets panel description file path
    * 
    * @return file absolute path
    */
   public String panelDescFilePath(String sessionId) {
      return sessionFolder(sessionId) + "panel." + Constants.PANEL_DESC_FILE_EXT;
   }

   /**
    * Gets lirc.conf file path
    * 
    * @return file absolute path
    */
   public String lircFilePath(String sessionId) {
      return sessionFolder(sessionId) + "lircd.conf";
   }

   /**
    * Gets compressed file path
    * 
    * @return file absolute path
    */
   public String openremoteZipFilePath(String sessionId) {
      return sessionFolder(sessionId) + "openremote." + UUID.randomUUID() + ".zip";
   }
   
   /**
    * Gets session folder path
    * 
    * @param sessionId
    * @return file absolute path
    */
   public String sessionFolder(String sessionId) {
      return tempFolder() + sessionId + File.separator;
   }
}
