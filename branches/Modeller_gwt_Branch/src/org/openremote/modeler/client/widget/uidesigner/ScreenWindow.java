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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.FormResetListener;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.proxy.ScreenBeanModelProxy;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Grid;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * The Class ScreenWindow.
 */
public class ScreenWindow extends FormWindow {

   /** The Constant SCREEN_NAME. */
   public static final String SCREEN_NAME = "screenName";
   
   public static final String SCREEN_PANEL = "panel";

   public static final String SCREEN_BACKGROUND = "background";
   
   public static final String SCREEN_RADIOLAYOUTGROUP = "layout";
   
   public static final String SCREEN_GRIDRADIO = "grid";

   public static final String SCREEN_ABSOLUTERADIO = "absolute";
   
   private String layout = SCREEN_ABSOLUTERADIO;
   
   /** The screen model. */
   private UIScreen screen = null;
   
   /**
    * Instantiates a new screen window.
    * 
    * @param activity the activity
    */
   public ScreenWindow() {
      super();
      initial("New Screen");
      show();
   }

   /**
    * Instantiates a new screen window.
    * 
    * @param screen the screen
    */
   public ScreenWindow(UIScreen screen) {
      super();
      this.screen = screen;
      initial("Edit Screen");
      show();
   }

   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setHeading(heading);
      setWidth(380);
      setAutoHeight(true);
      setLayout(new FlowLayout());
      createFields();
      createButtons();
      addListenersToForm();
   }

   /**
    * Creates the fields.
    */
   private void createFields() {
      Map<String, List<TouchPanelDefinition>> panels = TouchPanels.getInstance();
      TextField<String> screenNameField = new TextField<String>();
      screenNameField.setName(SCREEN_NAME);
      screenNameField.setFieldLabel("Name");
      screenNameField.setAllowBlank(false);
      screenNameField.setValue(UIScreen.getNewDefaultName());
      
      ComboBox<ModelData> panel = new ComboBox<ModelData>();
      ListStore<ModelData> store = new ListStore<ModelData>();
      panel.setStore(store);
      panel.setFieldLabel("Panel");
      panel.setName(SCREEN_PANEL);
      panel.setAllowBlank(false);
      for (String key : panels.keySet()) {
         for (TouchPanelDefinition touchPanel : panels.get(key)) {
            ComboBoxDataModel<TouchPanelDefinition> data = new ComboBoxDataModel<TouchPanelDefinition>(touchPanel.getName(), touchPanel);
            store.add(data);
         }
      }
      panel.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      panel.setEmptyText("Please Select Panel...");
      panel.setValueField(ComboBoxDataModel.getDataProperty());
      
      FileUploadField background = new FileUploadField();
      background.setFieldLabel("Background");
      background.setName(SCREEN_BACKGROUND);
      background.setRegex(".+?\\.(png|gif|jpg)");
      background.getMessages().setRegexText("Please select a gif, jpg or png type image.");
      background.setStyleAttribute("overflow", "hidden");
      
      Radio gridLayout = new Radio();
      gridLayout.setName(SCREEN_RADIOLAYOUTGROUP);
      gridLayout.setBoxLabel("Grid");
      gridLayout.setValueAttribute(SCREEN_GRIDRADIO);

      Radio absoluteLayout = new Radio();
      absoluteLayout.setName(SCREEN_RADIOLAYOUTGROUP);
      absoluteLayout.setBoxLabel("Absolute");
      absoluteLayout.setValueAttribute(SCREEN_ABSOLUTERADIO);
      absoluteLayout.setValue(true);
      
      final RadioGroup layoutGroup = new RadioGroup();
      layoutGroup.setId(SCREEN_RADIOLAYOUTGROUP);
      layoutGroup.setFieldLabel("Layout");
      layoutGroup.add(gridLayout);
      layoutGroup.add(absoluteLayout);
      layoutGroup.addListener(Events.Change, new Listener<FieldEvent>(){
         @Override
         public void handleEvent(FieldEvent be) {
            String value = layoutGroup.getValue().getValueAttribute();
            if(SCREEN_GRIDRADIO.equals(value)) {
               layout = SCREEN_GRIDRADIO;
               addGridAttrs();
            } else if (SCREEN_ABSOLUTERADIO.equals(value)) {
               layout = SCREEN_ABSOLUTERADIO;
               if (form.getItems().size() > 4) {
                  form.getItem(4).removeFromParent();
               }
            }
         }
         
      });
           
      form.add(screenNameField);
      form.add(panel);
      form.add(background);
      form.add(layoutGroup);
      
      if (screen != null) {
         TouchPanelDefinition touchPanelDefinition = screen.getTouchPanelDefinition();
         screenNameField.setValue(screen.getLabel());
         ComboBoxDataModel<TouchPanelDefinition> data = new ComboBoxDataModel<TouchPanelDefinition>(touchPanelDefinition
               .getName(), touchPanelDefinition);
         panel.setValue(data);
         panel.disable();
         if (screen.getBackground() != null) {
            background.setValue(screen.getBackground());
         }
         if (!screen.isAbsoluteLayout()) {
            gridLayout.setValue(true);
         }
         layoutGroup.disable();
      }
   }

   private void addGridAttrs() {
      FieldSet gridAttrSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      gridAttrSet.setLayout(layout);
      gridAttrSet.setHeading("Grid attributes");

      TextField<Integer> gridRowCountField = new TextField<Integer>();
      gridRowCountField.setName("gridRow");
      gridRowCountField.setFieldLabel("Row Count");
      gridRowCountField.setAllowBlank(false);
      
      TextField<Integer> gridColumnCountField = new TextField<Integer>();
      gridColumnCountField.setName("gridColumn");
      gridColumnCountField.setFieldLabel("Col Count");
      gridColumnCountField.setAllowBlank(false);
      
      if (screen != null) {
         Grid grid = screen.getGrid();
         gridRowCountField.setValue(grid.getRowCount());
         gridColumnCountField.setValue(grid.getColumnCount());
      }
      gridAttrSet.add(gridRowCountField);
      gridAttrSet.add(gridColumnCountField);
      form.add(gridAttrSet);
      form.layout();
   }
   
   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      Button resetBtn = new Button("Reset");

      submitBtn.addSelectionListener(new FormSubmitListener(form));
      resetBtn.addSelectionListener(new FormResetListener(form));

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }

   /**
    * Adds the listeners to form.
    */
   private void addListenersToForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            ComboBoxDataModel<TouchPanelDefinition> panelData = null;
            for (Field<?> field : list) {
               if(SCREEN_PANEL.equals(field.getName())){
                  panelData = (ComboBoxDataModel<TouchPanelDefinition>)field.getValue();
               }
            }
            
            Map<String, String> attrMap = getAttrMap();
            BeanModel screenBeanModel = null;
            if (screen == null) {
               screenBeanModel = ScreenBeanModelProxy.createScreen(attrMap, panelData.getData());
            } else {
               screenBeanModel = ScreenBeanModelProxy.updateScreen(screen, attrMap);
            }
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenBeanModel));
         }
      });
      add(form);
   }

   @Override
   public Map<String, String> getAttrMap() {
      List<Field<?>> list = form.getFields();
      Map<String, String> attrMap = new HashMap<String, String>();
      for (Field<?> f : list) {
         if (SCREEN_RADIOLAYOUTGROUP.equals(f.getId())) {
            attrMap.put(SCREEN_RADIOLAYOUTGROUP, layout);
         } else {
            if (f.getValue() != null) {
               attrMap.put(f.getName(), f.getValue().toString());
            }
         }
      }
      return attrMap;
   }
   
   
}
