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

public class Configuration {
   private String workDir;
   private String iconsDir;
   private String svnDir;
   public String getWorkDir() {
      return workDir;
   }

   public String getSyncHistoryDir() {
      return workDir+File.separator+Constant.SYNC_HISTORY;
   }

   public void setWorkDir(String workDir) {
      this.workDir = workDir;
   }

   public String getWorkCopyDir() {
      return workDir+File.separator+Constant.WORK_COPY;
   }

   public String getSvnDir() {
      if(workDir.startsWith("/")){
         svnDir = "file://"+workDir+File.separator+"svn-repos/lirc/trunk";
      }else{
         svnDir = "file:///"+workDir+File.separator+"svn-repos/lirc/trunk";
      }
      return svnDir;
   }

   public String getIconsDir() {
      return iconsDir;
   }

   public void setIconsDir(String iconsDir) {
      this.iconsDir = iconsDir;
   }

}
