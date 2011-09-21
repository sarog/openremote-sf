package org.openremote.web.console.panel;

import org.openremote.web.console.entity.Group;
import org.openremote.web.console.entity.Screen;
import org.openremote.web.console.entity.TabBar;

public interface Panel {
   public Group[] getGroups();

   public Screen[] getScreens();
   
   public TabBar getTabBar();
}
