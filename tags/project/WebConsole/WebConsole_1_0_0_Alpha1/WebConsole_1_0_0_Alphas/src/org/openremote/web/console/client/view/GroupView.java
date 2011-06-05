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
package org.openremote.web.console.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.event.OREvent;
import org.openremote.web.console.client.event.SubmitEvent;
import org.openremote.web.console.client.icon.Icons;
import org.openremote.web.console.client.listener.OREventListener;
import org.openremote.web.console.client.listener.SubmitListener;
import org.openremote.web.console.client.rpc.AsyncServiceFactory;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.client.widget.ScreenIndicator;
import org.openremote.web.console.client.window.LoginWindow;
import org.openremote.web.console.client.window.SettingsWindow;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.Navigate;
import org.openremote.web.console.domain.Screen;
import org.openremote.web.console.domain.TabBar;
import org.openremote.web.console.domain.TabBarItem;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Initialize group and screens.
 */
public class GroupView {

   private Icons icons = GWT.create(Icons.class);
   private Viewport viewport;
   private BorderLayout layout;
   private Group currentGroup;
   private Screen currentScreen;
   private ScreenView currentScreenView;
   private BorderLayoutData screenData;
   private ScreenIndicator screenIndicator;
   private Button previousButton;
   private Button nextButton;
   private Map<Integer, ScreenView> screenViews;
   private List<Navigate> navigationHistory;
   private ToolBar toolBar;
   
   /**
    * Instantiates a new group view.
    * A group view contains some screen views, the screen views can changed.
    */
   public GroupView() {
      viewport = new Viewport();
      layout = new BorderLayout();
      viewport.setLayout(layout);
      if (screenViews == null) {
         screenViews = new HashMap<Integer, ScreenView>();
      }
      if (navigationHistory == null) {
         navigationHistory = new ArrayList<Navigate>();
      }
      
      if (hasDefaultGroupAndScreen()) {
         createToolBar();
         createScreenView();
         createSouth();
         RootPanel.get().add(viewport);
         addPopSettingListener();
         addNavigateListener();
         // useful in IE
         KeyNav<ComponentEvent> keyNav = new KeyNav<ComponentEvent>(viewport) {

            public void onRight(ComponentEvent ce) {
               toNextScreen();
            }
            public void onLeft(ComponentEvent ce) {
               toPreviousScreen();
            }
         };
         keyNav.setCancelBubble(true);
         detectControllerIfSupportJsonp();
      }
   }
   
