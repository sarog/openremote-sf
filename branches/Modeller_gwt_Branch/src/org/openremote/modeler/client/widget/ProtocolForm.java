/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

// TODO: Auto-generated Javadoc
/**
 * This FormPanel widget is used for render protocol form.
 * 
 * @author allen.wei
 */
public class ProtocolForm extends FormPanel {

   /** The submit listeners. */
   private List<Listener<AppEvent<Map<String, String>>>> submitListeners = new ArrayList<Listener<AppEvent<Map<String, String>>>>();

   /**
    * Listener will be called after form submit and all the validator on fields pass.
    * 
    * @param listener the listener
    */
   public void addSubmitListener(Listener<AppEvent<Map<String, String>>> listener) {
      submitListeners.add(listener);
   }

   /**
    * Remote submit listener.
    * 
    * @param listener the listener
    */
   public void remoteSubmitListener(Listener<AppEvent<Map<String, String>>> listener) {
      submitListeners.remove(listener);
   }

   /**
    * Fire submit listener.
    * 
    * @param event the event
    */
   protected void fireSubmitListener(AppEvent<Map<String, String>> event) {
      for (Listener<AppEvent<Map<String, String>>> listener : submitListeners) {
         listener.handleEvent(event);
      }
   }

   /** The text fields. */
   private Map<String, TextField<String>> textFields = new HashMap<String, TextField<String>>();

   /**
    * Instantiates a new create protocol form.
    * 
    * @param definition ProtocolDefinition
    */
   public ProtocolForm(ProtocolDefinition definition) {
      setupForm(definition);
      createField(definition);
   }

   /** 
    * Instantiates a new edit protocol form.
    * 
    * @param definition the definition
    * @param attrs the attrs
    */
   public ProtocolForm(ProtocolDefinition definition, Map<String, String> attrs) {
      setupForm(definition);
      createFieldWithValues(definition, attrs);
   }


   /**
    * Sets the up form using ProtocolDefinition
    * 
    * @param definition the new up form
    */
   private void setupForm(ProtocolDefinition definition) {
      final FormPanel f = this;

      setHeading(definition.getName() + " Form");
      setFrame(true);
      setWidth(350);


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
    * 
    * @param attrName protocol attribute name
    * 
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
    * 
    * @return all TestFields
    */
   public Collection<TextField<String>> getAllTextField() {
      return textFields.values();
   }


   /**
    * Creates TextField according to Protocol Attribute.
    * 
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
    * Creates the field with values.
    * 
    * @param definition the definition
    * @param values the values
    */
   private void createFieldWithValues(ProtocolDefinition definition, Map<String, String> values) {
      for (ProtocolAttrDefinition attrDefinition : definition.getAttrs()) {
         TextField<String> attrField = new TextField<String>();
         attrField.setName(attrDefinition.getName());
         if (values.containsKey(attrDefinition.getName())) {
            attrField.setValue(values.get(attrDefinition.getName()));
         }

         TextField<String>.TextFieldMessages messages = attrField.getMessages();
         attrField.setFieldLabel(attrDefinition.getLabel());

         setValidators(attrField, messages, attrDefinition.getValidators());

         add(attrField);
         textFields.put(attrDefinition.getName(), attrField);
      }
   }


   /**
    * Sets validators on TestField.
    * 
    * @param attrFile           TextField
    * @param messages           TextField<String>.TextFieldMessages
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
