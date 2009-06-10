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
package org.openremote.beehive.file;

import java.util.Date;
import java.util.Locale;

import org.openremote.beehive.Constant;
import org.openremote.beehive.utils.StringUtil;

/**
 * The Class LIRCElement.
 * 
 * @author Tomsky
 */
public class LIRCElement {
   private boolean isModel;
   private String path;
   private String uploadDate;
   public LIRCElement() {
      isModel = false;
   }
   public boolean isModel() {
      return isModel;
   }
   public String getPath() {
      return path;
   }

   public void setModel(boolean isModel) {
      this.isModel = isModel;
   }
   public void setPath(String path) {
      this.path = path;
   }
   public void setUploadDate(String uploadDate) {
      this.uploadDate = uploadDate;
   }
   public Date getUploadDate(){
      return StringUtil.String2Date(uploadDate, "dd-MMM-yyyy kk:mm", Locale.ENGLISH);
   }
   public String getRelativePath(){
      return path.replaceAll(Constant.LIRC_ROOT_URL, "");
   }
}