   /**
    * The tool bar is for show the panel or group tabbar.
    */
   private void createToolBar() {
      toolBar = new ToolBar() {
         protected void afterRender() {
            super.afterRender();
            if (this.getItemCount() == 0) {
               layout.hide(Style.LayoutRegion.NORTH);
            }
         }
      };
      initTabBar();
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 52);
      data.setMargins(new Margins(0, 5, 0, 5));
      data.setCollapsible(true);
      viewport.add(toolBar, data);
   }

   /**
    * Inits the tab bar if there is have(group tabBar or panel tabBar).
    */
   private void initTabBar() {
      TabBar tabbar = currentGroup.getTabBar();
      if (tabbar == null) {
         tabbar = ClientDataBase.panelXmlEntity.globalTabBar;
      }
      
      if (tabbar != null && tabbar.getTabBarItems().size() > 0) {
         List<TabBarItem> tabbarItems = tabbar.getTabBarItems();
         for (TabBarItem tabbarItem : tabbarItems) {
            addTabBarItem(tabbarItem);
         }
      }
   }

   /**
    * Adds the tab bar item.
    * Use button to represent tabbar item.
    * 
    * @param tabbarItem the tabbar item
    */
   private void addTabBarItem(final TabBarItem tabbarItem) {
      Button btn = new Button();
      String name = tabbarItem.getName();
      btn.setToolTip(name);
      if (name.length() > 10) {
         btn.setText(name.substring(0, 10) + "...");
      } else {
         btn.setText(name);
      }
      btn.setWidth(50);
      btn.setIconAlign(IconAlign.TOP);
      btn.setScale(ButtonScale.MEDIUM);
      if (tabbarItem.getImage() != null) {
         btn.setIcon(IconHelper.create(ClientDataBase.getResourceRootPath() + tabbarItem.getImage().getSrc(), 24, 24));
      } else {
         btn.setIcon(icons.defaultIcon());
      }
      if (tabbarItem.getNavigate() != null) {
         btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
               ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, tabbarItem.getNavigate());
            }
         });
      }
      toolBar.add(btn);
   }
   
   /**
    * Creates the current screen view.
    */
   private void createScreenView() {
      currentScreenView = new ScreenView(currentScreen);
      screenViews.put(currentScreen.getScreenId(), currentScreenView);
      screenData = new BorderLayoutData(Style.LayoutRegion.CENTER);
      screenData.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(currentScreenView, screenData);
      startCurrentPolling();
      saveGroupAndScreenToCookie();
   }

   /**
    * Creates the south part the group view if the group have more than one screens.
    */
   private void createSouth() {
      LayoutContainer southContainer = new LayoutContainer();
      southContainer.setStyleAttribute("backgroundColor", "white");
      southContainer.setLayout(new RowLayout(Orientation.HORIZONTAL));
      
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 30);
      data.setMargins(new Margins(5));
      
      previousButton = new Button();
      previousButton.setToolTip("Previous screen");
      previousButton.setScale(ButtonScale.MEDIUM);
      previousButton.setIcon(icons.previous());
      nextButton = new Button();
      nextButton.setToolTip("Next screen");
      nextButton.setScale(ButtonScale.MEDIUM);
      nextButton.setIcon(icons.next());
      int screenIndex = currentGroup.getScreens().indexOf(currentScreen);
      if (screenIndex == 0) {
         previousButton.disable();
      }
      if (screenIndex == currentGroup.getScreens().size() - 1) {
         nextButton.disable();
      }
      
      previousButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            toPreviousScreen();
         }
      });
      
      nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            toNextScreen();
         }
      });
      
      screenIndicator = new ScreenIndicator(currentGroup.getGroupId(), screenIndex, currentGroup.getScreens());
      southContainer.add(previousButton, new RowData(-1, 1));
      southContainer.add(screenIndicator, new RowData(1, 1));
      southContainer.add(nextButton, new RowData(-1, 1));
      
      viewport.add(southContainer, data);
   }

   /**
    * get default group from client database.
    */
   private boolean hasDefaultGroupAndScreen() {
      currentGroup = ClientDataBase.getDefaultGroup();
      if (currentGroup == null) {
         ((Text)RootPanel.get("error-content").getWidget(0)).setText("No Group Found");
         ((Text)RootPanel.get("error-content").getWidget(1)).setText("Please check your setting or define a group with screens first.");
         DOM.setStyleAttribute(RootPanel.get("error-content").getElement(), "display", "block");
         return false;
      }
      if (currentGroup.getScreens().isEmpty()) {
         ((Text)RootPanel.get("error-content").getWidget(0)).setText("No Screen Found");
         ((Text)RootPanel.get("error-content").getWidget(1)).setText("Please associate screens with this group");
         DOM.setStyleAttribute(RootPanel.get("error-content").getElement(), "display", "block");
         return false;
      }
      
      currentScreen = ClientDataBase.getLastTimeScreen();
      if (currentScreen == null) {
         currentScreen = currentGroup.getScreens().get(0);
      }
      return true;
   }

   /**
    * Show the current screen's next screen if there is have. 
    * 
    * @return true, if successful
    */
   private boolean toNextScreen() {
      List<Screen> screens = currentGroup.getScreens();
      int index = screens.indexOf(currentScreen);
      if (index < screens.size() - 1) {
         currentScreen = screens.get(index + 1);
         cancelCurrentPolling();
         currentScreenView.removeFromParent();
         
         initCurrentScreenView();
         updateSouth(screens.size(), index + 1);
         viewport.add(currentScreenView, screenData);
         saveGroupAndScreenToCookie();
         viewport.layout();
         startCurrentPolling();
         return true;
      }
      return false;
   }

   /**
    * Show the current screen's previous screen if there is have. 
    * 
    * @return true, if successful
    */
   private boolean toPreviousScreen() {
      List<Screen> screens = currentGroup.getScreens();
      int index = screens.indexOf(currentScreen);
      if (index > 0) {
         currentScreen = screens.get(index - 1);
         cancelCurrentPolling();
         currentScreenView.removeFromParent();
         
         initCurrentScreenView();
         updateSouth(screens.size(), index - 1);
         viewport.add(currentScreenView, screenData);
         saveGroupAndScreenToCookie();
         viewport.layout();
         startCurrentPolling();
         return true;
      }
      return false;
   }
   
   /**
    * Update the south part the group view.
    * 
    * @param screenSize the screen size
    * @param screenIndex the screen index
    */
   private void updateSouth(int screenSize, int screenIndex) {
      if (screenSize == 1) {
         layout.hide(Style.LayoutRegion.SOUTH);
         return;
      } else {
         layout.show(Style.LayoutRegion.SOUTH);
      }
      
      // update next button and previous button.
      if (nextButton != null) {
         if (screenIndex == screenSize - 1) {
            nextButton.disable();
         } else if (!nextButton.isEnabled()) {
            nextButton.enable();
         }
      }
      if (previousButton != null) {
         if (screenIndex == 0) {
            previousButton.disable();
         } else if (!previousButton.isEnabled()) {
            previousButton.enable();
         }
      }
      
      if (screenIndicator != null) {
         screenIndicator.updateCurrentPageControl(currentGroup.getGroupId(), screenIndex, currentGroup.getScreens());
      }
   }
   
   /**
    * Inits the current screen view.
    */
   private void initCurrentScreenView() {
      currentScreenView = screenViews.get(currentScreen.getScreenId());
      if (currentScreenView == null) {
         currentScreenView = new ScreenView(currentScreen);
         screenViews.put(currentScreen.getScreenId(), currentScreenView);
      }
   }
   
   /**
    * Save group and screen to cookie.
    */
   private void saveGroupAndScreenToCookie() {
      ClientDataBase.userInfo.setLastGroupId(currentGroup.getGroupId());
      ClientDataBase.userInfo.setLastScreenId(currentScreen.getScreenId());
      Cookies.setCookie(Constants.CONSOLE_USERINFO, ClientDataBase.userInfo.toJson());
   }
   
   /**
    * Start current screen's polling.
    */
   private void startCurrentPolling() {
      if (currentScreenView != null) {
         currentScreenView.startPolling();
      }
   }
   
   /**
    * Cancel current screen's polling.
    */
   private void cancelCurrentPolling() {
      if (currentScreenView != null) {
         currentScreenView.cancelPolling();
      }
   }
   
   /**
    * Adds the navigate listener to handle navigate event.
    */
   private void addNavigateListener() {
      ORListenerManager.getInstance().addOREventListener(Constants.ListenerNavigateTo, new OREventListener() {
         public void handleEvent(OREvent event) {
            Navigate navigate = (Navigate) event.getData();
            if (navigate != null) {
               handleNavigate(navigate);
            }
         }
      });
   }
   
   private void handleNavigate(Navigate navigate) {
      if (currentGroup == null || currentScreen == null) {
         return;
      }
      
      Navigate historyNavigate = new Navigate(currentGroup.getGroupId(), currentScreen.getScreenId());
      if (navigateTo(navigate)) {
         navigationHistory.add(historyNavigate);
      }
   }
   
   private boolean navigateTo(Navigate navigate) {
      if (navigate.isNextScreen()) {
         return toNextScreen();
      } else if (navigate.isPreviousScreen()) {
         return toPreviousScreen();
      } else if (navigate.getToGroup() > 0) {
         return navigateToGroup(navigate.getToGroup(), navigate.getToScreen());
      } else if (navigate.isBack()) {
         if (navigationHistory.size() > 0) {
            Navigate backward = navigationHistory.get(navigationHistory.size() - 1);
            if (backward.getToGroup() > 0 && backward.getToScreen() > 0) {
               navigateToGroup(backward.getToGroup(), backward.getToScreen());
               navigationHistory.remove(backward);
            }
         }
      } else if (navigate.isSetting()) {
         ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerToPopSetting, null);
      } else if (navigate.isLogin()) {
         new LoginWindow();
      } else if (navigate.isLogout()) {
         doLogout();
      }
      return false;
   }
   
   private boolean navigateToGroup(int toGroupId, int toScreenId) {
      Group targetGroup = ClientDataBase.getGroupById(toGroupId);
      if (targetGroup != null) {
         if (currentGroup.getGroupId() != toGroupId) {
            if (targetGroup.getScreens().size() == 0) {
               return false;
            }
            cancelCurrentPolling();
            currentGroup = targetGroup;
            currentScreen = targetGroup.getScreens().get(0);
            if (toScreenId > 0) {
               Screen screen = ClientDataBase.getScreenById(toScreenId);
               if (screen != null && targetGroup.getScreens().indexOf(screen) > -1) {
                  currentScreen = screen;
               }
            }
            
            toolBar.removeAll();
            initTabBar();
            if (toolBar.getItemCount() == 0) {
               layout.hide(Style.LayoutRegion.NORTH);
            }
         } else if (toScreenId > 0) {
            // in same group.
            Screen screen = ClientDataBase.getScreenById(toScreenId);
            if (screen != null && targetGroup.getScreens().indexOf(screen) > -1) {
               cancelCurrentPolling();
               currentScreen = screen;
            } else {
               return false;
            }
         }
         currentScreenView.removeFromParent();
         initCurrentScreenView();
         viewport.add(currentScreenView, screenData);
         saveGroupAndScreenToCookie();
         updateSouth(currentGroup.getScreens().size(), currentGroup.getScreens().indexOf(currentScreen));
         viewport.layout();
         startCurrentPolling();
         return true;
      }
      return false;
   }
   
   /**
    * Do logout is to empty user password in cookies.
    */
   private void doLogout() {
      MessageBox.confirm("Logout", "Are you sure you want to logout?", new Listener<MessageBoxEvent>() {
         public void handleEvent(MessageBoxEvent be) {
            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
               ClientDataBase.userInfo.setPassword("");
               Cookies.setCookie(Constants.CONSOLE_USERINFO, ClientDataBase.userInfo.toJson());;
            }
         }
      });
   }
   
   /**
    * Adds the pop setting listener for pop settings window.
    */
   private void addPopSettingListener() {
      ORListenerManager.getInstance().addOREventListener(Constants.ListenerToPopSetting, new OREventListener() {
         public void handleEvent(OREvent event) {
            SettingsWindow settingWindow = new SettingsWindow();
            settingWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               public void afterSubmit(SubmitEvent be) {
                  Window.Location.reload();
               }
            });
         }
      });
   }
   
   /**
    * Detect controller if support jsonp.
    */
   private void detectControllerIfSupportJsonp() {
      String currentServer = ClientDataBase.appSetting.getCurrentServer();
      if (!"".equals(currentServer)) {
         AsyncServiceFactory.getPanelIdentityServiceAsync().isSupportJsonp(currentServer,
               ClientDataBase.userInfo.getUsername(), ClientDataBase.userInfo.getPassword(),
               new AsyncSuccessCallback<Boolean>() {
            public void onSuccess(Boolean support) {
               if (!support) {
                  MessageBox.alert("Warn", "The current controller doesn't support JSON API, " +
                  		"polling is disabled, please upgrade the controller.", null);
               }
            }
         });
      }
   }
}
