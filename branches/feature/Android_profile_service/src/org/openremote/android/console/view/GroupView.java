package org.openremote.android.console.view;

import java.util.List;

import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.Screen;

import android.content.Context;

public class GroupView {

   private Group group;
   private ScreenViewFlipper portraitScreenViewFlipper;
   private ScreenViewFlipper landscapeScreenViewFlipper;
   public GroupView(Context context, Group group) {
      this.group = group;
      if (group.getPortraitScreens().size() > 0) {
         portraitScreenViewFlipper = new ScreenViewFlipper(context);
         for (Screen screen : group.getPortraitScreens()) {
            portraitScreenViewFlipper.addView(new ScreenView(context, screen));
         }
      }
      
      if (group.getLandscapeScreens().size() > 0) {
         landscapeScreenViewFlipper = new ScreenViewFlipper(context);
         for (Screen screen : group.getLandscapeScreens()) {
            landscapeScreenViewFlipper.addView(new ScreenView(context, screen));
         }
      }
   }
   
   public Group getGroup() {
      return group;
   }
   public ScreenViewFlipper getScreenViewFlipperByOrientation(boolean landscape) {
      if (landscape) {
         return landscapeScreenViewFlipper;
      }
      return portraitScreenViewFlipper;
   }
   public void setGroup(Group group) {
      this.group = group;
   }
   public void setScreenViewFlipper(ScreenViewFlipper screenViewFlipper) {
//      this.screenViewFlipper = screenViewFlipper;
   }
   
   
}
