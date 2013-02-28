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
package org.openremote.modeler.client.widget.propertyform;

import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.model.ComboBoxDataModel;
import org.openremote.modeler.client.widget.ImageUploadField;
import org.openremote.modeler.client.widget.uidesigner.GestureWindow;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.Background;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.Background.RelativeType;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.ImageSource;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
/**
 * A property form for editing the screen's property . 
 * @author Javen
 *
 */
public class ScreenPropertyForm extends PropertyForm {
   public static final String SCREEN_NAME = "screenName";

   public static final String SCREEN_BACKGROUND = "background";
   
   public static final String SCREEN_RELETIVE = "relative";
   
   public static final String FILL_SCREEN = "fill screen ";
   
   public static final String yesFill = "true";

   public static final String notFill = "false";
   
  
   private ScreenCanvas canvas = null;
  
   public ScreenPropertyForm(ScreenCanvas canvas) {
      super();
      this.canvas = canvas;
      createFields();
   }
   @SuppressWarnings("unchecked")
   private void createFields() {
      
      FieldSet positionSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      layout.setDefaultWidth(110);
      positionSet.setLayout(layout);
      positionSet.setHeading("Position");

      setFieldWidth(165);
      setLabelWidth(80);
     // TextField<String> screenNameField = createNameField();
      
      FileUploadField background = createBackgroundField();
      
      RadioGroup whetherFillScreen = createScreenFillerField(positionSet);
      
      
      
      TextField<String> posLeftField = createLeftSetField();
      TextField<String> posTopField = createTopSetField();
//      TextField<String> widthField = createWidthSetField();
//      TextField<String> heightField = createHeightSetField();
      
//      createPositionField(positionSet,posLeftField,posTopField,widthField,heightField);
      createPositionField(positionSet,posLeftField,posTopField);
      positionSet.add(posLeftField);
      positionSet.add(posTopField);
//      positionSet.add(widthField);
//      positionSet.add(heightField);
      if(canvas.getScreen().getBackground().isFillScreen()){
         positionSet.hide();
      }
//      whetherFillScreen.hide();
//      this.add(screenNameField);
      this.add(background);
      this.add(whetherFillScreen);
      this.add(positionSet);
      this.add(createGestureField());
      
      addListenersToForm(whetherFillScreen);
   }

  /* private TextField<String> createHeightSetField() {
      final TextField<String> heightField = new TextField<String>();
      heightField.setName("height");
      heightField.setFieldLabel("Height");
      heightField.setAllowBlank(false);
      heightField.setRegex("^[1-9][0-9]*$");
      heightField.getMessages().setRegexText("The height must be a positive integer");
      heightField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            bkGrd.setHeight(Integer.parseInt(heightField.getValue()));
            canvas.updateGround();
         }
      });
      heightField.setLabelStyle("text-align:right;");
      heightField.setAllowBlank(true);
      return heightField;
   }

   private TextField<String> createWidthSetField() {
      final TextField<String> widthField = new TextField<String>();
      widthField.setName("width");
      widthField.setFieldLabel("Width");
      widthField.setAllowBlank(false);
      widthField.setRegex("^[1-9][0-9]*$");
      widthField.getMessages().setRegexText("The width must be a positive integer");
      widthField.setAllowBlank(true);
      widthField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            bkGrd.setWidth(Integer.parseInt(widthField.getValue()));
            canvas.updateGround();
         }
      });
      widthField.setLabelStyle("text-align:right;");
      return widthField;
   }*/

