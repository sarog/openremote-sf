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
package org.openremote.modeler.client.widget.UIDesigner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.proxy.ScreenBeanModelProxy;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;

// TODO: Auto-generated Javadoc
/**
 * The Class ScreenWindow.
 */
public class ScreenWindow extends FormWindow {

   /** The Constant SCREEN_NAME. */
   public static final String SCREEN_NAME = "screenName";

   /** The Constant SCREEN_ROW_COUNT. */
   public static final String SCREEN_ROW_COUNT = "screenRowCount";
   
   /** The Constant SCREEN_COLUMN_COUNT. */
   public static final String SCREEN_COLUMN_COUNT = "screenColumnCount";
   
   /** The activity. */
   private Activity activity = null;
   
   /** The screen model. */
   private Screen screen = null;

   /**
    * Instantiates a new screen window.
    * 
    * @param activity the activity
    */
   public ScreenWindow(Activity activity) {
      super();
      this.activity = activity;
      initial("New Screen");
      this.ensureDebugId(DebugId.NEW_SCREEN_WINDOW);
      show();
   }

   /**
    * Instantiates a new screen window.
    * 
    * @param screenModel the screen model
    */
   public ScreenWindow(Screen screen) {
      super();
      this.screen = screen;
      initial("Edit Screen");
      this.ensureDebugId(DebugId.EDIT_SCREEN_WINDOW);
      show();
   }

   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setHeading(heading);
      setSize(360, 200);
      createFields();
      createButtons();
      addListenersToForm();
   }

   /**
    * Creates the fields.
    */
   private void createFields() {
      TextField<String> screenNameField = new TextField<String>();
      screenNameField.setName(SCREEN_NAME);
      screenNameField.ensureDebugId(DebugId.SCREEN_NAME_FIELD);
      screenNameField.setFieldLabel("Name");
      screenNameField.setAllowBlank(false);screenNameField.setValue("iphone1");
      
      TextField<Integer> screenRowCountField = new TextField<Integer>();
      screenRowCountField.setName(SCREEN_ROW_COUNT);
      screenRowCountField.ensureDebugId(DebugId.SCREEN_ROW_COUNT_FIELD);
      screenRowCountField.setFieldLabel("Row Count");
      screenRowCountField.setAllowBlank(false);screenRowCountField.setValue(6);
      
      TextField<Integer> screenColumnCountField = new TextField<Integer>();
      screenColumnCountField.setName(SCREEN_COLUMN_COUNT);
      screenColumnCountField.ensureDebugId(DebugId.SCREEN_COLUMN_COUNT_FIELD);
      screenColumnCountField.setFieldLabel("Col Count");
      screenColumnCountField.setAllowBlank(false);screenColumnCountField.setValue(4);
      
      if (screen != null) {
         screenNameField.setValue(screen.getName());
         screenRowCountField.setValue(screen.getRowCount());
         screenColumnCountField.setValue(screen.getColumnCount());
      }
      form.add(screenNameField);
      form.add(screenRowCountField);
      form.add(screenColumnCountField);
   }

   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button submitBtn = new Button("submit");
      submitBtn.ensureDebugId(DebugId.NEW_SCREEN_WINDOW_SUBMIT_BTN);
      Button resetBtn = new Button("reset");
      resetBtn.ensureDebugId(DebugId.NEW_SCREEN_WINDOW_RESET_BTN);

      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               form.submit();
            }
         }
      });
      resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            form.reset();
         }
      });

      form.addButton(submitBtn);
      form.addButton(resetBtn);
   }

   /**
    * Adds the listeners to form.
    */
   private void addListenersToForm() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>(){
         public void handleEvent(FormEvent be) {
            List<Field<?>> list = form.getFields();
            Map<String, String> attrMap = new HashMap<String, String>();
            for (Field<?> field : list) {
               attrMap.put(field.getName(), field.getValue().toString());
            }
            BeanModel screenBeanModel = null;
            if (screen == null) {
               screenBeanModel = ScreenBeanModelProxy.createScreen(activity, attrMap);
            } else {
               screenBeanModel = ScreenBeanModelProxy.updateScreen(screen, attrMap);
            }
            fireEvent(SubmitEvent.Submit, new SubmitEvent(screenBeanModel));
         }
      });
      add(form);
   }
}
