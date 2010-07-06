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
package org.openremote.modeler.client.view;

import java.util.List;

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.event.ResponseJSONEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ResponseJSONListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.AuthorityRPCService;
import org.openremote.modeler.client.rpc.AuthorityRPCServiceAsync;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.Protocols;
import org.openremote.modeler.client.widget.uidesigner.ImportZipWindow;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Class ApplicationView.
 * 
 * @author Tomsky,Allen
 */
public class ApplicationView implements View {
   
   /** The viewport. */
   private Viewport viewport;
   
   /** The icon. */
   private Icons icons = GWT.create(Icons.class);
   
   /** The authority. */
   private Authority authority;
   
   /** The ui designer view. */
   private UIDesignerView uiDesignerView;
   
   /**
    * Initialize.
    * 
    * @see org.openremote.modeler.client.view.View#initialize()
    */
   public void initialize() {
      Protocols.getInstance(); // get protocol definition from xml files 
      viewport = new Viewport();
      viewport.setLayout(new BorderLayout());
      final AuthorityRPCServiceAsync auth = (AuthorityRPCServiceAsync) GWT.create(AuthorityRPCService.class);
      final ApplicationView that = this;
      auth.getAuthority(new AsyncCallback<Authority>() {
         public void onFailure(Throwable caught) {
            MessageBox.info("Info", caught.getMessage(), null);
         }
         public void onSuccess(Authority authority) {
            if (authority != null) {
               that.authority = authority;
               createNorth();
               createCenter(authority);
               createSouth();
               show();
            } else {
               Window.open("login.jsp", "_self", null);
            }
         }
         
      });
//      createNorth();
//      createCenter();
//      createSouth();
   }
   
   /**
    * Show.
    */
   private void show() {
      RootPanel.get().add(viewport);
   }
   
   
   /**
    * Creates the north.
    */
   private void createNorth() {
      HorizontalPanel headerPanel = new HorizontalPanel();
      headerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      
      ToolBar applicationToolBar = new ToolBar();
      applicationToolBar.add(createApplicationFileBtn());
      applicationToolBar.add(createApplicationHelpBtn());
      headerPanel.add(applicationToolBar);
      headerPanel.setCellHorizontalAlignment(applicationToolBar, HorizontalPanel.ALIGN_LEFT);
      
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(headerPanel, data);
   }
   
