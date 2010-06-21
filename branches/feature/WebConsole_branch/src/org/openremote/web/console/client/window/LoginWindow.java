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
import org.openremote.web.console.client.rpc.AsyncServiceFactory;
import org.openremote.web.console.client.rpc.AsyncSuccessCallback;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.Cookies;

public class LoginWindow extends FormWindow {

   private static final String USERNAME = "username";
   private static final String PASSWORD = "password";
   
   public LoginWindow() {
      super();
      setHeading("Login");
      setSize(350, 150);
      
      addFields();
      addButtons();
      addListener();
      
      show();
   }
   
   private void addFields() {
      TextField<String> usernameField = new TextField<String>();
      usernameField.setAllowBlank(false);
      usernameField.setFieldLabel("Username");
      usernameField.setName(USERNAME);
      usernameField.setValue(Cookies.getCookie(Constants.CONSOLE_USERNAME));
      
      TextField<String> passwordField = new TextField<String>();
      passwordField.setAllowBlank(false);
      passwordField.setFieldLabel("Passowrd");
      passwordField.setName(PASSWORD);
      passwordField.setPassword(true);
      passwordField.setValue(Cookies.getCookie(Constants.CONSOLE_PASSWORD));
      
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
            final String username = attrMap.get(USERNAME);
            final String password = attrMap.get(PASSWORD);
            AsyncServiceFactory.getUserCacheServiceAsync().saveUser(username, password, new AsyncSuccessCallback<Void>() {
               public void onSuccess(Void result) {
                  Cookies.setCookie(Constants.CONSOLE_USERNAME, username);
                  Cookies.setCookie(Constants.CONSOLE_PASSWORD, password);
               }
            });
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent());
            hide();
         }
      });
   }
}
