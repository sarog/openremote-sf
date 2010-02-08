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
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
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
   
   public ScreenWindow(ScreenTab screenTab, BeanModel selectItem, Operation operation) {
      super();
      this.operation = operation;
      this.selectItem = selectItem;
      setSize(350, 270);
      setHeading("New Screen");
      setLayout(new FillLayout());
      setModal(true);
      createButtons();
      createFields(screenTab);
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
      Button templateSelectBtn = new Button("from template");
      templateSelectBtn.addSelectionListener(new TemplateSelectLisntener());
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));
      
      form.addButton(templateSelectBtn);
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
               GroupRef groupRef = (GroupRef) bean;
               ScreenRef screenRef = null;
               switch (operation) {

               case EDIT:
                  screenRef = (ScreenRef) selectItem.getBean();
                  screen = screenRef.getScreen();
                  break;
               case CREATE_BY_TEMPLATE:
                  if (screen != null) {
                     screen.setName(nameField.getValue());
                     screenRef = createScreenFromTemplate(groupRef);
                     BeanModelDataBase.screenTable.insert(screen.getBeanModel());
                     break;
                  }
               case NEW:
                  screenRef = createScreen(groupRef);
                  screen.setName(nameField.getValue());
                  BeanModelDataBase.screenTable.insert(screen.getBeanModel());
                  break;
               }
               screen.setName(nameField.getValue());
               screenRef.setGroup(groupRef.getGroup());
               fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenRef));
            }
           
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
  
   class TemplateSelectLisntener extends SelectionListener<ButtonEvent>{

      @Override
      public void componentSelected(ButtonEvent ce) {
         SelectTemplateWindow templateWindow = new SelectTemplateWindow();
         templateWindow.addListener(SubmitEvent.SUBMIT, new Listener<SubmitEvent>(){

            @Override
            public void handleEvent(SubmitEvent be) {
               BeanModel screenBeanModel = be.getData();
               screen = screenBeanModel.getBean();
            }
            
         });
         
         operation = Operation.CREATE_BY_TEMPLATE;
      }
      
   }
   
   
   public static enum Operation{
      NEW,EDIT,CREATE_BY_TEMPLATE;
   }
}
