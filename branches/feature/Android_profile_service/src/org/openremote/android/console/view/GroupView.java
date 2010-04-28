package org.openremote.android.console.view;

import java.util.List;

import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.XScreen;

import android.content.Context;

public class GroupView {

   private Group group;
   private ScreenViewFlipper screenViewFlipper;
   public GroupView(Context context, Group group) {
      this.group = group;
      this.screenViewFlipper = new ScreenViewFlipper(context);
      List<XScreen> screens = group.getScreens();
      int screenSize = screens.size();
      for (int i = 0; i < screenSize; i++) {
         screenViewFlipper.addView(new ScreenView(context, screens.get(i)));
      }
   }
   
   public Group getGroup() {
      return group;
   }
   public ScreenViewFlipper getScreenViewFlipper() {
      return screenViewFlipper;
   }
   public void setGroup(Group group) {
      this.group = group;
   }
   public void setScreenViewFlipper(ScreenViewFlipper screenViewFlipper) {
      this.screenViewFlipper = screenViewFlipper;
   }
   
   
}
