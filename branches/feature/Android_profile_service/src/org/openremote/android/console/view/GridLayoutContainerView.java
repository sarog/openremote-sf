package org.openremote.android.console.view;

import java.util.ArrayList;

import org.openremote.android.console.bindings.GridCell;
import org.openremote.android.console.bindings.GridLayoutContainer;

import android.content.Context;
import android.widget.AbsoluteLayout;

public class GridLayoutContainerView extends LayoutContainerView {

   @SuppressWarnings("deprecation")
   public GridLayoutContainerView(Context context, GridLayoutContainer gridLayoutContainer) {
      super(context);
      AbsoluteLayout gridContainer = new AbsoluteLayout(context);
      ArrayList<GridCell> gridCells = gridLayoutContainer.getCells();
      int cellSize = gridCells.size();
      int cellWidth = gridLayoutContainer.getWidth()/gridLayoutContainer.getCols();
      int cellHeight = gridLayoutContainer.getHeight()/gridLayoutContainer.getRows();
      for (int i = 0; i < cellSize; i++) {
         GridCell gridCell = gridCells.get(i);
         gridContainer.addView(new GridCellView(context, gridCell), new AbsoluteLayout.LayoutParams(cellWidth
               * gridCell.getColspan(), cellHeight * gridCell.getRowspan(), cellWidth * gridCell.getX(), cellHeight
               * gridCell.getY()));
      }
      addView(gridContainer);
   }

}
