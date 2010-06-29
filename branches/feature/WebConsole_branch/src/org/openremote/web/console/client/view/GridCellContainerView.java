package org.openremote.web.console.client.view;

import org.openremote.web.console.domain.Button;
import org.openremote.web.console.domain.Component;
import org.openremote.web.console.domain.GridCell;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class GridCellContainerView extends LayoutContainer {

   public GridCellContainerView(int cellWidth, int cellHeight, GridCell gridCell) {
      setStyleAttribute("position", "absolute");
      setLayout(new FitLayout());
      setPosition(cellWidth * gridCell.getX(), cellHeight * gridCell.getY());
      setSize(cellWidth * gridCell.getColspan(), cellHeight * gridCell.getRowspan());
      Component component = gridCell.getComponent();
      if (component instanceof Button) {
         // temp display button.
         Button uiButton = (Button)component;
         com.extjs.gxt.ui.client.widget.button.Button btn = new com.extjs.gxt.ui.client.widget.button.Button();
         btn.setText(uiButton.getName());
         add(btn);
      }
   }
}
