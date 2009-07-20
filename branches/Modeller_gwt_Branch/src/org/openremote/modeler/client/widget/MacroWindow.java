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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.gxtExtends.ListViewDropTargetMacroDragExt;
import org.openremote.modeler.client.gxtExtends.TreePanelDragSourceMacroDragExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.DeviceMacroItemModel;
import org.openremote.modeler.client.model.TreeDataModel;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.DND.TreeSource;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

// TODO: Auto-generated Javadoc
/**
 * The Class MacroWindow.
 */
public class MacroWindow extends Window {

   /** The _device macro. */
   private DeviceMacro _deviceMacro = null;

   /** The Constant MACRO_DND_GROUP. */
   private static final String MACRO_DND_GROUP = "macro";

   /** The icons. */
   private Icons icons = GWT.create(Icons.class);

   /** The macro form. */
   private FormPanel macroForm = new FormPanel();

   /** The macro name field. */
   private TextField<String> macroNameField = null;

   /** The add macro item container. */
   private LayoutContainer addMacroItemContainer;

   /** The device command tree. */
   private TreePanel<TreeDataModel> deviceCommandTree = null;

   /** The left macro list. */
   private TreePanel<TreeDataModel> leftMacroList = null;

   /** The right macro item list view. */
   private ListView<DeviceMacroItemModel> rightMacroItemListView = null;

   /**
    * Instantiates a new macro window.
    */
   public MacroWindow() {
      setHeading("New Macro");
      setup();

   }

   /**
    * Instantiates a new macro window.
    * 
    * @param deviceMacro the device macro
    */
   public MacroWindow(DeviceMacro deviceMacro) {
      this._deviceMacro = deviceMacro;
      setHeading("Edit Macro");
      setup();
      
   }

   /** The submit listeners. */
   private List<Listener<AppEvent>> submitListeners = new ArrayList<Listener<AppEvent>>();

   /**
    * Listener will be called after form submit and all the validator on fields pass.
    * 
    * @param listener the listener
    */
   public void addSubmitListener(Listener<AppEvent> listener) {
      submitListeners.add(listener);
   }

   /**
    * Remote submit listener.
    * 
    * @param listener the listener
    */
   public void remoteSubmitListener(Listener<AppEvent> listener) {
      submitListeners.remove(listener);
   }

