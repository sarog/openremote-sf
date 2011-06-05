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
package org.openremote.web.console.client.rpc;

import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Reads and writes user info with userCache.json.
 * Reads and writes application settings with appSetting.json.
 * 
 */
@RemoteServiceRelativePath("usercache.smvc")
public interface UserCacheRPCService extends RemoteService {

   /**
    * Reads user info from userCache.json.
    * 
    * @return the user info
    */
   UserInfo getUserInfo();

   /**
    * Writes username and password to userCache.json.
    * 
    * @param username the username
    * @param password the password
    */
   void saveUser(String username, String password);
   
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
