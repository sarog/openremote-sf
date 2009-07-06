package org.openremote.modeler.client.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.protocol.ProtocolAttrDefinition;
import org.openremote.modeler.protocol.ProtocolDefinition;
import org.openremote.modeler.protocol.ProtocolValidator;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * This FormPanel widget is used for render protocol form.
 * @author allen.wei
 *
 */
public class ProtocolForm extends FormPanel {

   private List<Listener<AppEvent<Map<String, String>>>> submitListeners = new ArrayList<Listener<AppEvent<Map<String, String>>>>();

   /**
    * Listener will be called after form submit and all the validator on fields pass.  
    * @param listener
    */
   public void addSubmitListener(Listener<AppEvent<Map<String, String>>> listener) {
      submitListeners.add(listener);
   }

   public void remoteSubmitListener(Listener<AppEvent<Map<String, String>>> listener) {
      submitListeners.remove(listener);
   }

   protected void fireSubmitListener(AppEvent<Map<String, String>> event) {
      for (Listener<AppEvent<Map<String, String>>> listener : submitListeners) {
         listener.handleEvent(event);
      }
   }
   
   private Map<String,TextField<String>> textFields = new HashMap<String,TextField<String>>();
   
   /**
    * Default constructor, pass in a ProtocolDefinition, the formPanel will render it automatically. 
    * @param definition ProtocolDefinition
    */
   public ProtocolForm(ProtocolDefinition definition) {
      final FormPanel f = this;

      setHeading(definition.getName() + " Form");
      setFrame(true);
      setWidth(350);
      
      createField(definition);
      
     
      setButtonAlign(HorizontalAlignment.CENTER);

      Button submitBtn = new Button("Submit");
      Button resetButton = new Button("Reset");

      submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (f.isValid()) {
               f.submit();
            }
         }

      });

      resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            List<Field> list = f.getFields();
            for (Field f : list) {
               f.reset();
            }
         }

      });
      addButton(submitBtn);
      addButton(resetButton);
      
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            StringBuffer str = new StringBuffer();
            List<Field> list = f.getFields();
            Map<String, String> attrMap = new HashMap<String, String>();
            for (Field f : list) {
               attrMap.put(f.getName(), f.getValue().toString());
            }
            AppEvent<Map<String, String>> appEvent = new AppEvent<Map<String, String>>(Events.Submit, attrMap);

            fireSubmitListener(appEvent);
         }

      });
   }
   
   /**
    * Gets certain TextField according to attribute name.
    * @param attrName protocol attribute name
    * @return TextField<String>
    */
   public TextField<String> getTextField(String attrName) {
      if (textFields.containsKey(attrName)) {
         return textFields.get(attrName);
      }
      return null;
   }
   
   /**
    * Gets all TestFields.
    * @return all TestFields
    */
   public Collection<TextField<String>> getAllTextField() {
      return textFields.values();
   }
   
   
   /**
    * Creates TextField according to Protocol Attribute.
    * @param definition ProtocolDefinition
    */
   private void createField(ProtocolDefinition definition) {
      for (ProtocolAttrDefinition attrDefinition : definition.getAttrs()) {
         TextField<String> attrField = new TextField<String>();
         attrField.setName(attrDefinition.getName());
         TextField<String>.TextFieldMessages messages = attrField.getMessages();
         attrField.setFieldLabel(attrDefinition.getLabel());

         setValidators(attrField, messages, attrDefinition.getValidators());

         add(attrField);
         textFields.put(attrDefinition.getName(), attrField);
      }
   }
  
   

   /**
    * Sets validators on TestField.
    * @param attrFile TextField
    * @param messages TextField<String>.TextFieldMessages
    * @param protocolValidators ProtocolValidator list
    */
   private void setValidators(TextField<String> attrFile, TextField<String>.TextFieldMessages messages,
         List<ProtocolValidator> protocolValidators) {
      for (ProtocolValidator protocolValidator : protocolValidators) {
         if (protocolValidator.getType() == ProtocolValidator.ALLOW_BLANK_TYPE) {
            if (Boolean.valueOf(protocolValidator.getValue())) {
               attrFile.setAllowBlank(true);
            } else {
               attrFile.setAllowBlank(false);
            }
            messages.setBlankText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.MAX_LENGTH_TYPE) {
            attrFile.setMaxLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMaxLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.MIN_LENGTH_TYPE) {
            attrFile.setMinLength(Integer.valueOf(protocolValidator.getValue()));
            messages.setMinLengthText(protocolValidator.getMessage());
         } else if (protocolValidator.getType() == ProtocolValidator.REGEX_TYPE) {
            attrFile.setRegex(protocolValidator.getValue());
            messages.setRegexText(protocolValidator.getMessage());
         }
      }
   }
}
