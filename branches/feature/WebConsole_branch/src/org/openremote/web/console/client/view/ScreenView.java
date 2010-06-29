package org.openremote.web.console.client.view;

import java.util.ArrayList;

import org.openremote.web.console.domain.AbsoluteLayoutContainer;
import org.openremote.web.console.domain.GridLayoutContainer;
import org.openremote.web.console.domain.LayoutContainer;
import org.openremote.web.console.domain.Screen;

public class ScreenView extends com.extjs.gxt.ui.client.widget.LayoutContainer {

   public ScreenView(Screen screen) {
      setStyleAttribute("backgroundColor", "white");
      setStyleAttribute("position", "relative");
      init(screen);
   }
   
   private void init(Screen screen) {
      ArrayList<LayoutContainer> layouts = screen.getLayouts();
      if (layouts.size() > 0) {
         for (LayoutContainer layoutContainer : layouts) {
            if (layoutContainer instanceof AbsoluteLayoutContainer) {
               add(new AbsolutLayoutContainerView((AbsoluteLayoutContainer)layoutContainer));
            } else if (layoutContainer instanceof GridLayoutContainer) {
               add(new GridLayoutContainerView((GridLayoutContainer)layoutContainer));
            }
         }
      }
   }
   
}
