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
package org.openremote.modeler.client.widget.buildingmodeler;

import java.util.ArrayList;
import java.util.List;

import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;
import org.openremote.modeler.client.event.DevicesCreatedEvent;
import org.openremote.modeler.client.rpc.DeviceDiscoveryRPCService;
import org.openremote.modeler.client.rpc.DeviceDiscoveryRPCServiceAsync;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * This window is for creating devices and commands based on a wizard
 * 
 * @author <a href = "mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class DiscoveredDevicesWizardWindow extends Window {  
  
  private boolean oneDevicePerProtocol = false;
  private EventBus eventBus;
  
  @UiField(provided=true)
  Grid<DiscoveredDeviceDTO> newDevicesGrid;

  private ColumnModel<DiscoveredDeviceDTO> newDevicesListColumnModel;
  private ListStore<DiscoveredDeviceDTO> newDevicesListStore;
  private ArrayList<DiscoveredDeviceDTO> newDevicesList;
  private DeviceDiscoveryRPCServiceAsync ddService = (DeviceDiscoveryRPCServiceAsync) GWT.create(DeviceDiscoveryRPCService.class);
  
  private static CreateDeviceWizardWindowUiBinder uiBinder = GWT.create(CreateDeviceWizardWindowUiBinder.class);
  
  interface DiscoveredDeviceDTOProvider extends PropertyAccess<DiscoveredDeviceDTO> {    
    @Path("oid")
    ModelKeyProvider<DiscoveredDeviceDTO> key();
    
    ValueProvider<DiscoveredDeviceDTO, Long> oid();
    ValueProvider<DiscoveredDeviceDTO, String> model();
    ValueProvider<DiscoveredDeviceDTO, String> name();
    ValueProvider<DiscoveredDeviceDTO, String> protocol();
    ValueProvider<DiscoveredDeviceDTO, String> type();
    ValueProvider<DiscoveredDeviceDTO, Boolean> used();
    
  }

  private DiscoveredDeviceDTOProvider newDevicesProvider = GWT.create(DiscoveredDeviceDTOProvider.class);
  private IdentityValueProvider<DiscoveredDeviceDTO> identity = new IdentityValueProvider<DiscoveredDeviceDTO>();
  private CheckBoxSelectionModel<DiscoveredDeviceDTO> selectionModel = new CheckBoxSelectionModel<DiscoveredDeviceDTO>(identity);

  interface CreateDeviceWizardWindowUiBinder extends UiBinder<Widget, DiscoveredDevicesWizardWindow> {
  }
  
  @UiFactory
  Window itself() {
    return this;
  }

  public DiscoveredDevicesWizardWindow(ArrayList<DiscoveredDeviceDTO> result, EventBus eventBus) {
    this.eventBus = eventBus;
    this.newDevicesList = result;
    selectionModel.setSelectionMode(SelectionMode.MULTI);
    createNewDevicesGrid();
    uiBinder.createAndBindUi(this);
    newDevicesListStore.addAll(this.newDevicesList);
    show();
  }

  private void createNewDevicesGrid()
  {
    newDevicesListStore = new ListStore<DiscoveredDeviceDTO>(newDevicesProvider.key());

    ColumnConfig<DiscoveredDeviceDTO, String> nameColumn = new ColumnConfig<DiscoveredDeviceDTO, String>(newDevicesProvider.name(), 100, "Name");
    ColumnConfig<DiscoveredDeviceDTO, String> modelColumn = new ColumnConfig<DiscoveredDeviceDTO, String>(newDevicesProvider.model(), 100, "Model");
    ColumnConfig<DiscoveredDeviceDTO, String> protocolColumn = new ColumnConfig<DiscoveredDeviceDTO, String>(newDevicesProvider.protocol(), 100, "Protocol");
    ColumnConfig<DiscoveredDeviceDTO, String> typeColumn = new ColumnConfig<DiscoveredDeviceDTO, String>(newDevicesProvider.type(), 175, "Type");
    ColumnConfig<DiscoveredDeviceDTO, Boolean> usedColumn = new ColumnConfig<DiscoveredDeviceDTO, Boolean>(newDevicesProvider.used(), 100, "Used");
     
    List<ColumnConfig<DiscoveredDeviceDTO, ?>> l = new ArrayList<ColumnConfig<DiscoveredDeviceDTO, ?>>();
    l.add(selectionModel.getColumn());
    l.add(nameColumn);
    l.add(modelColumn);
    l.add(protocolColumn);
    l.add(typeColumn);
    l.add(usedColumn);

    newDevicesListColumnModel = new ColumnModel<DiscoveredDeviceDTO>(l);
    newDevicesGrid = new Grid<DiscoveredDeviceDTO>(newDevicesListStore, newDevicesListColumnModel);
    newDevicesGrid.setSelectionModel(selectionModel);
    newDevicesGrid.getView().setStripeRows(true);
    newDevicesGrid.getView().setColumnLines(true);
    newDevicesGrid.getView().setAutoExpandColumn(nameColumn);
    
  }
  
  @UiHandler(value = {"menuItemAll", "menuItemOnlyNew", "menuItemCreate", "menuItemExit", "menuEdit", "menuOptions"})
  public void onMenuSelection(SelectionEvent<Item> event) {
    MenuItem item = (MenuItem) event.getSelectedItem();
    if (item.getText().equals("Exit wizard")) {
      this.hide();
    } else if (item.getText().equals("Create objects per device")) {
      oneDevicePerProtocol = false;
    } else if (item.getText().equals("Create objects per protocol")) {
      oneDevicePerProtocol = true;
    } else if (item.getText().equals("Delete selected")) {
      deleteSelected();
    } else if (item.getText().equals("Show all")) {
      loadDiscoveredDevices(false);
    } else if (item.getText().equals("Show only new")) {
      loadDiscoveredDevices(true);
    } else if (item.getText().equals("Create selected devices")) {
      createDevices();
    }
  }

  private void createDevices()
  {
    ArrayList<DiscoveredDeviceDTO> itemsToCreate = (ArrayList<DiscoveredDeviceDTO>)selectionModel.getSelectedItems();
    ddService.createORDevices(itemsToCreate, oneDevicePerProtocol, new AsyncCallback<ArrayList<DeviceDTO>>() {
      public void onFailure(Throwable caught) {
         MessageBox.alert("Info", caught.getMessage(), null);
      }
      public void onSuccess(ArrayList<DeviceDTO> devices)
      {
        eventBus.fireEvent(new DevicesCreatedEvent(devices));
        Info.display("Info", "Added " + devices.size() + " devices successfully.");
        itself().hide();
      }
    });
  }

  private void loadDiscoveredDevices(boolean onlyNew)
  {
    newDevicesListStore.clear();
    newDevicesGrid.mask("Loading discovered devices ...");
    ddService.loadDevices(onlyNew, new AsyncCallback<ArrayList<DiscoveredDeviceDTO>>() {
        public void onFailure(Throwable caught) {
           MessageBox.alert("Info", caught.getMessage(), null);
           newDevicesGrid.unmask();
        }
        public void onSuccess(final ArrayList<DiscoveredDeviceDTO> result)
        {
          newDevicesListStore.addAll(result);
          newDevicesGrid.unmask();
        }
    });
  }

  private void deleteSelected()
  {
    ArrayList<DiscoveredDeviceDTO> itemsToDelete = (ArrayList<DiscoveredDeviceDTO>)selectionModel.getSelectedItems();
    ddService.deleteDevices(itemsToDelete, new AsyncCallback<Void>() {
      public void onFailure(Throwable caught) {
         MessageBox.alert("Info", caught.getMessage(), null);
      }
      public void onSuccess(Void result)
      {
        loadDiscoveredDevices(false);
      }
    });
  }

}