package org.openremote.android.console.view;

import org.openremote.android.console.bindings.GridCell;

import android.content.Context;
import android.widget.FrameLayout;

public class GridCellView extends FrameLayout {

   public GridCellView(Context context, GridCell gridCell) {
      super(context);
      ComponentView componentView = ComponentView.buildWithComponent(context, gridCell.getComponent());
      if (componentView != null) {
         addView(componentView);
      }
   }

}
