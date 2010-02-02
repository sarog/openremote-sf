package org.openremote.android.console.view;

import org.openremote.android.console.bindings.XButton;
import org.openremote.android.console.bindings.Control;

import android.content.Context;

public class ControlView extends ComponentView {

   protected ControlView(Context context) {
      super(context);
   }
   
   public static ControlView buildWithControl(Context context, Control control) {
      ControlView controlView = null;
      if (control instanceof XButton) {
         controlView = new ButtonView(context, (XButton)control);
      }
      return controlView;
   }
}
