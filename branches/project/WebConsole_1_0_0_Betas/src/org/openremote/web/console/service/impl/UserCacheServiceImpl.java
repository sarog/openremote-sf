/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.service.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserInfo;
import org.openremote.web.console.service.UserCacheService;
import org.openremote.web.console.utils.PathConfig;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class UserCacheServiceImpl implements UserCacheService {
   
   private static Logger log = Logger.getLogger(UserCacheServiceImpl.class);
   
   public UserInfo getUserCache() {
      File userCacheFile = new File(PathConfig.userCacheFilePath());
      UserInfo userCache = null;
      if (!userCacheFile.exists()) {
         return userCache;
      }
      try {
         String userCacheStr = FileUtils.readFileToString(userCacheFile, "UTF-8");
         userCache = new JSONDeserializer<UserInfo>().use(null, UserInfo.class).deserialize(userCacheStr);
      } catch (IOException e) {
         log.error("Can not read user cache from file.", e);
      }
      
      return userCache;
   }

   public void saveUserCache(UserInfo userCache) {
      if (userCache == null) {
         return;
      }
      
      String userCacheStr = new JSONSerializer().serialize(userCache);
      File userCacheFile = new File(PathConfig.userCacheFilePath());
      try {
         FileUtils.writeStringToFile(userCacheFile, userCacheStr, "UTF-8");
      } catch (IOException e) {
         log.error("Can not write user cache into file.", e);
         return;
      }
   }

   public AppSetting getAppSetting() {
      File appSettingFile = new File(PathConfig.appSettingFilePath());
      AppSetting appSetting = null;
      if (!appSettingFile.exists()) {
         return appSetting;
      }
      try {
         String appSettingStr = FileUtils.readFileToString(appSettingFile, "UTF-8");
         appSetting = new JSONDeserializer<AppSetting>().use(null, AppSetting.class).deserialize(appSettingStr);
      } catch (IOException e) {
         log.error("Can not read app setting from file.", e);
      }
      
      return appSetting;
   }

   public void saveAppSetting(AppSetting appSetting) {
      if (appSetting == null) {
         return;
      }
      
      String appSettingStr = new JSONSerializer().deepSerialize(appSetting);
      File appSettingFile = new File(PathConfig.appSettingFilePath());
      try {
         FileUtils.writeStringToFile(appSettingFile, appSettingStr, "UTF-8");
      } catch (IOException e) {
         log.error("Can not write app setting into file.", e);
         return;
      }
   }

}
