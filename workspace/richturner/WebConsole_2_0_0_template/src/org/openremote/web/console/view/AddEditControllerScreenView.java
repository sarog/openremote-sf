package org.openremote.web.console.view;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.form.FormButton;
import org.openremote.web.console.widget.panel.form.FormField;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;
import org.openremote.web.console.widget.panel.form.FormButton.EnumFormButtonType;
import org.openremote.web.console.widget.panel.form.FormField.EnumFormInputType;

public class AddEditControllerScreenView extends ScreenViewImpl {
	public static final String TITLE = "Add/Edit Controller";
	private FormPanelComponent form;
	
	public AddEditControllerScreenView() {
		// Create a label title
		LabelComponent title = new LabelComponent();
		title.setText(TITLE);
		title.setColor("#FFFFFF");
		title.setFontSize(20);
		
		// Create container for spinner
		AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
		absPanel.setHeight("50px");
		absPanel.setWidth("100%");
		absPanel.setPosition(0,0);
		absPanel.setComponent(title);
		
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
		FormButton submitBtn = new FormButton(EnumFormButtonType.SUBMIT);
		form.addButton(submitBtn);
		FormButton cancelBtn = new FormButton(EnumFormButtonType.CANCEL);
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
		super.addPanelComponent(absPanel);
		super.addPanelComponent(form);
	}
}
