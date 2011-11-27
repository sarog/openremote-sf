package org.openremote.web.console.widget.panel.form;

import org.openremote.web.console.widget.ButtonComponent;
import com.google.gwt.user.client.DOM;

public class FormButton extends ButtonComponent {
	public static final String CLASS_NAME = "formButtonComponent";
	private EnumFormButtonType buttonType;

	public enum EnumFormButtonType {
		SUBMIT("SUBMIT"),
		CLEAR("CLEAR"),
		CANCEL("CANCEL");
		
		private final String text;
		
		EnumFormButtonType(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}	
	
	public FormButton(EnumFormButtonType type) {
		super();
		setStylePrimaryName(CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "lineHeight", "35px");
		this.buttonType = type;
		
		switch(buttonType) {
			case SUBMIT:
				break;
			case CLEAR:
				break;
			case CANCEL:
				
		}
		setName(buttonType.getText());
	}
	
	public EnumFormButtonType getType() {
		return buttonType;
	}
}
