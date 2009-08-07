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
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtExtends.ListViewDropTargetMacroDragExt;
import org.openremote.modeler.client.gxtExtends.TreePanelDragSourceMacroDragExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
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


/**
 * The Class MacroWindow.
 */
public class MacroWindow extends Window {

   /** The _device macro. */
   private BeanModel deviceMacroBeanModel = null;

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
   private TreePanel<BeanModel> deviceCommandTree = null;

   /** The left macro list. */
   private TreePanel<BeanModel> leftMacroList = null;

   /** The right macro item list view. */
   private ListView<BeanModel> rightMacroItemListView = null;

   /** The Constant MACRO_ITEM_LIST_DISPLAY_FIELD. */
   private static final String MACRO_ITEM_LIST_DISPLAY_FIELD = "macro_item_label";

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
    * @param deviceMacroModel the device macro
    */
   public MacroWindow(BeanModel deviceMacroModel) {
      this.deviceMacroBeanModel = deviceMacroModel;
      setHeading("Edit Macro");
      setup();

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
   }

   /**
    * Creates the form element.
    */
   private void createFormElement() {

      macroNameField = new TextField<String>();
      if (deviceMacroBeanModel != null) {
         macroNameField.setValue(((DeviceMacro) deviceMacroBeanModel.getBean()).getName());
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
      dragSource.addDNDListener(new DNDListener() {
         @SuppressWarnings("unchecked")
         @Override
         public void dragStart(DNDEvent e) {
            TreePanel<BeanModel> tree = (TreePanel<BeanModel>) e.getComponent();
            BeanModel beanModel = tree.getSelectionModel().getSelectedItem();
            if (!(beanModel.getBean() instanceof DeviceCommand)) {
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
      dragSource.addDNDListener(new DNDListener() {

         @SuppressWarnings("unchecked")
         @Override
         public void dragStart(DNDEvent e) {
            TreePanel<BeanModel> tree = ((TreePanel<BeanModel>) e.getComponent());
            BeanModel beanModel = tree.getSelectionModel().getSelectedItem();
            if (!(beanModel.getBean() instanceof DeviceMacro)) {
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

      rightMacroItemListView = createRightMacroItemListView();

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
      deleteBtn.setIcon(icons.delete());
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
    * 
    * @return the list view< bean model>
    */
   private ListView<BeanModel> createRightMacroItemListView() {
      rightMacroItemListView = new ListView<BeanModel>() {
         @Override
         protected BeanModel prepareData(BeanModel model) {
            String s = model.get("name");
            if (model.getBean() instanceof DeviceMacro) {
               DeviceMacro deviceMacro = (DeviceMacro) model.getBean();
               s += "  (DeviceMacro " + deviceMacro.getName() + ")";
            } else if (model.getBean() instanceof DeviceCommand) {
               DeviceCommand command = (DeviceCommand) model.getBean();
               s += "  (Device " + command.getDevice().getName() + ")";
            }
            model.set(MACRO_ITEM_LIST_DISPLAY_FIELD, s);
            return model;
         }
      };
      rightMacroItemListView.setDisplayProperty(MACRO_ITEM_LIST_DISPLAY_FIELD);

      ListStore<BeanModel> store = new ListStore<BeanModel>();

      rightMacroItemListView.setStore(store);
      rightMacroItemListView.setHeight(203);

      if (deviceMacroBeanModel != null) {
         DeviceMacroBeanModelProxy.loadDeviceMaro(deviceMacroBeanModel,
               new AsyncSuccessCallback<List<BeanModel>>() {
                  @Override
                  public void onSuccess(List<BeanModel> result) {
                     for (BeanModel beanModel : result) {
                        if (beanModel.getBean() instanceof DeviceMacroRef) {
                           DeviceMacroRef deviceMacroRef = (DeviceMacroRef) beanModel.getBean();
                           rightMacroItemListView.getStore().add(deviceMacroRef.getTargetDeviceMacro().getBeanModel());
                        } else if (beanModel.getBean() instanceof DeviceCommandRef) {
                           DeviceCommandRef deviceCommandRef = (DeviceCommandRef) beanModel.getBean();
                           rightMacroItemListView.getStore().add(deviceCommandRef.getDeviceCommand().getBeanModel());
                        }
                     }
                  }
               });

      }
      return rightMacroItemListView;
   }

   /**
    * Before form submit.
    */
   private void beforeFormSubmit() {
      AsyncSuccessCallback<DeviceMacro> submitSuccessListener = new AsyncSuccessCallback<DeviceMacro>() {
         @Override
         public void onSuccess(DeviceMacro result) {
            fireEvent(SubmitEvent.Submit, new SubmitEvent(result));
         }
      };
      if (deviceMacroBeanModel != null) {
         
         ((DeviceMacro) deviceMacroBeanModel.getBean()).setName(macroNameField.getValue());
         DeviceMacroBeanModelProxy.updateDeviceMacro(deviceMacroBeanModel, rightMacroItemListView.getStore().getModels(),
               submitSuccessListener);
      } else {
         DeviceMacroBeanModelProxy.saveDeviceMacro(macroNameField.getValue(), rightMacroItemListView.getStore()
               .getModels(), submitSuccessListener);
      }

   }

   /**
    * On delete macro item btn clicked.
    */
   private void onDeleteMacroItemBtnClicked() {
      for (BeanModel data : rightMacroItemListView.getSelectionModel().getSelectedItems()) {
         int index = rightMacroItemListView.getStore().indexOf(data);
         rightMacroItemListView.getStore().remove(data);
         if (rightMacroItemListView.getStore().getCount() > 0) {
            rightMacroItemListView.getSelectionModel().select(index, false);
         }
      }

   }

}