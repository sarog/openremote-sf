package org.openremote.android.console.view;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.openremote.android.console.HTTPUtil;
import org.openremote.android.console.bindings.LayoutContainer;
import org.openremote.android.console.bindings.XScreen;
import org.openremote.android.console.model.AppSettingsModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

public class ScreenView extends AbsoluteLayout {

   private XScreen screen;
   @SuppressWarnings("deprecation")
   public ScreenView(Context context, XScreen screen) {
      super(context);
      this.screen = screen;
      setBackgroundColor(0);
      setTag(screen.getName());
      if (screen.getBackground() != null) {
         HTTPUtil.downLoadImage(context, AppSettingsModel.getCurrentServer(context), screen.getBackgroundSrc());
         ImageView backgroudView = new ImageView(this.getContext());
         try {
            Bitmap backgroud = BitmapFactory.decodeStream(this.getContext().openFileInput(screen.getBackgroundSrc()));
            backgroudView.setImageBitmap(backgroud);
         } catch (FileNotFoundException e) {
            Log.e("ScreenView", "screen background file" + screen.getBackgroundSrc() + " not found.", e);
         }
         addView(backgroudView);
      }
      
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
