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

import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.NestedJsonLoadResultReader;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.ScriptTagProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
/**
 * A wizard for creating a new screen from existed groups. 
 * @author Javen
 *
 */
public class ScreenWindow extends FormWindow {
   
   private Screen screen = null;
   
   private TextField<String> nameField = null;
   private BeanModel selectItem = null;
   
   private Operation operation = Operation.NEW;
   private TreePanel<BeanModel> groupSelectTree = null;
//   private TreePanel<BeanModel> templateSelectTree = null;
   private ListView<ModelData> templateView = null;
   
   private Text hintText = new Text();
   
   public ScreenWindow(ScreenTab screenTab, BeanModel selectItem, Operation operation) {
      super();
      this.operation = operation;
      this.selectItem = selectItem;
      
      setSize(350, 300);
      if(operation == Operation.NEW){
         setSize(350, 450);
      }
      setHeading("New Screen");
      setLayout(new FillLayout());
      setModal(true);
      createButtons();
      createFields(screenTab);
      createTemplateView();
      hintText.hide();
      setBodyBorder(false);
      add(form);
      show();
   }

   public ScreenWindow(ScreenTab screenTab, BeanModel selectItem) {
      this(screenTab, selectItem, Operation.NEW);
   }
   
   
   public void createFields(final ScreenTab screenTab) {
      form.setHeaderVisible(false);
      form.setBorders(false);
      form.setBodyBorder(true);
      form.setLabelWidth(60);
      nameField = new TextField<String>();
      nameField.setAllowBlank(false);
      nameField.setFieldLabel("Name");
      nameField.setName("name");
      
      AdapterField adapterField = new AdapterField(createGroupTreeView(screenTab));
      adapterField.setFieldLabel("Group");
      adapterField.setBorders(true);
      
      form.add(nameField);
      form.add(adapterField);
      addBeforHideListener(screenTab);
   }
   
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");
//      Button templateSelectBtn = new Button("from template");
//      templateSelectBtn.addSelectionListener(new TemplateSelectLisntener());
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));
      
