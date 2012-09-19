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
import org.openremote.modeler.client.rpc.AsyncSuccessCallbackGXT3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * This window is for changing passwords.
 * 
 * @author <a href = "mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class ChangePasswordWindow extends Window {  

  private static ChangePasswordWindowUiBinder uiBinder = GWT.create(ChangePasswordWindowUiBinder.class);

  interface ChangePasswordWindowUiBinder extends UiBinder<Widget, ChangePasswordWindow> {
  }
  
  @UiFactory
  Window itself() {
    return this;
  }
  
  @UiField
  FormPanel form;
  @UiField
  PasswordField oldPasswordField;
  @UiField
  PasswordField newPasswordField;
  @UiField
  PasswordField newPasswordFieldCheck;

  public ChangePasswordWindow() {
    uiBinder.createAndBindUi(this);
    Validator<String> myPwValidator1 = new Validator<String>() {
      @Override
      public List<EditorError> validate(Editor<String> editor, String value)
      {
        ArrayList<EditorError> a = new ArrayList<EditorError>();
        if (value.length() < 6 || value.length() > 16) {
          a.add(new DefaultEditorError(editor, "Password must be between 6 and 16 characters long.", value));
        }
        return a;
      }
    };
    Validator<String> myPwValidator2 = new Validator<String>() {
      @Override
      public List<EditorError> validate(Editor<String> editor, String value)
      {
        ArrayList<EditorError> a = new ArrayList<EditorError>();
        if (!newPasswordField.getValue().equals(newPasswordFieldCheck.getValue())) {
          a.add(new DefaultEditorError(editor, "Passwords do not match", value));
        }
        return a;
      }
    }; 
    newPasswordField.addValidator(myPwValidator1);
    newPasswordFieldCheck.addValidator(myPwValidator2);
    show();
  }
    
  @UiHandler("saveButton")
  void onSendClick(SelectEvent e) {
    if (form.isValid()) {
      form.mask("Saving password...");      
      AsyncServiceFactory.getUserRPCServiceAsync().changePassword(oldPasswordField.getValue(), newPasswordField.getValue(), new AsyncSuccessCallbackGXT3<Void>() {
         public void onSuccess(Void result) {
            form.unmask();
            Info.display("Info", "The password was changed.");
            itself().hide();
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
  
}