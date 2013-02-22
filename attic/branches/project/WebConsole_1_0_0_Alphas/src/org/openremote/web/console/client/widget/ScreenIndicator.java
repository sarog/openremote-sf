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
package org.openremote.web.console.client.widget;

import java.util.List;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.domain.Navigate;
import org.openremote.web.console.domain.Screen;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.user.client.Event;

/**
 * The ScreenIndicator is for showing the screen page control below the screen.
 * It support navigate to screen in a group.
 */
public class ScreenIndicator extends LayoutContainer {

   private LayoutContainer indicatorContainer;
   private int currentGroupId;
   private int currentScreenIndex = -1;
   private static final String NAVIGATETOSCREEN = "navigateToScreen";
   
   /**
    * Instantiates a new screen indicator.
    * 
    * @param groupId the group id
    * @param screenIndex the screen index
    * @param screens the screens
    */
   public ScreenIndicator(int groupId, int screenIndex, List<Screen> screens) {
      this.currentGroupId = groupId;
      int screenCount = screens.size();
      setLayout(new CenterLayout());
      if (screenCount > 1) {
         setStyleAttribute("backgroundColor", "gray");
         currentScreenIndex = screenIndex;
         indicatorContainer = new LayoutContainer();
         indicatorContainer.setSize(15 * screenCount, 14);
         initialIndicatorContainer(groupId, screenIndex, screens, screenCount);
         add(indicatorContainer);
      }
   }
   
   /**
    * Update current page control below the screen.
    * 
    * @param groupId the group id
    * @param screenIndex the screen index
    * @param screens the screens
    */
   public void updateCurrentPageControl(int groupId, int screenIndex, List<Screen> screens) {
      if (indicatorContainer != null) {
         int screenSize = screens.size();
         if (currentGroupId == groupId) {
            indicatorContainer.getItem(currentScreenIndex).setStyleName("indicator-default");
            indicatorContainer.getItem(currentScreenIndex).sinkEvents(Event.ONMOUSEDOWN);
            currentScreenIndex = screenIndex;
            indicatorContainer.getItem(currentScreenIndex).setStyleName("indicator-current");
            indicatorContainer.getItem(currentScreenIndex).unsinkEvents(Event.ONMOUSEDOWN);
         } else if (screenSize > 1) {
            currentGroupId = groupId;
            currentScreenIndex = screenIndex;
            indicatorContainer.removeAll();
            indicatorContainer.setWidth(15 * screenSize);
            initialIndicatorContainer(groupId, screenIndex, screens, screenSize);
            layout(true);
         }
      }
   }
   
   /**
    * Initial indicator container.
    * 
    * @param groupId the group id
    * @param screenIndex the screen index
    * @param screens the screens
    * @param screenCount the screen count
    */
   private void initialIndicatorContainer(int groupId, int screenIndex, List<Screen> screens, int screenCount) {
      for (int i = 0; i < screenCount; i++) {
         LayoutContainer indicator = new LayoutContainer() {
            @Override
            public void onComponentEvent(ComponentEvent ce) {
               super.onComponentEvent(ce);
               if (ce.getEventTypeInt() == Event.ONMOUSEDOWN) {
                  ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerNavigateTo, getData(NAVIGATETOSCREEN));
               }
            }
         };
         
         indicator.setData(NAVIGATETOSCREEN, new Navigate(groupId, screens.get(i).getScreenId()));
         indicator.setSize(13, 14);
         indicator.setToolTip("To " + screens.get(i).getName());
         if (i != screenIndex) {
            indicator.sinkEvents(Event.ONMOUSEDOWN);
            indicator.setStyleName("indicator-default");
         } else {
            indicator.setStyleName("indicator-current");
            indicator.unsinkEvents(Event.ONMOUSEDOWN);
         }
         indicatorContainer.add(indicator);
      }
   }
}
