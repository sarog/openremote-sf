/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.client.widget;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeDataModel;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroPanel.
 */
public class MacroPanel extends ContentPanel {
   
   /** The icons. */
   private Icons icons = GWT.create(Icons.class);
   
   /** The macro tree. */
   private TreePanel<ModelData> macroTree = null;

   /**
    * Instantiates a new macro panel.
    */
   public MacroPanel() {
      setHeading("Macros");
      setLayout(new FitLayout());
      createMenu();
      createMacroTree();
      setIcon(icons.macroIcon());
      setBodyBorder(false);

   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar macroToolBar = new ToolBar();
      
      Button newMacroBtn = new Button("New");
      newMacroBtn.setIcon(icons.macroAddIcon());
      newMacroBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){

         @Override
         public void componentSelected(ButtonEvent ce) {
            MacroWindow macroWindow = new MacroWindow();
            
         }
         
      });
      macroToolBar.add(newMacroBtn);
      
      Button editMacroBtn = new Button("Edit");
      editMacroBtn.setIcon(icons.macroEditIcon());
      macroToolBar.add(editMacroBtn);
      
      Button deleteMacroBtn = new Button("Delete");
      deleteMacroBtn.setIcon(icons.macroDeleteIcon());
      macroToolBar.add(deleteMacroBtn);
      
      macroToolBar.setBorders(true);
      
      setTopComponent(macroToolBar);
   }

   /**
    * Creates the macro tree.
    */
   private void createMacroTree() {
      LayoutContainer treeContainer = new LayoutContainer();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(true);

      TreeStore<ModelData> store = new TreeStore<ModelData>();
      macroTree = new TreePanel<ModelData>(store);
      macroTree.setDisplayProperty(TreeDataModel.getDisplayProperty());
      macroTree.getStyle().setLeafIcon(icons.macroIcon());
      treeContainer.add(macroTree);

      add(treeContainer);
   }

}