   /**
    * Creates the application file btn.
    * 
    * @return the button
    */
   private Button createApplicationFileBtn() {
      Button applicationFileButton = new Button("File");
      applicationFileButton.ensureDebugId(DebugId.APPLICATION_FILE_BTN);

      Menu fileMenu = new Menu();
      final MenuItem saveMenuItem = createSaveMenuItem();
      final MenuItem exportMenuItem = createExportMenuItem();
      
      exportMenuItem.setEnabled(false);
      
      fileMenu.add(saveMenuItem);
//      fileMenu.add(createImportMenuItem());
      fileMenu.add(exportMenuItem);
//      fileMenu.add(restoreMenuItem);
      fileMenu.add(createLogoutMenuItem());
      
      fileMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {
         @Override
         public void handleEvent(MenuEvent be) {
            final boolean enabled;
            if (BeanModelDataBase.panelTable.loadAll().size() > 0) {
               enabled = true;
            } else {
               enabled = false;
            }
            exportMenuItem.setEnabled(enabled);
            UtilsProxy.canRestore(new AsyncCallback<Boolean>(){
               @Override
               public void onFailure(Throwable caught) {
                  saveMenuItem.setEnabled(enabled || false);
               }

               @Override
               public void onSuccess(Boolean result) {
                  /*
                   * make sure the persist file will be deleted. 
                   */
                  saveMenuItem.setEnabled(enabled || result);
               }
               
            });
            
         }
         
      });
      applicationFileButton.setMenu(fileMenu);
      return applicationFileButton;
   }
   
   /**
    * Creates the application help btn.
    * 
    * @return the button
    */
   private Button createApplicationHelpBtn() {
      Button applicationHelpButton = new Button("Help");
      applicationHelpButton.ensureDebugId(DebugId.APPLICATION_HELP_BTN);
      return applicationHelpButton;
   }
   
   /**
    * Creates the save menu item.
    * 
    * @return the menu item
    */
   private MenuItem createSaveMenuItem() {
      MenuItem saveMenuItem = new MenuItem("Save");
      saveMenuItem.ensureDebugId(DebugId.SAVE);
      saveMenuItem.setIcon(icons.saveIcon());
      saveMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            uiDesignerView.saveUiDesignerLayout();
         }
      });
      return saveMenuItem;
   }
   
   /*private MenuItem createRestoreMenuItem() {
      final MenuItem restoreMenuItem = new MenuItem("Restore");
      restoreMenuItem.setEnabled(false);
      UtilsProxy.canRestore(new AsyncCallback<Boolean>(){
         @Override
         public void onFailure(Throwable caught) {
            restoreMenuItem.setEnabled(false);
         }

         @Override
         public void onSuccess(Boolean result) {
            restoreMenuItem.setEnabled(result);
         }
         
      });
      restoreMenuItem.setIcon(icons.saveIcon());
      restoreMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            uiDesignerView.mask("restoring from server...");
            uiDesignerView.restore();
            uiDesignerView.layout();
            uiDesignerView.unmask();
         }
      });
      return restoreMenuItem;
   }*/
   
   /**
    * Creates the import menu item.
    * 
    * @return the menu item
    */
   @SuppressWarnings("unused")
   private MenuItem createImportMenuItem() {
      MenuItem importMenuItem = new MenuItem("Import");
      importMenuItem.ensureDebugId(DebugId.IMPORT);
      importMenuItem.setIcon(icons.importIcon());
      final ApplicationView that = this;
      importMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            final ImportZipWindow importWindow = new ImportZipWindow();
            importWindow.addListener(ResponseJSONEvent.RESPONSEJSON, new ResponseJSONListener() {
               @Override
               public void afterSubmit(ResponseJSONEvent be) {
//                  that.activityPanel.reRenderTree(be.getData().toString(), screenTab);
                  importWindow.hide();
               }
            });
         }
      });
      return importMenuItem;
   }
   
   /**
    * Creates the export menu item.
    * 
    * @return the menu item
    */
   private MenuItem createExportMenuItem() {
      MenuItem exportMenuItem = new MenuItem("Export");
      exportMenuItem.ensureDebugId(DebugId.EXPORT);
      exportMenuItem.setIcon(icons.exportIcon());
      exportMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            if (!isExportedDataValid()) {
               MessageBox.info("Info", "Sorry, the data you want to export is invalid.", null);
               return;
            }
            viewport.mask("Exporting, please wait.");
            UtilsProxy.exportFiles(IDUtil.currentID(), uiDesignerView.getAllPanels(), new AsyncSuccessCallback<String>() {
               @Override
               public void onSuccess(String exportURL) {
                  viewport.unmask();
                  Window.open(exportURL, "_blank", "");
               }
            });
         }
      });
      return exportMenuItem;
   }
   
   /**
    * Checks if is exported data valid.
    * 
    * @return true, if is exported data valid
    */
   protected boolean isExportedDataValid() {   
      List<BeanModel> screenBeanModels = BeanModelDataBase.screenTable.loadAll();
      if (screenBeanModels == null || screenBeanModels.size() == 0) {
         return false;
      }
      return true;
   }
   
   /**
    * Creates the logout menu item.
    * 
    * @return the component
    */
   private MenuItem createLogoutMenuItem() {
      String currentUserName = (this.authority == null) ? "" : "(" + this.authority.getUsername() + ")";
      MenuItem logoutMenuItem = new MenuItem("Logout" + currentUserName);
      logoutMenuItem.ensureDebugId(DebugId.LOGOUT);
      logoutMenuItem.setIcon(icons.logout());
      logoutMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            Window.open("j_security_logout", "_self", "");
         }
      });
      return logoutMenuItem;
   }

   /**
    * Creates the center.
    * 
    * @param authority the authority
    */
   private void createCenter(Authority authority) {
      List<String> roles = authority.getRoles();
      TabPanel modelerTabPanel = new TabPanel();
      if (roles.contains("ROLE_MODELER")) {
         BuildingModelerView buildingModelerItem = new BuildingModelerView();
         buildingModelerItem.initialize();
         modelerTabPanel.add(buildingModelerItem);
      }
      if (roles.contains("ROLE_DESIGNER")) {
         UIDesignerView uiDesignerItem = new UIDesignerView();
         uiDesignerItem.initialize();
         modelerTabPanel.add(uiDesignerItem);
//         modelerTabPanel.setSelection(uiDesignerItem); // Temp to show uiDesigner. It will remove after development.
         this.uiDesignerView = uiDesignerItem;
      }
      modelerTabPanel.setAutoSelect(true);
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(modelerTabPanel, data);

   }
   
   /**
    * Creates the south.
    */
   private void createSouth() {
      // Status status = new Status();
      // BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 20);
      // data.setMargins(new Margins(0,5,0,5));
      // viewport.add(status, data);
   }

}