   private TextField<String> createTopSetField() {
      final TextField<String> posTopField = new TextField<String>();
      posTopField.setName("posTop");
      posTopField.setFieldLabel("Top");
      posTopField.setAllowBlank(false);
      posTopField.setRegex(Constants.REG_NONNEGATIVEINT);
      posTopField.getMessages().setRegexText("The top must be a nonnegative integer");
      posTopField.setValue("0"); // temp set top 0
      posTopField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            bkGrd.setTop(Integer.parseInt(posTopField.getValue()));
            canvas.updateGround();
         }
      });
      posTopField.setLabelStyle("text-align:right;");
      posTopField.setValue(canvas.getScreen().getBackground().getTop()+"");
      return posTopField;
   }

   private TextField<String> createLeftSetField() {
      final TextField<String> posLeftField = new TextField<String>();
      posLeftField.setName("posLeft");
      posLeftField.setFieldLabel("Left");
      posLeftField.setAllowBlank(false);
      posLeftField.setRegex(Constants.REG_NONNEGATIVEINT);
      posLeftField.getMessages().setRegexText("The left must be a nonnegative integer");
      posLeftField.setValue("0"); // temp set left 0
      posLeftField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            bkGrd.setLeft(Integer.parseInt(posLeftField.getValue()));
            canvas.updateGround();
         }
      });
      posLeftField.setLabelStyle("text-align:right;");
      posLeftField.setValue(canvas.getScreen().getBackground().getLeft()+"");
      return posLeftField;
   }

   private void createPositionField(FieldSet positionSet, final TextField<? extends Object>... fields) {
      CheckBox absolute = new CheckBox();
      absolute.setHideLabel(true);
      absolute.setBoxLabel("absolute");
      final ComboBox<ModelData> relative = new ComboBox<ModelData>();
      ListStore<ModelData> store = new ListStore<ModelData>();
      RelativeType[] relatedTypes  = RelativeType.values();
      for (int i = 0; i < relatedTypes.length; i++) {
         ComboBoxDataModel<RelativeType> relativeItem = new ComboBoxDataModel<RelativeType>(relatedTypes[i].toString(),
               relatedTypes[i]);
         store.add(relativeItem);
      }
      
      relative.setStore(store);
      relative.setFieldLabel("relative");
      relative.setName(SCREEN_RELETIVE);
      relative.setAllowBlank(false);
      relative.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

         @SuppressWarnings("unchecked")
         @Override
         public void selectionChanged(SelectionChangedEvent<ModelData> se) {
            Background bkGrd = canvas.getScreen().getBackground();
            ComboBoxDataModel<RelativeType> relativeItem;
            relativeItem = (ComboBoxDataModel<RelativeType>) se.getSelectedItem();
            bkGrd.setRelatedType(relativeItem.getData());
//            bkGrd.setAbsolute(false);
            canvas.updateGround();
         }
         
      });
      
      relative.setValue(new ComboBoxDataModel<RelativeType>(canvas.getScreen().getBackground().getRelatedType().toString(),canvas.getScreen().getBackground().getRelatedType()));
      absolute.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            Background bkGrd = canvas.getScreen().getBackground();
            if ("true".equals(be.getValue().toString())) {
               bkGrd.setAbsolute(true);
               relative.setEnabled(false);
               enableTextField(true, fields);
            } else {
               bkGrd.setAbsolute(false);
               relative.setEnabled(true);
               enableTextField(false, fields);
            }
            canvas.updateGround();
         }
      });
      
     
      positionSet.add(relative);
      positionSet.add(absolute);
      absolute.setValue(canvas.getScreen().getBackground().isAbsolute());
      relative.setEnabled(!canvas.getScreen().getBackground().isAbsolute());
      enableTextField(canvas.getScreen().getBackground().isAbsolute(), fields);
     
      relative.setDisplayField(ComboBoxDataModel.getDisplayProperty());
      relative.setEmptyText("Please select one... ");
   }

   private RadioGroup createScreenFillerField(final FieldSet positionSet) {
      Radio fillScreen = new Radio();
      fillScreen.setName(FILL_SCREEN);
      fillScreen.setBoxLabel("yes");
      fillScreen.setValueAttribute(yesFill);
      fillScreen.setValue(canvas.getScreen().getBackground().isFillScreen());
      
      Radio notFillScreen = new Radio();
      notFillScreen.setName(FILL_SCREEN);
      notFillScreen.setBoxLabel("no");
      notFillScreen.setValueAttribute(notFill);
      
      final RadioGroup whetherFieldGroup = new RadioGroup();
      whetherFieldGroup.setFieldLabel(FILL_SCREEN);
      whetherFieldGroup.add(fillScreen);
      whetherFieldGroup.add(notFillScreen);
      whetherFieldGroup.addListener(Events.Change, new Listener<FieldEvent>() {
         @Override
         public void handleEvent(FieldEvent be) {
            String value = whetherFieldGroup.getValue().getValueAttribute();
            Background bkGrd = canvas.getScreen().getBackground();
            if (yesFill.equals(value)) {
               positionSet.hide();
               bkGrd.setFillScreen(true);
            } else if (notFill.equals(value)) {
               positionSet.show();
               bkGrd.setFillScreen(false);
            }
            canvas.updateGround();
         }
         
      });
      whetherFieldGroup.hide();
      whetherFieldGroup.setValue(canvas.getScreen().getBackground().isFillScreen()?fillScreen:notFillScreen);
      String backgroundSrc = canvas.getScreen().getBackground().getImageSource().getSrc();
      if (backgroundSrc != null && !backgroundSrc.equals("")) {
         whetherFieldGroup.show();
      }
      return whetherFieldGroup;
   }

   private ImageUploadField createBackgroundField() {
      ImageUploadField background = new ImageUploadField(null) {
         @Override
         protected void onChange(ComponentEvent ce) {
            super.onChange(ce);
            if (!isValid()) {
               return;
            }
            submit();
            canvas.mask("Uploading image...");
         }
      };
      background.setValue(canvas.getScreen().getBackground().getImageSource().getSrc());
      background.setFieldLabel("Background");
      background.setActionToForm(ScreenPropertyForm.this);
      return background;
   }
   
   /*private TextField<String> createNameField() {
      TextField<String> screenNameField = new TextField<String>();
      screenNameField.setName("name");
      screenNameField.setFieldLabel("Name");
      screenNameField.setAllowBlank(false);
      screenNameField.setValue(Screen.getNewDefaultName());
      return screenNameField;
   }*/
   
   private void addListenersToForm(final RadioGroup whetherFillScreen) {
      addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String backgroundImgURL = be.getResultHtml();
            boolean success = !"".equals(backgroundImgURL);
            if (success) {
               setBackground(backgroundImgURL);
               whetherFillScreen.show();
            }
            BeanModel screenBeanModel = null;
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(screenBeanModel));
         }
      });
   }

   private void setBackground(String backgroundImgURL) {
      Screen screen = canvas.getScreen();
      screen.setBackground(new Background(new ImageSource(backgroundImgURL)));
      canvas.setStyleAttribute("backgroundImage", "url(" + screen.getCSSBackground() + ")");
      canvas.unmask();

   }
   private void enableTextField(boolean enable, TextField<?>... fields) {
      for (TextField<?> field : fields) {
         field.setEnabled(enable);
      }
   }
   
   private AdapterField createGestureField() {
      Button configGesture = new Button("Config");
      configGesture.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            GestureWindow configGestureWindow = new GestureWindow(canvas.getScreen().getGestures());
            configGestureWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @SuppressWarnings("unchecked")
               public void afterSubmit(SubmitEvent be) {
                  canvas.getScreen().setGestures((List<Gesture>)be.getData());
               }
            });
         }
      });
      AdapterField adapterConfigGesture = new AdapterField(configGesture);
      adapterConfigGesture.setFieldLabel("Gestures");
      adapterConfigGesture.setAutoWidth(true);
      return adapterConfigGesture;
   }
}
