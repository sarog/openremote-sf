package org.openremote.web.console.panel;

import org.openremote.web.console.panel.entity.Groups;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.Screens;
import org.openremote.web.console.panel.entity.TabBar;

public interface Panel {
   Groups getGroups();
   Screens getScreens();
   TabBar getTabBar();
   
   void setGroups(Groups groups);
   void setScreens(Screens screens);
   void setTabBar(TabBar tabBar);
}
