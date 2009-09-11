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

package org.openremote.modeler.configuration;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;

/**
 * The Class PathConfig.
 * 
 * @author allen.wei, handy.wang
 */
public class PathConfig {
   
   /** The Constant logger. */
   private static final Logger logger = Logger.getLogger(PathConfig.class);

   /** The Constant myInstance. */
   private static final PathConfig myInstance = new PathConfig();
   
   private static final String RESOURCEFOLDER = "tmp";
   /** The configuration. */
   private Configuration configuration;

   /**
    * Instantiates a new path config.
    */
   private PathConfig() {
   }

   /**
    * Gets the single instance of PathConfig.
    * 
    * @param configuration the configuration
    * 
    * @return single instance of PathConfig
    */
   public static PathConfig getInstance(Configuration configuration) {
      myInstance.configuration = configuration;
      return myInstance;
   }

   /**
    * Gets temp folder.
    * 
    * @return folder absolute path
    */
   public String tempFolder() {
      String root = configuration.getOsWebappsRoot() + File.separator;
      if (root == null) {
         logger.fatal("Can't find modeler.root in system property, please check web.xml.");
         throw new IllegalStateException("Can't find modeler.root in system property, please check web.xml.");
      }
      return  root + RESOURCEFOLDER + File.separator;
   }

  
   /**
    * Gets iphone xml path.
    * 
    * @param userId the session id
    * 
    * @return file absolute path
    */
   public String iPhoneXmlFilePath(String userId) {
      return userFolder(userId) + "iphone.xml";
   }

   /**
    * Gets controller xml file path.
    * 
    * @param userId the session id
    * 
    * @return file absolute path
    */
   public String controllerXmlFilePath(String userId) {
      return userFolder(userId) + "controller.xml";
   }

   /**
    * Gets panel description file path.
    * 
    * @param userId the session id
    * 
    * @return file absolute path
    */
   public String panelDescFilePath(String userId) {
      return userFolder(userId) + "panel." + Constants.PANEL_DESC_FILE_EXT;
   }

   /**
    * Gets lirc.conf file path
    * 
    * @param userId the session id
    * 
    * @return file absolute path
    */
   public String lircFilePath(String userId) {
      return userFolder(userId) + "lircd.conf";
   }

   /**
    * Gets compressed file path.
    * 
    * @param userId the session id
    * 
    * @return file absolute path
    */
   public String openremoteZipFilePath(String userId) {
      return userFolder(userId) + "openremote." + UUID.randomUUID() + ".zip";
   }
   
   /**
    * Gets session folder path.
    * 
    * @param userId the session id
    * 
    * @return file absolute path
    */
   public String userFolder(String userId)
   {
      return tempFolder() + userId + File.separator;
   }
   
   /**
    * Gets the zip url.
    * 
    * @return the zip url
    */
   public String getZipUrl(String userId) {
      return configuration.getWebappServerRoot() + "/" + RESOURCEFOLDER + "/" + userId + "/";
   }

   /**
    * Gets the relative resource path.
    * 
    * @param userId the user id
    * @param fileName the file name
    * 
    * @return the relative resource path
    */
   public String getRelativeResourcePath(String userId, String fileName) {
      return "../" + RESOURCEFOLDER + "/" + userId + "/" + fileName;
   }
   /**
    * Gets the configuration.
    * 
    * @return the configuration
    */
   public Configuration getConfiguration() {
      return configuration;
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
