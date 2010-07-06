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
import java.util.Set;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.TemplateProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.DeviceBeanModelTable;
import org.openremote.modeler.client.utils.DeviceMacroBeanModelTable;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.ScreenFromTemplate;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * A wizard for creating a new screen from existing groups.
 *
 * @author Javen
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class ScreenWindow extends FormWindow {
   
   private Screen screen = null;
   
   private TextField<String> nameField = null;
   private BeanModel selectItem = null;
   
   private Operation operation = Operation.NEW;
   private TreePanel<BeanModel> groupSelectTree = null;
   private ListView<BeanModel> templateView = null;
   
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
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));
      
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
                  return;
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
            BeanModel templateBeanModel = templateView.getSelectionModel().getSelectedItem();
            if (templateBeanModel == null) {
               MessageBox.alert("Error", "Please select a template.", null);
               be.cancelBubble();
            } else {
               Template template = templateBeanModel.getBean();
               TemplateProxy.buildScreenFromTemplate(template, new AsyncSuccessCallback<ScreenFromTemplate>(){

                  @Override
                  public void onSuccess(ScreenFromTemplate result) {
                     screen = result.getScreen();
                     screen.setOid(IDUtil.nextID());
                     screen.setName(nameField.getValue());
                     ScreenRef screenRef = createScreenFromTemplate(groupRef);
                     BeanModelDataBase.screenTable.insert(screen.getBeanModel());
                     
                     ScreenWindow.this.unmask();
                     //----------rebuild command 
                     Set<Device> devices = result.getDevices();
                     for(Device device: devices) {
                        ((DeviceBeanModelTable)BeanModelDataBase.deviceTable).insertAndNotifyDeviceInsertListener(device.getBeanModel());
                     }
                     
                     Set<DeviceMacro> macros = result.getMacros();
                     for (DeviceMacro macro : macros) {
                        ((DeviceMacroBeanModelTable)BeanModelDataBase.deviceMacroTable).insertAndNotifyMacroInsertListener(macro.getBeanModel());
                     }
                     fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
                  }

                  @Override
                  public void onFailure(Throwable caught) {
                     MessageBox.alert("Error", "Failed to create screen from template,error message: "+caught.getMessage(), null);
                     ScreenWindow.this.unmask();
                  }
                  
               });
            }
            ScreenWindow.this.mask("Downloading resources for this template... ");
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
      screen.setTouchPanelDefinition(selectedGroup.getPanel().getTouchPanelDefinition());
      ScreenRef screenRef = new ScreenRef(screen);
      selectedGroup.getGroup().addScreenRef(screenRef);
      screenRef.setGroup(selectedGroup.getGroup());
      BeanModelDataBase.screenTable.insert(screen.getBeanModel());
      return screenRef;
   }
   private ContentPanel createGroupTreeView(ScreenTab screenTab) {
      ContentPanel groupTreeContainer = new ContentPanel();
      groupTreeContainer.setHeaderVisible(false);
      groupTreeContainer.setSize(210, 150);
      groupTreeContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      groupTreeContainer.addStyleName("overflow-auto");
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
                  hintText.setText("There are no private template in beehive! ");
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
               initTemplateView(showPrivate);
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

   public BeanModel getSelectedGroupRefModel() {
      return (BeanModel) groupSelectTree.getSelectionModel().getSelectedItem();
   }
  
   private void buildTemplateList() {
      templateView = new ListView<BeanModel>();
      templateView.setStateful(true);
      templateView.setBorders(false);
      templateView.setHeight("100%");      
      templateView.setDisplayProperty("name");
      initTemplateView(true);
   }
   
   
   private void initTemplateView(final boolean isFromPrivate) {
      templateView.mask();
      TemplateProxy.getTemplates(isFromPrivate, new AsyncCallback<List<Template>> () {

         @Override
         public void onFailure(Throwable caught) {
            templateView.unmask();
            hintText.setText("Faild to get templates,error message: "+caught.getMessage());
            hintText.show();
         }

         @Override
         public void onSuccess(List<Template> result) {
            templateView.unmask();
            hintText.hide();
            if (result.size() == 0) {
               hintText.setText("There are no " +(isFromPrivate?"private ":"public ") +"templates in beehive. ");
               hintText.show();
            } 
            ListStore<BeanModel> store = new ListStore<BeanModel> ();
            store.add(Template.createModels(result));
            templateView.setStore(store);
         }
         
      });
   }
   public static enum Operation{
      NEW,EDIT,CREATE_BY_TEMPLATE;
   }
}
