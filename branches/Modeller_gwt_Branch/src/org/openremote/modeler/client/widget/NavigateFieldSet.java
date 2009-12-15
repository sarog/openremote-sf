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
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * The NavigateFieldSet is for edit navigate.
 */
public class NavigateFieldSet extends FieldSet {
   private Navigate navigate;
   private List<BeanModel> groupModels;
   private Radio toLogical = new Radio();
   private Radio toGroup = new Radio();
   private SimpleComboBox typeList = new SimpleComboBox();
   private SimpleComboBox groupList = new SimpleComboBox();
   private SimpleComboBox screenList = new SimpleComboBox();
   public NavigateFieldSet(Navigate navigate, List<BeanModel> groupModels) {
      this.navigate = navigate;
      this.groupModels = groupModels;
      init();
      createLeftRadios();
      createRightComboBoxes();
   }
   private void init() {
      setLayout(new ColumnLayout());
      setHeading("Navigate");
   }
   
   private void createLeftRadios() {
      RadioGroup navigateGroup = new RadioGroup();
      navigateGroup.setOrientation(Orientation.VERTICAL);
      
      toLogical.setBoxLabel("ToLogical");
      toGroup.setBoxLabel("ToGroup");
      toGroup.addListener(Events.Change, new Listener<FieldEvent>() {
       @Override
       public void handleEvent(FieldEvent be) {
          Boolean value = (Boolean) be.getValue();
          if (value) {
             groupList.enable();
             screenList.enable();
             navigate.clearToLogical();
             typeList.clearSelections();
             typeList.disable();
          } else {
             navigate.setToGroup(-1);
             navigate.setToScreen(-1);
             screenList.clearSelections();
             groupList.clearSelections();
             groupList.disable();
             screenList.disable();
             typeList.enable();
          }
       }
       
    });
         
      Radio toScreen = new Radio();
      toScreen.setBoxLabel("ToScreen");
      toScreen.disable();
      
      navigateGroup.add(toLogical);
      navigateGroup.add(toGroup);
      navigateGroup.add(toScreen);
      
      add(navigateGroup);
   }
   
   private void createRightComboBoxes() {
      LayoutContainer rightComboBoxes = new LayoutContainer();
      FormLayout layout = new FormLayout();
      layout.setHideLabels(true);
      layout.setDefaultWidth(110);
      rightComboBoxes.setLayout(layout);
      rightComboBoxes.setLayoutOnChange(true);

      
      typeList.setEmptyText("--type--");
      ToLogicalType[] toTypes = ToLogicalType.values();
      for (int i = 0; i < toTypes.length; i++) {
         ComboBoxDataModel<ToLogicalType> toTypeItem = new ComboBoxDataModel<ToLogicalType>(toTypes[i].toString().toLowerCase(),
               toTypes[i]);
         typeList.getStore().add(toTypeItem);
      }
      
      groupList.setEmptyText("--group--");
      groupList.disable();
      for (BeanModel groupModel : groupModels) {
         ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(groupModel.get("name").toString(), (Group) groupModel.getBean());
         groupList.getStore().add(data);
      }
      screenList.setEmptyText("--screen--");
      screenList.disable();
      
      rightComboBoxes.add(typeList);
      rightComboBoxes.add(groupList);
      rightComboBoxes.add(screenList);
      
      typeList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         @SuppressWarnings("unchecked")
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            navigate.setToLogical(((ComboBoxDataModel<ToLogicalType>) se.getSelectedItem()).getData());
         }
      });
      
      groupList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            updateScreenList(se);
         }
      });
      
      screenList.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
       @SuppressWarnings("unchecked")
       public void selectionChanged(SelectionChangedEvent<ModelData> se) {
          Screen selectedScreen = ((ComboBoxDataModel<Screen>) se.getSelectedItem()).getData();
          navigate.setToScreen(selectedScreen.getOid());
       }
       
    });
      add(rightComboBoxes);
   }
   
   @SuppressWarnings("unchecked")
   private void updateScreenList(SelectionChangedEvent<ModelData> se) {
      Group selectedGroup = ((ComboBoxDataModel<Group>) se.getSelectedItem()).getData();
      navigate.setToGroup(selectedGroup.getOid());
      screenList.clearSelections();
      screenList.getStore().removeAll();
      for (ScreenRef screenRef : selectedGroup.getScreenRefs()) {
         ComboBoxDataModel<Screen> data = new ComboBoxDataModel<Screen>(screenRef.getDisplayName(), screenRef.getScreen());
         screenList.getStore().add(data);
         if (navigate.getToScreen() == screenRef.getScreenId()) {
            screenList.setValue(data);
         }
      }
      if (screenList.getValue() == null) {
         navigate.setToScreen(-1);
      }
   }

   public void update(Navigate navigate) {
      this.navigate = navigate;
      if (navigate.isToLogic()) {
         toLogical.setValue(true);
         typeList.setValue(new ComboBoxDataModel<ToLogicalType>(navigate.getToLogical().toString().toLowerCase(), navigate.getToLogical()));
      } else if (navigate.isToGroup()) {
         toGroup.setValue(true);
         for (BeanModel groupModel : groupModels) {
            ComboBoxDataModel<Group> data = new ComboBoxDataModel<Group>(groupModel.get("name").toString(), (Group) groupModel.getBean());
            if (navigate.getToGroup() == ((Group) groupModel.getBean()).getOid()) {
               groupList.setValue(data);
          }
         }
      } else {
         toLogical.setValue(false);
         toGroup.setValue(false);
      }
   }
   private class SimpleComboBox extends ComboBox<ModelData> {
      public SimpleComboBox() {
         setStore(new ListStore<ModelData>());
         setDisplayField(ComboBoxDataModel.getDisplayProperty());
         setValueField(ComboBoxDataModel.getDataProperty());
      }
   }
}
