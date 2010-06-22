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
package org.openremote.web.console.client;

import org.openremote.web.console.client.event.SubmitEvent;
import org.openremote.web.console.client.listener.SubmitListener;
import org.openremote.web.console.client.rpc.AsyncServiceFactory;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.window.LoginWindow;
import org.openremote.web.console.client.window.SettingsWindow;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserCache;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

public class WebConsole implements EntryPoint {

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      checkUserAndSettings();
   }
   
   private void checkUserAndSettings() {
      AsyncServiceFactory.getUserCacheServiceAsync().getUserCache(new AsyncSuccessCallback<UserCache>() {
         public void onSuccess(UserCache result) {
            if (result != null) {
               ClientDataBase.userCache = result;
            }
            UserCache userCache = ClientDataBase.userCache;
            
            if ("".equals(userCache.getUsername()) || "".equals(userCache.getPassword())) {
               toLogin();
            } else {
               checkSettings();
            }
         }
      });
      
   }
   
   private void checkSettings() {
      AsyncServiceFactory.getUserCacheServiceAsync().getAppSetting(new AsyncSuccessCallback<AppSetting>() {
         public void onSuccess(AppSetting result) {
            if (result != null) {
               ClientDataBase.appSetting = result;
            }
            AppSetting appSetting = ClientDataBase.appSetting;
            
            String currentServer = appSetting.getCurrentServer();
            String currentPanel = appSetting.getCurrentPanelIdentity();
            
            if ("".equals(currentServer) || "".equals(currentPanel)) {
               toSetting();
            } else {
               // TODO:load resources.
            }
         }
      });
   }
   
   private void toLogin() {
      DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
      LoginWindow loginWindow = new LoginWindow();
      loginWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "block");
            checkSettings();
         }
         
      });
   }
   
   private void toSetting() {
      
      System.out.println("to setting");
      DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
      SettingsWindow settingWindow = new SettingsWindow();
   }
}
