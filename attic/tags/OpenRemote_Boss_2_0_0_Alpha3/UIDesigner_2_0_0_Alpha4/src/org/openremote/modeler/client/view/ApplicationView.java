/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

   private LayoutContainer modelerContainer;
   
   private BuildingModelerView buildingModelerView;
   
   /** The ui designer view. */
   private UIDesignerView uiDesignerView;

   private Button saveButton;
   
   private Button exportButton;
   
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
               show();
            } else {
               Window.open("login.jsp", "_self", null);
            }
         }

      });
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
      ToolBar applicationToolBar = new ToolBar();
      List<String> roles = authority.getRoles();
      if (roles.contains("ROLE_MODELER") && roles.contains("ROLE_DESIGNER")) {
         applicationToolBar.add(createBMButton());
         applicationToolBar.add(createUDButton());
         SeparatorToolItem separatorItem = new SeparatorToolItem();
         separatorItem.setWidth("20");
         applicationToolBar.add(separatorItem);
      }
      if (roles.contains("ROLE_DESIGNER")) {
         initSaveAndExportButtons();
         applicationToolBar.add(saveButton);
         applicationToolBar.add(exportButton);
         if (roles.contains("ROLE_MODELER")) {
            saveButton.setVisible(false);
            exportButton.setVisible(false);
         }
      }
      applicationToolBar.add(new FillToolItem());
      applicationToolBar.add(createLogoutButton());

      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(applicationToolBar, data);
   }

   private ToggleButton createBMButton() {
      final ToggleButton bmButton = new ToggleButton();
      bmButton.setToolTip("Building Modeler");
      bmButton.setIcon(icons.bmIcon());
      bmButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            if (!bmButton.isPressed()) {
               bmButton.toggle(true);
            } else {
               saveButton.setVisible(false);
               exportButton.setVisible(false);
               modelerContainer.remove(uiDesignerView);
               modelerContainer.add(buildingModelerView);
               modelerContainer.layout();
            }
         }
      });
      bmButton.setToggleGroup("modeler-switch");
      bmButton.toggle(true);
      return bmButton;
   }
   
   private ToggleButton createUDButton() {
      final ToggleButton udButton = new ToggleButton();
      udButton.setToolTip("UIDesigner");
      udButton.setIcon(icons.udIcon());
      udButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            if (!udButton.isPressed()) {
               udButton.toggle(true);
            } else {
               saveButton.setVisible(true);
               exportButton.setVisible(true);
               modelerContainer.remove(buildingModelerView);
               modelerContainer.add(uiDesignerView);
               modelerContainer.layout();
            }
         }
      });
      udButton.setToggleGroup("modeler-switch");
      return udButton;
   }
   
   private void initSaveAndExportButtons() {
      saveButton = new Button();
      saveButton.setIcon(icons.saveIcon());
      saveButton.setToolTip("Save");
      saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (!isExportedDataValid()) {
               MessageBox.info("Info", "Nothing to save.", null);
               return;
            }
            uiDesignerView.saveUiDesignerLayout();
         }
      });
      
      exportButton = new Button();
      exportButton.setIcon(icons.exportAsZipIcon());
      exportButton.setToolTip("Export as zip");
      exportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            if (!isExportedDataValid()) {
               MessageBox.info("Info", "Nothing to export.", null);
               return;
            }
            viewport.mask("Exporting, please wait.");
            UtilsProxy.exportFiles(IDUtil.currentID(), uiDesignerView.getAllPanels(),
                  new AsyncSuccessCallback<String>() {
                     @Override
                     public void onSuccess(String exportURL) {
                        viewport.unmask();
                        Window.open(exportURL, "_blank", "");
                     }
                  });
         }
      });
   }

   private Button createLogoutButton() {
      String currentUserName = (this.authority == null) ? "" : "(" + this.authority.getUsername() + ")";
      Button logoutButton = new Button();
      logoutButton.setIcon(icons.logout());
      logoutButton.setToolTip("Logout" + currentUserName);
      logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            Window.open("j_security_logout", "_self", "");
         }
      });
      return logoutButton;
   }


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
                  // that.activityPanel.reRenderTree(be.getData().toString(), screenTab);
                  importWindow.hide();
               }
            });
         }
      });
      return importMenuItem;
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
    * Creates the center.
    * 
    * @param authority
    *           the authority
    */
   private void createCenter(Authority authority) {
      List<String> roles = authority.getRoles();
      modelerContainer = new LayoutContainer();
      modelerContainer.setLayout(new FitLayout());
      if (roles.contains("ROLE_MODELER")) {
         this.buildingModelerView = new BuildingModelerView();
         modelerContainer.add(buildingModelerView);
      }
      if (roles.contains("ROLE_DESIGNER")) {
         this.uiDesignerView = new UIDesignerView();
         if (!roles.contains("ROLE_MODELER")) {
            modelerContainer.add(uiDesignerView);
         }
      }
      
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(modelerContainer, data);

   }

}
