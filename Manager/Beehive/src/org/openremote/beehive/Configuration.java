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
package org.openremote.beehive;

import java.io.File;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
public class Configuration {
   
   /** The work dir. */
   private String workDir;
   
   /** The icons dir. */
   private String iconsDir;
   
   /** The svn dir. */
   private String svnDir;
   
   /** The lirc craw regex. */
   private String lircCrawRegex;
   
   /**
    * Gets the work dir.
    * 
    * @return the work dir
    */
   public String getWorkDir() {
      return workDir;
   }

   /**
    * Gets the sync history dir.
    * 
    * @return the sync history dir
    */
   public String getSyncHistoryDir() {
      return workDir+File.separator+Constant.SYNC_HISTORY;
   }

   /**
    * Sets the work dir.
    * 
    * @param workDir the new work dir
    */
   public void setWorkDir(String workDir) {
      this.workDir = workDir;
   }

   /**
    * Gets the work copy dir.
    * 
    * @return the work copy dir
    */
   public String getWorkCopyDir() {
      return workDir+File.separator+Constant.WORK_COPY;
   }

   /**
    * Gets the svn dir.
    * 
    * @return the svn dir
    */
   public String getSvnDir() {
      if(workDir.startsWith("/")){
         svnDir = "file://"+workDir+File.separator+"svn-repos/lirc/trunk";
      }else{
         svnDir = "file:///"+workDir+File.separator+"svn-repos/lirc/trunk";
      }
      return svnDir;
   }

   /**
    * Gets the icons dir.
    * 
    * @return the icons dir
    */
   public String getIconsDir() {
      return iconsDir;
   }

   /**
    * Sets the icons dir.
    * 
    * @param iconsDir the new icons dir
    */
   public void setIconsDir(String iconsDir) {
      this.iconsDir = iconsDir;
   }

   /**
    * Gets the lirc craw regex.
    * 
    * @return the lirc craw regex
    */
   public String getLircCrawRegex() {
      return lircCrawRegex;
   }

   /**
    * Sets the lirc craw regex.
    * 
    * @param lircCrawRegex the new lirc craw regex
    */
   public void setLircCrawRegex(String lircCrawRegex) {
      this.lircCrawRegex = lircCrawRegex;
   }
   
   
}
