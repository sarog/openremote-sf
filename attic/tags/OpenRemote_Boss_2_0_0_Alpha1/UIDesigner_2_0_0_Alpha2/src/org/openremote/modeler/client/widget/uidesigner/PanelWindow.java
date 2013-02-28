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
import java.util.Map;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.ImageUploadField;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class PanelWindow extends FormWindow {

   private static final String PANEL_NAME = "panelName";
   private BeanModel panelModel = null;
   private TextField<String> panelNameField = null;
   private CheckBox createScreen = new CheckBox();
   private CheckBox customType = new CheckBox();
   private ComboBox<ModelData> predefinedPanel = null;
   private TextField<Integer> panelWidthField = null;
   private TextField<Integer> panelHeightField = null;
   private TextField<Integer> panelPaddingLeftField = null;
   private TextField<Integer> panelPaddingTopField = null;
   private ImageUploadField panelImage = null;
   private TextField<Integer> screenWidthField = null;
   private TextField<Integer> screenHeightField = null;
   /**
    * Create profile.
    */
   public PanelWindow() {
      super();
      initial("New Panel");
      show();
   }

   /**
    * Edit profile.
    * 
    */
   public PanelWindow(BeanModel panelModel) {
      super();
      this.panelModel = panelModel;
      initial("Edit Panel");
      show();
   }

   private void initial(String heading) {
      setWidth(380);
      setAutoHeight(true);
      setHeading(heading);
      setLayout(new FlowLayout());
      createFields();
      createButtons();
      addListenersToForm();
   }

   private void createFields() {
      form.setFrame(true);
      form.setHeaderVisible(false);
      form.setBorders(false);
      
      panelNameField = new TextField<String>();
      panelNameField.setName(PANEL_NAME);
      panelNameField.setFieldLabel("Name");
      panelNameField.setAllowBlank(false);
      panelNameField.setValue(Panel.getNewDefaultName());
      if (panelModel != null) {
         Panel panel = panelModel.getBean();
         panelNameField.setValue(panel.getName());
      }
      
      createScreen.setBoxLabel("Create a new screen");
      createScreen.setHideLabel(true);
      createScreen.setValue(true);
      
      form.add(panelNameField);
      if (panelModel == null) {
         form.add(createTypeField());
         form.add(createScreen);
      }
      form.setLabelWidth(60);
      add(form);
   }

   private FieldSet createTypeField() {
      Map<String, List<TouchPanelDefinition>> predefinedPanels = TouchPanels.getInstance();
      FieldSet typeSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(105);
      layout.setDefaultWidth(180);
      typeSet.setLayout(layout);
      typeSet.setHeading("Panel type");
      
      predefinedPanel = new ComboBox<ModelData>();
      ListStore<ModelData> store = new ListStore<ModelData>();
      predefinedPanel.setStore(store);
      predefinedPanel.setFieldLabel("Predefined");
      predefinedPanel.setName("predefine");
      predefinedPanel.setAllowBlank(false);
      ComboBoxDataModel<TouchPanelDefinition> iphoneData = null;
      for (String key : predefinedPanels.keySet()) {
         for (TouchPanelDefinition touchPanel : predefinedPanels.get(key)) {
            ComboBoxDataModel<TouchPanelDefinition> data = new ComboBoxDataModel<TouchPanelDefinition>(touchPanel
                  .getName(), touchPanel);
            if ("iPhone".equals(touchPanel.getName())) {
               iphoneData = data;
            }
            store.add(data);
         }
      }
      predefinedPanel.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      predefinedPanel.setEmptyText("Please Select Panel...");
      predefinedPanel.setValueField(ComboBoxDataModel.getDataProperty());
      predefinedPanel.setValue(iphoneData); // temp select iphone panel.
      
      customType.setBoxLabel("Custom");
      customType.setHideLabel(true);
      
      panelWidthField = new TextField<Integer>();
      panelWidthField.setName("panelWidth");
      panelWidthField.setFieldLabel("Panel width");
      panelWidthField.setRegex(Constants.REG_POSITIVEINT);
      panelWidthField.getMessages().setRegexText("The panel width must be a positive integer");
      
      panelHeightField = new TextField<Integer>();
      panelHeightField.setName("panelHeight");
      panelHeightField.setFieldLabel("Panel height");
      panelHeightField.setRegex(Constants.REG_POSITIVEINT);
      panelHeightField.getMessages().setRegexText("The panel height must be a positive integer");
      
      panelPaddingLeftField = new TextField<Integer>();
      panelPaddingLeftField.setName("panelPaddingLeft");
      panelPaddingLeftField.setFieldLabel("Panel padding left");
      panelPaddingLeftField.setRegex(Constants.REG_NONNEGATIVEINT);
      panelPaddingLeftField.getMessages().setRegexText("The padding left must be a nonnegative integer");
      
      panelPaddingTopField = new TextField<Integer>();
      panelPaddingTopField.setName("panelPaddingTop");
      panelPaddingTopField.setFieldLabel("Panel padding top");
      panelPaddingTopField.setRegex(Constants.REG_NONNEGATIVEINT);
      panelPaddingTopField.getMessages().setRegexText("The padding top must be a nonnegative integer");
      
      panelImage = new ImageUploadField(null);
      panelImage.setActionToForm(form);
      
      screenWidthField = new TextField<Integer>();
      screenWidthField.setName("screenWidth");
      screenWidthField.setFieldLabel("Screen width");
      screenWidthField.setRegex(Constants.REG_POSITIVEINT);
      screenWidthField.getMessages().setRegexText("The screen width must be a positive integer");
      
      screenHeightField = new TextField<Integer>();
      screenHeightField.setName("screenHeight");
      screenHeightField.setFieldLabel("Screen height");
      screenHeightField.setRegex(Constants.REG_POSITIVEINT);
      screenHeightField.getMessages().setRegexText("The screen height must be a positive integer");
      
      initIntegerFieldStyle(panelWidthField, panelHeightField, panelPaddingLeftField, panelPaddingTopField, panelImage,
            screenWidthField, screenHeightField);
      customType.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            if ("true".equals(be.getValue().toString())) {
               predefinedPanel.disable();
               enableCustomFields(true, panelWidthField, panelHeightField, panelPaddingLeftField,
                     panelPaddingTopField, panelImage, screenWidthField, screenHeightField);
            } else if ("false".equals(be.getValue().toString())) {
               predefinedPanel.enable();
               enableCustomFields(false, panelWidthField, panelHeightField, panelPaddingLeftField,
                     panelPaddingTopField, panelImage, screenWidthField, screenHeightField);
            }
         }
         
      });
      
      typeSet.add(predefinedPanel);
      typeSet.add(customType);
      typeSet.add(panelWidthField);
      typeSet.add(panelHeightField);
      typeSet.add(panelPaddingLeftField);
      typeSet.add(panelPaddingTopField);
      typeSet.add(panelImage);
      typeSet.add(screenWidthField);
      typeSet.add(screenHeightField);
      
      return typeSet;
   }
   
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");

      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }

   private void addListenersToForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         @Override
         public void handleEvent(FormEvent be) {
            Panel panel = new Panel();
            if (panelModel == null) {
               panel.setOid(IDUtil.nextID());
               Panel.increaseDefaultNameIndex();
               if (customType.getValue()) {
                  String panelImageURL = be.getResultHtml();
                  TouchPanelDefinition customPanel = new TouchPanelDefinition();
                  customPanel.setCanvas(new TouchPanelCanvasDefinition(Integer.valueOf(screenWidthField.getRawValue()),
                        Integer.valueOf(screenHeightField.getRawValue())));
                  customPanel.setType(Constants.CUSTOM_PANEL);
                  customPanel.setName(Constants.CUSTOM_PANEL);
                  customPanel.setBgImage(panelImageURL);
                  customPanel.setWidth(Integer.valueOf(panelWidthField.getRawValue()));
                  customPanel.setHeight(Integer.valueOf(panelHeightField.getRawValue()));
                  customPanel.setPaddingLeft(Integer.valueOf(panelPaddingLeftField.getRawValue()));
                  customPanel.setPaddingTop(Integer.valueOf(panelPaddingTopField.getRawValue()));
                  panel.setTouchPanelDefinition(customPanel);
               } else {
                  ComboBoxDataModel<TouchPanelDefinition> prededinedPanel = (ComboBoxDataModel<TouchPanelDefinition>) predefinedPanel
                        .getValue();
                  panel.setTouchPanelDefinition(prededinedPanel.getData());
               }
               Group defaultGroup = new Group();
               defaultGroup.setOid(IDUtil.nextID());
               defaultGroup.setName(Constants.DEFAULT_GROUP);
               GroupRef groupRef = new GroupRef(defaultGroup);
               panel.addGroupRef(groupRef);
               groupRef.setPanel(panel);
               if (createScreen.getValue()) {
                  Screen defaultScreen = new Screen();
                  defaultScreen.setOid(IDUtil.nextID());
                  defaultScreen.setName(Constants.DEFAULT_SCREEN);
                  defaultScreen.setTouchPanelDefinition(panel.getTouchPanelDefinition());
                  ScreenRef screenRef = new ScreenRef(defaultScreen);
                  screenRef.setGroup(defaultGroup);
                  defaultGroup.addScreenRef(screenRef);
                  BeanModelDataBase.screenTable.insert(defaultScreen.getBeanModel());
               }
               BeanModelDataBase.groupTable.insert(defaultGroup.getBeanModel());
            } else {
               panel = panelModel.getBean();
            }
            panel.setName(panelNameField.getValue());
            BeanModelDataBase.panelTable.insert(panel.getBeanModel());
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(panel));
         }
      });
   }
   
   private void initIntegerFieldStyle(TextField<?>... fields) {
      for (TextField<?> field : fields) {
         field.setLabelStyle("text-align:right;");
         field.setAllowBlank(false);
         field.disable();
      }
   }
   
   private void enableCustomFields(boolean enable, TextField<?>... fields){
      for (TextField<?> field : fields) {
         field.setEnabled(enable);
      }
   }
}
