/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.openremote.modeler.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.utils.NestedJsonLoadResultReader;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseListLoader;
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
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class SelectIRWindow extends Window{

   /** The submit listeners. */
   private List<Listener<AppEvent>> submitListeners = new ArrayList<Listener<AppEvent>>();

   private static final String LOADING = "Loading... ";
   LayoutContainer selectContainer = new LayoutContainer();
   LayoutContainer commandContainer = new LayoutContainer();

   RemoteJsonComboBox<ModelData> vendorList = null;
   RemoteJsonComboBox<ModelData> modelList = null;
   RemoteJsonComboBox<ModelData> sectionList = null;

   Grid<ModelData> codeGrid = null;
   ModelType codeType = null;
   ColumnModel cm = null;

   public SelectIRWindow() {
      setupWindow();
      addVendersList();
   }

   private void setupWindow() {
      setSize(570, 330);
      setPlain(true);
      setModal(true);
      setBlinkModal(true);
      setHeading("Select IR from Beehive");

      setLayout(new RowLayout(Orientation.VERTICAL));

      HBoxLayout selectContainerLayout = new HBoxLayout();
      selectContainerLayout.setPadding(new Padding(5));
      selectContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
      selectContainerLayout.setPack(BoxLayoutPack.START);
      selectContainer.setLayout(selectContainerLayout);

      add(selectContainer, new RowData(1, -1));

      commandContainer.setLayout(new CenterLayout());
      add(commandContainer, new RowData(1, 1));

      LayoutContainer buttonLayout = new LayoutContainer();
      buttonLayout.setLayout(new CenterLayout());

      Button button = new Button("import");
      button.setScale(ButtonScale.MEDIUM);
      button.setWidth(80);
      button.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
           
            AppEvent event = new AppEvent(Events.Submit);
            event.setData(codeGrid.getStore().getModels());
            fireSubmitListener(event);
         }
      });
      buttonLayout.add(button);
      add(buttonLayout, new RowData(-1, -1, new Margins(10)));
   }

   private void addVendersList() {
      ModelType venderType = new ModelType();
      venderType.setRoot("vendors.vendor");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      venderType.addField(idField);
      venderType.addField("name");

      final String emptyText = "Please Select Vendor ...";

      vendorList = new RemoteJsonComboBox<ModelData>("http://openremote.finalist.hk/beehive/rest/lirc", venderType);

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

   private void addModelList(final String vendor) {
      ModelType modelType = new ModelType();
      modelType.setRoot("models.model");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      modelType.addField(idField);
      modelType.addField("name");
      modelType.addField("fileName");
      final String emptyText = "Please Select Model ...";

      String url = "http://openremote.finalist.hk/beehive/rest/lirc/" + vendor;
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
         selectContainer.layout();
      }
   }

   private void addSectionList(String venderName, String modelName) {

      ModelType sectionType = new ModelType();
      sectionType.setRoot("sections.section");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      sectionType.addField(idField);
      sectionType.addField("name");
      String url = "http://openremote.finalist.hk/beehive/rest/lirc/" + venderName + "/" + modelName;
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
         selectContainer.layout();
      }
   }

   private void setStyleOfComboBox(RemoteJsonComboBox<ModelData> box) {
      box.setWidth(180);
      box.setMaxHeight(250);
   }

   private void clearComboBox(RemoteJsonComboBox<ModelData> box) {
      if (box != null) {
         box.clearSelections();
         box.getStore().removeAll();

      }
   }

   private void beginUpdate(RemoteJsonComboBox<ModelData> box, String url) {
      if (box != null) {
         box.reloadListStoreWithUrl(url);
         box.setEmptyText(LOADING);
         box.disable();
      }
   }

   private void endUpdate(RemoteJsonComboBox<ModelData> box, String emptyStr) {
      if (box != null) {
         box.enable();
         box.setEmptyText(emptyStr);
      }
   }

   private void showCodesGrid(String vendor, String model, long sectionId) {
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

      StringBuffer url = new StringBuffer(Constants.BEEHIVE_REST_URL);
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

   private void addCodeGrid(String url) {
      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(url
            .toString());

      NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
            codeType);

      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(
            scriptTagProxy, reader);

      ListStore<ModelData> listStore = new ListStore<ModelData>(loader);
      loader.load();

      if (cm == null) {
         List<ColumnConfig> codeGridColumns = new ArrayList<ColumnConfig>();
         codeGridColumns.add(new ColumnConfig("name", "Name", 120));
         codeGridColumns.add(new ColumnConfig("remoteName", "Remote Name", 150));
         codeGridColumns.add(new ColumnConfig("value", "Value", 200));

         cm = new ColumnModel(codeGridColumns);
      }

      codeGrid = new Grid<ModelData>(listStore, cm);
      codeGrid.setLoadMask(true);
      codeGrid.setHeight(200);
      commandContainer.add(codeGrid);
      commandContainer.layout();
   }

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
   }
     
   public void addSubmitListener(Listener<AppEvent> listener) {
      submitListeners.add(listener);

   }

   public void fireSubmitListener(AppEvent event) {
      for (Listener<AppEvent> listener : submitListeners) {
         listener.handleEvent(event);
      }

   }

   public void remoteSubmitListener(Listener<AppEvent> listener) {
      submitListeners.remove(listener);
   }

}
