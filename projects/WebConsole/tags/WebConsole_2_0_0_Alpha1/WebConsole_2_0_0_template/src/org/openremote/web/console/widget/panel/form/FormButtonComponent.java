package org.openremote.web.console.widget.panel.form;

import org.openremote.web.console.widget.ButtonComponent;
import com.google.gwt.user.client.DOM;

public class FormButtonComponent extends ButtonComponent {
	public static final String CLASS_NAME = "formButtonComponent";
	private EnumFormButtonType buttonType;

	public enum EnumFormButtonType {
		SUBMIT("submit","SUBMIT"),
		CLEAR("clear","CLEAR"),
		CANCEL("cancel","CANCEL");
		
		private final String text;
		private final String name;
		
		EnumFormButtonType(String name, String text) {
			this.name = name;
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
		
		public String getName() {
			return name;
		}
		
		public static EnumFormButtonType getButtonType(String name) {
			EnumFormButtonType result = null;
			for (EnumFormButtonType input : EnumFormButtonType.values()) {
				if (input.getName().equalsIgnoreCase(name)) {
					result = input;
					break;
				}
			}
			return result;
		}
	}	
	
	public FormButtonComponent(EnumFormButtonType type) {
		this(type, type.getText());
	}
	
	public FormButtonComponent(EnumFormButtonType type, String name) {
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
		setName(name);
	}
	
	public EnumFormButtonType getType() {
		return buttonType;
	}
}
