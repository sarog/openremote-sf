package org.openremote.web.console.view;

import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.ext.SpinnerComponent;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.form.FormButton;
import org.openremote.web.console.widget.panel.form.FormField;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;
import org.openremote.web.console.widget.panel.form.FormButton.EnumFormButtonType;
import org.openremote.web.console.widget.panel.form.FormField.EnumFormInputType;

import com.google.gwt.user.client.DOM;

public class ControllerCredentialsScreenView extends ScreenViewImpl {
	public static final String TITLE = "Controller Settings";
	
	public ControllerCredentialsScreenView() {
		// Create a label title
		LabelComponent title = new LabelComponent();
		title.setText(TITLE);
		title.setColor("#FFFFFF");
		title.setFontSize(30);
		
		// Create container for spinner
		AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
		absPanel.setHeight("50px");
		absPanel.setWidth("100%");
		absPanel.setPosition(0,0);
		absPanel.setComponent(title);
		
		// Create a form panel
		FormPanelComponent form = new FormPanelComponent();
		form.setHeight("200px");
		form.setWidth("95%");
		form.setPosition("2%", "60px");
		
		// Add fields
		FormField fieldUrl = new FormField();
		fieldUrl.setLabel("Controller URL:");
		fieldUrl.setInputType(EnumFormInputType.TEXTBOX);
		FormField fieldPanelName = new FormField();
		fieldPanelName.setLabel("Panel Name:");
		fieldPanelName.setInputType(EnumFormInputType.TEXTBOX);
		form.addField(fieldUrl);
		form.addField(fieldPanelName);
		
		// Add Buttons
		FormButton submitBtn = new FormButton(EnumFormButtonType.SUBMIT);
		form.addButton(submitBtn);
		FormButton cancelBtn = new FormButton(EnumFormButtonType.CANCEL);
		form.addButton(cancelBtn);
		
		// Add components to screen view
		super.addPanelComponent(absPanel);
		super.addPanelComponent(form);
	}
}
