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

import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.Screen;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * The Class GroupView for init group and screens.
 */
public class GroupView extends LayoutContainer {

   public GroupView() {
      setStyleAttribute("backgroundColor", "white");
      setLayout(new FitLayout());
      Group group = ClientDataBase.getDefaultGroup();
      if (group == null || group.getScreens().isEmpty()) {
         // TODO: group not found or no screens, forward to settings.
         return;
      }
      Screen screen = ClientDataBase.getLastTimeScreen();
      if (screen == null) {
         screen = group.getScreens().get(0);
      }
      add(new ScreenView(screen));
   }
}
