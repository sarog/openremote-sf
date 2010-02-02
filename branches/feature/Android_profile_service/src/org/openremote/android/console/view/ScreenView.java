package org.openremote.android.console.view;

import java.util.ArrayList;

import org.openremote.android.console.bindings.LayoutContainer;
import org.openremote.android.console.bindings.XScreen;

import android.content.Context;
import android.widget.AbsoluteLayout;

public class ScreenView extends AbsoluteLayout {

   private XScreen screen;
   @SuppressWarnings("deprecation")
   public ScreenView(Context context, XScreen screen) {
      super(context);
      this.screen = screen;
      setBackgroundColor(0);
      setTag(screen.getName());
      ArrayList<LayoutContainer> layouts = screen.getLayouts();
      for (int i = 0; i < layouts.size(); i++) {
         LayoutContainerView la = LayoutContainerView.buildWithLayoutContainer(context, layouts.get(i));
         if (la != null) {
            LayoutContainer layout = layouts.get(i);
            addView(la, new AbsoluteLayout.LayoutParams(layout.getWidth(), layout.getHeight(), layout.getLeft(), layout.getTop()));
         }
      }
   }
   

}
