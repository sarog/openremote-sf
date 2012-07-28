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
package org.openremote.modeler.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.icon.IconResources;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.AddControllerWindow.ControllerAddedEvent;
import org.openremote.useraccount.domain.ControllerDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * This window is for managing controller that are linked to the account.
 * 
 * @author <a href = "mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class ControllerManageWindow extends Window {  
  
  private IconResources icons = GWT.create(IconResources.class);
  
  @UiField(provided = true)
  BorderLayoutData northData = new BorderLayoutData(36);
    
  private static ControllerManageWindowUiBinder uiBinder = GWT.create(ControllerManageWindowUiBinder.class);

  interface ControllerDTOProvider extends PropertyAccess<ControllerDTO> {    
    @Path("oid")
    ModelKeyProvider<ControllerDTO> key();
    
    ValueProvider<ControllerDTO, String> macAddress();
    ValueProvider<ControllerDTO, Long> oid();
  }
  
  private ControllerDTOProvider controllers = GWT.create(ControllerDTOProvider.class);

  interface ControllerManageWindowUiBinder extends UiBinder<Widget, ControllerManageWindow> {
  }
  
  @UiFactory
  Window itself() {
    return this;
  }

  @UiField
  TextButton addControllerButton;
     
  @UiField(provided=true)
  Grid<ControllerDTO> linkedControllerGrid;

  private ColumnModel<ControllerDTO> linkedControllerColumnModel;
  private ListStore<ControllerDTO> linkedControllerStore;

  public ControllerManageWindow() {

    createLinkedControllerGrid();

    uiBinder.createAndBindUi(this);
      
    linkedControllerGrid.getView().setAutoExpandColumn(linkedControllerGrid.getColumnModel().getColumn(0));      
    linkedControllerGrid.mask("Loading linked controller...");

    show();
    
    AsyncServiceFactory.getLinkControllerRPCServiceAsync().getLinkedControllerDTOs(new AsyncSuccessCallback<ArrayList<ControllerDTO>>() {
      @Override
      public void onSuccess(ArrayList<ControllerDTO> linkedController) {
        linkedControllerStore.addAll(linkedController);
        linkedControllerGrid.unmask();
            setPixelSize(453, 281);
      }
    });

  }

  /**
   * Brings up a dialog to enter a MAC address for a new controller.
   * On success, linked controller table is updated with newly linked controller.
   */   
  @UiHandler("addControllerButton")
  void onAddControllerClick(SelectEvent e) {
    final AddControllerWindow addControllerWindow = new AddControllerWindow();

    addControllerWindow.addHandler(new AddControllerWindow.ControllerAddedHandler() {
      @Override
      public void controllerAdded(ControllerDTO controller) {
        if (controller != null) {
          linkedControllerStore.add(controller);
        }
        addControllerWindow.hide();
      }
    }, ControllerAddedEvent.TYPE);
    addControllerWindow.show();
  }
   
  private void createLinkedControllerGrid() {
    linkedControllerStore = new ListStore<ControllerDTO>(controllers.key());

    ColumnConfig<ControllerDTO, Long> idColumn = new ColumnConfig<ControllerDTO, Long>(controllers.oid(), 20, "Id");
    idColumn.setSortable(false);
     
    ColumnConfig<ControllerDTO, String> macAddressColumn = new ColumnConfig<ControllerDTO, String>(controllers.macAddress(), 210, "MAC Address");
     
    ColumnConfig<ControllerDTO, String> deleteColumn = createDeleteColumn(new TextButtonCell() {
      public void render(Context context, String value, SafeHtmlBuilder sb) {       
        super.render(context, "", sb);
      }
    }, linkedControllerStore);
     
    List<ColumnConfig<ControllerDTO, ?>> l = new ArrayList<ColumnConfig<ControllerDTO, ?>>();
    l.add(idColumn);
    l.add(macAddressColumn);
    l.add(deleteColumn);
    
    linkedControllerColumnModel = new ColumnModel<ControllerDTO>(l);
    linkedControllerGrid = new Grid<ControllerDTO>(linkedControllerStore, linkedControllerColumnModel);
  }

   
  private ColumnConfig<ControllerDTO, String> createDeleteColumn(TextButtonCell button, ListStore<ControllerDTO> store) {
    ColumnConfig<ControllerDTO, String> deleteColumn = new ColumnConfig<ControllerDTO, String>(controllers.macAddress(), 45, "Delete");
    deleteColumn.setSortable(false);
    button.setIcon(icons.delete());
    button.addSelectHandler(createDeleteSelectHandler(store));
    deleteColumn.setCell(button);
    return deleteColumn;
  }
   
  private SelectHandler createDeleteSelectHandler(final ListStore<ControllerDTO> store) {
    return new SelectHandler() { 
      @Override
      public void onSelect(SelectEvent event) {
        final ControllerDTO controller = store.get(event.getContext().getIndex());
        AsyncServiceFactory.getLinkControllerRPCServiceAsync().deleteController(controller.getOid(), new AsyncSuccessCallback<Void>() {
          public void onSuccess(Void result) {
            store.remove(controller);
            Info.display("Delete controller", "Delete controller '" + controller.getOid() + "' success.");
          }
        });
      }
    };
  }
}