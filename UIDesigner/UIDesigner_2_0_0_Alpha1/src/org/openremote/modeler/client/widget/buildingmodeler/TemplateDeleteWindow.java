package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.gxtextends.NestedJsonLoadResultReader;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class TemplateDeleteWindow extends FormWindow{
   private ListView<ModelData> templatesList = new ListView<ModelData>();
   private String restURL = "";
   
   public TemplateDeleteWindow(){
      setHeading("Select Template");
      setMinHeight(400);
      setWidth(240);
      setModal(true);
      initTemplatesList();
      showTemplateInfo();
      createDeleteButton();
      createCloseButton();
      show();
   }
   
   
   private void initTemplatesList() {
      createPriviteTemplateListView();
      templatesList.setDisplayProperty("name");
      ContentPanel templatesContainer = new ContentPanel();
      templatesContainer.setSize(235, 300);
      templatesContainer.setScrollMode(Scroll.AUTOY);
      templatesContainer.setBorders(false);
      templatesContainer.setBodyBorder(false);
      templatesContainer.add(templatesList);
      
      add(templatesContainer);
      layout();
   }
   
   private void showTemplateInfo() {
      final Html templateInfo = new Html("<p><b>Switch info</b></p>"); 
      templatesList.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            ModelData templateModel = se.getSelectedItem();
            if (templateModel != null) {
               Long oid = templateModel.get("id");
               String content = templateModel.get("content");
               String name = templateModel.get("name");
               
               Template template = new Template();
               template.setContent(content);
               template.setOid(oid);
               template.setName(name);
               String templateName = "<p><b>Template info</b></p>";
               if (template.getName() != null){
                  templateName = templateName + "<p>name: " + template.getName() + "</p>";
               }
               templateInfo.setHtml(templateName);
               
            }
         }
      });
      add(templateInfo, new RowData(1, -1, new Margins(4)));
   }
   
   private void createDeleteButton(){
      Button deleteBtn = new Button();
      deleteBtn.setText("Delete");
      deleteBtn.addSelectionListener(new DeleteListener());
      addButton(deleteBtn);
   }
   
   private void createCloseButton(){
      Button closeBtn = new Button();
      closeBtn.setText("Close");
      closeBtn.addSelectionListener(new CloseListener());
      addButton(closeBtn);
   }
   private void createPriviteTemplateListView(){
      UtilsProxy.getTemplatesListRestUrl(new AsyncSuccessCallback<String>() {

         @Override
         public void onSuccess(String result) {
            restURL = result;
            createView();
            layout();
         }

      });
   }
   
   private void createView(){
      /*
       * parse template from json. 
       */
      ModelType templateType = new ModelType();
      templateType.setRoot("templates.template");
      DataField idField = new DataField("id");
      idField.setType(Long.class);
      templateType.addField(idField);
      templateType.addField("content");
      templateType.addField("name");
      ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(restURL);
      NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
            templateType);
      final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(scriptTagProxy, reader);

      ListStore<ModelData> store = new ListStore<ModelData>(loader);
      loader.load();
      templatesList.setStore(store);
   }
   
   class DeleteListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         final ModelData templateModelData = templatesList.getSelectionModel().getSelectedItem();
         if (templateModelData == null) {
            MessageBox.alert("Error", "Please select a Template.", null);
            ce.cancelBubble();
         } else {
            long oid = templateModelData.get("id");
           
            TemplateProxy.deleteTemplateById(oid, new AsyncSuccessCallback<Boolean>(){

               @Override
               public void onSuccess(Boolean success) {
                  if(success){
                     templatesList.getStore().remove(templateModelData);
                     Info.display("delete template", "template delete successfully");
                  }
               }
               
            });
         }
      }
      
   }
   
   class CloseListener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         hide();
      }
      
   }
}

