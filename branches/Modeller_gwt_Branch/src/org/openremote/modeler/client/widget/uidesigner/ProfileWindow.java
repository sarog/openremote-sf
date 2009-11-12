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
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;

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

public class ProfileWindow extends Dialog {

   private static final String PANEL_NAME = "panelName";
   private BeanModel panelModel = null;
   private TextField<String> panelNameField = null;
   private CheckBoxListView<BeanModel> groupView = null;
   
   /**
    * Create profile.
    */
   public ProfileWindow() {
      super();
      initial("New Panel");
      show();
   }

   /**
    * Edit profile.
    * 
    */
   public ProfileWindow(BeanModel panelModel) {
      super();
      this.panelModel = panelModel;
      initial("Edit Panel");
      show();
   }

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
               Panel panel = new Panel();
               if (panelModel == null) {
                  panel.setOid(IDUtil.nextID());
                  Panel.increaseDefaultNameIndex();
               } else {
                  panel = panelModel.getBean();
               }
               updatePanelAttrs(panel);
               panelModel = panel.getBeanModel();
               BeanModelDataBase.panelTable.insert(panelModel);
               fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(panel));
            }
         }
      }); 
   }

   private void createFields() {
      FormPanel form = new FormPanel();
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      
      panelNameField = new TextField<String>();
      panelNameField.setName(PANEL_NAME);
      panelNameField.setFieldLabel("Name");
      panelNameField.setAllowBlank(false);
      panelNameField.setValue(Panel.getNewDefaultName());
      AdapterField adapterField = new AdapterField(createGroupList());
      adapterField.setFieldLabel("Groups");
      if (panelModel != null) {
         Panel panel = panelModel.getBean();
         panelNameField.setValue(panel.getName());
      }
      form.add(panelNameField);
      form.add(adapterField);
      form.setLabelWidth(60);
      add(form);
   }

   private ContentPanel createGroupList() {
      ContentPanel groupContainer = new ContentPanel();
      groupContainer.setHeaderVisible(false);
      groupContainer.setWidth(210);
      groupContainer.setHeight(150);
      groupContainer.setLayout(new FitLayout());
      
      groupView = new CheckBoxListView<BeanModel>();
      
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      List<BeanModel> groupModels = BeanModelDataBase.groupTable.loadAll();
      for (BeanModel groupModel : groupModels) {
         store.add(groupModel);
      }
      groupView.setStore(store);
      groupView.setDisplayProperty("name");
      groupContainer.add(groupView);
      if (panelModel != null) {
         Panel panel = panelModel.getBean();
         for (GroupRef groupRef : panel.getGroupRefs()) {
            for (BeanModel beanModel : groupModels) {
               if (groupRef.getDisplayName().equals(beanModel.get("name"))) {
                  groupView.setChecked(beanModel, true);
               }
            }
         }
      }
      return groupContainer;
   }
   
   private void updatePanelAttrs(Panel panel) {
      panel.setName(panelNameField.getValue().toString());
      List<BeanModel> groupModels = groupView.getChecked();
      panel.getGroupRefs().clear();
      if (groupModels.size() > 0) {
         for (BeanModel groupModel : groupModels) {
            GroupRef groupRef = new GroupRef((Group)groupModel.getBean());
            groupRef.setPanel(panel);
            panel.addGroupRef(groupRef);
         }
      }
   }

}
