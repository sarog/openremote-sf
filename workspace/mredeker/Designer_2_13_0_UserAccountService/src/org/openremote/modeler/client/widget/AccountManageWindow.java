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
import org.openremote.modeler.client.widget.InviteUserWindow.UserInvitedEvent;
import org.openremote.useraccount.domain.RoleDTO;
import org.openremote.useraccount.domain.UserDTO;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * This window is for managing users that with the same account, except for the current user.
 */
public class AccountManageWindow extends Window {  
  
  private IconResources icons = GWT.create(IconResources.class);
  
  private static AccountManageWindowUiBinder uiBinder = GWT.create(AccountManageWindowUiBinder.class);

  interface UserDTOProvider extends PropertyAccess<UserDTO> {    
    @Path("oid")
    ModelKeyProvider<UserDTO> key();
    
    ValueProvider<UserDTO, String> username();
    ValueProvider<UserDTO, String> email();
    ValueProvider<UserDTO, String> role();
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
     
  @UiField(provided=true)
  Grid<UserDTO> accessUsersGrid;
  
  /** The invited users grid.
   *  The user has been invited, but not accept the invitation.
   */
  @UiField(provided=true)
  Grid<UserDTO> invitedUsersGrid;
  
  @UiField
  ContentPanel invitedUsersPanel;
  
   private long cureentUserId = 0;
   
   private ColumnModel<UserDTO> accessUsersColumnModel;
   private ListStore<UserDTO> accessUsersStore;

   private ColumnModel<UserDTO> invitedUsersColumnModel;
   private ListStore<UserDTO> invitedUsersStore;

