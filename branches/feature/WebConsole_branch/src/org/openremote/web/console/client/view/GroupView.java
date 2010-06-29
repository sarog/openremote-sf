package org.openremote.web.console.client.view;

import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.domain.Group;
import org.openremote.web.console.domain.Screen;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

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
