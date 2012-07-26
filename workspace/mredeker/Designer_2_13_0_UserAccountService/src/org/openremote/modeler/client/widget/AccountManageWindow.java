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

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.useraccount.domain.UserDTO;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * This window is for managing users that with the same account, except for the current user.
 */
public class AccountManageWindow extends Window {  
  
//  private Icons icons = GWT.create(Icons.class);
  
  private static AccountManageWindowUiBinder uiBinder = GWT.create(AccountManageWindowUiBinder.class);

  interface UserDTOProvider extends PropertyAccess<UserDTO> {    
    @Path("oid")
    ModelKeyProvider<UserDTO> key();
    
    ValueProvider<UserDTO, String> username();
    ValueProvider<UserDTO, String> email();
  }
  
  private UserDTOProvider users = GWT.create(UserDTOProvider.class);

  interface AccountManageWindowUiBinder extends UiBinder<Widget, AccountManageWindow> {
  }
  
  @UiFactory
  Window itself() {
    return this;
  }

  @UiField
  TextButton inviteUserButton;
     
   /** The invited users grid.
    *  The user has been invited, but not accept the invitation.
    */
//   private Grid<UserDTO> invitedUsersGrid = null;
   
   private long cureentUserId = 0;
   
   private ColumnModel<UserDTO> cm;
   private ListStore<UserDTO> store;
   
   public AccountManageWindow(long cureentUserId) {
      this.cureentUserId = cureentUserId;
      
//      setButtonAlign(HorizontalAlignment.CENTER);
//      setAutoHeight(true);

      
//      addInvitedUsers();
//      createAccessUserGrid();

    ColumnConfig<UserDTO, String> emailColumn = new ColumnConfig<UserDTO, String>(users.email(), 180, "OpenRemote user");
    emailColumn.setSortable(false);
    emailColumn.setCell(new AbstractCell<String>() {
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
        UserDTO user = store.findModelWithKey((String)context.getKey());
        sb.appendHtmlConstant("<span title='");
        sb.appendEscaped(user.getUsername());
        sb.appendHtmlConstant("'>");
        sb.appendEscaped(value);
        if (AccountManageWindow.this.cureentUserId == Long.parseLong((String)context.getKey())) {
          sb.appendHtmlConstant("<b> - me </b>");
        }
        sb.appendHtmlConstant("</span>");
      }
    });
     List<ColumnConfig<UserDTO, ?>> l = new ArrayList<ColumnConfig<UserDTO, ?>>();
     l.add(emailColumn);
      //l.add(fileNameColumn);
     cm = new ColumnModel<UserDTO>(l);
     store = new ListStore<UserDTO>(users.key());
//      store.addSortInfo(new StoreSortInfo<UserDTO>(assets.name(), SortDir.ASC));

      uiBinder.createAndBindUi(this);
      
      AsyncServiceFactory.getUserRPCServiceAsync().getAccountAccessUsersDTO(new AsyncSuccessCallback<ArrayList<UserDTO>>() {
        @Override
        public void onSuccess(ArrayList<UserDTO> accessUsers) {
          store.addAll(accessUsers);

        /*
        if (accessUsers.size() > 0) {
          accessUsersGrid.getStore().add(DTOHelper.createModels(accessUsers));
          accessUsersGrid.unmask();
        }
        */          
          
          
        }
      });

      
      
      

