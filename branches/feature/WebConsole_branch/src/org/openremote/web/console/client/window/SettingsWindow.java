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

import org.openremote.web.console.client.event.SubmitEvent;
import org.openremote.web.console.client.gxtextends.StringModelData;
import org.openremote.web.console.client.icon.Icons;
import org.openremote.web.console.client.listener.FormSubmitListener;
import org.openremote.web.console.client.listener.SubmitListener;
import org.openremote.web.console.client.rpc.AsyncServiceFactory;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;
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

public class SettingsWindow extends FormWindow {
   
   private Icons icons = GWT.create(Icons.class);
   private ContentPanel customServerContainer;
   private ContentPanel autoServerContainer;
   
   private boolean autoMode;
   
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
      ToggleButton autoButton = new ToggleButton("OFF") {
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
                     //TODO: reload auto servers.
                     autoServerContainer.show();
                  } else {
                     createAutoServerGrid();
                  }
                  ClientDataBase.appSetting.setAutoMode(true);
               } else {
                  this.setText("OFF");
                  if (customServerContainer != null) {
                     customServerContainer.show();
                  }
                  if (autoServerContainer != null) {
                     autoServerContainer.hide();
                  }
                  ClientDataBase.appSetting.setAutoMode(false);
               }
            }
         }
      };
      autoButton.setWidth(200);
      
      autoMode = ClientDataBase.appSetting.isAutoMode();
      autoButton.toggle(autoMode);
      
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
      
      final StringModelData data1 = new StringModelData("autoServer", "http://127.0.0.1:8080/controller");
      Grid<StringModelData> autoServerGrid = new Grid<StringModelData>(new ListStore<StringModelData>(), new ColumnModel(autoServerConfigs)) {
         @Override
         protected void afterRenderView() {
            super.afterRenderView();
            this.getSelectionModel().select(data1, false);
         }
      };
      autoServerGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//      autoServerGrid.setHideHeaders(true);
      autoServerGrid.getStore().add(data1);
      autoServerGrid.getStore().add(new StringModelData("autoServer", "http://localhost:8080/controller"));
      
      autoServerContainer = new ContentPanel();
      autoServerContainer.setHeaderVisible(false);
      autoServerContainer.setBodyBorder(false);
      autoServerContainer.setLayout(new FitLayout());
      autoServerContainer.setStyleAttribute("marginTop", "5px");
      autoServerContainer.setStyleAttribute("marginBottom", "5px");
      autoServerContainer.setSize(360, 150);
      autoServerContainer.setBorders(true);
      
      autoServerContainer.add(autoServerGrid);
      form.insert(autoServerContainer, 1);
      layout();
   }
   
   private void addPanelIdentityField() {
      Button selectPanelButton = new Button("Select Panel...");
      AdapterField panelIdentityField = new AdapterField(selectPanelButton);
      panelIdentityField.setFieldLabel("Panel Identity");
      form.add(panelIdentityField);
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
            AsyncServiceFactory.getUserCacheServiceAsync().saveAppSetting(ClientDataBase.appSetting, new AsyncSuccessCallback<Void>() {
               public void onSuccess(Void result) {
                  fireEvent(SubmitEvent.SUBMIT, new SubmitEvent());
                  hide();
               }
            });
         }
      });
   }
}
