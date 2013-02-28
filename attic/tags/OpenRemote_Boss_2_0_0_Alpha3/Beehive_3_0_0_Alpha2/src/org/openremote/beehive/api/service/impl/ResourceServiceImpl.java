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
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openremote.beehive.Configuration;
import org.openremote.beehive.Constant;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.api.service.ResourceService;
import org.openremote.beehive.domain.User;
import org.openremote.beehive.utils.FileUtil;

/**
 * 
 * Account resources service, such as openremote.zip etc.
 * 
 * @author javen, Dan
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class ResourceServiceImpl implements ResourceService
{
   private static final Log logger = LogFactory.getLog(ResourceService.class);
   
   private AccountService accountService;
   
   protected Configuration configuration = null;

   @Override
   public boolean saveResource(long accountOid, InputStream input) {
      logger.debug("save resource from modeler to beehive");

      File dir = makeSureDir(accountOid);
      File zipFile = new File(dir, Constant.ACCOUNT_RESOURCE_ZIP_NAME);
      FileOutputStream fos = null;

      try {
         FileUtil.deleteFileOnExist(zipFile);
         fos = new FileOutputStream(zipFile);
         byte[] buffer = new byte[1024];
         int length = 0;

         while ((length = input.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
         }
         fos.flush();
         logger.info("save resource success!");

         return true;
      } catch (IOException e) {
         logger.error("failed to save resource from modeler to beehive", e);
      } finally {
         if (fos != null) {
            try {
               fos.close();
            } catch (IOException ioException) {
               logger.warn("Error in closing file output stream to '" + Constant.ACCOUNT_RESOURCE_ZIP_NAME + "': "
                     + ioException.getMessage(), ioException);
            }
         }
      }
      return false;
   }
   
   public File getResourceZip(String username) {
      
      User user = accountService.loadByUsername(username);
      if (user == null) {
         return null;
      }
      File[] files = getDirByAccountOid(user.getAccount().getOid()).listFiles(new FilenameFilter() {

         @Override
         public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase(Constant.ACCOUNT_RESOURCE_ZIP_NAME);
         }

      });
      if (files != null && files.length != 0) {
         return files[0];
      }
      return null;
   }

   
   private File getDirByAccountOid(long accountOid) {
      return new File(configuration.getModelerResourcesDir() + File.separator + accountOid);
   }
   
   private File makeSureDir(long accountOid) {
      File dir = getDirByAccountOid(accountOid);
      if (!dir.exists()) {
         dir.mkdirs();
      }
      return dir;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public void setAccountService(AccountService accountService) {
      this.accountService = accountService;
   }
   
   
   
}
