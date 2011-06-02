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
package org.openremote.web.console.service;

import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserInfo;

/**
 * Reads or writes user info with file userCache.json.
 * Reads or writes application setting with file appSetting.json.
 */
public interface UserCacheService {
   
   /**
    * Reads user info from userCache.json.
    * 
    * @return the user info
    */
   UserInfo getUserCache();
   
   /**
    * Writes user info to userCache.json.
    * 
    * @param userCache the user info
    */
   void saveUserCache(UserInfo userInfo);
   
   /**
    * Reads the app setting from appSetting.json.
    * 
    * @return the app setting
    */
   AppSetting getAppSetting();
   
   /**
    * Writes app setting to appSetting.json.
    * 
    * @param appSetting the app setting
    */
   void saveAppSetting(AppSetting appSetting);
}
