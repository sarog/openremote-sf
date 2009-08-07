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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.gxtExtends.NestedJsonLoadResultReader;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.ConfigurationRPCService;
import org.openremote.modeler.client.rpc.ConfigurationRPCServiceAsync;
import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.core.client.GWT;

/**
 * The window allow user to select Infrared.
 */
public class SelectIRWindow extends Window {

   /** The beehive rest url. */
   private String beehiveRESTUrl = null;
   
   /** The configuration service. */
   private ConfigurationRPCServiceAsync configurationService = (ConfigurationRPCServiceAsync) GWT
         .create(ConfigurationRPCService.class);
   
   /** The submit listeners. */
   private List<Listener<AppEvent>> submitListeners = new ArrayList<Listener<AppEvent>>();
   
   /** The device. */
   private Device device = null;
   
   /** The Constant LOADING. */
   private static final String LOADING = "Loading... ";
   
   /** The select container. */
   LayoutContainer selectContainer = new LayoutContainer();
   
   /** The command container. */
   LayoutContainer commandContainer = new LayoutContainer();

   /** The import button. */
   Button importButton = null;

   /** The vendor list. */
   RemoteJsonComboBox<ModelData> vendorList = null;
   
   /** The model list. */
   RemoteJsonComboBox<ModelData> modelList = null;
   
   /** The section list. */
   RemoteJsonComboBox<ModelData> sectionList = null;

   /** The code grid. */
   Grid<ModelData> codeGrid = null;
   
   /** The code type. */
   ModelType codeType = null;
   
   /** The cm. */
   ColumnModel cm = null;
   
   /**
    * Instantiates a new select ir window.
    * 
    * @param device the device
    */
   public SelectIRWindow(Device device) {
      if (beehiveRESTUrl == null) {
         this.device = device;
         configurationService.beehiveRESTUrl(new AsyncSuccessCallback<String>() {
            @Override
            public void onSuccess(String result) {
               beehiveRESTUrl = result;
               setupWindow();
               addVendorsList();
               layout();
               show();
            }
            
         });
      }
      
   }

