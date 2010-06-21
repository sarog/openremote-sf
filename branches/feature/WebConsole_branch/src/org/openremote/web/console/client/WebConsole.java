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
import org.openremote.web.console.client.window.LoginWindow;
import org.openremote.web.console.client.window.SettingsWindow;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.UserCache;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

public class WebConsole implements EntryPoint {

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      checkUserAndSettings();
//      RootPanel.get("loading-msg").getElement().setInnerText("Loading panel.xml");
   }
   
   private void checkUserAndSettings() {
      
      String username = Cookies.getCookie(Constants.CONSOLE_USERNAME);
      String password = Cookies.getCookie(Constants.CONSOLE_PASSWORD);
      
      if (username == null || password == null || "".equals(username) || "".equals(password)) {
         AsyncServiceFactory.getUserCacheServiceAsync().getUserCache(new AsyncSuccessCallback<UserCache>() {
            public void onSuccess(UserCache userCache) {
               if (userCache != null) {
                  Cookies.setCookie(Constants.CONSOLE_USERNAME, userCache.getUsername());
                  Cookies.setCookie(Constants.CONSOLE_PASSWORD, userCache.getPassword());
                  if (!"".equals(userCache.getUsername()) && !"".equals(userCache.getPassword())) {
                     System.out.println("has login and check setting");
                     checkSettings();
                  } else {
                     toLogin();
                  }
               } else {
                  toLogin();
               }
            }
         });
      } else {
         checkSettings();
      }
   }
   
   private void checkSettings() {
      String currentServer = Cookies.getCookie(Constants.CURRENT_SERVER);
      String currentPanel = Cookies.getCookie(Constants.CURRENT_PANEL);
      
      if (currentServer == null || currentPanel == null || "".equals(currentServer) || "".equals(currentPanel)) {
         System.out.println("no settings");
         AsyncServiceFactory.getUserCacheServiceAsync().getAppSetting(new AsyncSuccessCallback<AppSetting>() {
            public void onSuccess(AppSetting appSetting) {
               if (appSetting != null) {
                  Cookies.setCookie(Constants.CURRENT_SERVER, appSetting.getCurrentServer());
                  Cookies.setCookie(Constants.CURRENT_PANEL, appSetting.getCurrentPanelIdentity());
                  if (!"".equals(appSetting.getCurrentServer()) && !"".equals(appSetting.getCurrentPanelIdentity())) {
                     // TODO:load resources.
                  } else {
                     toSetting();
                  }
               } else {
                  toSetting();
               }
            }
         });
      } else {
         // TODO:load resources.
      }
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
