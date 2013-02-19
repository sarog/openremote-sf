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
package org.openremote.modeler.client.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.DevicesCreatedEvent;
import org.openremote.modeler.client.event.ResponseJSONEvent;
import org.openremote.modeler.client.event.ScreenTableLoadedEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ResponseJSONListener;
import org.openremote.modeler.client.presenter.UIDesignerPresenter;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.AuthorityRPCService;
import org.openremote.modeler.client.rpc.AuthorityRPCServiceAsync;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.Protocols;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.AccountManageWindow;
import org.openremote.modeler.client.widget.uidesigner.ImportZipWindow;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;
import org.openremote.modeler.exception.UIRestoreException;
import org.openremote.modeler.shared.dto.DeviceDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * The application's main view, which create a viewport and added it into rootPanel.
 * 
 * The viewport use border layout, initial a toolbar in the north part, 
 * buildingModelerView or uiDesignerView in the center part.
 * It would show buildingModelerView or uiDesignerView by the user role.
 * 
 * @author Tomsky,Allen
 */
public class ApplicationView implements View {

  /** Event bus used for communication throughout application */
  private EventBus eventBus;

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
   private UIDesignerPresenter uiDesignerPresenter;

   private Button saveButton;
   
   private Button exportButton;
   
   public ApplicationView(EventBus eventBus) {
    super();
    this.eventBus = eventBus;
  }

  /**
    * Initialize the application's main view.
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

         public void onSuccess(final Authority authority) {
            if (authority != null) {
               that.authority = authority;
               
               UtilsProxy.loadPanelsFromSession(new AsyncSuccessCallback<Collection<Panel>>() {
                 @Override
                 public void onSuccess(Collection<Panel> panels) {                   
                    if (panels.size() > 0) {
                       initModelDataBase(panels);
                       eventBus.fireEvent(new ScreenTableLoadedEvent());
                    }
                    UtilsProxy.loadMaxID(new AsyncSuccessCallback<Long>() {
                       @Override
                       public void onSuccess(Long maxID) {
                          if (maxID > 0) {              // set the layout component's max id after refresh page.
                             IDUtil.setCurrentID(maxID.longValue());
                          }
                          createNorth();
                          createCenter(authority);
                          show();
                          uiDesignerView.getProfilePanel().setInitialized(true);
                       }                       
                    });
                 }
                 
                 @Override
                 public void onFailure(Throwable caught) {
                    if (caught instanceof UIRestoreException) {
                      uiDesignerView.getProfilePanel().setInitialized(true);
                    }
                    super.onFailure(caught);
                    super.checkTimeout(caught);
                 }

                 private void initModelDataBase(Collection<Panel> panels) {
                    BeanModelDataBase.panelTable.clear();
                    BeanModelDataBase.groupTable.clear();
                    BeanModelDataBase.screenTable.clear();
                    Set<Group> groups = new LinkedHashSet<Group>();
                    Set<ScreenPair> screens = new LinkedHashSet<ScreenPair>();
                    for (Panel panel : panels) {
                       List<GroupRef> groupRefs = panel.getGroupRefs();
                       for (GroupRef groupRef : groupRefs) {
                          groups.add(groupRef.getGroup());
                       }
                       BeanModelDataBase.panelTable.insert(panel.getBeanModel());
                    }
                    
                    for (Group group : groups) {
                       List<ScreenPairRef> screenRefs = group.getScreenRefs();
                       for (ScreenPairRef screenRef : screenRefs) {
                          screens.add(screenRef.getScreen());
                          BeanModelDataBase.screenTable.insert(screenRef.getScreen().getBeanModel());
                       }
                       BeanModelDataBase.groupTable.insert(group.getBeanModel());
                    }
                 }
              });
            } else {
               Window.open("login.jsp", "_self", null);
            }
         }

      });
   }

   /**
    * Add the viewport into rootPanel and show it.
    */
   private void show() {
      RootPanel.get().add(viewport);
   }

