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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openremote.web.console.SpringTestContext;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserInfo;
import org.openremote.web.console.utils.PathConfig;


/**
 * The Class UserCacheServiceTest.
 */
public class UserCacheServiceTest {
   
   private UserCacheService userCacheService =
      (UserCacheService) SpringTestContext.getInstance().getBean("userCacheService");
   
   @Before
   public void setUp() {
      String path = getClass().getResource("/").getPath();
      PathConfig.WEBROOTPATH = path.substring(0, path.length() - 1);
      File userResourceFolder = new File(PathConfig.userResourceFolder());
      if (!userResourceFolder.exists()) {
         userResourceFolder.mkdirs();
      }
   }
   
   @Test
   public void testUserInfo() {
      UserInfo userCache = new UserInfo();
      userCache.setUsername("tomsky");
      userCache.setPassword("tomsky");
      userCache.setLastGroupId(2);
      userCache.setLastScreenId(8);
      
      userCacheService.saveUserCache(userCache);
      
      UserInfo storedUserCache = userCacheService.getUserCache();

      Assert.assertEquals(userCache.getUsername(), storedUserCache.getUsername());
      Assert.assertEquals(userCache.getPassword(), storedUserCache.getPassword());
      Assert.assertEquals(userCache.getLastGroupId(), storedUserCache.getLastGroupId());
      Assert.assertEquals(userCache.getLastScreenId(), storedUserCache.getLastScreenId());
   }
   
   @Test
   public void testAppSetting() {
      AppSetting appSetting = new AppSetting();
      appSetting.setAutoDiscovery(false);
      appSetting.setCurrentServer("http://127.0.0.1:8080/controller");
      appSetting.setCurrentPanelIdentity("NoTab");
      List<String> customServers = new ArrayList<String>();
      customServers.add("http://localhost:8080/controller");
      customServers.add("http://192.168.4.63:8080/controller");
      appSetting.setCustomServers(customServers);
      
      userCacheService.saveAppSetting(appSetting);
      
      AppSetting newAppSetting = userCacheService.getAppSetting();
      
      Assert.assertFalse(newAppSetting.isAutoDiscovery());
      Assert.assertEquals(appSetting.getCurrentServer(), newAppSetting.getCurrentServer());
      Assert.assertEquals(appSetting.getCurrentPanelIdentity(), newAppSetting.getCurrentPanelIdentity());
      Assert.assertEquals(2, newAppSetting.getCustomServers().size());
   }
}
