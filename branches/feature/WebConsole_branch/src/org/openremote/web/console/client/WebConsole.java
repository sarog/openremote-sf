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
import org.openremote.web.console.client.view.ApplicationView;
import org.openremote.web.console.client.window.LoginWindow;
import org.openremote.web.console.client.window.SettingsWindow;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.exception.NotAuthenticatedException;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

public class WebConsole implements EntryPoint {

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      initUserCache();
      checkSettings();
   }
   
   /**
    * Check controller server and panel identity configuration.
    */
   private void checkSettings() {
      AppSetting appSetting = ClientDataBase.appSetting;
      
      String currentServer = appSetting.getCurrentServer();
      String currentPanel = appSetting.getCurrentPanelIdentity();
      
      if ("".equals(currentServer) || "".equals(currentPanel)) {
         toSetting();
      } else {
         readPanelXmlEntity();
      }
   }
   
   /**
    * Show setting window.
    */
   private void toSetting() {      
      DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
      SettingsWindow settingWindow = new SettingsWindow();
      settingWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         public void afterSubmit(SubmitEvent be) {
            readPanelXmlEntity();
         }
      });
   }
   
   private void initUserCache() {
      String userInfoJson = Cookies.getCookie(Constants.CONSOLE_USERINFO);
      String appSettingJson = Cookies.getCookie(Constants.CONSOLE_SETTINGS);
      ClientDataBase.userInfo.initFromJson(userInfoJson);
      ClientDataBase.appSetting.initFromJson(appSettingJson);
   }
   
   /**
    * read panel entity from server.
    */
   private void readPanelXmlEntity() {
      final String url = ClientDataBase.appSetting.getCurrentServer() + "/rest/panel/"
            + ClientDataBase.appSetting.getCurrentPanelIdentity();
      
      AsyncSuccessCallback<PanelXmlEntity> callback = new AsyncSuccessCallback<PanelXmlEntity>() {
         public void onSuccess(PanelXmlEntity panelXmlEntity) {
            if (panelXmlEntity != null) {
               DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
               ClientDataBase.panelXmlEntity.setGroups(panelXmlEntity.getGroups());
               ClientDataBase.panelXmlEntity.setScreens(panelXmlEntity.getScreens());
               ClientDataBase.panelXmlEntity.setGlobalTabBar(panelXmlEntity.getGlobalTabBar());
               // TODO: initial screen view.
               new ApplicationView();
            }
         }
         public void onFailure(Throwable caught) {
            if (caught instanceof NotAuthenticatedException) {
               final AsyncSuccessCallback<PanelXmlEntity> callback = this;
               LoginWindow loginWindow = new LoginWindow();
               loginWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                  public void afterSubmit(SubmitEvent be) {
                     DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "block");
                     AsyncServiceFactory.getPanelIdentityServiceAsync().getPanelXmlEntity(url,
                           ClientDataBase.userInfo.getUsername(), ClientDataBase.userInfo.getPassword(), callback);
                  }
               });
            } else {
               MessageBox.alert("ERROR", caught.getMessage(), null);
            }
         }
      };
      AsyncServiceFactory.getPanelIdentityServiceAsync().getPanelXmlEntity(url,
            ClientDataBase.userInfo.getUsername(), ClientDataBase.userInfo.getPassword(), callback);
   }
}
