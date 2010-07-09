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

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.icon.Icons;
import org.openremote.web.console.client.polling.PollingHelper;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.domain.AbsoluteLayoutContainer;
import org.openremote.web.console.domain.Background;
import org.openremote.web.console.domain.GridLayoutContainer;
import org.openremote.web.console.domain.LayoutContainer;
import org.openremote.web.console.domain.Navigate;
import org.openremote.web.console.domain.Screen;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

/**
 * The Class ScreenView for init screen components.
 */
public class ScreenView extends com.extjs.gxt.ui.client.widget.LayoutContainer {

   private Icons icons = GWT.create(Icons.class);
   private Screen screen;
   private PollingHelper polling;
   
   public ScreenView(Screen screen) {
      this.screen = screen;
      setStyleAttribute("backgroundColor", "white");
      setStyleAttribute("position", "relative");
      setStyleAttribute("overflow", "auto");
      setBorders(true);
      init(screen);
      addContextMenu();
   }
   
   private void init(Screen screen) {
      ArrayList<LayoutContainer> layouts = screen.getLayouts();
      if (layouts.size() > 0) {
         for (LayoutContainer layoutContainer : layouts) {
            if (layoutContainer instanceof AbsoluteLayoutContainer) {
               add(new AbsolutLayoutContainerView((AbsoluteLayoutContainer)layoutContainer));
            } else if (layoutContainer instanceof GridLayoutContainer) {
               add(new GridLayoutContainerView((GridLayoutContainer)layoutContainer));
            }
         }
      }
      
      if (screen.getBackground() != null) {
         addBackground(screen.getBackground());
      }
   }
   
   private void addBackground(Background background) {
      String url = ClientDataBase.appSetting.getResourceRootPath()
            + URL.encode(background.getBackgroundImage().getSrc());
      setStyleAttribute("backgroundImage", "url(" + url + ")");
      setStyleAttribute("backgroundRepeat", "no-repeat");
      if (background.isFillScreen()) {
         setStyleAttribute("backgroundPosition", "top left");
      } else if (background.isBackgroundImageAbsolutePosition()) {
         setStyleAttribute("backgroundPosition", background.getBackgroundImageAbsolutePositionLeft() + " "
               + background.getBackgroundImageAbsolutePositionTop());
      } else {
         setStyleAttribute("backgroundPosition", Background.getRelativeMap().get(
               background.getBackgroundImageRelativePosition()));
      }
   }

   public void startPolling() {
      if (!screen.getPollingComponentsIds().isEmpty()) {
         polling = new PollingHelper(screen.getPollingComponentsIds());
         polling.requestCurrentStatusAndStartPolling();
      }
   }
   
   public void cancelPolling() {
      if (polling != null) {
         polling.cancelPolling();
      }
   }

   private void addContextMenu() {
      Menu contextMenu = new Menu();
      contextMenu.setWidth(140);

      MenuItem setting = new MenuItem();
      setting.setIcon(icons.setting());
      setting.setText("Setting...");
      setting.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerToPopSetting, null);
         }
      });
      contextMenu.add(setting);
      
      MenuItem login = new MenuItem();
      login.setIcon(icons.login());
      login.setText("Login...");
      login.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            Navigate loginNavigate = new Navigate();
            loginNavigate.setLogin(true);
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, loginNavigate);
         }
      });
      contextMenu.add(login);
      
      MenuItem logout = new MenuItem();
      logout.setIcon(icons.logout());
      logout.setText("Logout");
      logout.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            Navigate logoutNavigate = new Navigate();
            logoutNavigate.setLogout(true);
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, logoutNavigate);
         }
      });
      contextMenu.add(logout);
      
      MenuItem back = new MenuItem();
      back.setIcon(icons.back());
      back.setText("Back");
      back.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            Navigate backNavigate = new Navigate();
            backNavigate.setBack(true);
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, backNavigate);
         }
      });
      contextMenu.add(back);
      
      MenuItem previous = new MenuItem();
      previous.setIcon(icons.left());
      previous.setText("Previous screen");
      previous.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            Navigate previousNavigate = new Navigate();
            previousNavigate.setPreviousScreen(true);
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, previousNavigate);
         }
      });
      contextMenu.add(previous);
      
      MenuItem next = new MenuItem();
      next.setIcon(icons.right());
      next.setText("Next screen");
      next.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            Navigate nextNavigate = new Navigate();
            nextNavigate.setNextScreen(true);
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, nextNavigate);
         }
      });
      contextMenu.add(next);
      
      setContextMenu(contextMenu);
   }
}