   /**
    * Setup window.
    */
   private void setupWindow() {
      setSize(570, 330);
      setModal(true);
      setHeading("Select IR commands from Beehive");
      
      setLayout(new RowLayout(Orientation.VERTICAL));

      HBoxLayout selectContainerLayout = new HBoxLayout();
      selectContainerLayout.setPadding(new Padding(5));
      selectContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      
      selectContainer.setLayout(selectContainerLayout);
      selectContainer.setLayoutOnChange(true);
      add(selectContainer, new RowData(1, 35));

      commandContainer.setLayout(new CenterLayout());
      commandContainer.setLayoutOnChange(true);
      add(commandContainer, new RowData(1, 1));

      LayoutContainer buttonLayout = new LayoutContainer();
      buttonLayout.setLayout(new CenterLayout());

      importButton = new Button("Import");
      importButton.setScale(ButtonScale.MEDIUM);
      importButton.setWidth(80);
      importButton.setEnabled(false);
      final Window window = this;
      importButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            onImportBtnClicked(window, device);
         }
      });
      buttonLayout.add(importButton);
      add(buttonLayout, new RowData(-1, -1, new Margins(10)));
   }

   /**
    * Adds the vendors list.
    */
   private void addVendorsList() {
      ModelType vendorType = new ModelType();
      vendorType.setRoot("vendors.vendor");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      vendorType.addField(idField);
      vendorType.addField("name");

      final String emptyText = "Please Select Vendor ...";

      vendorList = new RemoteJsonComboBox<ModelData>(beehiveRESTUrl, vendorType);

      vendorList.setEmptyText(emptyText);
      vendorList.setDisplayField("name");
      vendorList.setValueField("name");

      setStyleOfComboBox(vendorList);

      vendorList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            addModelList(se.getSelectedItem().get("name").toString());
         }

      });
      selectContainer.add(vendorList);
   }

   /**
    * Adds the model list.
    * 
    * @param vendor the vendor
    */
   private void addModelList(final String vendor) {
      ModelType modelType = new ModelType();
      modelType.setRoot("models.model");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      modelType.addField(idField);
      modelType.addField("name");
      modelType.addField("fileName");
      final String emptyText = "Please Select Model ...";

      String url = beehiveRESTUrl + vendor;
      if (modelList != null) {
         clearComboBox(modelList);
         clearComboBox(sectionList);
         beginUpdate(modelList, url);
      } else {
         modelList = new RemoteJsonComboBox<ModelData>(url, modelType);
         modelList.setEmptyText(emptyText);
         modelList.setDisplayField("name");
         modelList.setValueField("name");
         setStyleOfComboBox(modelList);
         modelList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
               addSectionList(vendorList.getRawValue(), se.getSelectedItem().get("name").toString());
            }

         });
         modelList.addListener(ListStore.DataChanged, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
               endUpdate(modelList, emptyText);
            }

         });
         selectContainer.add(modelList);
      }
   }

   /**
    * Adds the section list.
    * 
    * @param venderName the vender name
    * @param modelName the model name
    */
   private void addSectionList(String venderName, String modelName) {

      ModelType sectionType = new ModelType();
      sectionType.setRoot("sections.section");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      sectionType.addField(idField);
      sectionType.addField("name");
      String url = beehiveRESTUrl + venderName + "/" + modelName;
      final String emptyText = "Please Select Section ...";

      if (sectionList != null) {
         clearComboBox(sectionList);
         beginUpdate(sectionList, url);

      } else {
         sectionList = new RemoteJsonComboBox<ModelData>(url, sectionType);
         sectionList.setEmptyText(emptyText);
         sectionList.setDisplayField("name");
         sectionList.setValueField("id");
         setStyleOfComboBox(sectionList);

         sectionList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
               long idstr = se.getSelectedItem().get("id");
               showCodesGrid(vendorList.getRawValue(), sectionList.getRawValue(), idstr);
            }

         });

         sectionList.addListener(ListStore.DataChanged, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
               endUpdate(sectionList, emptyText);
            }

         });
         selectContainer.add(sectionList);
      }
   }

   /**
    * Sets the style of combo box.
    * 
    * @param box the new style of combo box
    */
   private void setStyleOfComboBox(RemoteJsonComboBox<ModelData> box) {
      box.setWidth(180);
      box.setMaxHeight(250);
   }

   /**
    * Clear combo box.
    * 
    * @param box the box
    */
   private void clearComboBox(RemoteJsonComboBox<ModelData> box) {
      if (box != null) {
         box.clearSelections();
         box.getStore().removeAll();

      }
   }

   /**
    * Begin update.
    * 
    * @param box the box
    * @param url the url
    */
   private void beginUpdate(RemoteJsonComboBox<ModelData> box, String url) {
      if (box != null) {
         box.reloadListStoreWithUrl(url);
         box.setEmptyText(LOADING);
         box.disable();
      }
   }

   /**
    * End update.
    * 
    * @param box the box
    * @param emptyStr the empty str
    */
   private void endUpdate(RemoteJsonComboBox<ModelData> box, String emptyStr) {
      if (box != null) {
         box.enable();
         box.setEmptyText(emptyStr);
      }
   }

   /**
    * Show codes grid.
    * 
    * @param vendor the vendor
    * @param model the model
    * @param sectionId the section id
    */
   private void showCodesGrid(String vendor, String model, long sectionId) {
      importButton.setEnabled(true);
      if (codeType == null) {
         codeType = new ModelType();
         codeType.setRoot("codes.code");
         DataField idField = new DataField("id");
         idField.setType(Long.class);
         codeType.addField(idField);
         codeType.addField("name");
         codeType.addField("remoteName");
         codeType.addField("value");
         codeType.addField("comment");
      }

      StringBuffer url = new StringBuffer(beehiveRESTUrl);
      url.append(vendor);
      url.append("/");
      url.append(model);
      url.append("/");
      url.append(sectionId);
      url.append("/");
      url.append("codes");

      if (codeGrid == null) {
         addCodeGrid(url.toString());
      } else {
         reloadGrid(url.toString());
      }
   }

   /**
    * Adds the code grid.
    * 
    * @param url the url
    */
   private void addCodeGrid(String url) {
      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(url
            .toString());

      NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
            codeType);

      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(
            scriptTagProxy, reader);

      ListStore<ModelData> listStore = new ListStore<ModelData>(loader);

      if (cm == null) {
         List<ColumnConfig> codeGridColumns = new ArrayList<ColumnConfig>();
         codeGridColumns.add(new ColumnConfig("name", "Name", 120));
         codeGridColumns.add(new ColumnConfig("remoteName", "Remote Name", 150));
         codeGridColumns.add(new ColumnConfig("value", "Value", 250));

         cm = new ColumnModel(codeGridColumns);
      }

      codeGrid = new Grid<ModelData>(listStore, cm);
      codeGrid.setLoadMask(true);
      codeGrid.setHeight(200);
      commandContainer.add(codeGrid);
      
      loader.load();

      importButton.setEnabled(true);
   }

   /**
    * Reload grid.
    * 
    * @param url the url
    */
   private void reloadGrid(String url) {
      codeGrid.getStore().removeAll();
      codeGrid.setLoadMask(true);
      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(url
            .toString());

      NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
            codeType);

      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(
            scriptTagProxy, reader);

      ListStore<ModelData> listStore = new ListStore<ModelData>(loader);
      codeGrid.reconfigure(listStore, cm);
      loader.load();
      importButton.setEnabled(true);
   }

   /**
    * Adds the submit listener.
    * 
    * @param listener the listener
    */
   public void addSubmitListener(Listener<AppEvent> listener) {
      submitListeners.add(listener);

   }

   /**
    * Fire submit listener.
    * 
    * @param event the event
    */
   public void fireSubmitListener(AppEvent event) {
      for (Listener<AppEvent> listener : submitListeners) {
         listener.handleEvent(event);
      }

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
    * On import btn clicked.
    * 
    * @param window the window
    * @param device the device
    */
   private void onImportBtnClicked(final Window window, Device device) {
      window.mask("Wait...");
      importButton.setEnabled(false);
      if (codeGrid != null) {
         DeviceCommandBeanModelProxy.saveAllDeviceCommands(device, codeGrid.getStore().getModels(), new AsyncSuccessCallback<List<BeanModel>>() {
            @Override
            public void onSuccess(List<BeanModel> deviceCommandModels) {
               AppEvent event = new AppEvent(Events.Submit);
               event.setData(deviceCommandModels);
               fireSubmitListener(event);
            }
         });
         
      } else {
         MessageBox.alert("Warn", "Please select vendor, model first.", null);
      }
   }

}
