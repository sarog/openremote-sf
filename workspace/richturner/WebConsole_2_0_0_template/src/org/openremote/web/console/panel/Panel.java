package org.openremote.web.console.panel;

import java.util.Map;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Screen;

public interface Panel {
   public Map<Integer, Group> getGroups();

   public Map<Integer, Screen> getScreens();
}
