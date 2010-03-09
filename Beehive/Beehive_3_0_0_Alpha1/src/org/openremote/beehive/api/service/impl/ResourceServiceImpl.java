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
package org.openremote.beehive.api.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.utils.FileUtil;
/**
 * 
 * @author javen
 *
 */
public class ResourceServiceImpl implements ResourceService {
   private static final Log logger = LogFactory.getLog(ResourceService.class);
   
   protected Configuration configuration = null;

   @Override
   public boolean saveResource(long accountOid,InputStream input) {
      logger.debug("save resource from modeler to beehive");
      File dir = makeSureDir(accountOid);
      File zipFile = new File(dir,ZIP_FILE_NAME);
      FileOutputStream fos = null;
      try {
         FileUtil.deleteFileOnExist(zipFile);
         fos = new FileOutputStream(zipFile);
         byte[] buffer = new byte[1024];
         int length = 0;
         while ((length = input.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
         }
         logger.info("save resource success!");
         return true;
      }catch(Exception e){
         logger.error("falied to save resource from modeler to beehive", e);
      } finally {
         if(fos != null){
            try{fos.close();}catch(Exception e){}
         }
      }
      return false;
   }

   
   private File makeSureDir(long accountOid) {
      File dir = new File(configuration.getModelerResourcesDir() + File.separator + accountOid);
      if (!dir.exists()) {
         dir.mkdirs();
      }
      return dir;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
   
}
