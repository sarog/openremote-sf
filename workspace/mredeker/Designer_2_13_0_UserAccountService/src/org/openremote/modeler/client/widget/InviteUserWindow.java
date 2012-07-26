package org.openremote.modeler.client.widget;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallbackGXT3;
import org.openremote.useraccount.domain.RoleDTO;
import org.openremote.useraccount.domain.UserDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.sencha.gxt.widget.core.client.info.Info;

public class InviteUserWindow extends Window {

  private static InviteUserWindowUiBinder uiBinder = GWT.create(InviteUserWindowUiBinder.class);

  interface RoleProvider extends PropertyAccess<Role> {
    @Path("displayName")
    ModelKeyProvider<Role> key();
    
    LabelProvider<Role> displayName();
  }  
  private RoleProvider roles = GWT.create(RoleProvider.class);
  
  private ListStore<Role> rolesStore = new ListStore<Role>(roles.key());

  interface InviteUserWindowUiBinder extends UiBinder<Widget, InviteUserWindow> {
  }

  public InviteUserWindow() {
    rolesStore.add(new Role(RoleDTO.ROLE_ADMIN_DISPLAYNAME));
    Role selectedRole = new Role(RoleDTO.ROLE_MODELER_DISPLAYNAME);
    rolesStore.add(selectedRole);
    rolesStore.add(new Role(RoleDTO.ROLE_DESIGNER_DISPLAYNAME));
    rolesStore.add(new Role(RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME));

            
    uiBinder.createAndBindUi(this);
   

    
    // Note: when widget is provided, none of the settings in the ui.xml file is taken into account  
//      allowBlank="false" forceSelection="true"

//    rolesCombo.setAllowBlank(false);

   rolesCombo.setForceSelection(true);
   
//   rolesCombo.setValue(selectedRole);


    emailField.addValidator(new RegExValidator(Constants.REG_EMAIL, "Please input a correct email."));
    
    rolesCombo.setWidth(220);
    
    form.addSubmitHandler(new SubmitHandler() {
      
      @Override
      public void onSubmit(SubmitEvent event) {
        form.mask("sending email...");
        
        // TODO: where does validation happen on the form ???
        
        
        AsyncServiceFactory.getUserRPCServiceAsync().inviteUser(emailField.getValue(), rolesCombo.getValue().getDisplayName(), new AsyncSuccessCallbackGXT3<UserDTO>() {
                   public void onSuccess(UserDTO userDTO) {
                      form.unmask();
//                      fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(userDTO));
                   }
                   public void onFailure(Throwable caught) {
                      super.onFailure(caught);
                      form.unmask();
                   }
        });     
        }
    });
    Info.display("INFO", "Out of constructor");
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
  ComboBox<Role> rolesCombo = new ComboBox<Role>(rolesStore, roles.displayName());

  @UiHandler("sendInvitationButton")
  void onSendClick(SelectEvent e) {
    
    
    // TODO: check how form validation works
    
    form.submit();
  }
  
  @UiHandler("cancelButton")
  void onCancelClick(SelectEvent e) {
    this.hide();
  }
  
  public class Role {
    
    private String displayName;
    
    public Role(String displayName) {
      super();
      this.displayName = displayName;
    }

    public String getDisplayName() {
      return displayName;
    }

  }

}
