/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.FormWindow;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;


/**
 * @author marcus@openremote.org
 */
public class KNXImportWindow extends FormWindow {
   
    private Device device;
    private Button windowOkBtn;
    private final KNXImportWindow importWindow;
    private MemoryProxy<String> proxy;
    private BaseListLoader<ListLoadResult<ModelData>> loader; 
    private FileUploadField fileUploadField;
        
   /**
    * Instantiates a new import window.
    */
   public KNXImportWindow(BeanModel deviceBeanModel) {
      super();
      importWindow = this;
      setSize(800, 600);
      initial("Import ETS4 project");
      this.ensureDebugId(DebugId.IMPORT_WINDOW);
      this.device = (Device) deviceBeanModel.getBean();
      show();
   }

   /**
    * Initial.
    * 
    * @param heading the heading
    */
   private void initial(String heading) {
      setHeading(heading);
      form.setAction(GWT.getModuleBaseURL() + "fileUploadController.htm?method=importETS4");
      form.setEncoding(Encoding.MULTIPART);
      form.setMethod(Method.POST);
      
      createFileUploadField();
      createLoadResetButton();
      createResultGrid();
      createWindowButtons();

      addListenersToForm();
      add(form);
   }

   /**
    * Creates the fields.
    */
   private void createResultGrid() {
       // create the column model
       List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  

       //Col1
       columns.add(new ColumnConfig("GroupAddress", "GroupAddress", 100));  

       //Col2
       ColumnConfig column = new ColumnConfig();  
       column.setId("Name");  
       column.setHeader("Name");  
       column.setWidth(165);  
       TextField<String> text = new TextField<String>();  
       text.setAllowBlank(false);  
       column.setEditor(new CellEditor(text));  
       columns.add(column);  
       
       //Col3
       columns.add(new ColumnConfig("DPT", "DPT", 100));
       
       //Col4
       final SimpleComboBox<String> combo = new SimpleComboBox<String>();  
       combo.setForceSelection(true);  
       combo.setTriggerAction(TriggerAction.ALL);  
       combo.add("On/Off Switch");
       combo.add("On/Off Status");
       combo.add("Value Set");
       combo.add("Value Status");
       combo.add("Scene");  
       combo.add("Dimmer/Blind Step");
       combo.add("N/A");
     
       CellEditor editor = new CellEditor(combo) {  
         @Override  
         public Object preProcessValue(Object value) {  
           if (value == null) {  
             return value;  
           }  
           return combo.findModel(value.toString());  
         }  
     
         @Override  
         public Object postProcessValue(Object value) {  
           if (value == null) {  
             return value;  
           }  
           return ((ModelData) value).get("value");  
         }  
       };  
     
       column = new ColumnConfig();  
       column.setId("commandType");  
       column.setHeader("Command");  
       column.setWidth(130);  
       column.setEditor(editor);  
       columns.add(column);  
       
       //Col5
       CheckColumnConfig checkColumn = new CheckColumnConfig("import", "Import?", 55);  
       CellEditor checkBoxEditor = new CellEditor(new CheckBox());  
       checkColumn.setEditor(checkBoxEditor);  
       columns.add(checkColumn); 
       
       ColumnModel cm = new ColumnModel(columns);  
     
       // defines the xml structure  
       ModelType type = new ModelType();  
       type.setRoot("records");  
       type.addField("GroupAddress", "groupAddress");  
       type.addField("Name", "name");  
       type.addField("DPT", "dpt");
       type.addField("commandType", "command");
       type.addField("import", "importGA");
      
       // need a loader, proxy, and reader  
       proxy = new MemoryProxy<String>(null);  
       JsonLoadResultReader<ListLoadResult<ModelData>> reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(type);  
       loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
       ListStore<ModelData> store = new ListStore<ModelData>(loader);
       
       final RowEditor<ModelData> re = new RowEditor<ModelData>();
       final Grid<ModelData> grid = new Grid<ModelData>(store, cm);
       grid.addPlugin(checkColumn);  
       grid.addPlugin(re); 
       grid.setBorders(true);  
       grid.setLoadMask(true);  
       grid.getView().setEmptyText("Please hit the load button.");  
       grid.setAutoExpandColumn("Name");  
     
       ContentPanel panel = new ContentPanel();  
       panel.setFrame(true);  
       panel.setCollapsible(false);  
       panel.setButtonAlign(HorizontalAlignment.CENTER);  
       panel.setHeading("Available group addresses");  
       panel.setLayout(new FitLayout());  
       panel.add(grid);  
       panel.setSize(750, 480);
       form.add(panel);
   }

   
   /**
    * Creates the fields.
    */
   private void createFileUploadField() {
      fileUploadField = new FileUploadField();
      fileUploadField.setName("file");
      fileUploadField.setAllowBlank(false);
      fileUploadField.setFieldLabel("File");
      fileUploadField.setStyleAttribute("overflow", "hidden");
      form.add(fileUploadField);
   }
   
   

   /**
    * Creates the load button
    */
   private void createLoadResetButton() {
      Button loadBtn = new Button("Load");
      loadBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_LOAD_BTN);

      loadBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (form.isValid()) {
              form.submit();
            }
         }
      });
      form.addButton(loadBtn);
      
      Button resetBtn = new Button("Clear");
      resetBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_CLEAR_BTN);
      resetBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
             proxy.setData("{\"records\":[]}");
             loader.load();
             fileUploadField.clear();
         }
      });
      form.addButton(resetBtn);
   }

   /**
    * Creates the buttons.
    */
   private void createWindowButtons() {
      windowOkBtn = new Button("OK");
      windowOkBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_OK_BTN);
      windowOkBtn.setEnabled(false);
      windowOkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent ce) {
              importSelectedGridData();
          }
       });

      
      Button cancelBtn = new Button("Cancel");
      cancelBtn.ensureDebugId(DebugId.KNX_IMPORT_WINDOW_CANCEL_BTN);
      cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
             importWindow.hide();
         }
      });
      form.addButton(windowOkBtn);
      form.addButton(cancelBtn);
   }

   private void importSelectedGridData() {
       List<BeanModel> deviceCommandModels = new ArrayList<BeanModel>();
       //TODO use grid data to create list 
       fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(deviceCommandModels));   
   }
   
   /**
    * Adds the listeners to form.
    */
   private void addListenersToForm() {
      form.addListener(Events.Submit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
             proxy.setData(be.getResultHtml());
             loader.load();
         }
      });
   }
   
}
