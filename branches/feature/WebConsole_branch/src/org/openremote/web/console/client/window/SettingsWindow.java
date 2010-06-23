/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.client.window;


import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.event.SubmitEvent;
import org.openremote.web.console.client.gxtextends.StringModelData;
import org.openremote.web.console.client.icon.Icons;
import org.openremote.web.console.client.listener.FormSubmitListener;
import org.openremote.web.console.client.listener.SubmitListener;
import org.openremote.web.console.client.rpc.AsyncServiceFactory;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.domain.AppSetting;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SettingsWindow extends FormWindow {
   
   private Icons icons = GWT.create(Icons.class);
   private ToggleButton autoButton;
   private ContentPanel customServerContainer;
   private ContentPanel autoServerContainer;
   
   public SettingsWindow() {
      super();
      setStyle();
      addAutoField();
      createCustomServerGrid();
      addPanelIdentityField();
      addButtons();
      addListener();
      show();
   }
   
   private void setStyle() {
      setHeading("AppSettings");
      setSize(400,450);
      form.setLabelWidth(100);
      form.setFieldWidth(200);
   }
   
   private void addAutoField() {
      autoButton = new ToggleButton("OFF") {
         @Override
         protected void toggle(boolean state, boolean silent) {
            super.toggle(state, silent);
            if (rendered) {
               if (state) {
                  this.setText("ON");
                  if (customServerContainer != null) {
                     customServerContainer.hide();
                  }
                  if (autoServerContainer != null) {
                     autoServerContainer.show();
                  } else {
                     createAutoServerGrid();
                  }
                  ClientDataBase.appSetting.setAutoDiscovery(true);
               } else {
                  this.setText("OFF");
                  if (customServerContainer != null) {
                     customServerContainer.show();
                  }
                  if (autoServerContainer != null) {
                     autoServerContainer.hide();
                  }
                  ClientDataBase.appSetting.setAutoDiscovery(false);
               }
            }
         }
      };
      autoButton.setWidth(200);
      
      autoButton.toggle(ClientDataBase.appSetting.isAutoDiscovery());
      
      AdapterField autoField = new AdapterField(autoButton);
      autoField.setFieldLabel("Auto Discovery");
      form.add(autoField);
   }
   
   private void createCustomServerGrid() {
     List<ColumnConfig> customServerConfigs = new ArrayList<ColumnConfig>();
     ColumnConfig serverColumn = new ColumnConfig("customServer", "Server", 338);
     serverColumn.setSortable(false);
     customServerConfigs.add(serverColumn);
     
     ListStore<StringModelData> customListStore = new ListStore<StringModelData>();
     AppSetting appSetting = ClientDataBase.appSetting;
     int tempserverIndex = -1;
     if (!"".equals(appSetting.getCurrentServer()) && appSetting.getCustomServers() != null) {
        String currentServer = appSetting.getCurrentServer();
        List<String> customServers = appSetting.getCustomServers();
        for (int i = 0; i < customServers.size(); i++) {
           customListStore.add(new StringModelData("customServer", customServers.get(i)));
           if (currentServer.equals(customServers.get(i))) {
              tempserverIndex = i;
           }
      }
     }
     final int currentServerIndex = tempserverIndex;
     final Grid<StringModelData> customServerGrid = new Grid<StringModelData>(customListStore, new ColumnModel(customServerConfigs)) {
        @Override
        protected void afterRenderView() {
           super.afterRenderView();
           if (currentServerIndex != -1) {
              this.getSelectionModel().select(currentServerIndex, false);
           }
        }
     };
     customServerGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
     customServerGrid.setHideHeaders(true);
     customServerGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<StringModelData>() {
      public void selectionChanged(SelectionChangedEvent<StringModelData> se) {
         ClientDataBase.appSetting.setCurrentServer(se.getSelectedItem().getValue());
      }
     });
     
     customServerContainer = new ContentPanel();
     customServerContainer.setHeaderVisible(false);
     customServerContainer.setBodyBorder(false);
     customServerContainer.setLayout(new FitLayout());
     customServerContainer.setStyleAttribute("marginTop", "5px");
     customServerContainer.setStyleAttribute("marginBottom", "5px");
     customServerContainer.setSize(360, 150);
     customServerContainer.setBorders(true);
     
     ToolBar toolBar = new ToolBar();
     Button addButton = new Button("Add", icons.add());
     addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
      public void componentSelected(ButtonEvent ce) {
         AddServerWindow addServerWindow = new AddServerWindow();
         addServerWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
            @Override
            public void afterSubmit(SubmitEvent be) {
               if (be.getData() != null) {
                  StringModelData newData = new StringModelData("customServer", be.getData().toString());
                  customServerGrid.getStore().add(newData);
                  customServerGrid.getSelectionModel().select(newData, false);
                  ClientDataBase.appSetting.addCustomServer(be.getData().toString());
               }
            }
            
         });
      }
     });
     toolBar.add(addButton);
     toolBar.add(new SeparatorToolItem());
     
     Button deleteButton = new Button("Delete", icons.delete());
     deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
         StringModelData selectedModel = customServerGrid.getSelectionModel().getSelectedItem();
         if (selectedModel != null) {
            customServerGrid.getStore().remove(selectedModel);
            ClientDataBase.appSetting.removeCustomServer(selectedModel.getValue());
            ClientDataBase.appSetting.setCurrentServer("");
         } else {
            MessageBox.alert("Warn", "Please select a custom server url.", null);
         }
      }
     });
     toolBar.add(deleteButton);
     
     customServerContainer.setTopComponent(toolBar);
     customServerContainer.add(customServerGrid);
     form.add(customServerContainer);
   }
   
   private void createAutoServerGrid() {
      List<ColumnConfig> autoServerConfigs = new ArrayList<ColumnConfig>();
      ColumnConfig serverColumn = new ColumnConfig("autoServer", "Auto Servers", 338);
      serverColumn.setSortable(false);
      autoServerConfigs.add(serverColumn);
      final ListStore<StringModelData> autoServersStore = new ListStore<StringModelData>();
      final Grid<StringModelData> autoServerGrid = new Grid<StringModelData>(autoServersStore, new ColumnModel(autoServerConfigs)) {
         @Override
         protected void afterRenderView() {
            super.afterRenderView();
            this.getSelectionModel().select(0, false);
         }
      };
      autoServerGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      
      autoServerContainer = new ContentPanel() {
         @Override
         public void show() {
            super.show();
            this.mask("Loading auto discovery servers...");
            autoServersStore.removeAll();
            autoButton.disable();
            AsyncServiceFactory.getIPAutoDiscoveryServiceAsync().getAutoDiscoveryServers(new AsyncCallback<List<String>>() {
               public void onFailure(Throwable caught) {
                  autoServerContainer.unmask();
                  autoButton.enable();
               }

               public void onSuccess(List<String> autoServers) {
                  for (String autoServer : autoServers) {
                     autoServersStore.add(new StringModelData("autoServer", autoServer));
                  }
                  autoServerGrid.getSelectionModel().select(0, false);
                  autoServerContainer.unmask();
                  autoButton.enable();
               }
            });
         }
      };
      autoServerContainer.setHeaderVisible(false);
      autoServerContainer.setBodyBorder(false);
      autoServerContainer.setLayout(new FitLayout());
      autoServerContainer.setStyleAttribute("marginTop", "5px");
      autoServerContainer.setStyleAttribute("marginBottom", "5px");
      autoServerContainer.setSize(360, 150);
      autoServerContainer.setBorders(true);
      autoServerContainer.show(); // for loading auto discovery servers.
      autoServerContainer.add(autoServerGrid);
      form.insert(autoServerContainer, 1);
      layout();
   }
   
   private void addPanelIdentityField() {
       Button selectPanelButton = new Button("Select Panel...");
       AdapterField panelIdentityField = new AdapterField(selectPanelButton);
       panelIdentityField.setFieldLabel("Panel Identity");
       form.add(panelIdentityField);
      
//      String url = "http://192.168.100.113:8080/controller/rest/panels";
//      RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
//      HttpProxy<ListLoadResult<ModelData>> proxy = new HttpProxy<ListLoadResult<ModelData>>(builder);
////      ScriptTagProxy<ListLoadResult<ModelData>> proxy = new ScriptTagProxy<ListLoadResult<ModelData>>(url);
//
//      ModelType type = new ModelType();
//      type.setRoot("openremote");
//      type.setRecordName("panel");
//      type.addField("id");
//      type.addField("name");
//
//      XmlReader<ListLoadResult<ModelData>> reader = new XmlReader<ListLoadResult<ModelData>>(type);
//
//      BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
////      loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
////         public void handleEvent(LoadEvent be) {
////            be.<ModelData> getConfig().set("start", be.<ModelData> getConfig().get("offset"));
////         }
////      });
//      ListStore<ModelData> store = new ListStore<ModelData>(loader);
//      ComboBox<ModelData> combo = new ComboBox<ModelData>();
//      combo.setFieldLabel("Panel Identity");
//      combo.setDisplayField("name");
//      combo.setStore(store);
//      combo.setTriggerAction(TriggerAction.ALL);
//      form.add(combo);
   }
   
   private void addButtons() {
      Button okButton = new Button("OK");
      okButton.addSelectionListener(new FormSubmitListener(form));

      Button cancelButton = new Button("Cancel");
      cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            hide();
         }
      });
      
      form.addButton(okButton);
      form.addButton(cancelButton);
   }
   
   private void addListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            Cookies.setCookie(Constants.CONSOLE_SETTINGS, ClientDataBase.appSetting.toJson());
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent());
            hide();
         }
      });
   }
}
