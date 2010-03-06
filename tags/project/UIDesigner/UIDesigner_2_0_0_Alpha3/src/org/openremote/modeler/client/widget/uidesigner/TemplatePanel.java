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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.NestedJsonLoadResultReader;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.buildingmodeler.TemplateCreateWindow;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

/**
 * The Template panel. 
 * @author Javen
 */
public class TemplatePanel extends ContentPanel {

   private ListView<ModelData> templateView;
   private Icons icon = GWT.create(Icons.class);
   /**
    * Instantiates a new profile panel.
    */
   public TemplatePanel() {
      setHeading("Template");
      setIcon(icon.templateIcon());
      setLayout(new FitLayout());
      createMenu();
      buildTemplateList();
   }

   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      List<Button> editDelBtns = new ArrayList<Button>();
      toolBar.add(createNewTemplateMenuItem());

      Button deleteBtn = createDeleteBtn();
      deleteBtn.setEnabled(true);
      
      toolBar.add(deleteBtn);
      editDelBtns.add(deleteBtn);
      setTopComponent(toolBar);
   }


   /**
    * Creates the delete btn.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            final ModelData templateModelData = templateView.getSelectionModel().getSelectedItem();
            if (templateModelData == null) {
               MessageBox.alert("Error", "Please select a Template.", null);
               ce.cancelBubble();
            } else {
               Long oid = templateModelData.get("id");
               if (oid == null) {
                  oid = templateModelData.get("oid");
               }
               TemplateProxy.deleteTemplateById(oid, new AsyncSuccessCallback<Boolean>() {

                  @Override
                  public void onSuccess(Boolean success) {
                     if (success) {
                        templateView.getStore().remove(templateModelData);
                        Info.display("delete template", "template delete successfully");
                     }
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error","failed to delete the template.The beehive may be not avaliable now ",null);
                  }
                  
                  
               });
            }
         }
      });
      return deleteBtn;
   }


   private Button createNewTemplateMenuItem() {
      Button newPanelItem = new Button("New");
      newPanelItem.setIcon(icon.add());
      newPanelItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            final TemplateCreateWindow templateCreateWindow = new TemplateCreateWindow();
            templateCreateWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  Template template = be.getData();
                  if(template.getShareTo() != Template.PUBLIC){
                     templateView.getStore().add(template.getBeanModel());
                  }
               }

            });

         }
      });
      return newPanelItem;
   }

   private void buildTemplateList() {
      this.templateView = new ListView<ModelData>();
      templateView.setStateful(true);
      templateView.setBorders(false);
      templateView.setHeight("100%");      
      templateView.setDisplayProperty("name");
      UtilsProxy.getTemplatesListRestUrl(new AsyncSuccessCallback<String> (){
         public void onSuccess(String result){
            ModelType templateType = new ModelType();
            templateType.setRoot("templates.template");
            DataField idField = new DataField("id");
            idField.setType(Long.class);
            templateType.addField(idField);
            templateType.addField("content");
            templateType.addField("name");
            ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(result);
            NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
                  templateType);
            final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(scriptTagProxy, reader);

            ListStore<ModelData> store = new ListStore<ModelData>(loader);
            loader.load();
            templateView.setStore(store);
            layout();
         }
      });
      add(templateView);
   }
}
