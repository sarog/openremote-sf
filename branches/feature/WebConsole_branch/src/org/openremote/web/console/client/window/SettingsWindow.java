package org.openremote.web.console.client.window;


import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.client.gxtextends.StringModelData;
import org.openremote.web.console.client.icon.Icons;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class SettingsWindow extends Dialog {
   
   private Icons icons = GWT.create(Icons.class);
   
   public SettingsWindow() {
      setHeading("Account management");
      setButtonAlign(HorizontalAlignment.CENTER);
      setAutoHeight(true);
      setButtons(Dialog.OKCANCEL);
      setWidth(452);
      setHeight(350);
      createCustomServerGrid();
      show();
   }
   
   private void createCustomServerGrid() {
      List<ColumnConfig> accessUserConfigs = new ArrayList<ColumnConfig>();
      
      GridCellRenderer<StringModelData> serverRenderer = new GridCellRenderer<StringModelData>() {
        public Object render(final StringModelData model, String property, ColumnData config, final int rowIndex,
              final int colIndex, ListStore<StringModelData> store, Grid<StringModelData> grid) {
           addStyleName("x-grid3-cell-selected");
           return model.getValue();
        }
        
     };
     
     GridCellRenderer<StringModelData> buttonRenderer = new GridCellRenderer<StringModelData>() {
        public Object render(final StringModelData model, String property, ColumnData config, final int rowIndex,
              final int colIndex, final ListStore<StringModelData> store, Grid<StringModelData> grid) {
           Button deleteButton = createDeleteButton(model, store);
           return deleteButton;
        }
     };
     
     ColumnConfig serverColumn = new ColumnConfig("customServer", "Server", 220);
     serverColumn.setSortable(false);
     serverColumn.setRenderer(serverRenderer);
     accessUserConfigs.add(serverColumn);
     
     ColumnConfig actionColumn = new ColumnConfig("delete", "Delete", 50);
     actionColumn.setSortable(false);
     actionColumn.setRenderer(buttonRenderer);
     accessUserConfigs.add(actionColumn);
     
     final StringModelData data1 = new StringModelData("customServer", "http://127.0.0.1:8080/controller");
     EditorGrid<StringModelData> accessUsersGrid = new EditorGrid<StringModelData>(new ListStore<StringModelData>(), new ColumnModel(accessUserConfigs)) {
        @Override
        protected void afterRender() {
           super.afterRender();
           this.getSelectionModel().select(0, false);
           System.out.println("selected:"+this.getSelectionModel().getSelectedItem().getValue());
        }
     };
     
     accessUsersGrid.getStore().add(data1);
     accessUsersGrid.getStore().add(new StringModelData("customServer", "http://localhost:8080/controller"));
     
     ContentPanel accessUsersContainer = new ContentPanel();
     accessUsersContainer.setBodyBorder(false);
     accessUsersContainer.setHeading("Users with account access");
     accessUsersContainer.setLayout(new FitLayout());
     accessUsersContainer.setStyleAttribute("paddingTop", "5px");
     accessUsersContainer.setSize(440, 150);
     accessUsersContainer.add(accessUsersGrid);
     add(accessUsersContainer);
   }
   
   private Button createDeleteButton(StringModelData model, ListStore<StringModelData> store) {
      Button deleteButton = new Button();
      deleteButton.setIcon(icons.delete());
      deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
//            AsyncServiceFactory.getUserRPCServiceAsync().deleteUser(((User)model.getBean()).getOid(), new AsyncSuccessCallback<Void>() {
//               public void onSuccess(Void result) {
//                  store.remove(model);
//                  Info.display("Delete user", "Delete user " + model.get("username").toString() + " success.");
//               }
//            });
         }
      });
      return deleteButton;
   }
}
