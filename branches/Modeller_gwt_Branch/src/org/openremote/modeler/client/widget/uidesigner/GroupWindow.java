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
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * The Class GroupWindow.
 */
public class GroupWindow extends Dialog {

   /** The Constant GROUP_NAME. */
   private static final String GROUP_NAME = "groupName";

   /** The group model. */
   private BeanModel groupModel = null;

   /** The group name field. */
   private TextField<String> groupNameField = null;
   
   /** The screen view. */
   private CheckBoxListView<BeanModel> screenView = null;
   
   /**
    * Instantiates a new group window.
    */
   public GroupWindow() {
      super();
      initial("New Group");
      show();
   }

   /**
    * Instantiates a new group window.
    * 
    * @param groupModel the group model
    */
   public GroupWindow(BeanModel groupModel) {
      super();
      this.groupModel = groupModel;
      initial("Edit Group");
      show();
   }

   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setSize(330, 270);
      setHeading(heading);
      setLayout(new FillLayout());
      setModal(true);
      createFields();
      setButtons(Dialog.OKCANCEL);
      setHideOnButtonClick(true);
      setBodyBorder(false);
      
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               Group group = new Group();
               if (groupModel == null) {
                  group.setOid(IDUtil.nextID());
                  Group.increaseDefaultNameIndex();
                  updateGroupAttrs(group);
                  BeanModelDataBase.groupTable.insert(group.getBeanModel());
               } else {
                  group = groupModel.getBean();
                  updateGroupAttrs(group);
                  BeanModelDataBase.groupTable.update(group.getBeanModel());
               }
               fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(group));
            }
         }
      }); 
   }

   /**
    * Creates the fields.
    */
   private void createFields() {
      FormPanel form = new FormPanel();
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      
      groupNameField = new TextField<String>();
      groupNameField.setName(GROUP_NAME);
      groupNameField.setFieldLabel("Name");
      groupNameField.setAllowBlank(false);
      groupNameField.setValue(Group.getNewDefaultName());
      AdapterField adapterField = new AdapterField(createScreenList());
      adapterField.setFieldLabel("Screens");
      if (groupModel != null) {
         Group group = groupModel.getBean();
         groupNameField.setValue(group.getName());
      }
      form.add(groupNameField);
      form.add(adapterField);
      form.setLabelWidth(60);
      add(form);
   }

   /**
    * Creates the screen list.
    * 
    * @return the content panel
    */
   private ContentPanel createScreenList() {
      ContentPanel screenContainer = new ContentPanel();
      screenContainer.setHeaderVisible(false);
      screenContainer.setWidth(210);
      screenContainer.setHeight(150);
      screenContainer.setLayout(new FitLayout());
      
      screenView = new CheckBoxListView<BeanModel>();
      
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      List<BeanModel> screenModels = BeanModelDataBase.screenTable.loadAll();
      for (BeanModel screenModel : screenModels) {
         store.add(screenModel);
      }
      screenView.setStore(store);
      screenView.setDisplayProperty("name");
      screenContainer.add(screenView);
      if (groupModel != null) {
         Group group = groupModel.getBean();
         for (ScreenRef screenRef : group.getScreenRefs()) {
            for (BeanModel beanModel : screenModels) {
               if (screenRef.getDisplayName().equals(beanModel.get("name"))) {
                  screenView.setChecked(beanModel, true);
               }
            }
         }
      }
      return screenContainer;
   }
   
   /**
    * Update group attrs.
    * 
    * @param group the group
    */
   private void updateGroupAttrs(Group group) {
      group.setName(groupNameField.getValue().toString());
      List<BeanModel> screenModels = screenView.getChecked();
      group.getScreenRefs().clear();
      if (screenModels.size() > 0) {
         for (BeanModel screenModel : screenModels) {
            ScreenRef screenRef = new ScreenRef((Screen)screenModel.getBean());
            screenRef.setGroup(group);
            group.addScreenRef(screenRef);
         }
      }
   }
}
