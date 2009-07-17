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
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.DeviceMacroService;
import org.openremote.modeler.client.rpc.DeviceMacroServiceAsync;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
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

   /** The device macro service. */
   private DeviceMacroServiceAsync deviceMacroService = (DeviceMacroServiceAsync) GWT.create(DeviceMacroService.class);

   /** The icons. */
   private Icons icons = GWT.create(Icons.class);

   /** The macro tree. */
   private TreePanel<TreeDataModel> macroTree = null;

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
      newMacroBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            final MacroWindow macroWindow = new MacroWindow();

            macroWindow.addSubmitListener(new Listener<AppEvent>() {

               public void handleEvent(AppEvent be) {
                  afterCreateDeviceMacro(macroWindow, be);
               }

            });
            macroWindow.show();
         }

      });
      macroToolBar.add(newMacroBtn);

      Button editMacroBtn = new Button("Edit");
      editMacroBtn.setIcon(icons.macroEditIcon());
      editMacroBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            onEditDeviceMacroBtnClicked();

         }

      });
      macroToolBar.add(editMacroBtn);

      Button deleteMacroBtn = new Button("Delete");
      deleteMacroBtn.setIcon(icons.macroDeleteIcon());
      deleteMacroBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            onDeleteDeviceMacroBtnClicked();
         }

      });
      macroToolBar.add(deleteMacroBtn);

      macroToolBar.setBorders(true);

      setTopComponent(macroToolBar);
   }

   /**
    * Creates the model with device macro.
    * 
    * @param deviceMacro the device macro
    * 
    * @return the tree data model< device macro>
    */
   private TreeDataModel createModelWithDeviceMacro(DeviceMacro deviceMacro){
      TreeDataModel deviceMacroModel = new TreeDataModel(deviceMacro,
            deviceMacro.getName());
      for (DeviceMacroItem deviceMacroItem : deviceMacro.getDeviceMacroItems()) {
         TreeDataModel macroItemModel = new TreeDataModel(
               deviceMacroItem, deviceMacroItem.getLabel());
         deviceMacroModel.add(macroItemModel);
      }
      return deviceMacroModel;
   }

   /**
    * Creates the macro tree.
    */
   private void createMacroTree() {

      LayoutContainer macroListContainer = new LayoutContainer();
      macroListContainer.setScrollMode(Scroll.AUTO);
      macroListContainer.setStyleAttribute("backgroundColor", "white");
      macroListContainer.setBorders(true);

      macroTree = TreePanelBuilder.buildMacroTree();
      macroListContainer.setHeight("100%");
      macroListContainer.add(macroTree);
      macroListContainer.layout();

      add(macroListContainer);

   }

   /*
    * (non-Javadoc)
    * 
    * @see com.extjs.gxt.ui.client.widget.ContentPanel#afterExpand()
    */
   @Override
   protected void afterExpand() {
      // TODO This is not a good way to solve tree can't display at first.
      macroTree.expandAll();
      macroTree.collapseAll();
      super.afterExpand();
   }

   /**
    * After create device macro.
    * 
    * @param macroWindow the macro window
    * @param be the be
    */
   private void afterCreateDeviceMacro(final MacroWindow macroWindow, AppEvent be) {
      DeviceMacro deviceMacro = be.getData();
      deviceMacroService.saveDeviceMacro(deviceMacro, new AsyncSuccessCallback<DeviceMacro>() {

         public void onSuccess(DeviceMacro deviceMacro) {
            if (macroTree != null) {
               macroTree.getStore().add(createModelWithDeviceMacro(deviceMacro), true);
            }

         }
      });
      macroWindow.hide();
   }

   /**
    * On edit device macro btn clicked.
    */
   private void onEditDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItem() != null) {
         final TreeDataModel dataModel = macroTree.getSelectionModel().getSelectedItem();
         if (dataModel.getData() instanceof DeviceMacro) {
            final MacroWindow macroWindow = new MacroWindow((DeviceMacro) dataModel.getData());
            macroWindow.addSubmitListener(new Listener<AppEvent>() {
               public void handleEvent(AppEvent be) {
                  afterUpdateDeviceMacroSubmit(dataModel, macroWindow, be);
               }

            });
            macroWindow.show();

         }
      }
   }

   /**
    * On delete device macro btn clicked.
    */
   private void onDeleteDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItems().size() > 0) {
         for (final TreeDataModel data : macroTree.getSelectionModel().getSelectedItems()) {
            if (data.getData() instanceof DeviceMacro) {
               DeviceMacro deviceMacro = (DeviceMacro) data.getData();
               deviceMacroService.deleteDeviceMacro(deviceMacro.getOid(), new AsyncSuccessCallback<Void>() {
                  public void onSuccess(Void result) {
                     macroTree.getStore().remove(data);
                  }

               });
            }

         }
      }
   }

   /**
    * After update device macro submit.
    * 
    * @param dataModel the data model
    * @param macroWindow the macro window
    * @param be the be
    */
   private void afterUpdateDeviceMacroSubmit(final TreeDataModel dataModel, final MacroWindow macroWindow, AppEvent be) {
      DeviceMacro deviceMacro = be.getData();
      deviceMacroService.updateDeviceMacro(deviceMacro, new AsyncSuccessCallback<DeviceMacro>() {
         public void onSuccess(DeviceMacro result) {
            int index = macroTree.getStore().indexOf(dataModel);
            macroTree.getStore().remove(dataModel);
            TreeDataModel newModel = createModelWithDeviceMacro(result);
            macroTree.getStore().insert(newModel, index, true);
            macroTree.setExpanded(newModel, true);
            macroWindow.hide();
            macroTree.getSelectionModel().select(newModel, false);
         }
      });
   }

}
