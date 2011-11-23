package org.openremote.web.console.widget.panel.form;

import org.openremote.web.console.widget.InteractiveConsoleComponent;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormField extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "formFieldComponent";
	public static final String LABEL_CLASS_NAME = "formFieldLabelComponent";
	public static final String INPUT_CLASS_NAME = "formFieldInputComponent";
	private String label = null;
	private EnumFormInputType inputType;
	private Widget input = null;
	
	public enum EnumFormInputType {
		TEXTBOX,
		TEXTAREA,
		SELECT;
	}
	
	public FormField() {
		super(new VerticalPanel(), CLASS_NAME);
		VerticalPanel container = (VerticalPanel)getWidget();
		container.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		container.setSpacing(0);
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setInputType(EnumFormInputType inputType) {
		this.inputType = inputType;
	}

	@Override
	public void onRender(int width, int height) {
		setHeight("50px");
		setWidth("95%");
		
		switch (inputType) {
			case TEXTBOX:
				input = new TextBox();
				input.setWidth("100%");
				input.addStyleName("formTextBoxComponent");
				break;
		}
		
		if (label != null && input != null) {
			Label lbl = new Label(label);
			lbl.setHeight("20px");
			lbl.setWidth("100%");
			lbl.setStylePrimaryName(LABEL_CLASS_NAME);
			
			input.setStylePrimaryName(INPUT_CLASS_NAME);
			((VerticalPanel)getWidget()).add(lbl);
			((VerticalPanel)getWidget()).add(input);
			setVisible(true);
		}
	}
}