   public AccountManageWindow(long cureentUserId) {
      this.cureentUserId = cureentUserId;
      
//      setButtonAlign(HorizontalAlignment.CENTER);
//      setAutoHeight(true);

      accessUsersStore = new ListStore<UserDTO>(users.key());

    ColumnConfig<UserDTO, String> emailColumn = new ColumnConfig<UserDTO, String>(users.email(), 180, "OpenRemote user");
    emailColumn.setSortable(false);
    emailColumn.setCell(new AbstractCell<String>() {
      @Override
      public void render(Context context, String value, SafeHtmlBuilder sb) {
        UserDTO user = accessUsersStore.findModelWithKey((String)context.getKey());
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
    
    ColumnConfig<UserDTO, String> roleColumn = new ColumnConfig<UserDTO, String>(users.role(), 210, "Role");
    
    ColumnConfig<UserDTO, String> deleteColumn = createDeleteColumn(new TextButtonCell() {
      @Override
      public void render(Context context, String value, SafeHtmlBuilder sb) {       
        if (AccountManageWindow.this.cureentUserId != Long.parseLong((String)context.getKey())) {
          super.render(context, "", sb);
        }
      }
    }, accessUsersStore);
    
     List<ColumnConfig<UserDTO, ?>> l = new ArrayList<ColumnConfig<UserDTO, ?>>();
     l.add(emailColumn);
     l.add(roleColumn);
     l.add(deleteColumn);
     
     accessUsersColumnModel = new ColumnModel<UserDTO>(l);
//   store.addSortInfo(new StoreSortInfo<UserDTO>(assets.name(), SortDir.ASC));
     accessUsersGrid = new Grid<UserDTO>(accessUsersStore, accessUsersColumnModel);
     
     final GridInlineEditing<UserDTO> accessUsersGridEditing = new GridInlineEditing<UserDTO>(accessUsersGrid);

     SimpleComboBox<String> rolesCombo = createRoleComboBox(accessUsersGridEditing, invitedUsersStore);     
     accessUsersGridEditing.addEditor(roleColumn, rolesCombo);
     accessUsersGridEditing.addBeforeStartEditHandler(new BeforeStartEditHandler<UserDTO>() {
      @Override
      public void onBeforeStartEdit(BeforeStartEditEvent<UserDTO> event) {
        UserDTO user = accessUsersStore.get(event.getEditCell().getRow());
        if (AccountManageWindow.this.cureentUserId == user.getOid()) {
          event.setCancelled(true);
        }
      }
    });
     
     invitedUsersStore = new ListStore<UserDTO>(users.key());

     l = new ArrayList<ColumnConfig<UserDTO, ?>>();
     emailColumn = new ColumnConfig<UserDTO, String>(users.email(), 180, "Invited user");

     roleColumn = new ColumnConfig<UserDTO, String>(users.role(), 210, "Role");

     deleteColumn = createDeleteColumn(new TextButtonCell() {
       @Override
       public void render(Context context, String value, SafeHtmlBuilder sb) {
           super.render(context, "", sb);
       }
     }, invitedUsersStore);
     
     l.add(emailColumn);
     l.add(roleColumn);
     l.add(deleteColumn);
     
     invitedUsersColumnModel = new ColumnModel<UserDTO>(l);
     invitedUsersGrid = new Grid<UserDTO>(invitedUsersStore, invitedUsersColumnModel);
     
    
     final GridInlineEditing<UserDTO> gridEditing = new GridInlineEditing<UserDTO>(invitedUsersGrid);
     
     rolesCombo = createRoleComboBox(gridEditing, invitedUsersStore);     
     gridEditing.addEditor(roleColumn, rolesCombo);

      uiBinder.createAndBindUi(this);
      
      accessUsersGrid.getView().setAutoExpandColumn(accessUsersGrid.getColumnModel().getColumn(2));
      invitedUsersGrid.getView().setAutoExpandColumn(emailColumn);
//      invitedUsersGrid.getView().setStripeRows(true); // This is working
      
      
      
      invitedUsersPanel.setVisible(false);
      
      AsyncServiceFactory.getUserRPCServiceAsync().getAccountAccessUsersDTO(new AsyncSuccessCallback<ArrayList<UserDTO>>() {
        @Override
        public void onSuccess(ArrayList<UserDTO> accessUsers) {
          accessUsersStore.addAll(accessUsers);

        /*
        if (accessUsers.size() > 0) {
          accessUsersGrid.getStore().add(DTOHelper.createModels(accessUsers));
          accessUsersGrid.unmask();
        }
        */          
          
          
        }
      });

      AsyncServiceFactory.getUserRPCServiceAsync().getPendingInviteesByAccount(new AsyncSuccessCallback<ArrayList<UserDTO>>() {
        public void onSuccess(ArrayList<UserDTO> invitedUsers) {
           if (invitedUsers.size() > 0) {
             invitedUsersStore.addAll(invitedUsers);
             invitedUsersPanel.setVisible(true);
           }
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

     inviteUserWindow.addHandler(new InviteUserWindow.UserInvitedHandler() {
        @Override
        public void userInvited(UserDTO user) {
          if (user != null) {
            invitedUsersStore.add(user);
          }
          inviteUserWindow.hide();
        }
     }, UserInvitedEvent.TYPE);
     inviteUserWindow.show();
   }

   /**
    * Creates the user accessed grid, the grid stores the user that can access the account.
    * The grid is used for managing the accessed users, except the current user, it has three 
    * columns: email, role and delete.
    */
   private void createAccessUserGrid() {
      List<ColumnConfig> accessUserConfigs = new ArrayList<ColumnConfig>();
      /*
      

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
   
   private SimpleComboBox<String> createRoleComboBox(final GridInlineEditing<UserDTO> gridEditing, final ListStore<UserDTO> store) {
     SimpleComboBox<String> rolesCombo = new SimpleComboBox<String>(new StringLabelProvider<String>());
     rolesCombo.add(RoleDTO.ROLE_ADMIN_DISPLAYNAME);
     rolesCombo.add(RoleDTO.ROLE_MODELER_DISPLAYNAME);
     rolesCombo.add(RoleDTO.ROLE_DESIGNER_DISPLAYNAME);
     rolesCombo.add(RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME);
     rolesCombo.setValue(RoleDTO.ROLE_MODELER_DISPLAYNAME);
     rolesCombo.setAllowBlank(false);
     rolesCombo.setAllowTextSelection(false);
     rolesCombo.setEditable(false);
     rolesCombo.setForceSelection(true);
     rolesCombo.setTriggerAction(TriggerAction.ALL);     
     rolesCombo.addSelectionHandler(new SelectionHandler<String>() {
      
      @Override
      public void onSelection(SelectionEvent<String> event) {
        final UserDTO user = store.get(gridEditing.getActiveCell().getRow());
        final String roleStrs = event.getSelectedItem();        
        
        if (!roleStrs.equals(user.getRole())) {
           AsyncServiceFactory.getUserRPCServiceAsync().updateUserRoles(user.getOid(), roleStrs, new AsyncSuccessCallback<UserDTO>() {
              public void onSuccess(UserDTO userDTO) {
                user.setRoles(userDTO.getRoles());
                 Info.display("Change role", "Change role to " + roleStrs + " success.");
              }
           });
        }
      }
    });
     return rolesCombo;
   }
   
   private ColumnConfig<UserDTO, String> createDeleteColumn(TextButtonCell button, ListStore<UserDTO> store) {
     ColumnConfig<UserDTO, String> deleteColumn = new ColumnConfig<UserDTO, String>(users.email(), 45, "Delete");
     deleteColumn.setSortable(false);
     button.setIcon(icons.delete());
     button.addSelectHandler(createDeleteSelectHandler(store));
     deleteColumn.setCell(button);
     return deleteColumn;
   }
   
   private SelectHandler createDeleteSelectHandler(final ListStore<UserDTO> store) {
     return new SelectHandler() { 
       @Override
       public void onSelect(SelectEvent event) {
         final UserDTO user = store.get(event.getContext().getIndex());         
         AsyncServiceFactory.getUserRPCServiceAsync().deleteUser(user.getOid(), new AsyncSuccessCallback<Void>() {
           public void onSuccess(Void result) {
              store.remove(user);
              Info.display("Delete user", "Delete user " + user.getUsername() + " success.");
           }
        });
       }
     };
   }
}