   /**
    * Setup.
    */
   private void setup() {
      setPlain(true);
      setModal(true);
      setBlinkModal(true);
      setLayout(new FillLayout());
      setWidth(530);
      setResizable(false);
      createFormElement();

      macroForm.setHeaderVisible(false);
      macroForm.setFrame(true);
      macroForm.setLabelAlign(LabelAlign.TOP);
      macroForm.setHeight(380);
      macroForm.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            beforeFormSubmit();
         }

      });
      add(macroForm);
      show();
   }

   /**
    * Creates the form element.
    */
   private void createFormElement() {

      macroNameField = new TextField<String>();
      if (_deviceMacro != null) {
         macroNameField.setValue(_deviceMacro.getName());
      }
      macroNameField.setAllowBlank(false);
      macroNameField.setFieldLabel("Macro Name");
      macroNameField.setName("macroName");
      macroNameField.setStyleAttribute("marginBottom", "10px");

      macroForm.add(macroNameField);

      createSelectCommandContainer();

      Button submitBtn = new Button("OK");
      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            if (macroForm.isValid()) {
               macroForm.submit();
            }
         }

      });
      
      macroForm.setButtonAlign(HorizontalAlignment.CENTER);
      macroForm.addButton(submitBtn);
   }

   /**
    * Creates the select command container.
    */
   private void createSelectCommandContainer() {
      addMacroItemContainer = new LayoutContainer();
      FieldSet fieldSet = new FieldSet();
      
      AdapterField adapterField = new AdapterField(addMacroItemContainer);
      adapterField.setAutoWidth(true);
      fieldSet.add(adapterField);
      fieldSet.setHeading("Add Macro Item");

      macroForm.add(fieldSet);
      
      HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      addMacroItemContainer.setLayout(layout);
      addMacroItemContainer.setHeight(280);

      createLeftCommandMacroTab();
      createRightMacroList();
   }

   /**
    * Creates the left command macro tab.
    */
   private void createLeftCommandMacroTab() {
      TabPanel leftCommandMacroTabPanel = new TabPanel();
      leftCommandMacroTabPanel.setWidth(220);
      leftCommandMacroTabPanel.setPlain(true);
      leftCommandMacroTabPanel.setHeight(232);
      
      TabItem deviceCommandTab = new TabItem("Device Command");
      deviceCommandTab.setLayout(new FitLayout());
      
      deviceCommandTab.add(createDeviceCommandTree());
      leftCommandMacroTabPanel.add(deviceCommandTab);
      deviceCommandTab.scrollIntoView(leftCommandMacroTabPanel);
      
      TabItem macroTab = new TabItem("Macro");
      macroTab.setLayout(new FitLayout());
      macroTab.add(createLeftMacroTree());
      leftCommandMacroTabPanel.add(macroTab);

      addMacroItemContainer.add(leftCommandMacroTabPanel);
   }

   /**
    * Creates the device command tree.
    * 
    * @return the layout container
    */
   private LayoutContainer createDeviceCommandTree() {
      LayoutContainer treeContainer = new LayoutContainer();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);

      deviceCommandTree = TreePanelBuilder.buildDeviceCommandTree();
      deviceCommandTree.setHeight("100%");

      TreePanelDragSourceMacroDragExt dragSource = new TreePanelDragSourceMacroDragExt(deviceCommandTree);
      dragSource.addDNDListener(new DNDListener(){

         @Override
         public void dragStart(DNDEvent e) {
            TreePanel<TreeDataModel> tree = ((TreePanel<TreeDataModel>) e.getComponent());  
            TreeDataModel dataModel = tree.getSelectionModel().getSelectedItem();
            if (!(dataModel.getData() instanceof DeviceCommand)) {
               e.setCancelled(true);
               e.getStatus().setStatus(false);
            }
            super.dragStart(e);
         }
         
      });
      dragSource.setGroup(MACRO_DND_GROUP);

      treeContainer.add(deviceCommandTree);

      return treeContainer;
   }

   /**
    * Creates the left macro list.
    * 
    * @return the layout container
    */
   private LayoutContainer createLeftMacroTree() {
      LayoutContainer leftMacroListContainer = new LayoutContainer();
      leftMacroListContainer.setScrollMode(Scroll.AUTO);
      leftMacroListContainer.setStyleAttribute("backgroundColor", "white");
      leftMacroListContainer.setBorders(false);

      leftMacroList = TreePanelBuilder.buildMacroTree();
      leftMacroListContainer.setHeight("100%");
      leftMacroListContainer.add(leftMacroList);
      
  

      TreePanelDragSourceMacroDragExt dragSource = new TreePanelDragSourceMacroDragExt(leftMacroList);
      dragSource.addDNDListener(new DNDListener(){

         @Override
         public void dragStart(DNDEvent e) {
            TreePanel<TreeDataModel> tree = ((TreePanel<TreeDataModel>) e.getComponent());  
            TreeDataModel dataModel = tree.getSelectionModel().getSelectedItem();
            if (!(dataModel.getData() instanceof DeviceMacro)) {
               e.setCancelled(true);
               e.getStatus().setStatus(false);
            }
            super.dragStart(e);
         }
         
      });
      dragSource.setGroup(MACRO_DND_GROUP);

      return leftMacroListContainer;
   }

   /**
    * Creates the right macro list.
    */
   private void createRightMacroList() {
      ContentPanel rightListContainer = new ContentPanel();
      rightListContainer.setHeaderVisible(false);
      rightListContainer.setWidth(230);
      rightListContainer.setLayout(new FitLayout());
      
      ToolBar toolBar = createRightMacroItemListToolbar();

      rightListContainer.setTopComponent(toolBar);

      setupRightMacroItemListView();
      
      setupRightMacroItemDND();

      rightListContainer.add(rightMacroItemListView);
      HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
      flex.setFlex(1);
      addMacroItemContainer.add(new Text(), flex);
      addMacroItemContainer.add(rightListContainer);
   }

   /**
    * Creates the right macro item list toolbar.
    * 
    * @return the tool bar
    */
   private ToolBar createRightMacroItemListToolbar() {
      ToolBar toolBar = new ToolBar();

      Button addDelayBtn = new Button();
      addDelayBtn.setToolTip("Add Delay");
      addDelayBtn.setIcon(icons.addDelayIcon());
      toolBar.add(addDelayBtn);

      Button deleteBtn = new Button();
      deleteBtn.setToolTip("Delete Macro Item");
      deleteBtn.setIcon(icons.macroDeleteIcon());
      deleteBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            onDeleteMacroItemBtnClicked();
         }

      });
      toolBar.add(deleteBtn);
      return toolBar;
   }

   /**
    * Setup right macro item dnd.
    */
   private void setupRightMacroItemDND() {
//      ListViewDropTarget dropTarget = new ListViewDropTarget(rightMacroItemListView);
      ListViewDropTargetMacroDragExt dropTarget = new ListViewDropTargetMacroDragExt(rightMacroItemListView);
      dropTarget.setAllowSelfAsSource(true);
      dropTarget.setGroup(MACRO_DND_GROUP);
      dropTarget.setFeedback(Feedback.INSERT);
      dropTarget.setOperation(Operation.MOVE);

      ListViewDragSource dragSource = new ListViewDragSource(rightMacroItemListView);
      dragSource.setGroup(MACRO_DND_GROUP);
   }

   /**
    * Setup right macro item list view.
    */
   private void setupRightMacroItemListView() {
      rightMacroItemListView = new ListView<DeviceMacroItemModel>(){
          @Override  
          protected DeviceMacroItemModel prepareData(DeviceMacroItemModel model) {  
            String s = model.getLabel();
            if (model.getData() instanceof DeviceMacroRef) {
               DeviceMacroRef deviceMacroRef = (DeviceMacroRef)model.getData();
               s+="  (DeviceMacro "+deviceMacroRef.getLabel() +")";
            } else if (model.getData() instanceof DeviceCommandRef) {
               DeviceCommandRef commandRef = (DeviceCommandRef)model.getData();
               s+="  (DeviceCommand "+commandRef.getLabel()+")";
            }
            model.set("display", s);
            return model;  
          }  
      };
      rightMacroItemListView.setDisplayProperty("display");

      ListStore<DeviceMacroItemModel> store = new ListStore<DeviceMacroItemModel>();

      rightMacroItemListView.setStore(store);
      rightMacroItemListView.setHeight(203);
      
      if (_deviceMacro !=null && _deviceMacro.getDeviceMacroItems().size() > 0) {
         for (DeviceMacroItem deviceMacroItem : _deviceMacro.getDeviceMacroItems()) {
           DeviceMacroItemModel deviceMacroItemModel = new DeviceMacroItemModel(deviceMacroItem.getLabel(),deviceMacroItem);
           rightMacroItemListView.getStore().add(deviceMacroItemModel);
         }
       
      }
   }

   /**
    * Fire submit listener.
    * 
    * @param event the event
    */
   protected void fireSubmitListener(AppEvent event) {
      for (Listener<AppEvent> listener : submitListeners) {
         listener.handleEvent(event);
      }
   }

   /**
    * Before form submit.
    */
   private void beforeFormSubmit() {
      DeviceMacro newDeviceMacro = new DeviceMacro();
      if (_deviceMacro != null) {
         newDeviceMacro.setOid(_deviceMacro.getOid());
      }
      newDeviceMacro.setName(macroNameField.getValue());

      for (ModelData modelData : rightMacroItemListView.getStore().getModels()) {
         if (modelData instanceof DeviceMacroItemModel) {
            DeviceMacroItemModel macroItemModel = (DeviceMacroItemModel) modelData;
            DeviceMacroItem deviceMacroItem = (DeviceMacroItem)macroItemModel.getData();
            newDeviceMacro.getDeviceMacroItems().add(deviceMacroItem);
            deviceMacroItem.setParentDeviceMacro(newDeviceMacro);
         }
      }
      AppEvent appEvent = new AppEvent(Events.Submit, newDeviceMacro);

      fireSubmitListener(appEvent);
   }

   /**
    * On delete macro item btn clicked.
    */
   private void onDeleteMacroItemBtnClicked() {
      if (rightMacroItemListView.getSelectionModel().getSelectedItems().size() > 0) {
         for (DeviceMacroItemModel data : rightMacroItemListView.getSelectionModel().getSelectedItems()) {
            int index = rightMacroItemListView.getStore().indexOf(data);
            rightMacroItemListView.getStore().remove(data);
            if (rightMacroItemListView.getStore().getCount() > 0) {
               rightMacroItemListView.getSelectionModel().select(index, false);
            }
         }
      }
   }

}
