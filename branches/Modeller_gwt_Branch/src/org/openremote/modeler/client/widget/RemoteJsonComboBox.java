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

public class RemoteJsonComboBox<D extends ModelData> extends ComboBox<D> {

   private String remoteJsonURL = "";
   private ModelType modelType = null;

   public RemoteJsonComboBox(String remoteJsonURL, ModelType modelType) {
      super();
      this.remoteJsonURL = remoteJsonURL;
      this.modelType = modelType;
      setup();
   }

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

   @Override
   protected void onTriggerClick(ComponentEvent ce) {
      final RemoteJsonComboBox<D> box = this;
      box.setRawValue("");
      super.onTriggerClick(ce);
      
   }
   

}
