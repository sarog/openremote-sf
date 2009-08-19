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
package org.openremote.modeler.client.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

/**
 * The Class MacroPanel.
 */
public class MacroPanel extends ContentPanel {

   /** The icons. */
   private Icons icons = GWT.create(Icons.class);

   /** The macro tree. */
   private TreePanel<BeanModel> macroTree = null;

   /** The macro list container. */
   private LayoutContainer macroListContainer = null;

   /** The change listener map. */
   private Map<BeanModel, ChangeListener> changeListenerMap = null;

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

            macroWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  afterCreateDeviceMacro(be.<DeviceMacro> getData());
                  macroWindow.hide();
               }
            });
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
         addTreeStoreEventListener();

         macroListContainer.add(macroTree);
      }
      super.afterExpand();
   }

   /**
    * Adds the tree store event listener.
    */
   private void addTreeStoreEventListener() {
      macroTree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {

         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }

      });
      macroTree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {

         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addChangeListenerToDragSource(be.getChildren());
         }

      });
      macroTree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {

         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }

      });
      macroTree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {

         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeChangeListenerToDragSource(be.getChildren());
         }

      });
   }

   /**
    * Adds the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void addChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.addChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
      }
   }

   /**
    * Removes the change listener to drag source.
    * 
    * @param models
    *           the models
    */
   private void removeChangeListenerToDragSource(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceMacroRef) {
            BeanModelDataBase.deviceMacroTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));

         }
         if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.removeChangeListener(BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel), getDragSourceBeanModelChangeListener(beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }

   /**
    * After create device macro.
    * 
    * @param deviceMacro
    *           the device macro
    */
   private void afterCreateDeviceMacro(DeviceMacro deviceMacro) {
      BeanModel deviceBeanModel = deviceMacro.getBeanModel();
      macroTree.getStore().add(deviceBeanModel, false);
      macroTree.setExpanded(deviceBeanModel, true);
   }

   /**
    * On edit device macro btn clicked.
    */
   private void onEditDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItem() != null) {
         final BeanModel oldModel = macroTree.getSelectionModel().getSelectedItem();
         final MacroWindow macroWindow = new MacroWindow(macroTree.getSelectionModel().getSelectedItem());
         macroWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               afterUpdateDeviceMacroSubmit(oldModel, be.<DeviceMacro> getData());
               macroWindow.hide();
            }
         });
      }
   }

   /**
    * On delete device macro btn clicked.
    */
   private void onDeleteDeviceMacroBtnClicked() {
      if (macroTree.getSelectionModel().getSelectedItems().size() > 0) {
         for (final BeanModel data : macroTree.getSelectionModel().getSelectedItems()) {
            if (data.getBean() instanceof DeviceMacro) {
               DeviceMacroBeanModelProxy.deleteDeviceMacro(data, new AsyncSuccessCallback<Void>() {
                  @Override
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
    * @param dataModel
    *           the data model
    * @param deviceMacro
    *           the device macro
    */
   private void afterUpdateDeviceMacroSubmit(final BeanModel dataModel, DeviceMacro deviceMacro) {
      DeviceMacro old = dataModel.getBean();
      old.setName(deviceMacro.getName());
      old.setDeviceMacroItems(deviceMacro.getDeviceMacroItems());
      List<BeanModel> macroItemBeanModels = BeanModelDataBase.getBeanModelsByBeans(deviceMacro.getDeviceMacroItems(),
            BeanModelDataBase.deviceMacroItemMap);
      macroTree.getStore().removeAll(dataModel);
      for (BeanModel beanModel : macroItemBeanModels) {
         macroTree.getStore().add(dataModel, beanModel, false);
      }
      macroTree.getStore().update(dataModel);
      macroTree.setExpanded(dataModel, true);
   }

   /**
    * Gets the drag source bean model change listener.
    * 
    * @param target
    *           the target
    * 
    * @return the drag source bean model change listener
    */
   private ChangeListener getDragSourceBeanModelChangeListener(final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);

      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  macroTree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof DeviceMacro) {
                     DeviceMacro deviceMacro = (DeviceMacro) source.getBean();
                     DeviceMacroRef deviceMacroRef = (DeviceMacroRef) target.getBean();
                     deviceMacroRef.setTargetDeviceMacro(deviceMacro);
                  }

                  if (source.getBean() instanceof DeviceCommand) {
                     DeviceCommand deviceCommand = (DeviceCommand) source.getBean();
                     DeviceCommandRef deviceCommandRef = (DeviceCommandRef) target.getBean();
                     deviceCommandRef.setDeviceCommand(deviceCommand);
                  }
                  macroTree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }

}
