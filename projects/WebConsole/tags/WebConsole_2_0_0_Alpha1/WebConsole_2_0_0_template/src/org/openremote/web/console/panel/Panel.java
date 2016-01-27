package org.openremote.web.console.panel;

import org.openremote.web.console.panel.entity.GroupList;
import org.openremote.web.console.panel.entity.ScreenList;
import org.openremote.web.console.panel.entity.TabBar;

public interface Panel {
   GroupList getGroups();
   ScreenList getScreens();
   TabBar getTabbar();
   
   void setGroups(GroupList groups);
   void setScreens(ScreenList screens);
   void setTabbar(TabBar tabBar);
}
