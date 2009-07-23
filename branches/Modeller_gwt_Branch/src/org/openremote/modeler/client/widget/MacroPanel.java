/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

/**
 * The Class MacroPanel.
 */
public class MacroPanel extends ContentPanel {

   /**
    * The icons.
    */
   private Icons icons = GWT.create(Icons.class);

   /**
    * The macro tree.
    */
   private TreePanel<BeanModel> macroTree = null;

   private LayoutContainer macroListContainer = null;
   
   //Do NOT use it directly, use getter method
   private ChangeListener dragSourceBeanModelChangeListener = null;

   /**
    * Instantiates a new macro panel.
    */
   public MacroPanel() {
      setHeading("Macros");
      setLayout(new FitLayout());
      createMenu();
      createMacroTree();
      setIcon(icons.macroIcon());
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
                  afterCreateDeviceMacro(be.<DeviceMacro>getData());
                  macroWindow.hide();
               }
            });

            macroWindow.setDragSourceBeanModelChangeListener(getDragSourceBeanModelChangeListener());
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

      setTopComponent(macroToolBar);
   }

   /**
    * Creates the macro tree.
    */
   private void createMacroTree() {

      macroListContainer = new LayoutContainer();
      macroListContainer.setScrollMode(Scroll.AUTO);
      macroListContainer.setStyleAttribute("backgroundColor", "white");
      macroListContainer.setBorders(false);
      macroListContainer.setLayoutOnChange(true);

      macroListContainer.setHeight("100%");

      add(macroListContainer);

   }

   /*
    * (non-Javadoc)
    * 
    * @see com.extjs.gxt.ui.client.widget.ContentPanel#afterExpand()
    */
   @Override
   protected void afterExpand() {
      if (macroTree == null) {
         macroTree = TreePanelBuilder.buildMacroTree();
         macroTree.getStore().addStoreListener(new StoreListener<BeanModel>(){

            @Override
            public void storeDataChanged(StoreEvent<BeanModel> se) {
               super.storeDataChanged(se);
               for (BeanModel beanModel : se.getModels()) {
                  if (beanModel.getBean() instanceof DeviceMacroItem) {
                     DeviceMacroItem deviceMacroItem = (DeviceMacroItem) beanModel.getBean();
                     BeanModel deviceMacroItemModel = BeanModelDataBase.getDeviceMacroItemBeanModel(deviceMacroItem);
                     deviceMacroItemModel.addChangeListener(getDragSourceBeanModelChangeListener());
                  }
               }
            }
         });
         macroListContainer.add(macroTree);
      }
      super.afterExpand();

   }

   private void afterCreateDeviceMacro(DeviceMacro deviceMacro) {
      BeanModel deviceBeanModel = deviceMacro.getBeanModel();
      macroTree.getStore().add(deviceBeanModel, false);
      for (DeviceMacroItem deviceMacroItem : deviceMacro.getDeviceMacroItems()) {
         macroTree.getStore().add(deviceBeanModel, deviceMacroItem.getBeanModel(), false);
      }
   }

   /**
    * On edit device macro btn clicked.
    */
   private void onEditDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItem() != null) {
         final BeanModel oldModel = macroTree.getSelectionModel().getSelectedItem();
         final MacroWindow macroWindow = new MacroWindow(macroTree.getSelectionModel().getSelectedItem());
         macroWindow.addSubmitListener(new Listener<AppEvent>() {
            public void handleEvent(AppEvent be) {
               afterUpdateDeviceMacroSubmit(oldModel, be.<DeviceMacro>getData());
               macroWindow.hide();
            }
         });
         macroWindow.show();

      }
   }

   /**
    * On delete device macro btn clicked.
    */
   private void onDeleteDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItems().size() > 0) {
         for (final BeanModel data : macroTree.getSelectionModel().getSelectedItems()) {
            if (data.getBean() instanceof DeviceMacro) {
               DeviceMacroBeanModelProxy.deleteDeviceMacro(data,new AsyncSuccessCallback<Void>() {
                  @Override
                  public void onSuccess(Void result) {
                     ChangeEvent evt = new ChangeEvent(ChangeEventSupport.Remove, macroTree.getStore().getParent(data),data);
                     data.notify(evt);
                     macroTree.getStore().remove(data);
                  }
               });
            }

         }
      }
   }

   private void afterUpdateDeviceMacroSubmit(final BeanModel dataModel, DeviceMacro deviceMacro) {
      int index = macroTree.getStore().indexOf(dataModel);
      macroTree.getStore().remove(dataModel);
      BeanModel newDeviceMacroModel = deviceMacro.getBeanModel();
      macroTree.getStore().insert(newDeviceMacroModel, index, false);
      macroTree.getStore().insert(newDeviceMacroModel, DeviceMacroItem.createModels(deviceMacro.getDeviceMacroItems()), 0, false);
      macroTree.setExpanded(newDeviceMacroModel, true);
   }

   private ChangeListener getDragSourceBeanModelChangeListener() {
      if (dragSourceBeanModelChangeListener == null) {
         dragSourceBeanModelChangeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  if (changeEvent.getItem() instanceof BeanModel) {
                     BeanModel beanModel = (BeanModel) changeEvent.getItem();
                     if (beanModel.getBean() instanceof DeviceMacro || beanModel.getBean() instanceof DeviceCommand) {

                        macroTree.getStore().remove((BeanModel) changeEvent.getItem());
                     }
                  }
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  
               }
            }
         };
      } 
      return dragSourceBeanModelChangeListener;
   }

}
