/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.SliderBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.SliderTree;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.SliderCommandRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class SliderPanel extends ContentPanel {
   private Icons icons = GWT.create(Icons.class);
   private Button newBtn = new Button("New");
   private Button editBtn = new Button("Edit");
   private Button delBtn = new Button("Delete");
   private TreePanel <BeanModel> sliderTree = null;
   
   private SelectionServiceExt<BeanModel> selectionService;
   private Map<BeanModel, ChangeListener> changeListenerMap = null;
   
   public SliderPanel() {
      this.setHeading("Slider");
      this.setIcon(icons.sliderIcon());
      selectionService = new SelectionServiceExt<BeanModel>();
      

      createMenu();
      createSliderTree();
      layout();
   }
   
   private void createMenu() {
      ToolBar sliderToolBar = new ToolBar();
      newBtn.setToolTip("Create a slider");
      newBtn.setIcon(icons.sliderAddIcon());
      
      editBtn.setToolTip("Edit the slider you select");
      editBtn.setIcon(icons.sliderEditIcon());
      editBtn.setEnabled(false);
      
      delBtn.setToolTip("Delete the slider you select");
      delBtn.setIcon(icons.sliderDeleteIcon());
      delBtn.setEnabled(false);
      
      sliderToolBar.add(newBtn);
      sliderToolBar.add(editBtn);
      sliderToolBar.add(delBtn);
      
      makeCreateAndDeleteControlable();
      add(sliderToolBar);
      
      newBtn.addSelectionListener(new NewSwitchListener());
      editBtn.addSelectionListener(new EditSwitchListener());
      delBtn.addSelectionListener(new DeleteSwitchListener());
   }
   
   private void makeCreateAndDeleteControlable() {
      List<Button> btns = new ArrayList<Button>();
      btns.add(this.delBtn);
      btns.add(this.editBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(btns) {
         @Override
         protected boolean isEditableAndDeletable(List<BeanModel> sels) {
            if (sels.size() > 1) {
               return false;
            }
            BeanModel selectModel = sels.get(0);
            if (selectModel.getBean() instanceof Slider) {
               return true;
            }
            return false;
         }
      });
   }

   private void createSliderTree() {
      sliderTree = SliderTree.buildsliderTree();
      LayoutContainer treeContainer = new LayoutContainer() {

         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            selectionService.addListener(new SourceSelectionChangeListenerExt(sliderTree.getSelectionModel()));
            selectionService.register(sliderTree.getSelectionModel());
            addTreeStoreEventListenerToTree(sliderTree);
         }
         
      };
      treeContainer.add(sliderTree);
      treeContainer.setLayoutOnChange(true);
      add(treeContainer);
   }
   
   private void addTreeStoreEventListenerToTree(TreePanel<BeanModel> tree) {
      tree.getStore().addListener(Store.Add, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addDeviceCommnadChangeListener(be.getChildren());
         }
      });
      tree.getStore().addListener(Store.DataChanged, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            addDeviceCommnadChangeListener(be.getChildren());
         }
      });
      tree.getStore().addListener(Store.Clear, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeDeviceCommandChangeListener(be.getChildren());
         }
      });
      tree.getStore().addListener(Store.Remove, new Listener<TreeStoreEvent<BeanModel>>() {
         public void handleEvent(TreeStoreEvent<BeanModel> be) {
            removeDeviceCommandChangeListener(be.getChildren());
         }
      });
   }
   
   private void addDeviceCommnadChangeListener(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof SliderCommandRef) {
            BeanModelDataBase.deviceCommandTable.addChangeListener(BeanModelDataBase
                  .getOriginalCommandRefItemBeanModelId(beanModel), getTreeUpdateListener(sliderTree, beanModel));
            BeanModelDataBase.deviceTable.addChangeListener(BeanModelDataBase.getSourceBeanModelId(beanModel),
                  getTreeUpdateListener(sliderTree, beanModel));
         }
      }
   }

   /**
    * Removes the change listener to drag source.
    * 
    * @param models  the models
    */
   private void removeDeviceCommandChangeListener(List<BeanModel> models) {
      if (models == null) {
         return;
      }
      for (BeanModel beanModel : models) {
         if (beanModel.getBean() instanceof DeviceCommandRef) {
            BeanModelDataBase.deviceCommandTable.removeChangeListener((BeanModelDataBase
                  .getOriginalDeviceMacroItemBeanModelId(beanModel)), getTreeUpdateListener(sliderTree, beanModel));
         }
         changeListenerMap.remove(beanModel);
      }
   }
   
   private ChangeListener getTreeUpdateListener(final TreePanel<BeanModel> tree, final BeanModel target) {
      if (changeListenerMap == null) {
         changeListenerMap = new HashMap<BeanModel, ChangeListener>();
      }
      ChangeListener changeListener = changeListenerMap.get(target);
      if (changeListener == null) {
         changeListener = new ChangeListener() {
            public void modelChanged(ChangeEvent changeEvent) {
               if (changeEvent.getType() == ChangeEventSupport.Remove) {
                  tree.getStore().remove(target);
               }
               if (changeEvent.getType() == ChangeEventSupport.Update) {
                  BeanModel source = (BeanModel) changeEvent.getItem();
                  if (source.getBean() instanceof DeviceCommand) {
                     DeviceCommand deviceCommand = (DeviceCommand) source.getBean();
                     CommandRefItem cmdRefItem = target.getBean();
                     cmdRefItem.setDeviceCommand(deviceCommand);
                  } else if (source.getBean() instanceof Device) {
                     Device device = (Device) source.getBean();
                     DeviceCommandRef targetDeviceCommandRef = (DeviceCommandRef) target.getBean();
                     targetDeviceCommandRef.setDeviceName(device.getName());
                  }
                  tree.getStore().update(target);
               }
            }
         };
         changeListenerMap.put(target, changeListener);
      }
      return changeListener;
   }

   class NewSwitchListener extends SelectionListener<ButtonEvent> {

      @Override
      public void componentSelected(ButtonEvent ce) {
         final SliderWindow sliderWindow = new SliderWindow(null);
         sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {

            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel sliderBeanModel = be.getData();
               Slider slider = sliderBeanModel.getBean();
               sliderTree.getStore().add(sliderBeanModel, true);
               sliderTree.getStore().add(sliderBeanModel, slider.getSetValueCmd().getBeanModel(), false);
               sliderWindow.hide();
            }
            
         });
         sliderWindow.show();
      }
   }
   
   class EditSwitchListener extends SelectionListener<ButtonEvent> {

      @Override
      public void componentSelected(ButtonEvent ce) {
         BeanModel selectedSwitchBean = sliderTree.getSelectionModel().getSelectedItem();
         Slider slider = selectedSwitchBean.getBean();
         
         final SliderWindow sliderWindow = new SliderWindow(slider);
         sliderWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {

            @Override
            public void afterSubmit(SubmitEvent be) {
               BeanModel sliderBeanModel = be.getData();
               Slider slider = sliderBeanModel.getBean();
               sliderTree.getStore().update(sliderBeanModel);
               sliderTree.getStore().removeAll(sliderBeanModel);
               sliderTree.getStore().add(slider.getBeanModel(), slider.getSetValueCmd().getBeanModel(), false);

               sliderTree.setExpanded(sliderBeanModel, true);
               sliderWindow.hide();
            }
            
         });
         sliderWindow.show();
      }
   }
   
   class DeleteSwitchListener extends SelectionListener<ButtonEvent> {

      @Override
      public void componentSelected(ButtonEvent ce) {
         final BeanModel selectedSwitchBean = sliderTree.getSelectionModel().getSelectedItem();
         MessageBox box = new MessageBox();
         box.setButtons(MessageBox.YESNO);
         box.setIcon(MessageBox.QUESTION);
         box.setTitle("Delete");
         box.setMessage("Are you sure you want to delete?");
         box.addCallback(new Listener<MessageBoxEvent>() {

             public void handleEvent(MessageBoxEvent be) {
                 if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                    SliderBeanModelProxy.delete(selectedSwitchBean, new AsyncSuccessCallback<Void>() {
                     @Override
                     public void onSuccess(Void result) {
                        sliderTree.getStore().remove(selectedSwitchBean);
                     }
                       
                    });
                 }
             }
         });
         box.show();
      }
   }

   public TreePanel<BeanModel> getSliderTree() {
      return sliderTree;
   }
}
