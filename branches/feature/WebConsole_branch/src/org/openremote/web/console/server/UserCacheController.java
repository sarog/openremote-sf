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
package org.openremote.web.console.server;

import org.openremote.web.console.client.rpc.UserCacheRPCService;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserCache;
import org.openremote.web.console.service.UserCacheService;

public class UserCacheController extends BaseGWTSpringController implements UserCacheRPCService {

   private static final long serialVersionUID = 8586067195519365526L;
   
   private UserCacheService userCacheService;
   
   public void setUserCacheService(UserCacheService userCacheService) {
      this.userCacheService = userCacheService;
   }
   
   public UserCache getUserCache() {
      return userCacheService.getUserCache();
   }

   public void saveUser(String username, String password) {
      UserCache userCache = getUserCache();
      if (userCache == null) {
         userCache = new UserCache();
      }
      userCache.setUsername(username);
      userCache.setPassword(password);
      userCacheService.saveUserCache(userCache);
   }

   public AppSetting getAppSetting() {
      return userCacheService.getAppSetting();
   }

   public void saveAppSetting(AppSetting appSetting) {
      userCacheService.saveAppSetting(appSetting);
   }
   
}
