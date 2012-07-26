package org.openremote.modeler.client.widget;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallbackGXT3;
import org.openremote.useraccount.domain.RoleDTO;
import org.openremote.useraccount.domain.UserDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent;
import com.sencha.gxt.widget.core.client.event.SubmitEvent.SubmitHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.sencha.gxt.widget.core.client.info.Info;

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
    
    rolesCombo.setAllowBlank(false);
    rolesCombo.setAllowTextSelection(false);
    rolesCombo.setEditable(false);
    rolesCombo.setForceSelection(true);
    rolesCombo.setTriggerAction(TriggerAction.ALL);
    rolesCombo.setWidth(220);
    
    uiBinder.createAndBindUi(this);
   

    
    // Note: when widget is provided, none of the settings in the ui.xml file is taken into account  
//      allowBlank="false" forceSelection="true"

   
    emailField.addValidator(new RegExValidator(Constants.REG_EMAIL, "Please input a correct email."));
    
    form.addSubmitHandler(new SubmitHandler() {
      
      @Override
      public void onSubmit(SubmitEvent event) {
        form.mask("sending email...");
        
        // TODO: where does validation happen on the form ???
        
        
        AsyncServiceFactory.getUserRPCServiceAsync().inviteUser(emailField.getValue(), rolesCombo.getValue(), new AsyncSuccessCallbackGXT3<UserDTO>() {
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
  SimpleComboBox<String> rolesCombo;

  // Even try with the provided, but doubt it'll work
  // TODO: try with constructor and see if then XML can be used to define attributes
  
  
  @UiHandler("sendInvitationButton")
  void onSendClick(SelectEvent e) {
    
    
    // TODO: check how form validation works
    
    form.submit();
  }
  
  @UiHandler("cancelButton")
  void onCancelClick(SelectEvent e) {
    this.hide();
  }
  


  
  
  
  
  
  
  /**
   * The inner class is for inviting a user have the same account, it would send a invitation to the email.
   */
/*
  private class InviteUserWindow extends FormWindow {
     public InviteUserWindow() {
//        setSize(370, 150);
//        setHeading("Invite user");
        form.setLabelAlign(LabelAlign.RIGHT);
        createFields();
        createButtons(this);
        add(form);
        show();
     }
     */
     /**
      * Creates two fields: email address input and role combobox.
      */
/*
     private void createFields() {
        final TextField<String> emailField = new TextField<String>();
        emailField.setFieldLabel("Email address");
        emailField.setAllowBlank(false);
        emailField.setRegex(Constants.REG_EMAIL);
        emailField.getMessages().setRegexText("Please input a correct email.");
        
        final ComboBoxExt roleList = new ComboBoxExt();
        roleList.setFieldLabel("Role");
        roleList.getStore().add(new ComboBoxDataModel<String>(RoleDTO.ROLE_ADMIN_DISPLAYNAME, RoleDTO.ROLE_ADMIN_DISPLAYNAME));
        roleList.getStore().add(new ComboBoxDataModel<String>(RoleDTO.ROLE_MODELER_DISPLAYNAME, RoleDTO.ROLE_MODELER_DISPLAYNAME));
        roleList.getStore().add(new ComboBoxDataModel<String>(RoleDTO.ROLE_DESIGNER_DISPLAYNAME, RoleDTO.ROLE_DESIGNER_DISPLAYNAME));
        roleList.getStore().add(new ComboBoxDataModel<String>(RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME, RoleDTO.ROLE_MODELER_DESIGNER_DISPLAYNAME));
        roleList.setValue(new ComboBoxDataModel<String>(RoleDTO.ROLE_MODELER_DISPLAYNAME, RoleDTO.ROLE_MODELER_DISPLAYNAME));
        form.add(emailField);
        form.add(roleList);
        
        form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
           public void handleEvent(FormEvent be) {
              form.mask("sending email...");
              AsyncServiceFactory.getUserRPCServiceAsync().inviteUser(emailField.getValue(),
                    roleList.getValue().get("data").toString(), new AsyncSuccessCallback<UserDTO>() {
                       public void onSuccess(UserDTO userDTO) {
                          form.unmask();
                          fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(userDTO));
                       }
                       public void onFailure(Throwable caught) {
                          super.onFailure(caught);
                          form.unmask();
                       }
                       
                    });
           }
        });
     }
        */
     
     /**
      * Creates two buttons to send invitation or cancel.
      * 
      * @param window the window
      */
     /*
     private void createButtons(final InviteUserWindow window) {
        Button send = new Button("Send invitation");
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
     */
}
