package org.openremote.android.console.view;

import org.openremote.android.console.bindings.XScreen;

import android.content.Context;
import android.widget.AbsoluteLayout;
import android.widget.Button;

public class ScreenView extends AbsoluteLayout {

   private XScreen screen;
   public ScreenView(Context context, XScreen screen) {
      super(context);
      this.screen = screen;
      setBackgroundColor(0);
      setTag(screen.getName());
      Button btn = new Button(context);
      btn.setText(screen.getName());
      addView(btn);
   }
   

}
