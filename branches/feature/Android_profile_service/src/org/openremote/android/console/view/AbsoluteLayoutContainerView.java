package org.openremote.android.console.view;

import org.openremote.android.console.bindings.AbsoluteLayoutContainer;

import android.content.Context;

public class AbsoluteLayoutContainerView extends LayoutContainerView {

   public AbsoluteLayoutContainerView(Context context, AbsoluteLayoutContainer absoluteLayoutContainer) {
      super(context);
      ComponentView componentView = ComponentView.buildWithComponent(context, absoluteLayoutContainer.getComponent());
      if (componentView != null) {
         addView(componentView);
      }
   }

}
