/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.modeler.client.widget.UIDesigner;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.widget.BlockWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

/**
 * The Class ActivityPanel.
 */
public class ActivityPanel extends ContentPanel {

   /** The icon. */
   private Icons icon = GWT.create(Icons.class);

   /**
    * Instantiates a new activity panel.
    */
   public ActivityPanel() {
      setHeading("Browser");
      setLayout(new FitLayout());
      createMenu();

      createActivityTree();
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      Button newButton = new Button("New");
      newButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            onNewBtnClick();
         }

      });
      newButton.setIcon(icon.add());

      Button editBtn = new Button("Edit");
      editBtn.setIcon(icon.edit());

      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.edit());

      toolBar.add(newButton);
      toolBar.add(editBtn);
      toolBar.add(deleteBtn);

      setTopComponent(toolBar);
   }

   private void onNewBtnClick() {
      BlockWindow blockWindow = new BlockWindow(new ActivityFormPanel());
      blockWindow.setWidth(400);
      blockWindow.show();
   }

   /**
    * Creates the activity tree.
    */
   private void createActivityTree() {
   }

}
