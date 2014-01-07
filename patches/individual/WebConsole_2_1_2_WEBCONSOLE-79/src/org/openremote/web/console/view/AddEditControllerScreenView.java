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
package org.openremote.web.console.view;

import org.openremote.web.console.widget.panel.form.FormField;
import org.openremote.web.console.widget.panel.form.FormField.EnumFormInputType;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class AddEditControllerScreenView extends ScreenViewImpl {
	public static final String TITLE = "Add/Edit Controller";
	private FormPanelComponent form;
	
	public AddEditControllerScreenView() {
		// Create a form panel
		form = new FormPanelComponent();
		form.setHeight("320px");
		form.setWidth("95%");
		form.setPosition("2%", "60px", null, null);
		form.setDataSource("defaultControllerCredentials");
		
		// Add fields
		FormField fieldUrl = new FormField();
		fieldUrl.setLabel("Controller URL:");
		fieldUrl.setInputType(EnumFormInputType.TEXTBOX);
		fieldUrl.setValidationString("^(http|https)\\://.+:\\d{1,5}/.+$");
		fieldUrl.setName("url");
		
		FormField fieldPanelName = new FormField();
		fieldPanelName.setLabel("Default Panel Name:");
		fieldPanelName.setInputType(EnumFormInputType.TEXTBOX);
		fieldPanelName.setIsOptional(true);
		fieldPanelName.setName("defaultPanel");
		
		FormField fieldUsername = new FormField();
		fieldUsername.setLabel("Username:");
		fieldUsername.setInputType(EnumFormInputType.TEXTBOX);
		fieldUsername.setIsOptional(true);
		fieldUsername.setName("username");
		
		FormField fieldPassword = new FormField();
		fieldPassword.setLabel("Password:");
		fieldPassword.setInputType(EnumFormInputType.PASSWORD);
		fieldPassword.setValidationString("^(\\w|\\d)+$");
		fieldPassword.setIsOptional(true);
		fieldPassword.setName("password");
		
		form.addField(fieldUrl);
		form.addField(fieldPanelName);
		form.addField(fieldUsername);
		form.addField(fieldPassword);

		// Add Buttons
//		FormButtonComponent submitBtn = new FormButtonComponent(form, EnumFormButtonType.SUBMIT);
//		form.addButton(submitBtn);
//		FormButtonComponent cancelBtn = new FormButtonComponent(form, EnumFormButtonType.CANCEL);
//		Navigate navigate = AutoBeanService.getInstance().getFactory().create(Navigate.class).as();
//		navigate.setTo("controllerlist");
//		List<DataValuePair> dataValues = new ArrayList<DataValuePair>();
//		DataValuePair pair = AutoBeanService.getInstance().getFactory().create(DataValuePair.class).as();
//		pair.setName("controllerName");
//		pair.setName("http://multimation.co.uk:8080/controller");
//		dataValues.add(pair);
//		navigate.setData(dataValues);
//		cancelBtn.setNavigate(navigate);
//		form.addButton(cancelBtn);
		
		// Add components to screen view
		super.addPanelComponent(form);
	}
}
