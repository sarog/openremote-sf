package org.openremote.android.console.view;

import org.openremote.android.console.bindings.XButton;

import android.content.Context;
import android.widget.Button;

public class ButtonView extends ControlView {

   private Button uiButton;
   public ButtonView(Context context, XButton button) {
      super(context);
      if (button != null) {
         uiButton  = new Button(context);
         initButton(button);
      }
   }
   
   private void initButton(XButton button) {
      uiButton.setId(button.getButtonId());
      uiButton.setText(button.getName());
      addView(uiButton);
   }

}
