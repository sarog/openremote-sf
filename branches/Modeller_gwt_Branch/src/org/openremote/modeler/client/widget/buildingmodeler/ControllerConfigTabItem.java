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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.Set;

import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.ControllerConfigBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.domain.Config;
import org.openremote.modeler.domain.ConfigCategory;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
/**
 * A tab item for configuring the controller under a specific category. 
 * @author javen
 *
 */
public class ControllerConfigTabItem extends TabItem {

   private ConfigCategory category;
   private Set<Config> configs = null;
   private TextArea hintArea = new TextArea();
   private FormPanel configContainer = new FormPanel();
   private FieldSet hintFieldSet = new FieldSet();
   
   public ControllerConfigTabItem(ConfigCategory category){
      this.category = category;
      this.setHeight(500);
      this.setScrollMode(Scroll.AUTO);
      
      setText(category.getName());
      setClosable(true);
      setLayout(new FitLayout());  
      
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(150);
      layout.setDefaultWidth(400);
      
      configContainer.setLayout(layout);
      configContainer.setAutoHeight(true);
      configContainer.setScrollMode(Scroll.AUTOY);
      configContainer.setButtonAlign(HorizontalAlignment.RIGHT);
      configContainer.setBorders(false);
      configContainer.setBodyBorder(false);
      configContainer.setLabelWidth(55);
      configContainer.setPadding(5);
      configContainer.setHeaderVisible(false);
      configContainer.setWidth(500);
      configContainer.setHeight(300);
      Button submitBtn =   new Button("Submit");
      submitBtn.addSelectionListener(new SaveListener());
      
      configContainer.addButton(submitBtn);
      add(configContainer);
      
      hintFieldSet.setLayout(new FitLayout());
      hintFieldSet.setHeading("Hint");
      
      
      hintArea.setValue(category.getDescription());
      hintArea.setWidth("100%");
      hintArea.setHeight("34%");
      hintArea.setEnabled(false);
      setStyleAttribute("overflowY", "auto");
      initForm();
   }
   
   
   private void initForm() {
      if (configs == null) {
         ControllerConfigBeanModelProxy.getConfigs(category, new AsyncSuccessCallback<Set<Config>>() {

            @Override
            public void onSuccess(Set<Config> result) {
               configs = result;
               for (Config config : configs) {
                  createProperty(config);
               }
               hintFieldSet.add(hintArea);
               configContainer.add(hintFieldSet);
               layout();
            }
         });
      }
   }


   public ConfigCategory getCategory() {
      return category;
   }


   public void setCategory(ConfigCategory category) {
      this.category = category;
   }
   
   private void createProperty(Config config) {
      if (config.getOptions().trim().length() == 0) {
         TextField<String> configValueField = new TextField<String>();
         configValueField.setFieldLabel(config.getName());
         configValueField.setName(config.getName());
         configValueField.setValue(config.getValue());
         configValueField.setRegex(config.getValidation());
         configValueField.getMessages().setRegexText("This property must match: " + config.getValidation());
         addUpdateListenerToTextField(config,configValueField);
         configContainer.add(configValueField);
      } else {
         final ComboBox<ModelData> optionComboBox = new ComboBox<ModelData>();
         ListStore<ModelData> store = new ListStore<ModelData>();
         String[] options = config.optionsArray();
         for (int i = 0; i < options.length; i++) {
            ComboBoxDataModel<String> option = new ComboBoxDataModel<String>(options[i], options[i]);
            store.add(option);
         }
         optionComboBox.setValue(new ComboBoxDataModel<String>(config.getValue(),config.getValue()));
         optionComboBox.setStore(store);
         optionComboBox.setDisplayField(ComboBoxDataModel.getDisplayProperty());
         optionComboBox.setFieldLabel("options");
         optionComboBox.setName(config.getName() + "Options");
         optionComboBox.setAllowBlank(false);
         addUpdateListenerToComboBox(config,optionComboBox);
         configContainer.add(optionComboBox);
      }
   }
   
   private void addUpdateListenerToTextField(final Config config,final TextField<String> configValueField){
      configValueField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            if(configValueField.isValid()){
               config.setValue(configValueField.getValue());
               hintArea.setValue(category.getDescription());
            }
         }
      });
      configValueField.addListener(Events.Focus, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            hintArea.setValue(config.getHint());
         }
      });
   }
   
   private void addUpdateListenerToComboBox(final Config config,final ComboBox<ModelData> configValueComboBox){
      configValueComboBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            ComboBoxDataModel<String> optionItem = (ComboBoxDataModel<String>) se.getSelectedItem();;
            String option = optionItem.getData();
            config.setValue(option);
         }
         
      });
      configValueComboBox.addListener(Events.Focus, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            hintArea.setValue(config.getHint());
         }
      });
      
      configValueComboBox.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            hintArea.setValue(category.getDescription());
         }
      });
   }
   
 class SaveListener extends SelectionListener<ButtonEvent>{
   @Override
   public void componentSelected(ButtonEvent ce) {
      ControllerConfigBeanModelProxy.saveAllConfigs(configs, new AsyncSuccessCallback<Set<Config>>(){

         @Override
         public void onSuccess(Set<Config> result) {
            configs = result;
            Info.display("save", "Property saved successfully");
         }
         
      });
   }
    
 }
}
