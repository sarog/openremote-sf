package org.openremote.modeler.client.widget;

import org.openremote.modeler.client.utils.NestedJsonLoadResultReader;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.Layout;
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

   private ListStore<D> getListStoreWithUrl(String url) {
      ScriptTagProxy<ListLoadResult<D>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<D>>(url);
      NestedJsonLoadResultReader<ListLoadResult<D>> reader = new NestedJsonLoadResultReader<ListLoadResult<D>>(
            modelType);
      final BaseListLoader<ListLoadResult<D>> loader = new BaseListLoader<ListLoadResult<D>>(scriptTagProxy, reader);

      ListStore<D> store = new ListStore<D>(loader);
      
      loader.load();
      return store;
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
      setTypeAhead(true);
      setTriggerAction(TriggerAction.ALL);
      setMinChars(1);
      setStore(getListStoreWithUrl(remoteJsonURL));
      setLoadingText("loading...");
   }

   public void reloadDataWithUrl(String url) {
      setStore(getListStoreWithUrl(url));
      this.onLoad();
   }
}
