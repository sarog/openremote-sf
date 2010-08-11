/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.client.window;

import java.util.Map;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.event.SubmitEvent;
import org.openremote.web.console.client.listener.FormSubmitListener;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.domain.UserInfo;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.Cookies;

/**
 * Store the username and password to Cookies.
 */
public class LoginWindow extends FormWindow {

   private static final String USERNAME = "username";
   private static final String PASSWORD = "password";
   
   public LoginWindow() {
      super();
      setHeading("Login");
      setSize(360, 150);
      
      addFields();
      addButtons();
      addListener();
      
      show();
      setFocusWidget(form.getWidget(0));
   }
   
   private void addFields() {
      UserInfo userInfo = ClientDataBase.userInfo;
      TextField<String> usernameField = new TextField<String>();
      usernameField.setAllowBlank(false);
      usernameField.setFieldLabel("Username");
      usernameField.setName(USERNAME);
      if (!"".equals(userInfo.getUsername())) {
         usernameField.setValue(userInfo.getUsername());
      }
      
      TextField<String> passwordField = new TextField<String>();
      passwordField.setAllowBlank(false);
      passwordField.setFieldLabel("Passowrd");
      passwordField.setName(PASSWORD);
      passwordField.setPassword(true);
      
      form.add(usernameField);
      form.add(passwordField);
   }
   
   private void addButtons() {
      Button loginButton = new Button("Login");
      loginButton.addSelectionListener(new FormSubmitListener(form));

      Button cancelButton = new Button("Cancel");
      cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            hide();
         }
      });
      
      form.addButton(loginButton);
      form.addButton(cancelButton);
   }
   
   private void addListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            Map<String, String> attrMap = getAttrMap();
            String username = attrMap.get(USERNAME);
            String password = attrMap.get(PASSWORD);
            ClientDataBase.userInfo.setUsername(username);
            ClientDataBase.userInfo.setPassword(password);
            Cookies.setCookie(Constants.CONSOLE_USERINFO, ClientDataBase.userInfo.toJson());
            
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent());
            hide();
         }
      });
   }
}
