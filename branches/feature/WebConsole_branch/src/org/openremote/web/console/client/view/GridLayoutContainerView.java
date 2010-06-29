package org.openremote.web.console.client.view;

import org.openremote.web.console.domain.GridCell;
import org.openremote.web.console.domain.GridLayoutContainer;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class GridLayoutContainerView extends LayoutContainer {

   public GridLayoutContainerView(GridLayoutContainer gridLayoutContainer) {
      setStyleAttribute("position", "absolute");
      setSize(gridLayoutContainer.getWidth(), gridLayoutContainer.getHeight());
      setPosition(gridLayoutContainer.getLeft(), gridLayoutContainer.getTop());
      init(gridLayoutContainer);
   }
   
   private void init(GridLayoutContainer gridLayoutContainer) {
      int cellWidth = gridLayoutContainer.getWidth() / gridLayoutContainer.getCols();
      int cellHeight = gridLayoutContainer.getHeight() / gridLayoutContainer.getRows();
      for (GridCell gridCell: gridLayoutContainer.getCells()) {
         add(new GridCellContainerView(cellWidth, cellHeight, gridCell));
      }
   }
}
