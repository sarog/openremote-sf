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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.client.widget.ImageUploadField;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class CustomPanelWindow extends FormWindow {

   private static final String PANEL_NAME = "panelName";
   private Panel panel = null;
   private TextField<String> panelNameField = null;
   private TextField<Integer> screenWidthField = null;
   private TextField<Integer> screenHeightField = null;
   private ImageUploadField panelImage = null;
   private TextField<Integer> panelPaddingLeftField = null;
   private TextField<Integer> panelPaddingTopField = null;
   /**
    * Create profile.
    */
   public CustomPanelWindow() {
      super();
      initial("New Custom Panel");
      show();
   }

   public CustomPanelWindow(Panel panel) {
      super();
      this.panel = panel;
      initial("Edit Custom Panel");
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
      if (panel != null) {
         panelNameField.setValue(panel.getName());
      } else {
         panelNameField.setValue(Panel.getNewDefaultName());
      }
      
      form.add(panelNameField);
      form.add(createTypeField());
      form.setLabelWidth(60);
      add(form);
   }

   private FieldSet createTypeField() {
      FieldSet typeSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(105);
      layout.setDefaultWidth(180);
      typeSet.setLayout(layout);
      typeSet.setHeading("Panel type");
      
      screenWidthField = new TextField<Integer>();
      screenWidthField.setName("screenWidth");
      screenWidthField.setFieldLabel("Screen width");
      screenWidthField.setAllowBlank(false);
      screenWidthField.setRegex(Constants.REG_POSITIVEINT);
      screenWidthField.getMessages().setRegexText("The screen width must be a positive integer");
      
      screenHeightField = new TextField<Integer>();
      screenHeightField.setName("screenHeight");
      screenHeightField.setFieldLabel("Screen height");
      screenHeightField.setAllowBlank(false);
      screenHeightField.setRegex(Constants.REG_POSITIVEINT);
      screenHeightField.getMessages().setRegexText("The screen height must be a positive integer");
      
      panelImage = new ImageUploadField("panelImage");
      panelImage.setActionToForm(form);
      
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
      
      if (panel != null) {
         TouchPanelDefinition touchPanelDefinition = panel.getTouchPanelDefinition();
         screenWidthField.setValue(touchPanelDefinition.getCanvas().getWidth());
         screenHeightField.setValue(touchPanelDefinition.getCanvas().getHeight());
         panelPaddingLeftField.setValue(touchPanelDefinition.getPaddingLeft());
         panelPaddingTopField.setValue(touchPanelDefinition.getPaddingTop());
         panelImage.setValue(touchPanelDefinition.getBgImage());
      }
      initIntegerFieldStyle(panelPaddingLeftField, panelPaddingTopField, panelImage,
            screenWidthField, screenHeightField);
      
      typeSet.add(screenWidthField);
      typeSet.add(screenHeightField);
      typeSet.add(panelImage);
      typeSet.add(panelPaddingLeftField);
      typeSet.add(panelPaddingTopField);
      
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
      form.addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String panelImageURL = be.getResultHtml();
            TouchPanelDefinition customPanel;
            if (panel == null) {
               panel = new Panel();
               panel.setOid(IDUtil.nextID());
               Panel.increaseDefaultNameIndex();
               customPanel = new TouchPanelDefinition();
               customPanel.setCanvas(new TouchPanelCanvasDefinition(Integer.valueOf(screenWidthField.getRawValue()),
                     Integer.valueOf(screenHeightField.getRawValue())));
               customPanel.setType(Constants.CUSTOM_PANEL);
               customPanel.setName(Constants.CUSTOM_PANEL);
               initCustomPanelDefinition(panelImageURL, customPanel);
               panel.setTouchPanelDefinition(customPanel);
               
               Group defaultGroup = new Group();
               defaultGroup.setParentPanel(panel);
               defaultGroup.setOid(IDUtil.nextID());
               defaultGroup.setName(Constants.DEFAULT_GROUP);
               GroupRef groupRef = new GroupRef(defaultGroup);
               panel.addGroupRef(groupRef);
               groupRef.setPanel(panel);
               
               Screen defaultScreen = new Screen();
               defaultScreen.setOid(IDUtil.nextID());
               defaultScreen.setName(Constants.DEFAULT_SCREEN);
               defaultScreen.setTouchPanelDefinition(panel.getTouchPanelDefinition());
               
               ScreenPair screenPair = new ScreenPair();
               screenPair.setOid(IDUtil.nextID());
               screenPair.setTouchPanelDefinition(panel.getTouchPanelDefinition());
               screenPair.setPortraitScreen(defaultScreen);
               screenPair.setParentGroup(defaultGroup);
               
               ScreenPairRef screenRef = new ScreenPairRef(screenPair);
               screenRef.setTouchPanelDefinition(panel.getTouchPanelDefinition());
               screenRef.setGroup(defaultGroup);
               defaultGroup.addScreenRef(screenRef);
               BeanModelDataBase.screenTable.insert(screenPair.getBeanModel());
               BeanModelDataBase.groupTable.insert(defaultGroup.getBeanModel());
               
            } else {
               customPanel = panel.getTouchPanelDefinition();
               customPanel.getCanvas().setWidth(Integer.valueOf(screenWidthField.getRawValue()));
               customPanel.getCanvas().setHeight(Integer.valueOf(screenHeightField.getRawValue()));
               initCustomPanelDefinition(panelImageURL, customPanel);
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
      }
   }

   /**
    * @param panelImageURL
    * @param customPanel
    */
   private void initCustomPanelDefinition(String panelImageURL, TouchPanelDefinition customPanel) {
      if (panelImageURL != null && !"".equals(panelImageURL)) {
         JSONObject jsonObj = JSONParser.parse(panelImageURL).isObject();
         customPanel.setBgImage(jsonObj.get("name").isString().stringValue());
         customPanel.setWidth(Integer.valueOf(jsonObj.get("width").toString()));
         customPanel.setHeight(Integer.valueOf(jsonObj.get("height").toString()));
      }
      if (!"".equals(panelPaddingLeftField.getRawValue())) {
         customPanel.setPaddingLeft(Integer.valueOf(panelPaddingLeftField.getRawValue()));
      }
      if (!"".equals(panelPaddingTopField.getRawValue())) {
         customPanel.setPaddingTop(Integer.valueOf(panelPaddingTopField.getRawValue()));
      }
   }
   
}
