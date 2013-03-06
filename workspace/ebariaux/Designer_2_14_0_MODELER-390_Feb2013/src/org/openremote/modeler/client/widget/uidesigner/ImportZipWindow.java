/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

import java.util.ArrayList;

import org.openremote.modeler.client.event.ImportConfigurationDoneEvent;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.openremote.modeler.shared.dto.MacroDTO;
import org.openremote.modeler.shared.dto.MacroItemDTO;
import org.openremote.modeler.shared.dto.MacroItemType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.sencha.gxt.widget.core.client.info.Info;


/**
 * The Class ImportWindow.
 * 
 * @author handy.wang
 */
public class ImportZipWindow extends FormWindow {
   
   /**
    * Instantiates a new import window.
    */
   public ImportZipWindow() {
      super();
      initial("Import");
      this.ensureDebugId(DebugId.IMPORT_WINDOW);
      show();
   }

   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setSize(360, 140);
      setHeading(heading);
      createFields();
      createButtons();
      form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importFile");
      form.setEncoding(Encoding.MULTIPART);
      form.setMethod(Method.POST);
      addListenersToForm();
   }

   /**
    * Creates the fields.
    */
   private void createFields() {
      FileUploadField fileUploadField = new FileUploadField();
      fileUploadField.setName("file");
      fileUploadField.setAllowBlank(false);
      fileUploadField.setFieldLabel("File");
      fileUploadField.setStyleAttribute("overflow", "hidden");
      form.add(fileUploadField);
   }

   /**
    * Creates the buttons.
    */
   private void createButtons() {
      Button importBtn = new Button("import");
      importBtn.ensureDebugId(DebugId.IMPORT_WINDOW_UPLOAD_BTN);
      Button cancelBtn = new Button("cancel");
      cancelBtn.ensureDebugId(DebugId.IMPORT_WINDOW_CANCEL_BTN);

      importBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
               MessageBox box = new MessageBox();
               box.setButtons(MessageBox.YESNO);
               box.setIcon(MessageBox.QUESTION);
               box.setTitle("Import");
               box.setMessage("The activities tree will be rebuilt. Are you sure you want to import?");
               box.addCallback(new Listener<MessageBoxEvent>() {
                   public void handleEvent(MessageBoxEvent be) {
                       if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                          form.submit();
                          mask("Importing, please wait.");
                       }
                   }
               });
               box.show();       
            }
         }
      });
      final ImportZipWindow that = this;
      cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            that.hide();
         }
      });
      form.addButton(importBtn);
      form.addButton(cancelBtn);
   }

   /**
    * Adds the listeners to form.
    */
   private void addListenersToForm() {
      form.addListener(Events.Submit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
           JSONObject jsonResponse = JSONParser.parseStrict(be.getResultHtml()).isObject();

           if (jsonResponse != null) {
             JSONString jsonErrorMessage = jsonResponse.get("errorMessage").isString();
             if (jsonErrorMessage == null) {
               JSONObject jsonResult = jsonResponse.get("result").isObject();
               
               JSONArray jsonDeviceDTOs = jsonResult.get("devices").isArray();
               
               // TODO: additional test for non nulls
               ArrayList<DeviceDTO> deviceDTOs = new ArrayList<DeviceDTO>();
               for (int i = 0; i < jsonDeviceDTOs.size(); i++) {
                 JSONObject jsonDeviceDTO = jsonDeviceDTOs.get(i).isObject();
                 DeviceDTO deviceDTO = new DeviceDTO((long)jsonDeviceDTO.get("oid").isNumber().doubleValue(), jsonDeviceDTO.get("displayName").isString().stringValue());
                 deviceDTOs.add(deviceDTO);
               }

               ArrayList<MacroDTO> macroDTOs = new ArrayList<MacroDTO>();
               JSONArray jsonMacroDTOs = jsonResult.get("macros").isArray();
               for (int i = 0; i < jsonMacroDTOs.size(); i++) {
                 JSONObject jsonMacroDTO = jsonMacroDTOs.get(i).isObject();
                 MacroDTO macroDTO = new MacroDTO((long)jsonMacroDTO.get("oid").isNumber().doubleValue(), jsonMacroDTO.get("displayName").isString().stringValue());
                 JSONArray jsonMacroItemDTOs = jsonMacroDTO.get("items").isArray();
                 ArrayList<MacroItemDTO> macroItemDTOs = new ArrayList<MacroItemDTO>();
                 for (int j = 0; j < jsonMacroItemDTOs.size(); j++) {
                   JSONObject jsonMacroItemDTO = jsonMacroItemDTOs.get(j).isObject();
                   MacroItemDTO itemDTO = new MacroItemDTO(jsonMacroItemDTO.get("displayName").isString().stringValue(), MacroItemType.valueOf(jsonMacroItemDTO.get("type").isString().stringValue()));
                   macroItemDTOs.add(itemDTO);
                 }
                 macroDTO.setItems(macroItemDTOs);
                 macroDTOs.add(macroDTO);
               }
              fireEvent(ImportConfigurationDoneEvent.IMPORT_CONFIGURATION_DONE, new ImportConfigurationDoneEvent(deviceDTOs, macroDTOs));

             } else {
               String errorMessage = jsonErrorMessage.stringValue();
               
               Info.display("ERROR", errorMessage);
               // TODO: correctly display to user
             }
           } else {
             // TODO
           }
         }
      });
      add(form);
   }
}
