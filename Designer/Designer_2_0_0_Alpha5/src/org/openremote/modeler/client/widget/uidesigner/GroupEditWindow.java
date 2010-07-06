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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class GroupEditWindow extends FormWindow {

   private TextField<String> nameField = null;
   private CheckBoxListView<BeanModel> screenListView = null;
   private BeanModel groupRefBeanModel = null;
   public GroupEditWindow(BeanModel groupRefBeanModel) {
      this.groupRefBeanModel = groupRefBeanModel;
      setHeading("Edit Group");
      setSize(370, 260);
      createFields();
      createButtons();
      add(form);
      form.setLabelWidth(50);
      form.setFieldWidth(260);
      show();
   }
   
   private void createFields() {
      GroupRef groupRef = (GroupRef) groupRefBeanModel.getBean();
      
      nameField = new TextField<String>();
      nameField.setName("name");
      nameField.setFieldLabel("Name");
      nameField.setAllowBlank(false);
      if (groupRef.getGroup().getName() != null) {
         nameField.setValue(groupRef.getGroup().getName());
      }
      
      AdapterField screenField = new AdapterField(createScreenList(groupRef));
      screenField.setFieldLabel("Screen");
      form.add(nameField);
      form.add(screenField);
      
   }
   
   private ContentPanel createScreenList(GroupRef groupRef) {
      TouchPanelDefinition touchPanel = groupRef.getPanel().getTouchPanelDefinition();
      
      ContentPanel screenContainer = new ContentPanel();
      screenContainer.setHeaderVisible(false);
      screenContainer.setWidth(260);
      screenContainer.setHeight(150);
      screenContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      screenContainer.addStyleName("overflow-auto");
      
      screenListView = new CheckBoxListView<BeanModel>();
      ListStore<BeanModel> store = new ListStore<BeanModel>();
      
      List<BeanModel> otherModels = new ArrayList<BeanModel>();
      List<BeanModel> screenModels = BeanModelDataBase.screenTable.loadAll();
      List<BeanModel> selectedModels = new ArrayList<BeanModel>();
      for (ScreenRef screenRef: groupRef.getGroup().getScreenRefs()) {
         selectedModels.add(screenRef.getScreen().getBeanModel());
      }
      for (BeanModel screenModel : screenModels) {
         if (((Screen) screenModel.getBean()).getTouchPanelDefinition().equals(touchPanel)) {
            store.add(screenModel);
            screenListView.getSelectionModel().select(screenModel, true);
         } else if (((Screen) screenModel.getBean()).getTouchPanelDefinition().getCanvas().equals(touchPanel.getCanvas())){
            otherModels.add(screenModel);
         }
      }
      
      store.add(otherModels);
      for (BeanModel selectedModel : selectedModels) {
         screenListView.setChecked(selectedModel, true);
      }
      screenListView.setStore(store);
      screenListView.setDisplayProperty("panelName");
      screenListView.setStyleAttribute("overflow", "auto");
      screenListView.setSelectStyle("screen-view-item-sel");
      screenContainer.add(screenListView);
      return screenContainer;
   }
   
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");
      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));
      
      form.addButton(submitBtn);
      form.addButton(resetBtn);
      addBeforSubmitListener();
   }
   private void addBeforSubmitListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            Group group = ((GroupRef) groupRefBeanModel.getBean()).getGroup();
            TouchPanelDefinition touchPanelDefinition = ((GroupRef) groupRefBeanModel.getBean()).getPanel().getTouchPanelDefinition();
            for (ScreenRef screenRef : group.getScreenRefs()) {
               screenRef.getScreen().releaseRef();
            }
            group.getScreenRefs().clear();
            List<BeanModel> screenModels = screenListView.getChecked();
            if (screenModels.size() > 0) {
               for (BeanModel screenModel : screenModels) {
                  ScreenRef screenRef = new ScreenRef((Screen) screenModel.getBean());
                  screenRef.setTouchPanelDefinition(touchPanelDefinition);
                  screenRef.setGroup(group);
                  group.addScreenRef(screenRef);
               }
            }
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(groupRefBeanModel));
         }
      });
   }
}
