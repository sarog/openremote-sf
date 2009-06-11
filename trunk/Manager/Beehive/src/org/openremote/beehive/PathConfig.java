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

package org.openremote.beehive;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.openremote.beehive.utils.DateUtil;

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
      String root = System.getProperty("beehive.root");
      if (root == null) {
         logger.fatal("Can't find beehive.root in system property, please check web.xml.");
         throw new IllegalStateException("Can't find modeler.root in system property, please check web.xml.");
      }
      return  root+"tmp"+File.separator;
   }

   /**
    * Gets controller xml file path
    * 
    * @return file absolute path
    */
   public String commitProgressFilePath() {
      return tempFolder() + Constant.COMMIT_PROGRESS_FILE;
   }

   /**
    * Gets the file path by date.
    * 
    * @param date the date
    * @param fileName the file name
    * 
    * @return the file path by date
    */
   public String getFilePathByDate(Date date, String fileName){
      String[] time = DateUtil.getTimeFormat(date, "yyyy-MM-dd.HH-mm").split("\\.");
      return time[0]+"/"+time[1]+fileName;
   }
}