   /**
    * Creates the applicationToolBar in the viewport's north part.
    * The applicationToolBar's buttons lies on the user role. 
    */
   private void createNorth() {
      ToolBar applicationToolBar = new ToolBar();
      List<String> roles = authority.getRoles();
      if (roles.contains(Role.ROLE_ADMIN) || (roles.contains(Role.ROLE_DESIGNER) && roles.contains(Role.ROLE_MODELER))) {
         applicationToolBar.add(createBMButton());
         applicationToolBar.add(createUDButton());
         SeparatorToolItem separatorItem = new SeparatorToolItem();
         separatorItem.setWidth("20");
         applicationToolBar.add(separatorItem);
         if (roles.contains(Role.ROLE_ADMIN)) {
            applicationToolBar.add(createAccountManageButton());
         }
         initSaveAndExportButtons();
         applicationToolBar.add(saveButton);
         applicationToolBar.add(exportButton);
         
         applicationToolBar.add(createImportButton());
         
         applicationToolBar.add(createOnLineTestBtn());
      } else if (roles.contains(Role.ROLE_DESIGNER) && !roles.contains(Role.ROLE_MODELER)) {
         initSaveAndExportButtons();
         applicationToolBar.add(saveButton);
         applicationToolBar.add(exportButton);
         applicationToolBar.add(createOnLineTestBtn());
      }
      applicationToolBar.add(new FillToolItem());
      applicationToolBar.add(createLogoutButton());

      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(applicationToolBar, data);
   }