      show();
   }
   
   /**
    * Adds a button, if click it, it would pop up a window to input a email and select role.
    * After submit the window's data, there would send a invitation to the email, and the invited
    * user grid would be insert a record.  
    */
   
   @UiHandler("inviteUserButton")
   void onInviteClick(SelectEvent e) {
     final InviteUserWindow inviteUserWindow = new InviteUserWindow();
     inviteUserWindow.show();
     /*
     inviteUserWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
        public void afterSubmit(SubmitEvent be) {
           inviteUserWindow.hide();
           UserDTO userDTO = be.getData();
           if (userDTO != null) {
              if (invitedUsersGrid == null) {
                 createInvitedUserGrid();
              }
              invitedUsersGrid.stopEditing();
              invitedUsersGrid.getStore().insert(DTOHelper.getBeanModel(userDTO), 0);
              invitedUsersGrid.startEditing(0, 1);
           }
        }
        */
   }

   /**
    * Initialize the invited user grid's store by getting the invited users from server.
    */
   private void addInvitedUsers() {
      AsyncServiceFactory.getUserRPCServiceAsync().getPendingInviteesByAccount(new AsyncSuccessCallback<ArrayList<UserDTO>>() {
         public void onSuccess(ArrayList<UserDTO> invitedUsers) {
            if (invitedUsers.size() > 0) {
               createInvitedUserGrid();
//               invitedUsersGrid.getStore().add(DTOHelper.createModels(invitedUsers));
            }
         }
      });
   }
   
   /**
    * Initialize the invited user grid.
    * The grid has three columns: invited user info, role combobox and the delete button. 
    */
   private void createInvitedUserGrid() {
     /*
      List<ColumnConfig> invitedUserConfigs = new ArrayList<ColumnConfig>();
      invitedUserConfigs.add(new ColumnConfig("eMail", "Invited user", 180));
      
       GridCellRenderer<BeanModel> comboRenderer = new GridCellRenderer<BeanModel>() {
         public Object render(final BeanModel model, String property, ColumnData config, final int rowIndex,
               final int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid) {
            return createRoleCombo(model, property);
         }
      };
      
      GridCellRenderer<BeanModel> buttonRenderer = new GridCellRenderer<BeanModel>() {
         public Object render(final BeanModel model, String property, ColumnData config, final int rowIndex,
               final int colIndex, final ListStore<BeanModel> store, Grid<BeanModel> grid) {
            return createDeleteButton(model, store);
         }
      };
      
      ColumnConfig roleColumn = new ColumnConfig("role", "Role", 190);
      roleColumn.setRenderer(comboRenderer);
      invitedUserConfigs.add(roleColumn);
      
      ColumnConfig actionColumn = new ColumnConfig("delete", "Delete", 50);
      actionColumn.setRenderer(buttonRenderer);
      invitedUserConfigs.add(actionColumn);
      
      invitedUsersGrid = new EditorGrid<BeanModel>(new ListStore<BeanModel>(), new ColumnModel(invitedUserConfigs));
      ContentPanel pendingContainer = new ContentPanel();
      pendingContainer.setBodyBorder(false);
      pendingContainer.setHeading("Pending invitations");
      pendingContainer.setLayout(new FitLayout());
      pendingContainer.setSize(440, 150);
      pendingContainer.add(invitedUsersGrid);
      insert(pendingContainer, 1);
      layout();
      center();
      */
   }
   
   @UiFactory
   Grid<UserDTO> createGrid() {
     Grid<UserDTO> grid = new Grid<UserDTO>(store, cm);
     return grid;
   }


   /**
    * Creates the user accessed grid, the grid stores the user that can access the account.
    * The grid is used for managing the accessed users, except the current user, it has three 
    * columns: email, role and delete.
    */
   private void createAccessUserGrid() {
      List<ColumnConfig> accessUserConfigs = new ArrayList<ColumnConfig>();
      /*
      
       GridCellRenderer<BeanModel> comboRenderer = new GridCellRenderer<BeanModel>() {
         public Object render(final BeanModel model, String property, ColumnData config, final int rowIndex,
               final int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid) {
            if (cureentUserId != (Long) model.get("oid")) {
               return createRoleCombo(model, property);
            } else {
               return (String) model.get(property);
            }
            
         }
      };
      
      GridCellRenderer<BeanModel> buttonRenderer = new GridCellRenderer<BeanModel>() {
         public Object render(final BeanModel model, String property, ColumnData config, final int rowIndex,
               final int colIndex, final ListStore<BeanModel> store, Grid<BeanModel> grid) {
            Button deleteButton = createDeleteButton(model, store);
            if (cureentUserId == (Long) model.get("oid")) {
               deleteButton.disable();
               deleteButton.hide();
            }
            return deleteButton;
         }
      };
      
      
      ColumnConfig roleColumn = new ColumnConfig("role", "Role", 190);
      roleColumn.setSortable(false);
      roleColumn.setRenderer(comboRenderer);
      accessUserConfigs.add(roleColumn);
      
      ColumnConfig actionColumn = new ColumnConfig("delete", "Delete", 50);
      actionColumn.setSortable(false);
      actionColumn.setRenderer(buttonRenderer);
      accessUserConfigs.add(actionColumn);
      
      final Grid<UserDTO> accessUsersGrid = new Grid<UserDTO>(new ListStore<UserDTO>(), new ColumnModel(accessUserConfigs)) {
         @Override
         protected void afterRender() {
            super.afterRender();
            layout();
            center();
            this.mask("Loading users...");
         }
      };
      
//      ContentPanel accessUsersContainer = new ContentPanel();
//      accessUsersContainer.setBodyBorder(false);
//      accessUsersContainer.setHeading("Users with account access");
      accessUsersContainer.setLayout(new FitLayout());
      accessUsersContainer.setStyleAttribute("paddingTop", "5px");
//      accessUsersContainer.setSize(440, 150);
      accessUsersContainer.add(accessUsersGrid);
      add(accessUsersContainer);
      AsyncServiceFactory.getUserRPCServiceAsync().getAccountAccessUsersDTO(new AsyncSuccessCallback<ArrayList<UserDTO>>() {
         public void onSuccess(ArrayList<UserDTO> accessUsers) {
            if (accessUsers.size() > 0) {
               accessUsersGrid.getStore().add(DTOHelper.createModels(accessUsers));
               accessUsersGrid.unmask();
            }
         }
         public void onFailure(Throwable caught) {
            super.onFailure(caught);
            accessUsersGrid.unmask();
         }
      });
      */
   }
   
   /**
    * Creates the role combobox for selecting role.
    * 
    * @param model the model
    * @param property the property
    * 
    * @return the simple combo box< string>
    */
   /*
   private SimpleComboBox<String> createRoleCombo(final BeanModel model, String property) {
      SimpleComboBox<String> combo = new SimpleComboBox<String>();
      combo.setWidth(182);
      combo.setForceSelection(true);
      combo.setEditable(false);
      combo.setTriggerAction(TriggerAction.ALL);
      combo.add(RoleDTO.ROLE_ADMIN_DISPLAYNAME);
      combo.add(RoleDTO.ROLE_MODELER_DISPLAYNAME);
      combo.add(RoleDTO.ROLE_DESIGNER_DISPLAYNAME);
      combo.add(RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME);
      combo.setValue(combo.findModel((String) model.get(property)));
      combo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){
         public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
            final String roleStrs = se.getSelectedItem().getValue();
            if (!roleStrs.equals(model.get("role"))) {
               AsyncServiceFactory.getUserRPCServiceAsync().updateUserRoles(((UserDTO)model.getBean()).getOid(), roleStrs, new AsyncSuccessCallback<UserDTO>() {
                  public void onSuccess(UserDTO userDTO) {
                     ((UserDTO)model.getBean()).setRole(userDTO.getRole());
                     Info.display("Change role", "Change role to " + roleStrs + " success.");
                  }
               });
               
            }
         }
         
      });
      return combo;
   }
      */

   /**
    * Creates the delete button to delete the user record in the grid.
    * 
    * @param model the model
    * @param store the store
    * 
    * @return the button
    */
/*
   private Button createDeleteButton(final BeanModel model, final ListStore<BeanModel> store) {
      Button deleteButton = new Button();
      deleteButton.setIcon(icons.delete());
      deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            AsyncServiceFactory.getUserRPCServiceAsync().deleteUser(((UserDTO)model.getBean()).getOid(), new AsyncSuccessCallback<Void>() {
               public void onSuccess(Void result) {
                  store.remove(model);
                  Info.display("Delete user", "Delete user " + model.get("username").toString() + " success.");
               }
            });
         }
      });
      return deleteButton;
   }
   */

}
