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

package org.openremote.irbuilder.configuration;

import org.openremote.irbuilder.Constants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
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
      Properties properties = new Properties();
      try {
         File pFile = new File(getClass().getResource("/directoryConfig.properties").getFile());
         properties.load(new FileInputStream(pFile));
      } catch (IOException e) {
         logger.error("Can't read directoryConfig.properties file.",e);
         throw new IllegalStateException("Can't read directoryConfig.properties file.", e);
      }
      if (properties.get("tmp.dir") != null) {
         return properties.get("tmp.dir").toString();
      } else {
         logger.error("Can't find tmp.dir in properties, system initialize error.");
         throw new IllegalStateException("Can't find tmp.dir in properties, system initialize error.");
      }
   }

   /**
    * Gets iphone xml path
    *
    * @return file absolute path
    */
   public String iPhoneXmlFilePath() {
      return tempFolder() + File.separator + "iphone.xml" + "_" + UUID.randomUUID();
   }

   /**
    * Gets controller xml file path
    *
    * @return file absolute path
    */
   public String controllerXmlFilePath() {
      return tempFolder() + File.separator + "controller.xml" + "_" + UUID.randomUUID();
   }

   /**
    * Gets panel description file path
    *
    * @return file absolute path
    */
   public String panelDescFilePath() {
      return tempFolder() + File.separator + "panel."+ Constants.PANEL_DESC_FILE_EXT+"" + "_" + UUID.randomUUID();
   }

   /**
    * Gets lirc.conf file path
    *
    * @return file absolute path
    */
   public String lircFilePath() {
      return tempFolder() + File.separator + "lircd.conf" + "_" + UUID.randomUUID();
   }

   /**
    * Gets compressed file path
    *
    * @return file absolute path
    */
   public String openremoteZipFilePath() {
      return tempFolder() + File.separator + "openremote." + UUID.randomUUID() + ".zip";
   }

}
