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

import org.openremote.web.console.client.event.SubmitEvent;
import org.openremote.web.console.client.listener.FormSubmitListener;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;

/**
 * Adds custom server to custom server grid in SettingsWindow.
 */
public class AddServerWindow extends FormWindow {
   private static  final String SERVER_URL = "server_url";
   public AddServerWindow() {
      super();
      setHeading("Add Server");
      setSize(350, 150);
      form.setLabelAlign(LabelAlign.TOP);
      form.setLabelWidth(280);
      form.setFieldWidth(280);
      
      addField();
      addButton();
      addListener();
      show();
      setFocusWidget(form.getWidget(0));
   }
   
   private void addField() {
      TextField<String> serverText = new TextField<String>();
      serverText.setName(SERVER_URL);
      serverText.setFieldLabel("Server URL(e.g.:'127.0.0.1:8080/controller')");
      serverText.setAllowBlank(false);
      serverText.setRegex("[a-zA-Z0-9-.]+(.[a-zA-Z]{2,3})?(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9-._?,\'/\\\\+&amp;%$#=~])*");
      serverText.getMessages().setRegexText("Should be a URL, just like 'localhost:8080/controller' ");
      form.add(serverText);
   }
   
   private void addButton() {
      Button loginButton = new Button("OK");
      loginButton.addSelectionListener(new FormSubmitListener(form));

      form.addButton(loginButton);
   }
   
   private void addListener() {
      form.addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            Map<String, String> attrMap = getAttrMap();
            String server = attrMap.get(SERVER_URL);
            
            fireEvent(SubmitEvent.SUBMIT, new SubmitEvent("http://" + server));
            hide();
         }
      });
   }
}
