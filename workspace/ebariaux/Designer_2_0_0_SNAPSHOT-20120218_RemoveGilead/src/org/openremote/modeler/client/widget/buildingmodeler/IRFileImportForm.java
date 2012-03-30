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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.proxy.IrFileParserProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.irfileparser.BrandInfo;
import org.openremote.modeler.irfileparser.CodeSetInfo;
import org.openremote.modeler.irfileparser.DeviceInfo;
import org.openremote.modeler.irfileparser.IRCommandInfo;
import org.openremote.modeler.shared.dto.DeviceDTO;
import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.MediaType;
import org.restlet.client.resource.ClientResource;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * IR File Command Import Form.
 * 
 */
public class IRFileImportForm extends CommonForm {

   /** The device. */
   protected DeviceDTO device = null;

   /** The select container. */
   private LayoutContainer selectContainer = new LayoutContainer();

   /** The command container. */
   private LayoutContainer commandContainer = new LayoutContainer();

   /** The next button. */
   protected Button nextButton;

   /** The code grid. */
   protected Grid<IRCommandInfo> codeGrid = null;

   private ColumnModel cm = null;

   protected ListStore<BrandInfo> brandInfos = null;
   protected ComboBox<BrandInfo> brandInfoList = null;

   protected ListStore<DeviceInfo> deviceInfos = null;
   protected ComboBox<DeviceInfo> deviceInfoList = null;

   protected ListStore<CodeSetInfo> codeSetInfos = null;
   protected ComboBox<CodeSetInfo> codeSetInfoList = null;

   ListStore<IRCommandInfo> listStore;

   protected Component wrapper;
   
   
   private String prontoFileHandle;

   /**
    * Instantiates a new iR command file import form.
    * 
    * @param wrapper
    *           the wrapper
    * @param deviceBeanModel
    *           the device bean model
    */
   public IRFileImportForm(final Component wrapper, BeanModel deviceBeanModel) {

      super();

      setHeight(500);
      this.wrapper = wrapper;
      setLayout(new RowLayout(Orientation.VERTICAL));
      
      HBoxLayout selectContainerLayout = new HBoxLayout();
      selectContainerLayout.setPadding(new Padding(5));

      selectContainerLayout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);

      selectContainer.setLayout(selectContainerLayout);
      selectContainer.setLayoutOnChange(true);
      add(selectContainer, new RowData(1, 35));

      commandContainer.setLayout(new CenterLayout());
      commandContainer.setLayoutOnChange(true);
      add(commandContainer, new RowData(1, 1));
      
      device = (DeviceDTO) deviceBeanModel.getBean();
      
