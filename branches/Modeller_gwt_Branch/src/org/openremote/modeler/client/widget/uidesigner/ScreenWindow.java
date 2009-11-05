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
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;
import org.openremote.modeler.touchpanel.TouchPanelCanvasDefinition;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;

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
      screenNameField.ensureDebugId(DebugId.SCREEN_NAME_FIELD);
      screenNameField.setName(SCREEN_NAME);
      screenNameField.setFieldLabel("Name");
      screenNameField.setAllowBlank(false);
      screenNameField.setValue(UIScreen.getNewDefaultName());
      
      ComboBox<ModelData> panel = new ComboBox<ModelData>();
      ListStore<ModelData> store = new ListStore<ModelData>();
      panel.ensureDebugId(DebugId.SCREEN_PANEL_FIELD);
      panel.setId(SCREEN_PANEL);
      panel.setStore(store);
      panel.setFieldLabel("Panel");
      panel.setName(SCREEN_PANEL);
      panel.setAllowBlank(false);
      ComboBoxDataModel<TouchPanelDefinition> iphoneData = null;
      for (String key : panels.keySet()) {
         for (TouchPanelDefinition touchPanel : panels.get(key)) {
            ComboBoxDataModel<TouchPanelDefinition> data = new ComboBoxDataModel<TouchPanelDefinition>(touchPanel
                  .getName(), touchPanel);
            if ("iphone".equals(touchPanel.getName())) {
               iphoneData = data;
            }
            store.add(data);
         }
      }
      panel.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      panel.setEmptyText("Please Select Panel...");
      panel.setValueField(ComboBoxDataModel.getDataProperty());
      panel.setValue(iphoneData); // temp select iphone panel.
      
      FileUploadField background = new FileUploadField();
      background.ensureDebugId(DebugId.SCREEN_BG_FIELD);
      background.setFieldLabel("Background");
      background.setName(SCREEN_BACKGROUND);
      background.setRegex(".+?\\.(png|gif|jpg|PNG|GIF|JPG)");
      background.getMessages().setRegexText("Please select a gif, jpg or png type image.");
      background.setStyleAttribute("overflow", "hidden");
      
      Radio gridLayout = new Radio();
      gridLayout.ensureDebugId(DebugId.SCREEN_GRID_RADIO);
      gridLayout.setName(SCREEN_RADIOLAYOUTGROUP);
      gridLayout.setBoxLabel("Grid");
      gridLayout.setValueAttribute(SCREEN_GRIDRADIO);

      Radio absoluteLayout = new Radio();
      absoluteLayout.ensureDebugId(DebugId.SCREEN_ABSOLUTE_RADIO);
      absoluteLayout.setName(SCREEN_RADIOLAYOUTGROUP);
      absoluteLayout.setBoxLabel("Absolute");
      absoluteLayout.setValueAttribute(SCREEN_ABSOLUTERADIO);
      absoluteLayout.setValue(true);
      
      final RadioGroup layoutGroup = new RadioGroup();
      layoutGroup.setId(SCREEN_RADIOLAYOUTGROUP);
      layoutGroup.setFieldLabel("Layout");
      layoutGroup.add(gridLayout);
      layoutGroup.add(absoluteLayout);
      layoutGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            String value = layoutGroup.getValue().getValueAttribute();
            if (SCREEN_GRIDRADIO.equals(value)) {
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
         screenNameField.setValue(screen.getName());
         ComboBoxDataModel<TouchPanelDefinition> data = new ComboBoxDataModel<TouchPanelDefinition>(
               touchPanelDefinition.getName(), touchPanelDefinition);
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

   @SuppressWarnings("unchecked")
   private void addGridAttrs() {
      FieldSet gridAttrSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      gridAttrSet.setLayout(layout);
      gridAttrSet.setHeading("Grid attributes");
      
      /*
       * temp for set the width and height of gird.
       */
      ComboBoxDataModel<TouchPanelDefinition> panelData = (ComboBoxDataModel<TouchPanelDefinition>) ((ComboBox<ModelData>)form.getItemByItemId(SCREEN_PANEL)).getValue();
      TouchPanelCanvasDefinition canvas = panelData.getData().getCanvas();
      
      TextField<Integer> gridRowCountField = new TextField<Integer>();
      gridRowCountField.ensureDebugId(DebugId.SCREEN_GRID_ROW_FIELD);
      gridRowCountField.setName("gridRow");
      gridRowCountField.setFieldLabel("Row Count");
      gridRowCountField.setAllowBlank(false);
      gridRowCountField.setRegex("^\\d+$");
      gridRowCountField.setValue(6);              // temp set 6 rows.
      
      TextField<Integer> gridColumnCountField = new TextField<Integer>();
      gridColumnCountField.ensureDebugId(DebugId.SCREEN_GRID_COLUMN_FIELD);
      gridColumnCountField.setName("gridColumn");
      gridColumnCountField.setFieldLabel("Col Count");
      gridColumnCountField.setAllowBlank(false);
      gridColumnCountField.setRegex("^\\d+$");
      gridColumnCountField.setValue(4);           //temp set 4 columns.
      
      TextField<Integer> posLeftField = new TextField<Integer>();
      posLeftField.setName("posLeft");
      posLeftField.setFieldLabel("Left");
      posLeftField.setAllowBlank(false);
      posLeftField.setRegex("^\\d+$");
      posLeftField.getMessages().setRegexText("The left must be a positive integer");
      posLeftField.setValue(0);                    // temp set left 0
      
      TextField<Integer> posTopField = new TextField<Integer>();
      posTopField.setName("posTop");
      posTopField.setFieldLabel("Top");
      posTopField.setAllowBlank(false);
      posTopField.setRegex("^\\d+$");
      posTopField.getMessages().setRegexText("The top must be a positive integer");
      posTopField.setValue(0);                     // temp set top 0
      
      TextField<Integer> widthField = new TextField<Integer>();
      widthField.setName("width");
      widthField.setFieldLabel("Width");
      widthField.setAllowBlank(false);
      widthField.setRegex("^\\d+$");
      widthField.getMessages().setRegexText("The width must be a positive integer");
      widthField.setValue(canvas.getWidth());       // temp set width full fill the canvas
      
      TextField<Integer> heightField = new TextField<Integer>();
      heightField.setName("height");
      heightField.setFieldLabel("Height");
      heightField.setAllowBlank(false);
      heightField.setRegex("^\\d+$");
      heightField.getMessages().setRegexText("The height must be a positive integer");
      heightField.setValue(canvas.getHeight());      // temp set height full fill the canvas
      
      if (screen != null) {
         Grid grid = screen.getGrid();
         gridRowCountField.setValue(grid.getRowCount());
         gridColumnCountField.setValue(grid.getColumnCount());
         posLeftField.setValue(grid.getLeft());
         posTopField.setValue(grid.getTop());
         widthField.setValue(grid.getWidth());
         heightField.setValue(grid.getHeight());
      }
      gridAttrSet.add(gridRowCountField);
      gridAttrSet.add(gridColumnCountField);
      gridAttrSet.add(posLeftField);
      gridAttrSet.add(posTopField);
      gridAttrSet.add(widthField);
      gridAttrSet.add(heightField);
      form.add(gridAttrSet);
      form.layout();
   }
   
   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button submitBtn = new Button("Submit");
      submitBtn.ensureDebugId(DebugId.SCREEN_SUBMIT_BTN);
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
      form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=uploadImage&uploadFieldName="
            + SCREEN_BACKGROUND);
      form.setEncoding(Encoding.MULTIPART);
      form.setMethod(Method.POST);

      form.addListener(Events.Submit, new Listener<FormEvent>() {
         @SuppressWarnings("unchecked")
         @Override
         public void handleEvent(FormEvent be) {
            String backgroundImgURL = be.getResultHtml();
            String uploadFieldValue = "";

            List<Field<?>> list = form.getFields();
            ComboBoxDataModel<TouchPanelDefinition> panelData = null;
            for (Field<?> field : list) {
               if (SCREEN_PANEL.equals(field.getName())) {
                  panelData = (ComboBoxDataModel<TouchPanelDefinition>) field.getValue();
               } else if (SCREEN_BACKGROUND.equals(field.getName())
                     && !(field.getValue() == null || field.getValue().equals(""))) {
                  uploadFieldValue = field.getValue().toString();
               }
            }
            
            Map<String, String> attrMap = getAttrMap();
            setBackground(backgroundImgURL, uploadFieldValue, attrMap);

            BeanModel screenBeanModel = null;
            if (screen == null) {
               screenBeanModel = ScreenBeanModelProxy.createScreen(attrMap, panelData.getData());
               UIScreen.increaseDefaultNameIndex();
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
   
   
   private void setBackground(String backgroundImgURL, String uploadFieldValue, Map<String, String> attrMap) {
      boolean uploadSuccessfully = !"".equals(backgroundImgURL);
      boolean editMode = screen != null;
      boolean uploadFieldHasValue = uploadFieldValue != null && !uploadFieldValue.equals("");

      if (uploadSuccessfully) {
         attrMap.put(SCREEN_BACKGROUND, backgroundImgURL);
         return;
      }
      if (!uploadSuccessfully && uploadFieldHasValue) {
         if (editMode) {
            boolean wantToChangeBackground = !uploadFieldValue.equals(screen.getBackground());
            boolean hasHadBackgroundImg = !screen.getBackground().equals("");
            if (wantToChangeBackground && hasHadBackgroundImg) {
               MessageBox.alert("Warning", "Update background failed!<br />The background will not be changed!", null);
               attrMap.put(SCREEN_BACKGROUND, screen.getBackground());
            } else if (wantToChangeBackground && !hasHadBackgroundImg) {
               MessageBox.alert("Warning", "Upload background failed<br />The background will not be changed!", null);
               attrMap.put(SCREEN_BACKGROUND, "");
            }
         } else { // new mode
            MessageBox.alert("Failed", "Invalid image size!<br />Upload background failed",
                  null);
            attrMap.put(SCREEN_BACKGROUND, "");
         }

      }
   }
}
