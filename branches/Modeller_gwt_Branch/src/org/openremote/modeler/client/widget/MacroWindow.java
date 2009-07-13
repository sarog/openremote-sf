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
import org.openremote.modeler.domain.DeviceMacro;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
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
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
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
   /** The icons. */
   private Icons icons = GWT.create(Icons.class);

   /** The macro form. */
   private FormPanel macroForm = new FormPanel();
      
   /** The add macro item container. */
   private LayoutContainer addMacroItemContainer;
   
   /** The device command tree. */
   private TreePanel<ModelData> deviceCommandTree = null;
   
   /** The left macro list. */
   private TreePanel<ModelData> leftMacroList = null;

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
      add(macroForm);
      show();
   }

   /**
    * Creates the form element.
    */
   private void createFormElement() {

      TextField<String> macroNameField = new TextField<String>();
      macroNameField.setAllowBlank(false);
      macroNameField.setFieldLabel("Macro Name");
      macroNameField.setName("macroName");
      macroNameField.setStyleAttribute("marginBottom", "10px");

      macroForm.add(macroNameField);

      createSelectCommandContainer();

      Button submitBtn = new Button("OK");
      macroForm.addButton(submitBtn);
      Button cancelBtn = new Button("Cancel");
      macroForm.addButton(cancelBtn);
      macroForm.setButtonAlign(HorizontalAlignment.CENTER);

   }

   /**
    * Creates the select command container.
    */
   private void createSelectCommandContainer() {
      addMacroItemContainer = new LayoutContainer();
      AdapterField adapterField = new AdapterField(addMacroItemContainer);
      adapterField.setAutoWidth(true);
      adapterField.setFieldLabel("Add Macro Item");
      macroForm.add(adapterField);

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

      deviceCommandTab.add(createDeviceCommandTree());
      leftCommandMacroTabPanel.add(deviceCommandTab);

      TabItem macroTab = new TabItem("Macro");
      macroTab.add(createLeftMacroList());
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
      treeContainer.setBorders(true);

      deviceCommandTree = new TreePanel<ModelData>(new TreeStore<ModelData>());
      deviceCommandTree.setDisplayProperty(TreeDataModel.getDisplayProperty());

      deviceCommandTree.getStyle().setLeafIcon(icons.macroIcon());
      treeContainer.add(deviceCommandTree);

      return treeContainer;
   }

   /**
    * Creates the left macro list.
    * 
    * @return the layout container
    */
   private LayoutContainer createLeftMacroList() {
      LayoutContainer leftMacroListContainer = new LayoutContainer();
      leftMacroListContainer.setScrollMode(Scroll.AUTO);
      leftMacroListContainer.setStyleAttribute("backgroundColor", "white");
      leftMacroListContainer.setBorders(true);
      return leftMacroListContainer;
   }

   /**
    * Creates the right macro list.
    */
   private void createRightMacroList() {
      ContentPanel rightListContainer = new ContentPanel();
      rightListContainer.setHeaderVisible(false);
      rightListContainer.setWidth(230);

      ToolBar toolBar = new ToolBar();

      Button addDelayBtn = new Button();
      addDelayBtn.setToolTip("Add Delay");
      addDelayBtn.setIcon(icons.addDelayIcon());
      toolBar.add(addDelayBtn);

      Button deleteBtn = new Button();
      deleteBtn.setToolTip("Delete Macro Item");
      deleteBtn.setIcon(icons.macroDeleteIcon());
      toolBar.add(deleteBtn);

      rightListContainer.setTopComponent(toolBar);

      ListView<ModelData> listView = new ListView<ModelData>();
      listView.setDisplayProperty("label");

      ListStore<ModelData> store = new ListStore<ModelData>();

      listView.setStore(store);
      BaseModelData model = new BaseModelData();
      model.set("label", "model1");
      BaseModelData model2 = new BaseModelData();
      model2.set("label", "model2");
      store.add(model);
      store.add(model2);
      listView.setHeight(203);

      ListViewDropTarget dropTarget = new ListViewDropTarget(listView);
      dropTarget.setAllowSelfAsSource(true);
      dropTarget.setGroup("macro");
      dropTarget.setFeedback(Feedback.INSERT);

      ListViewDragSource dragSource = new ListViewDragSource(listView);
      dragSource.setGroup("macro");

      rightListContainer.add(listView);
      HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
      flex.setFlex(1);
      addMacroItemContainer.add(new Text(), flex);
      addMacroItemContainer.add(rightListContainer);

   }

}
