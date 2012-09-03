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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.FormSubmitListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.useraccount.domain.ControllerDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

/**
 * This window is for managing controller, linked to the account
 * @author marcus
 */
public class ControllerManageWindow extends Dialog {
   private Icons icons = GWT.create(Icons.class);
   
   /** 
    * The linked controller grid.
    */
   private EditorGrid<BeanModel> linkedControllerGrid = null;
   
   public ControllerManageWindow() {
      setHeading("Controller management");
      setHideOnButtonClick(true);
      setButtonAlign(HorizontalAlignment.CENTER);
      setAutoHeight(true);
      setButtons("");
      setWidth(452);
      setMinHeight(280);
      addControllerButton();
      createLinkedControllerGrid();
      show();
   }
   
   /**
    * Adds a button, if click it, it would pop up a window to input a controller MAC address<br>
    * After the MAC address is submitted, it's checked against unlinked controller MAC address and<br> 
    * if a match is found, the controller is linked to this account and the link shown in the linked<br>
    * controller grid. 
    */
   private void addControllerButton() {
      Button inviteUserButton = new Button("Link controller");
      inviteUserButton.setIcon(icons.controllerAddIcon());
      inviteUserButton.setIconAlign(IconAlign.LEFT);
      inviteUserButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            final LinkControllerWindow linkControllerWindow = new LinkControllerWindow();
            linkControllerWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               public void afterSubmit(SubmitEvent be) {
                 linkControllerWindow.hide();
                  ControllerDTO controllerDTO = be.getData();
                  if (controllerDTO != null) {
                     if (linkedControllerGrid == null) {
                       createLinkedControllerGrid();
                     }
                     linkedControllerGrid.stopEditing();
//                     linkedControllerGrid.getStore().insert(DTOHelper.getBeanModel(controllerDTO), 0);
                     linkedControllerGrid.startEditing(0, 1);
                  }
               }
            });
         }
      });
      add(inviteUserButton);
   }
   
   
   /**
    * Creates the linkedControllerGrid, the grid stores the controller that are linked to the account.
    * The grid is used for managing the linked controller 
    * columns: MACAddress and delete.
    */
   private void createLinkedControllerGrid() {
      List<ColumnConfig> linkedControllerConfigs = new ArrayList<ColumnConfig>();
      linkedControllerConfigs.add(new ColumnConfig("macAddress", "MAC Address", 180));
      
      GridCellRenderer<BeanModel> buttonRenderer = new GridCellRenderer<BeanModel>() {
        public Object render(final BeanModel model, String property, ColumnData config, final int rowIndex,
              final int colIndex, final ListStore<BeanModel> store, Grid<BeanModel> grid) {
           Button deleteButton = createDeleteButton(model, store);
           return deleteButton;
        }
      };
      ColumnConfig actionColumn = new ColumnConfig("delete", "Delete", 50);
      actionColumn.setSortable(false);
      actionColumn.setRenderer(buttonRenderer);
      linkedControllerConfigs.add(actionColumn);
      
      final EditorGrid<BeanModel> linkedControllerGrid = new EditorGrid<BeanModel>(new ListStore<BeanModel>(), new ColumnModel(linkedControllerConfigs)) {
         @Override
         protected void afterRender() {
            super.afterRender();
            layout();
            center();
            this.mask("Loading linked controller...");
         }
      };
      
      ContentPanel accessUsersContainer = new ContentPanel();
      accessUsersContainer.setBodyBorder(false);
      accessUsersContainer.setHeading("Linked controller");
      accessUsersContainer.setLayout(new FitLayout());
      accessUsersContainer.setStyleAttribute("paddingTop", "5px");
      accessUsersContainer.setSize(440, 150);
      accessUsersContainer.add(linkedControllerGrid);
      add(accessUsersContainer);
      AsyncServiceFactory.getLinkControllerRPCServiceAsync().getLinkedControllerDTOs(new AsyncSuccessCallback<ArrayList<ControllerDTO>>() {
         public void onSuccess(ArrayList<ControllerDTO> linkedControllers) {
            if (linkedControllers.size() > 0) {
              //linkedControllerGrid.getStore().add(DTOHelper.createModels(linkedControllers));
              linkedControllerGrid.unmask();
            }
         }
         public void onFailure(Throwable caught) {
            super.onFailure(caught);
            linkedControllerGrid.unmask();
         }
      });
   }
   

   /**
    * Creates the delete button to remove a linked controller from the account
    * 
    * @param model the model
    * @param store the store
    * 
    * @return the button
    */
   private Button createDeleteButton(final BeanModel model, final ListStore<BeanModel> store) {
      Button deleteButton = new Button();
      deleteButton.setIcon(icons.controllerDeleteIcon());
      deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            AsyncServiceFactory.getLinkControllerRPCServiceAsync().deleteController(((ControllerDTO)model.getBean()).getOid(), new AsyncSuccessCallback<Void>() {
               public void onSuccess(Void result) {
                  store.remove(model);
                  Info.display("Delete controller", "Delete controller '" + model.get("macAddress").toString() + "' success.");
               }
            });
         }
      });
      return deleteButton;
   }

   /**
    * The inner class is for linking a controller
    */
   private class LinkControllerWindow extends FormWindow {
      public LinkControllerWindow() {
         setSize(370, 150);
         setHeading("Add controller");
         form.setLabelAlign(LabelAlign.RIGHT);
         createFields();
         createButtons(this);
         add(form);
         show();
      }
      
      /**
       * Creates one fields: MAC address input
       */
      private void createFields() {
         final TextField<String> macAddressField = new TextField<String>();
         macAddressField.setFieldLabel("MAC address");
         macAddressField.setAllowBlank(false);
         form.add(macAddressField);
         
         form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
            public void handleEvent(FormEvent be) {
               form.mask("Adding controller ...");
               AsyncServiceFactory.getLinkControllerRPCServiceAsync().linkController(macAddressField.getValue(), new AsyncSuccessCallback<ControllerDTO>() {
                    public void onSuccess(ControllerDTO controller) {
                       form.unmask();
                       fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(controller));
                    }
                    public void onFailure(Throwable caught) {
                       super.onFailure(caught);
                       form.unmask();
                    }
                 });
            }
         });
      }
      
      /**
       * Creates two buttons to link controller or cancel.
       * 
       * @param window the window
       */
      private void createButtons(final LinkControllerWindow window) {
         Button send = new Button("Add controller");
         send.addSelectionListener(new FormSubmitListener(form, send));
         Button cancel = new Button("Cancel");
         cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
               window.hide();
            }
         });
         form.addButton(send);
         form.addButton(cancel);
      }
   }
}