   /**
    * Creates the button that can convert to building modeler view.
    * 
    * @return the toggle button
    */
   private ToggleButton createBMButton() {
      final ToggleButton bmButton = new ToggleButton();
      bmButton.setToolTip("Building Modeler");
      bmButton.setIcon(icons.bmIcon());
      bmButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            if (!bmButton.isPressed()) {
               bmButton.toggle(true);
            } else {
               modelerContainer.remove(uiDesignerView);
               modelerContainer.add(buildingModelerView);
               Cookies.setCookie(Constants.CURRETN_ROLE, Role.ROLE_MODELER);
               modelerContainer.layout();
            }
         }
      });
      bmButton.setToggleGroup("modeler-switch");
      if (Cookies.getCookie(Constants.CURRETN_ROLE) == null || Role.ROLE_MODELER.equals(Cookies.getCookie(Constants.CURRETN_ROLE))) {
         bmButton.toggle(true);
      }
      return bmButton;
   }
   
   /**
    * Creates the button that can convert to ui designer view.
    * 
    * @return the toggle button
    */
   private ToggleButton createUDButton() {
      final ToggleButton udButton = new ToggleButton();
      udButton.setToolTip("UI Designer");
      udButton.setIcon(icons.udIcon());
      udButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            if (!udButton.isPressed()) {
               udButton.toggle(true);
            } else {
               modelerContainer.remove(buildingModelerView);
               modelerContainer.add(uiDesignerView);
               Cookies.setCookie(Constants.CURRETN_ROLE, Role.ROLE_DESIGNER);
               modelerContainer.layout();
            }
         }
      });
      udButton.setToggleGroup("modeler-switch");
      if (Role.ROLE_DESIGNER.equals(Cookies.getCookie(Constants.CURRETN_ROLE))) {
         udButton.toggle(true);
      }
      return udButton;
   }
   
   /**
    * Creates the button that can pop up the testOnline dialog.
    * 
    * @return the button
    */
   private Button createOnLineTestBtn() {
      final Button showDemoBtn = new Button();
      showDemoBtn.setToolTip("Test UI online in your panel. ");
      showDemoBtn.setIcon(icons.onLineTestIcon());
      showDemoBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            UtilsProxy.getOnTestLineURL(new AsyncSuccessCallback<String> () {
               @Override
               public void onSuccess(String result) {
                  Dialog dialog = new Dialog();
                  dialog.setHideOnButtonClick(true);
                  dialog.setButtonAlign(HorizontalAlignment.CENTER);
                  dialog.setClosable(false);
                  dialog.setBodyBorder(false);
                  dialog.setHeading("Test UI Online");
                  dialog.setWidth(310);
                  dialog.setAutoHeight(true);
                  Text text = new Text();
                  text.setWidth("100%");
                  text.setStyleAttribute("backgroundColor", "CCD9E8");
                  text.setText("To test your UI without installing any Controller or deploying configuration, " +
                        "type the following URL into your panel setting as Controller URL :");
                  TextField<String> url = new TextField<String>();
                  url.setWidth("100%");
                  url.setValue(result);
                  url.setReadOnly(true);
                  dialog.add(text);
                  dialog.add(url);
                  dialog.show();
               }
            });
         }
      });
      return showDemoBtn;
   }
   
   /**
    * Inits the save and export buttons on applicationToolBar.
    */
   private void initSaveAndExportButtons() {
      saveButton = new Button();
      saveButton.setIcon(icons.saveIcon());
      saveButton.setToolTip("Save");
      saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            uiDesignerPresenter.saveUiDesignerLayout();
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
            UtilsProxy.exportFiles(IDUtil.currentID(), uiDesignerPresenter.getAllPanels(),
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

   /**
    * Creates the logout button for the user logout the application.
    * 
    * @return the button
    */
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
   private Button createImportButton() {
     
     Button importButton = new Button();
     importButton.setIcon(icons.saveIcon());
     importButton.setToolTip("Import");
     importButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(ButtonEvent ce) {
        final ImportZipWindow importWindow = new ImportZipWindow();
        importWindow.addListener(ResponseJSONEvent.RESPONSEJSON, new ResponseJSONListener() {
           @Override
           public void afterSubmit(ResponseJSONEvent be) {
             
             
             // TODO: encapsulated in std response and test for error messages
             // TODO: or this is done one level above (in import window)
             
             // TODO: depending on what was displayed in the tree, must reload either device or macro or panel ...
             // -> fire all events, depending on listener will pickup what is required

             ArrayList<DeviceDTO> deviceDTOs = new ArrayList<DeviceDTO>();
             JSONArray jsonDeviceDTOs = JSONParser.parseStrict((String) be.getData()).isArray();

             for (int i = 0; i < jsonDeviceDTOs.size(); i++) {
               JSONObject jsonDeviceDTO = jsonDeviceDTOs.get(i).isObject();
               DeviceDTO deviceDTO = new DeviceDTO((long)jsonDeviceDTO.get("oid").isNumber().doubleValue(), jsonDeviceDTO.get("displayName").isString().stringValue());
               deviceDTOs.add(deviceDTO);
             }
             eventBus.fireEvent(new DevicesCreatedEvent(deviceDTOs));

             importWindow.hide();
           }
        });
      }
     });
      return importButton;
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
    * Creates the center view in viewport by the user role, if the role is building modeler, then show the building modeler view, 
    * else show the ui designer view.
    * 
    * @param authority
    *           the authority
    */
   private void createCenter(Authority authority) {
      List<String> roles = authority.getRoles();
      modelerContainer = new LayoutContainer();
      modelerContainer.setLayout(new FitLayout());
      WidgetSelectionUtil widgetSelectionUtil = new WidgetSelectionUtil(eventBus);
      if (roles.contains(Role.ROLE_ADMIN) || (roles.contains(Role.ROLE_DESIGNER) && roles.contains(Role.ROLE_MODELER))) {
         this.buildingModelerView = new BuildingModelerView(eventBus);
         this.uiDesignerView = new UIDesignerView(widgetSelectionUtil);
         this.uiDesignerPresenter = new UIDesignerPresenter(eventBus, this.uiDesignerView, widgetSelectionUtil);
         if (Role.ROLE_DESIGNER.equals(Cookies.getCookie(Constants.CURRETN_ROLE))) {
            modelerContainer.add(uiDesignerView);
         } else {
            modelerContainer.add(buildingModelerView);
         }
      } else if (roles.contains(Role.ROLE_MODELER) && !roles.contains(Role.ROLE_DESIGNER)) {
         this.buildingModelerView = new BuildingModelerView(eventBus);
         modelerContainer.add(buildingModelerView);
      } else if(roles.contains(Role.ROLE_DESIGNER) && !roles.contains(Role.ROLE_MODELER)) {
        this.uiDesignerView = new UIDesignerView(widgetSelectionUtil);
        this.uiDesignerPresenter = new UIDesignerPresenter(eventBus, this.uiDesignerView, widgetSelectionUtil);
         modelerContainer.add(uiDesignerView);
      }
      
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(modelerContainer, data);

   }

   /**
    * Creates the button to pop up the account management window.
    * 
    * @return the button
    */
   private Button createAccountManageButton() {
      Button accountManageBtn = new Button();
      accountManageBtn.setToolTip("Account management");
      accountManageBtn.setIcon(icons.userIcon());
      accountManageBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){
         public void componentSelected(ButtonEvent ce) {
            AsyncServiceFactory.getUserRPCServiceAsync().getUserId(new AsyncSuccessCallback<Long>() {
               public void onSuccess(Long currentUserId) {
                  new AccountManageWindow(currentUserId);
               }
            });
         }
      });
      return accountManageBtn;
   }
}
