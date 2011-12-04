package org.openremote.web.console.view;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.form.FormButtonComponent;
import org.openremote.web.console.widget.panel.form.FormField;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;
import org.openremote.web.console.widget.panel.form.FormButtonComponent.EnumFormButtonType;
import org.openremote.web.console.widget.panel.form.FormField.EnumFormInputType;

public class AddEditControllerScreenView extends ScreenViewImpl {
	public static final String TITLE = "Add/Edit Controller";
	private FormPanelComponent form;
	
	public AddEditControllerScreenView() {
		// Create a form panel
		form = new FormPanelComponent();
		form.setHeight("320px");
		form.setWidth("95%");
		form.setPosition("2%", "60px");
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
		FormButtonComponent submitBtn = new FormButtonComponent(EnumFormButtonType.SUBMIT);
		form.addButton(submitBtn);
		FormButtonComponent cancelBtn = new FormButtonComponent(EnumFormButtonType.CANCEL);
		Navigate navigate = AutoBeanService.getInstance().getFactory().create(Navigate.class).as();
		navigate.setTo("controllerlist");
		List<DataValuePair> dataValues = new ArrayList<DataValuePair>();
		DataValuePair pair = AutoBeanService.getInstance().getFactory().create(DataValuePair.class).as();
		pair.setName("controllerName");
		pair.setName("http://multimation.co.uk:8080/controller");
		dataValues.add(pair);
		navigate.setData(dataValues);
		cancelBtn.setNavigate(navigate);
		form.addButton(cancelBtn);
		
		// Add components to screen view
		super.addPanelComponent(form);
	}
}
