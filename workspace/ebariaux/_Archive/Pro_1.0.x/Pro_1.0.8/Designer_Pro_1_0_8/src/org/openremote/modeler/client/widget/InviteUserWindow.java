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

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallbackGXT3;
import org.openremote.useraccount.domain.RoleDTO;
import org.openremote.useraccount.domain.UserDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;

/**
 * Dailog for inviting a user to "share" a designer account.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class InviteUserWindow extends Window {

  private static InviteUserWindowUiBinder uiBinder = GWT.create(InviteUserWindowUiBinder.class);

  interface InviteUserWindowUiBinder extends UiBinder<Widget, InviteUserWindow> {
  }

  public InviteUserWindow() {
    rolesCombo = new SimpleComboBox<String>(new StringLabelProvider<String>());
    rolesCombo.add(RoleDTO.ROLE_ADMIN_DISPLAYNAME);
    rolesCombo.add(RoleDTO.ROLE_MODELER_DISPLAYNAME);
    rolesCombo.add(RoleDTO.ROLE_DESIGNER_DISPLAYNAME);
    rolesCombo.add(RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME);
    rolesCombo.setValue(RoleDTO.ROLE_MODELER_DISPLAYNAME);
    
    uiBinder.createAndBindUi(this);
    
//    form.setLabelAlign(LabelAlign.RIGHT); // This was specified in previous version but is not supported as of GXT3.0.0b
   
    emailField.addValidator(new RegExValidator(Constants.REG_EMAIL, "Please input a correct email."));
  }

  @UiFactory
  Window itself() {
    return this;
  }

  @UiField
  FormPanel form;

  @UiField
  TextField emailField;
  
  @UiField(provided = true)
  SimpleComboBox<String> rolesCombo;
  
  @UiHandler("sendInvitationButton")
  void onSendClick(SelectEvent e) {
    if (form.isValid()) {
      form.mask("sending email...");      
      AsyncServiceFactory.getUserRPCServiceAsync().inviteUser(emailField.getValue(), rolesCombo.getValue(), new AsyncSuccessCallbackGXT3<UserDTO>() {
         public void onSuccess(UserDTO userDTO) {
            form.unmask();
            fireEvent(new UserInvitedEvent(userDTO));
         }
         public void onFailure(Throwable caught) {
            super.onFailure(caught);
            form.unmask();
         }
      });     
    }
  }
  
  @UiHandler("cancelButton")
  void onCancelClick(SelectEvent e) {
    this.hide();
  }
  
  public interface UserInvitedHandler extends EventHandler {
    void userInvited(UserDTO user);
  }
  
  public static class UserInvitedEvent extends GwtEvent<UserInvitedHandler> {
    
    public static Type<UserInvitedHandler> TYPE = new Type<UserInvitedHandler>();
    
    private UserDTO user;
    
    public UserInvitedEvent(UserDTO user) {
      super();
      this.user = user;
    }

    @Override
    public Type<UserInvitedHandler> getAssociatedType() {
      return TYPE;
    }

    @Override
    protected void dispatch(UserInvitedHandler handler) {
      handler.userInvited(this.user);
    }    
  }
}
