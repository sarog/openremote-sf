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

import org.openremote.modeler.client.utils.NestedJsonLoadResultReader;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * The Class RemoteJsonComboBox.
 */
public class RemoteJsonComboBox<D extends ModelData> extends ComboBox<D> {

   /** The remote json url. */
   private String remoteJsonURL = "";
   
   /** The model type. */
   private ModelType modelType = null;

   /**
    * Instantiates a new remote json combo box.
    * 
    * @param remoteJsonURL the remote json url
    * @param modelType the model type
    */
   public RemoteJsonComboBox(String remoteJsonURL, ModelType modelType) {
      super();
      this.remoteJsonURL = remoteJsonURL;
      this.modelType = modelType;
      setup();
   }

   /**
    * Reload list store with a different url.
    * 
    * @param url the url
    */
   public void reloadListStoreWithUrl(String url) {

      final RemoteJsonComboBox<D> box = this;
      ScriptTagProxy<ListLoadResult<D>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<D>>(url);
      NestedJsonLoadResultReader<ListLoadResult<D>> reader = new NestedJsonLoadResultReader<ListLoadResult<D>>(
            modelType);
      final BaseListLoader<ListLoadResult<D>> loader = new BaseListLoader<ListLoadResult<D>>(scriptTagProxy, reader);

      ListStore<D> store = new ListStore<D>(loader);
      store.addListener(ListStore.DataChanged, new StoreListener<D>() {
         @Override
         public void storeDataChanged(StoreEvent<D> se) {
            box.getStore().add(se.getStore().getModels());
            box.fireEvent(ListStore.DataChanged);
         }

      });
      loader.load();
   }

   /**
    * Setup.
    */
   private void setup() {
      ScriptTagProxy<ListLoadResult<D>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<D>>(remoteJsonURL);
      NestedJsonLoadResultReader<ListLoadResult<D>> reader = new NestedJsonLoadResultReader<ListLoadResult<D>>(
            modelType);
      final BaseListLoader<ListLoadResult<D>> loader = new BaseListLoader<ListLoadResult<D>>(scriptTagProxy, reader);

      ListStore<D> store = new ListStore<D>(loader);
      loader.load();
      setStore(store);

      setTypeAhead(true);
      setTriggerAction(TriggerAction.ALL);
      setMinChars(1);

      this.onLoad();
      setLoadingText("loading...");

      addListener(Events.BeforeQuery, new Listener<FieldEvent>() {

         public void handleEvent(FieldEvent be) {
            // cancel default event, we will handler query request.
            be.setCancelled(true);  
            RemoteJsonComboBox<D> box = be.getComponent();
            System.out.println(box.getRawValue());

            if (box.getRawValue() != null && box.getRawValue().length() > 0) {
               box.getStore().filter(getDisplayField(), box.getRawValue());
            } else {
               box.getStore().clearFilters();
            }
            box.expand();

         }

      });
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.widget.form.ComboBox#onTriggerClick(com.extjs.gxt.ui.client.event.ComponentEvent)
    */
   @Override
   protected void onTriggerClick(ComponentEvent ce) {
      final RemoteJsonComboBox<D> box = this;
      // click the trigger of the combobox will show all the options. 
      box.setRawValue("");
      super.onTriggerClick(ce);
      
   }
   

}