//      form.addButton(templateSelectBtn);
      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }
   private void addBeforHideListener(final ScreenTab screenTab) {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {

         @Override
         public void handleEvent(FormEvent be) {
            BeanModel groupModel = groupSelectTree.getSelectionModel().getSelectedItem();
            if (groupModel == null || !(groupModel.getBean() instanceof GroupRef)) {
               MessageBox.alert("New Screen Error", "Please select a group.", null);
               be.cancelBubble();
               return;
            }
            Object bean = groupModel.getBean();
            if (bean != null && bean instanceof GroupRef) {
               final GroupRef groupRef = (GroupRef) bean;
               ScreenRef screenRef = null;
               switch (operation) {

               case EDIT:
                  screenRef = (ScreenRef) selectItem.getBean();
                  screen = screenRef.getScreen();
                  break;
               case CREATE_BY_TEMPLATE:
                  buildScreenFromTemplate(be, groupRef);
                  if (screen == null) {
                    return;
                  }
               case NEW:
                  screenRef = createScreen(groupRef);
                  screen.setName(nameField.getValue());
                  if (groupRef.getGroup().getTabbarItems().size() > 0 || groupRef.getPanel().getTabbarItems().size() > 0){
                	  screen.setHasTabbar(true);
                  }
                  BeanModelDataBase.screenTable.insert(screen.getBeanModel());
                  break;
               }
               screen.setName(nameField.getValue());
               screenRef.setGroup(groupRef.getGroup());
               fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
            }
           
         }

         private void buildScreenFromTemplate(FormEvent be, final GroupRef groupRef) {
            ModelData templateModelData = templateView.getSelectionModel().getSelectedItem();
            if (templateModelData == null) {
               MessageBox.alert("Error", "Please select a Template.", null);
               be.cancelBubble();
            } else {
               Long oid = templateModelData.get("id");
               String content = templateModelData.get("content");
               String name = templateModelData.get("name");
               
               Template template = new Template();
               template.setContent(content);
               template.setOid(oid);
               template.setName(name);
               TemplateProxy.buildScreenFromTemplate(template, new AsyncSuccessCallback<Screen>(){

                  @Override
                  public void onSuccess(Screen result) {
                     screen = result;
                     screen.setName(nameField.getValue());
                     ScreenRef screenRef = createScreenFromTemplate(groupRef);
                     BeanModelDataBase.screenTable.insert(screen.getBeanModel());
                     ScreenWindow.this.unmask();
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error", "failed to create screen by template.", null);
                  }
                  
                  
               });
            }
            ScreenWindow.this.mask("Download resources for this template... ");
         }

      });
   }

   private ScreenRef createScreen(GroupRef selectedGroup) {
      screen = new Screen();
      screen.setOid(IDUtil.nextID());
      screen.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      ScreenRef screenRef = new ScreenRef(screen);
      selectedGroup.getGroup().addScreenRef(screenRef);
      return screenRef;
   }
   
   private ScreenRef createScreenFromTemplate(GroupRef selectedGroup) {
//      templateView.getSelectionModel().getSelectedItem();
      screen.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      BeanModelDataBase.screenTable.insert(screen.getBeanModel());
      ScreenRef screenRef = new ScreenRef(screen);
      selectedGroup.getGroup().addScreenRef(screenRef);
      return screenRef;
   }
   private ContentPanel createGroupTreeView(ScreenTab screenTab) {
      ContentPanel groupTreeContainer = new ContentPanel();
      groupTreeContainer.setHeaderVisible(false);
      groupTreeContainer.setSize(210, 150);
      groupTreeContainer.setLayout(new FitLayout());
      groupTreeContainer.setScrollMode(Scroll.AUTO);
      List<BeanModel> panels = BeanModelDataBase.panelTable.loadAll();
      groupSelectTree = buildGroupSelectTree(panels);
      groupTreeContainer.add(groupSelectTree);
      groupTreeContainer.setEnabled(operation==Operation.NEW || operation==Operation.CREATE_BY_TEMPLATE);
      groupTreeContainer.setStyleAttribute("backgroundColor", "white");

      if (null != this.selectItem) {
         if (this.selectItem.getBean() instanceof GroupRef && (operation==Operation.NEW || operation==Operation.CREATE_BY_TEMPLATE)) {
            groupSelectTree.getSelectionModel().select(selectItem, false);
         } else if (selectItem.getBean() instanceof ScreenRef && operation == Operation.EDIT) {
            ScreenRef screenRef = (ScreenRef) selectItem.getBean();
            nameField.setValue(screenRef.getScreen().getName());
            BeanModel selectedGroup = TreePanelBuilder.buildPanelTree(screenTab).getStore().getParent(selectItem);
            groupSelectTree.getSelectionModel().select(selectedGroup, false);
         }
      }
      return groupTreeContainer;
   }

   private TreePanel<BeanModel> buildGroupSelectTree(List<BeanModel> panels) {
      TreeStore<BeanModel> groups = new TreeStore<BeanModel>();
      TreePanel<BeanModel> groupTree = TreePanelBuilder.buildPanelTree(groups);
      groups.add(panels, false);
      for (BeanModel panelModel : panels) {
         Panel panel = panelModel.getBean();
         List<GroupRef> groupRefs = panel.getGroupRefs();
         for (GroupRef ref : groupRefs) {
            groups.add(panelModel, ref.getBeanModel(), false);
         }
      }
      return groupTree;
   }

   private void createTemplateView() {
      if (operation == Operation.NEW) {
         FieldSet templateFieldSet = new FieldSet();
         templateFieldSet.setHeading("Select from template");
         templateFieldSet.setCheckboxToggle(true);
         templateFieldSet.setExpanded(false);
         buildTemplateList();
         templateFieldSet.addListener(Events.BeforeExpand, new Listener<FieldSetEvent>() {
            public void handleEvent(FieldSetEvent be) {
               operation = Operation.CREATE_BY_TEMPLATE;
               if(templateView.getStore().getCount() == 0){
                  hintText.setText("No template");
                  hintText.show();
               }
            }

         });
         templateFieldSet.addListener(Events.BeforeCollapse, new Listener<FieldSetEvent>() {
            public void handleEvent(FieldSetEvent be) {
               operation = Operation.NEW;
               hintText.hide();
            }
         });
         
         RadioGroup shareRadioGroup = new RadioGroup();
         
         Radio shareNoneRadio = new Radio();
         shareNoneRadio.setBoxLabel("Private");
         shareNoneRadio.setValue(true);
         shareNoneRadio.addListener(Events.Change, new Listener<FieldEvent>(){

            @Override
            public void handleEvent(FieldEvent be) {
               Boolean showPrivate = (Boolean) be.getValue();
               if(showPrivate) {
                  createPriviteTemplateListView();
               } else {
                  createPublicTemplateListView();
               }
               
            }
            
         });
         
         Radio shareToAllRadio = new Radio();
         shareToAllRadio.setName("Public");
         shareToAllRadio.setBoxLabel("Public");
         shareRadioGroup.setFieldLabel("From:");
         shareRadioGroup.add(shareNoneRadio);
         shareRadioGroup.add(shareToAllRadio);
         templateFieldSet.add(shareRadioGroup);
         templateFieldSet.add(templateView);
         templateFieldSet.add(hintText);
         form.add(templateFieldSet);
      }
   }
   public BeanModel getSelectItem() {
      return selectItem;
   }

   public void setSelectItem(BeanModel selectItem) {
      this.selectItem = selectItem;
   }

   public BeanModel getSelectedGroupRefModel() {
      return (BeanModel) groupSelectTree.getSelectionModel().getSelectedItem();
   }
   public TreePanel<BeanModel> getGroupSelectTree() {
      return groupSelectTree;
   }
  
   private void buildTemplateList() {
      templateView = new ListView<ModelData>();
      templateView.setStateful(true);
      templateView.setBorders(false);
      templateView.setHeight("100%");      
      templateView.setDisplayProperty("name");
      UtilsProxy.getTemplatesListRestUrl(new AsyncSuccessCallback<String> (){
         public void onSuccess(String result){
            createView(result);
         }
      });
   }
   
   private void createPriviteTemplateListView(){
      UtilsProxy.getTemplatesListRestUrl(new AsyncSuccessCallback<String>() {

         @Override
         public void onSuccess(String result) {
            createView(result);
            layout();
            ScreenWindow.this.unmask();
//            updateHintText();
         }

         @Override
         public void onFailure(Throwable caught) {
            MessageBox.alert("falid", "unable to load the template information from beehive", null);
         }

      });
      ScreenWindow.this.mask("loading ...");
   }
   
   private void createPublicTemplateListView(){
      UtilsProxy.getAllPublicTemplateRestURL(new AsyncSuccessCallback<String>(){

         @Override
         public void onSuccess(String result) {
            createView(result);
            layout();
            ScreenWindow.this.unmask();
//            updateHintText();
         }
         @Override
         public void onFailure(Throwable caught) {
            MessageBox.alert("falid", "unable to load the template information from beehive", null);
         }
      });
      ScreenWindow.this.mask("loading ...");
   }
   private void createView(String restURL){
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
      loader.setReuseLoadConfig(false);
      ListStore<ModelData> store = new ListStore<ModelData>(loader);
      loader.addLoadListener(new LoadListener(){

         
         @Override
         public void loaderBeforeLoad(LoadEvent le) {
            super.loaderBeforeLoad(le);
            hintText.setText("No template");
            hintText.show();
         }

         @Override
         public void loaderLoad(LoadEvent le) {
            super.loaderLoad(le);
            hintText.hide();
         }
         
      });
      loader.load();
      //loader.
      templateView.setStore(store);
   }
   
   /*private void updateHintText(){
      if (operation == Operation.CREATE_BY_TEMPLATE) {
         if (templateView.getStore().getModels().size() == 0) {
            hintText.show();
            hintText.setText("No Templates");
         } else {
            hintText.hide();
         }
      } else {
         hintText.hide();
      }
   }*/
   public static enum Operation{
      NEW,EDIT,CREATE_BY_TEMPLATE;
   }
}
