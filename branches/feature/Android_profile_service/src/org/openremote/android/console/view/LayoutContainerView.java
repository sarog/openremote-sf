package org.openremote.android.console.view;

import org.openremote.android.console.bindings.AbsoluteLayoutContainer;
import org.openremote.android.console.bindings.GridLayoutContainer;
import org.openremote.android.console.bindings.LayoutContainer;

import android.content.Context;
import android.widget.FrameLayout;

public class LayoutContainerView extends FrameLayout {

   protected LayoutContainerView(Context context) {
      super(context);
   }

   public static LayoutContainerView buildWithLayoutContainer(Context context, LayoutContainer layout) {
      LayoutContainerView layoutConatinerView = null;
      if (layout instanceof AbsoluteLayoutContainer) {
         layoutConatinerView = new AbsoluteLayoutContainerView(context, (AbsoluteLayoutContainer)layout);
      } else if(layout instanceof GridLayoutContainer) {
         layoutConatinerView = new GridLayoutContainerView(context, (GridLayoutContainer)layout);
      }
      return layoutConatinerView;
   }
}
