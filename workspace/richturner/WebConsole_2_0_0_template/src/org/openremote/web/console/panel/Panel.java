package org.openremote.web.console.panel;

import org.openremote.web.console.panel.entity.GroupList;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.ScreenList;
import org.openremote.web.console.panel.entity.TabBar;

public interface Panel {
   GroupList getGroups();
   ScreenList getScreens();
   TabBar getTabBar();
   
   void setGroups(GroupList groups);
   void setScreens(ScreenList screens);
   void setTabBar(TabBar tabBar);
}
