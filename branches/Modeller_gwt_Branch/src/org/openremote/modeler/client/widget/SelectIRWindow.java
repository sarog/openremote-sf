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

import com.extjs.gxt.ui.client.data.BaseListLoadConfig;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.JsonReader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.data.XmlLoadResultReader;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:allen.wei@finalist.cn">allen.wei</a>
 */
public class SelectIRWindow extends Window {

   public SelectIRWindow() {
      setupWindow();
      addVendersList();
   }

   private void setupWindow() {
      setSize(800, 300);
      setPlain(true);
      setModal(true);
      setBlinkModal(true);
      setHeading("Select IR from Beehive");
   }

   private void addVendersList() {
      // ModelType venderType = new ModelType();
      // venderType.setRoot("vendor");
      // venderType.addField("id");
      // venderType.addField("name");
      ModelType venderType = new ModelType();
      venderType.setRoot("records");
      venderType.addField("Sender", "name");
      venderType.addField("Email", "email");
      venderType.addField("Phone", "phone");
      venderType.addField("State", "state");
      venderType.addField("Zip", "zip");

      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(
            "http://extjs.com/examples/data/data.json");
      // ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(
      // "http://openremote.finalist.hk/beehive/rest/lirc");
      JsonLoadResultReader<ListLoadResult<ModelData>> reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(
            venderType);
      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(
            scriptTagProxy, reader);
      scriptTagProxy.load(reader, new BaseListLoadConfig(), new AsyncCallback<ListLoadResult<ModelData>>() {

         public void onFailure(Throwable arg0) {
            System.out.println(arg0.getLocalizedMessage());

         }

         public void onSuccess(ListLoadResult<ModelData> result) {
            for (ModelData data : result.getData()) {
               System.out.println(data.get("Sender"));
            }

         }

      });
      ListStore<ModelData> store = new ListStore<ModelData>(loader);
      ListView<ModelData> listView = new ListView<ModelData>(store);
      listView.setDisplayProperty("Sender");
//      listView.setDisplayProperty("name");
      add(listView);

      // loader.load();

   }
}
