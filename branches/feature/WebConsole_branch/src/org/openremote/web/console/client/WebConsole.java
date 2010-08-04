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
import org.openremote.web.console.client.utils.ORRoundRobin;
import org.openremote.web.console.client.view.GroupView;
import org.openremote.web.console.client.window.LoginWindow;
import org.openremote.web.console.client.window.SettingsWindow;
import org.openremote.web.console.domain.AppSetting;
import org.openremote.web.console.domain.PanelXmlEntity;
import org.openremote.web.console.exception.NotAuthenticatedException;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The EntryPoint of GWT application. all the code must put into {@link #onModuleLoad()} method.
 */
public class WebConsole implements EntryPoint {

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      initUserCache();
      initErrorView();
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
   
   /**
    * Inits the user cache from cookies.
    * include user info, application setting and ssl configurations.  
    */
   private void initUserCache() {
      String userInfoJson = Cookies.getCookie(Constants.CONSOLE_USERINFO);
      String appSettingJson = Cookies.getCookie(Constants.CONSOLE_SETTINGS);
      ClientDataBase.userInfo.initFromJson(userInfoJson);
      ClientDataBase.appSetting.initFromJson(appSettingJson);
      
      String sslStatus = Cookies.getCookie(Constants.SSL_STATUS);
      if (sslStatus == null || "".equals(sslStatus)) {
         Cookies.setCookie(Constants.SSL_STATUS, Constants.SSL_DISABLED);
      }
      
      String sslPort = Cookies.getCookie(Constants.SSL_PORT);
      if (sslPort == null || "".equals(sslPort)) {
         Cookies.setCookie(Constants.SSL_PORT, Constants.DEFAULT_SSL_PORT);
      }
   }
   
   /**
    * read panel entity from server side.
    */
   private void readPanelXmlEntity() {
      ORRoundRobin.detectGroupMembers();
      final String url = ClientDataBase.getSecuredServer() + "/rest/panel/"
            + URL.encode(ClientDataBase.appSetting.getCurrentPanelIdentity());
      DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "block");
      
      AsyncSuccessCallback<PanelXmlEntity> callback = new AsyncSuccessCallback<PanelXmlEntity>() {
         public void onSuccess(PanelXmlEntity panelXmlEntity) {
            if (panelXmlEntity != null) {
               DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
               ClientDataBase.panelXmlEntity.setGroups(panelXmlEntity.getGroups());
               ClientDataBase.panelXmlEntity.setScreens(panelXmlEntity.getScreens());
               ClientDataBase.panelXmlEntity.setGlobalTabBar(panelXmlEntity.getGlobalTabBar());
               new GroupView();
            }
         }
         public void onFailure(Throwable caught) {
            if (caught instanceof NotAuthenticatedException) {
               final AsyncSuccessCallback<PanelXmlEntity> callback = this;
               DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
               DOM.setStyleAttribute(RootPanel.get("error-content").getElement(), "display", "block");
               LoginWindow loginWindow = new LoginWindow();
               loginWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                  public void afterSubmit(SubmitEvent be) {
                     DOM.setStyleAttribute(RootPanel.get("error-content").getElement(), "display", "none");
                     DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "block");
                     AsyncServiceFactory.getPanelIdentityServiceAsync().getPanelXmlEntity(url,
                           ClientDataBase.userInfo.getUsername(), ClientDataBase.userInfo.getPassword(), callback);
                  }
               });
            } else {
               DOM.setStyleAttribute(RootPanel.get("welcome-content").getElement(), "display", "none");
               MessageBox.alert("ERROR", caught.getMessage(), new Listener<MessageBoxEvent>() {
                  public void handleEvent(MessageBoxEvent be) {
                     System.out.println("get panel xml entity");
                     toSetting();
                  }
               });
            }
         }
      };
      AsyncServiceFactory.getPanelIdentityServiceAsync().getPanelXmlEntity(url,
            ClientDataBase.userInfo.getUsername(), ClientDataBase.userInfo.getPassword(), callback);
   }
   
   /**
    * Inits the error view.
    * It would be shown if there is no screen to display.
    */
   private void initErrorView() {
      Text errorTitle = new Text("No Panel Found");
      errorTitle.addStyleName("error-text");
      errorTitle.setStyleAttribute("fontSize", "20");
      RootPanel.get("error-content").add(errorTitle);
      
      Text errorMessage = new Text("Please check your settings.");
      errorMessage.addStyleName("error-text");
      RootPanel.get("error-content").add(errorMessage);
      
      Button settingBtn = new Button("Settings...");
      settingBtn.setWidth(200);
      settingBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            DOM.setStyleAttribute(RootPanel.get("error-content").getElement(), "display", "none");
            SettingsWindow settingWindow = new SettingsWindow();
            settingWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               public void afterSubmit(SubmitEvent be) {
                  Window.Location.reload();
               }
            });
         }
      });
      RootPanel.get("error-content").add(settingBtn);
   }
}
