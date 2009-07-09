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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class SelectIRWindow extends Window {

   RemoteJsonComboBox<ModelData> vendorList = null;
   RemoteJsonComboBox<ModelData> modelList = null;
   RemoteJsonComboBox<ModelData> sectionList = null;

   public SelectIRWindow() {
      setupWindow();
      addVendersList();
   }

   private void setupWindow() {
      setSize(500, 300);
      setPlain(true);
      setModal(true);
      setBlinkModal(true);
      setHeading("Select IR from Beehive");
   }

   private void addVendersList() {
      ModelType venderType = new ModelType();
      venderType.setRoot("vendors.vendor");
      venderType.addField("id");
      venderType.addField("name");

      vendorList = new RemoteJsonComboBox<ModelData>(
            "http://openremote.finalist.hk/beehive/rest/lirc", venderType);

      vendorList.setEmptyText("Select a vendor...");
      vendorList.setDisplayField("name");
      vendorList.setValueField("name");
      vendorList.setWidth(150);
      vendorList.setMaxHeight(200);

      vendorList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            addModelList(se.getSelectedItem().get("name").toString());
         }

      });
      add(vendorList);
   }

   private void addModelList(final String vendor) {
      ModelType modelType = new ModelType();
      modelType.setRoot("models.model");
      modelType.addField("id");
      modelType.addField("name");
      modelType.addField("fileName");
      String url = "http://openremote.finalist.hk/beehive/rest/lirc/" + vendor;
      if (modelList != null) {
         modelList.clearSelections();
         modelList.getStore().removeAll();
         
         sectionList.clearSelections();
         sectionList.getStore().removeAll();
         
         modelList.reloadListStoreWithUrl(url);
         modelList.setEmptyText("Loading... ");
         modelList.disable();
      } else {
         modelList = new RemoteJsonComboBox<ModelData>(url, modelType);

         modelList.setEmptyText("Select a model...");
         modelList.setDisplayField("name");
         modelList.setValueField("name");
         modelList.setWidth(150);
         modelList.setMaxHeight(200);
         modelList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> se) {
               System.out.println("select vender count is " + vendorList.getSelection().get(0).get("name"));
               
               addSectionList(vendorList.getRawValue(), se.getSelectedItem().get("name").toString());
            }

         });
         modelList.addListener(ListStore.DataChanged, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
               modelList.enable();
               modelList.setEmptyText("Select a model...");
            }
            
         });
         add(modelList);
         layout();
      }
   }

   private void addSectionList(String venderName, String modelName) {

      ModelType modelType = new ModelType();
      modelType.setRoot("sections.section");
      modelType.addField("id");
      modelType.addField("name");
      String url = "http://openremote.finalist.hk/beehive/rest/lirc/" + venderName + "/" + modelName;
      if (sectionList != null) {
         sectionList.clearSelections();
         sectionList.getStore().removeAll();
         sectionList.reloadListStoreWithUrl(url);
         sectionList.setEmptyText("Loading... ");
         sectionList.disable();
      } else {
         sectionList = new RemoteJsonComboBox<ModelData>(url, modelType);

         sectionList.setEmptyText("Select a section...");
         sectionList.setDisplayField("name");
         sectionList.setValueField("name");
         sectionList.setWidth(150);
         sectionList.setMaxHeight(200);
         sectionList.addListener(ListStore.DataChanged, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
               sectionList.enable();
               sectionList.setEmptyText("Select a section...");
            }
            
         });
         add(sectionList);
         layout();
      }

   }

}
