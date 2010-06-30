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

import java.util.List;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.icon.Icons;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.widget.ScreenIndicator;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.Screen;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Class GroupView for init group and screens.
 */
public class GroupView extends LayoutContainer {

   private Icons icons = GWT.create(Icons.class);
   private Viewport viewport;
   private BorderLayout layout;
   private Group currentGroup;
   private Screen currentScreen;
   private int screenSize;
   private ScreenView currentScreenView;
   private BorderLayoutData screenData;
   private ScreenIndicator screenIndicator;
   
   public GroupView() {
      viewport = new Viewport();
      layout = new BorderLayout();
      viewport.setLayout(layout);
      if (hasDefaultGroupAndScreen()) {
         createToolBar();
         createScreenView();
         createSouth();
         RootPanel.get().add(viewport);
      }
   }
   
   /**
    * The tool bar is for show the panel or group tabbar.
    */
   private void createToolBar() {
      ToolBar toolBar = new ToolBar() {
         protected void afterRender() {
            super.afterRender();
            layout.hide(Style.LayoutRegion.NORTH);
            // TODO: add global tabbar items.
         }
      };
      
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0, 5, 0, 5));
      data.setCollapsible(true);
      viewport.add(toolBar, data);
   }
   
   private void createScreenView() {
      currentScreenView = new ScreenView(currentScreen);
      screenData = new BorderLayoutData(Style.LayoutRegion.CENTER);
      screenData.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(currentScreenView, screenData);
      
   }

   private void createSouth() {
      LayoutContainer southContainer = new LayoutContainer();
      southContainer.setStyleAttribute("backgroundColor", "white");
      southContainer.setLayout(new RowLayout(Orientation.HORIZONTAL));
      
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 30);
      data.setMargins(new Margins(5));
      
      final Button previousButton = new Button();
      previousButton.setScale(ButtonScale.MEDIUM);
      previousButton.setIcon(icons.previous());
      final Button nextButton = new Button();
      nextButton.setScale(ButtonScale.MEDIUM);
      nextButton.setIcon(icons.next());
      int screenIndex = currentGroup.getScreens().indexOf(currentScreen);
      if (screenIndex == 0) {
         previousButton.disable();
      }
      if (screenIndex == screenSize - 1) {
         nextButton.disable();
      }
      
      previousButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            toNextScreen(previousButton, nextButton);
         }
      });
      
      nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            toPreviousScreen(previousButton, nextButton);
         }
      });
      
      screenIndicator = new ScreenIndicator(currentGroup.getScreens().size(), screenIndex);
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
      if (currentGroup == null || currentGroup.getScreens().isEmpty()) {
         // TODO: group not found or no screens, forward to settings.
         return false;
      }
      screenSize = currentGroup.getScreens().size();
      currentScreen = ClientDataBase.getLastTimeScreen();
      if (currentScreen == null) {
         currentScreen = currentGroup.getScreens().get(0);
      }
      return true;
   }

   /**
    * @param previousButton
    * @param nextButton
    */
   private void toNextScreen(final Button previousButton, final Button nextButton) {
      List<Screen> screens = currentGroup.getScreens();
      int index = screens.indexOf(currentScreen);
      if (index > 0) {
         currentScreen = screens.get(index - 1);
         currentScreenView.removeFromParent();
         currentScreenView = new ScreenView(currentScreen);
         if (index == 1) {
            previousButton.disable();
         }
         if (!nextButton.isEnabled()) {
            nextButton.enable();
         }
         if (screenIndicator != null) {
            screenIndicator.updateCurrentPageControl(index - 1);
         }
         viewport.add(currentScreenView, screenData);
         saveGroupAndScreenToCookie();
         viewport.layout();
      }
   }

   /**
    * @param previousButton
    * @param nextButton
    */
   private void toPreviousScreen(final Button previousButton, final Button nextButton) {
      List<Screen> screens = currentGroup.getScreens();
      int index = screens.indexOf(currentScreen);
      if (index < screenSize - 1) {
         currentScreen = screens.get(index + 1);
         currentScreenView.removeFromParent();
         currentScreenView = new ScreenView(currentScreen);
         if (index == screenSize - 2) {
            nextButton.disable();
         }
         if (!previousButton.isEnabled()) {
            previousButton.enable();
         }
         if (screenIndicator != null) {
            screenIndicator.updateCurrentPageControl(index + 1);
         }
         viewport.add(currentScreenView, screenData);
         saveGroupAndScreenToCookie();
         viewport.layout();
      }
   }
   
   private void saveGroupAndScreenToCookie() {
      ClientDataBase.userInfo.setLastGroupId(currentGroup.getGroupId());
      ClientDataBase.userInfo.setLastScreenId(currentScreen.getScreenId());
      Cookies.setCookie(Constants.CONSOLE_USERINFO, ClientDataBase.userInfo.toJson());
   }
}