      cleanBrandComboBox();
      cleanCodeGrid();
      cleanCodeSetComboBox();
      cleanDeviceComboBox();
      onSubmit(wrapper);
   }

   /**
    * On submit.
    * 
    * @param wrapper
    *           the wrapper
    */
   protected void onSubmit(final Component wrapper) {
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
         }
      });
   }

   @Override
   protected void addButtons() {
      nextButton = new Button("Next");
      nextButton.setEnabled(false);
      nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
           IRFileImportToProtocolForm protocolChooserForm = new IRFileImportToProtocolForm(wrapper, device);
           protocolChooserForm.setSelectedFunctions(codeGrid.getSelectionModel().getSelectedItems());
           protocolChooserForm.setVisible(true);
           protocolChooserForm.show();
         }
      });
      addButton(nextButton);
   }

   /**
    * populates and shows the brand combo box
    */
   public void showBrands() {     
    ClientResource clientResource = new ClientResource("/irservice/rest/" + prontoFileHandle + "/brands"); // TODO : get base URL from some configuration
    clientResource.setOnResponse(new Uniform() {
      public void handle(Request request, Response response) {
        try {
          String jsonString = response.getEntity().getText();
          Info.display("INFO", "Received > " + jsonString + "<");

          if (brandInfos == null) {
            brandInfos = new ListStore<BrandInfo>();
            brandInfoList = new ComboBox<BrandInfo>();
            brandInfoList.setEmptyText("Please select Brand...");
            brandInfoList.setDisplayField("brandName");
            brandInfoList.setWidth(150);
            brandInfoList.setStore(brandInfos);
            brandInfoList.setTriggerAction(TriggerAction.ALL);
            brandInfoList.setEditable(false);
            selectContainer.add(brandInfoList);
            setStyleOfComboBox(brandInfoList);
            brandInfoList.addSelectionChangedListener(new SelectionChangedListener<BrandInfo>() {
              @Override
              public void selectionChanged(SelectionChangedEvent<BrandInfo> se) {
                showDevices(se.getSelectedItem());
              }
            });

          } else {
            cleanCodeGrid();
            cleanDeviceComboBox();
            cleanCodeSetComboBox();
            cleanBrandComboBox();
          }
          JSONArray brands = JSONParser.parse(jsonString).isArray();
          for (int i = 0; i < brands.size(); i++) {
            JSONObject brand = brands.get(i).isObject();
            brandInfos.add(new BrandInfo(brand.get("brandName").isString().stringValue()));
          }
          brandInfoList.setVisible(true);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    clientResource.get(MediaType.APPLICATION_JSON);
  }

   /**
    * populates and shows the devices combobox for the currently selected brand
    * 
    * @param brandInfo
    */
   private void showDevices(BrandInfo brandInfo) {
      IrFileParserProxy.loadModels(brandInfo,
            new AsyncSuccessCallback<ArrayList<DeviceInfo>>() {

               @Override
               public void onSuccess(ArrayList<DeviceInfo> devices) {
                  if (deviceInfos == null) {
                     deviceInfos = new ListStore<DeviceInfo>();
                     deviceInfoList = new ComboBox<DeviceInfo>();
                     deviceInfoList.setEmptyText("Please select Device...");
                     deviceInfoList.setDisplayField("modelName");
                     deviceInfoList.setWidth(150);
                     deviceInfoList.setStore(deviceInfos);
                     deviceInfoList.setTriggerAction(TriggerAction.ALL);
                     deviceInfoList.setEditable(false);
                     selectContainer.add(deviceInfoList);
                     deviceInfos.add(devices);
                     setStyleOfComboBox(deviceInfoList);
                     deviceInfoList
                           .addSelectionChangedListener(new SelectionChangedListener<DeviceInfo>() {

                              @Override
                              public void selectionChanged(
                                    SelectionChangedEvent<DeviceInfo> se) {
                                 showCodeSets(se.getSelectedItem());

                              }

                           });
                  } else {
                     cleanCodeGrid();
                     cleanCodeSetComboBox();
                     cleanDeviceComboBox();
                     deviceInfos.add(devices);
                     deviceInfoList.setVisible(true);
                  }

               }

            });

   }

   /**
    * populates and shows the code set combo box for the currently selected
    * device
    * 
    * @param device
    */
   private void showCodeSets(DeviceInfo device) {
      IrFileParserProxy.loadCodeSets(device,
            new AsyncSuccessCallback<ArrayList<CodeSetInfo>>() {

               @Override
               public void onSuccess(final ArrayList<CodeSetInfo> codeSets) {
                  if (codeSetInfos == null) {

                     codeSetInfos = new ListStore<CodeSetInfo>();
                     codeSetInfoList = new ComboBox<CodeSetInfo>();
                     codeSetInfoList.setEmptyText("Please select CodeSet...");
                     codeSetInfoList.setDisplayField("index");
                     codeSetInfoList.setSimpleTemplate("{category}");

                     codeSetInfoList.setWidth(150);
                     codeSetInfoList.setStore(codeSetInfos);
                     codeSetInfoList.setTriggerAction(TriggerAction.ALL);
                     codeSetInfoList.setEditable(false);

                     selectContainer.add(codeSetInfoList);
                     codeSetInfos.add(codeSets);
                     setStyleOfComboBox(codeSetInfoList);
                     codeSetInfoList
                           .addSelectionChangedListener(new SelectionChangedListener<CodeSetInfo>() {

                              @Override
                              public void selectionChanged(
                                    SelectionChangedEvent<CodeSetInfo> se) {
                                 showGrid(se.getSelectedItem());
                                 codeSetInfoList.setRawValue(se
                                       .getSelectedItem().getCategory());
                              }

                           });

                  } else {
                     cleanCodeGrid();
                     cleanCodeSetComboBox();
                     codeSetInfos.add(codeSets);
                     codeSetInfoList.setVisible(true);
                  }

               }
            });

   }

   /**
    * show the Ir commands from the currently selected code set
    * 
    * @param selectedItem
    */
   private void showGrid(CodeSetInfo selectedItem) {
      // wrapper.mask("Please Wait...");
      IrFileParserProxy.loadIRCommands(selectedItem,
            new AsyncSuccessCallback<ArrayList<IRCommandInfo>>() {

               @Override
               public void onSuccess(ArrayList<IRCommandInfo> iRCommands) {

                  if (listStore == null) {
                     listStore = new ListStore<IRCommandInfo>();
                  } else {
                     listStore.removeAll();
                  }
                  listStore.add(iRCommands);

                  if (cm == null) {
                     List<ColumnConfig> codeGridColumns = new ArrayList<ColumnConfig>();
                     codeGridColumns.add(new ColumnConfig("name", "Name", 120));
                     codeGridColumns.add(new ColumnConfig("originalCode",
                           "Original Code", 250));
                     codeGridColumns.add(new ColumnConfig("comment", "Comment",
                           250));
                     cm = new ColumnModel(codeGridColumns);
                  }

                  if (codeGrid == null) {
                     codeGrid = new Grid<IRCommandInfo>(listStore, cm);

                     GridView gv = new GridView();
                     codeGrid.setView(gv);
                     // invalid code lines are rendered in red
                     gv.setViewConfig(new GridViewConfig() {
                        @Override
                        public String getRowStyle(ModelData model,
                              int rowIndex, ListStore<ModelData> ds) {
                           if (model != null) {
                              if (model.get("code") == null) {
                                 return "row-invalid_file_imported_code";
                              } else {
                                 return "";
                              }
                           } else {
                              return "";
                           }
                        }

                     });

                     codeGrid.setLoadMask(true);
                     codeGrid.setHeight(400);
                     codeGrid.getSelectionModel().addSelectionChangedListener(
                           new SelectionChangedListener<IRCommandInfo>() {
                              // if trying to select invalid line,
                              // remove it from selection
                              @Override
                              public void selectionChanged(
                                    SelectionChangedEvent<IRCommandInfo> se) {
                                 List<IRCommandInfo> selectedItems = se
                                       .getSelection();
                                 for (IRCommandInfo irCommandInfo : selectedItems) {
                                    if (irCommandInfo.getCode() == null) {
                                       codeGrid.getSelectionModel().deselect(
                                             irCommandInfo);
                                    }
                                 }
                                 if (codeGrid.getSelectionModel()
                                       .getSelectedItems().size() > 0) {
                                    nextButton.setEnabled(true);
                                 } else {
                                    nextButton.setEnabled(false);
                                 }

                              }
                           });

                     commandContainer.add(codeGrid);
                  } else {
                     codeGrid.getStore().removeAll();
                     codeGrid.getStore().add(iRCommands);
                  }
                  wrapper.unmask();
               }
            });

   }

   /**
    * Hides the combobox and clean the grid
    */
   public void hideComboBoxes() {
      if (brandInfoList != null) {
         brandInfoList.setVisible(false);
      }
      if (deviceInfoList != null) {

         deviceInfoList.setVisible(false);
      }
      if (codeSetInfoList != null) {

         codeSetInfoList.setVisible(false);
      }
      cleanCodeGrid();
   }

   /**
    * cleans the grid
    */
   private void cleanCodeGrid() {
      if (codeGrid != null) {
         codeGrid.getSelectionModel().deselectAll();
         codeGrid.removeFromParent();
         codeGrid.removeAllListeners();
         nextButton.setEnabled(false);
         codeGrid = null;
      }

   }

   /**
    * cleans the device combobox
    */
   private void cleanDeviceComboBox() {
      if (deviceInfos != null) {
         deviceInfos.removeAll();
         deviceInfoList.clearSelections();
         deviceInfoList.getStore().removeAll();
      }

   }

   /**
    * cleans the code set combo box
    */
   private void cleanCodeSetComboBox() {
      if (codeSetInfos != null) {
         codeSetInfos.removeAll();
         codeSetInfoList.clearSelections();
         codeSetInfoList.getStore().removeAll();
      }
   }

   /**
    * cleans the brand combo box
    */
   private void cleanBrandComboBox() {
      if (brandInfos != null) {
         brandInfoList.clearSelections();
         brandInfos.removeAll();
      }
   }

   /**
    * Sets the style of combo box.
    * 
    * @param box
    *           the new style of combo box
    */
   private void setStyleOfComboBox(ComboBox<?> box) {
      box.setWidth(170);
      box.setMaxHeight(250);

   }

  public void setProntoFileHandle(String prontoFileHandle) {
    this.prontoFileHandle = prontoFileHandle;
  }
      
}
