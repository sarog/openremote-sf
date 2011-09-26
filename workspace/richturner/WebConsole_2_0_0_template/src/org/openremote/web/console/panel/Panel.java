package org.openremote.web.console.panel;

import org.openremote.web.console.panel.entity.Groups;
import org.openremote.web.console.panel.entity.Screens;
import org.openremote.web.console.panel.entity.TabBar;

public interface Panel {
   public Groups getGroups();
   public void setGroups(Groups groups);
   
   public Screens getScreens();
   public void setScreens(Screens screens);
   
   public TabBar getTabBar();
   public void getTabBar(TabBar tabBar);
}
