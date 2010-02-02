package org.openremote.android.console.view;

import org.openremote.android.console.bindings.Component;
import org.openremote.android.console.bindings.Control;

import android.content.Context;
import android.widget.FrameLayout;

public class ComponentView extends FrameLayout {

   protected ComponentView(Context context) {
      super(context);
   }
   
   public static ComponentView buildWithComponent(Context context, Component component) {
      ComponentView componentView = null;
      if (component instanceof Control) {
         componentView = ControlView.buildWithControl(context, (Control)component);
      }
      return componentView;
   }

}
