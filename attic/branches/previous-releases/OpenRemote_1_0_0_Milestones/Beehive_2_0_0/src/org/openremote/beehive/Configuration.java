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

public class Configuration {

   private String downloadDir;
   private String uploadDir;
   private String downloadUrlRoot;
   private String scrapDir;
   private String workCopyDir;
   private String svnDir;
   private String iconsDir;
   
   public String getDownloadDir() {
      return downloadDir;
   }

   public void setDownloadDir(String downloadDir) {
      this.downloadDir = downloadDir;
   }

   public String getUploadDir() {
      return uploadDir;
   }

   public void setUploadDir(String uploadDir) {
      this.uploadDir = uploadDir;
   }

   public String getDownloadUrlRoot() {
      return downloadUrlRoot;
   }

   public void setDownloadUrlRoot(String downloadUrlRoot) {
      this.downloadUrlRoot = downloadUrlRoot;
   }

   public String getScrapDir() {
      return scrapDir;
   }

   public void setScrapDir(String scrapDir) {
      this.scrapDir = scrapDir;
   }

   public String getWorkCopyDir() {
      return workCopyDir;
   }

   public void setWorkCopyDir(String workCopyDir) {
      this.workCopyDir = workCopyDir;
   }

   public String getSvnDir() {
      return svnDir;
   }

   public void setSvnDir(String svnDir) {
      this.svnDir = svnDir;
   }

   public String getIconsDir() {
      return iconsDir;
   }

   public void setIconsDir(String iconsDir) {
      this.iconsDir = iconsDir;
   }

}
