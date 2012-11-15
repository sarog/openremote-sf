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
package org.openremote.web.console.widget.panel.form;

import org.openremote.web.console.widget.InteractiveConsoleComponent;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class FormField extends InteractiveConsoleComponent implements KeyUpHandler, BlurHandler, FocusHandler {
	public static final String CLASS_NAME = "formFieldComponent";
	public static final String LABEL_CLASS_NAME = "formFieldLabelComponent";
	public static final String INPUT_CLASS_NAME = "formFieldInputComponent";
	private String label = null;
	private EnumFormInputType inputType;
	private Label lbl = null;
	private String id = "";
	private Widget input = null;
	private String validationStr = null;
	private boolean isValid;
	private boolean isOptional = false;
	private String name = null;
	private String defaultValue = null;

	public enum EnumFormInputType {
		TEXTBOX("textbox"),
		PASSWORD("password"),
		TEXTAREA("textarea"),
		SELECT("select");
		
		private final String name;
		
		EnumFormInputType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static EnumFormInputType getInputType(String name) {
			EnumFormInputType result = null;
			for (EnumFormInputType input : EnumFormInputType.values()) {
				if (input.getName().equalsIgnoreCase(name)) {
					result = input;
					break;
				}
			}
			return result;
		}
	}
	
	public FormField() {
		super(new VerticalPanel(), CLASS_NAME);
		VerticalPanel container = (VerticalPanel)getWidget();
		container.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		container.setSpacing(0);
		
		lbl = new Label();
		lbl.setWidth("100%");
		lbl.setStylePrimaryName(LABEL_CLASS_NAME);
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setInputType(EnumFormInputType inputType) {
		this.inputType = inputType;
	}
	
	public void setId(String id) {
		if (id != null)	this.id = id;
	}
	
	public EnumFormInputType getInputType() {
		return inputType;
	}
	
	public void setValidationString(String validationStr) {
		this.validationStr = validationStr; 
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public boolean getIsOptional() {
		return isOptional;
	}
	
	public void setIsOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setValue(String value) {
		switch (inputType) {
			case TEXTBOX:
			case PASSWORD:
				((TextBox)input).setText(value); 
				break;
		}
	}
	
	public String getValue() {
		String value = "";
		switch (inputType) {
			case TEXTBOX:
			case PASSWORD:
				value = ((TextBox)input).getText(); 
				break;
		}
		return value;
	}
	
	private void setInputValid(boolean valid) {
		isValid = valid;
		if (valid) {
			lbl.removeStyleName("invalid");
		} else {
			lbl.addStyleName("invalid");			
		}
	}
	
	private void validateInput() {
		String value = "";
		switch (inputType) {
			case TEXTBOX:
				value = ((TextBox)input).getValue();
				break;
			case TEXTAREA:
				value = ((TextArea)input).getValue();
				break;
			case PASSWORD:
				value = ((PasswordTextBox)input).getValue();
				break;
		}
		if (isOptional && value.length() == 0) {
			setInputValid(true);
		} else {
			if (validationStr != null) {
				setInputValid(value.matches(validationStr));
			} else {
				setInputValid(true);
			}
		}
	}

	@Override
	public void onRender(int width, int height) {
		if (!isInitialised) {
			switch (inputType) {
				case TEXTBOX:
					input = new TextBox();
					input.addStyleName("formInputComponent");
					input.addStyleName("formTextBoxComponent");
					((TextBox)input).setText(defaultValue);
					break;
				case PASSWORD:
					input = new PasswordTextBox();
					input.addStyleName("formInputComponent");
					input.addStyleName("formPasswordComponent");
					((TextBox)input).setText(defaultValue);
			}
			
			if (label != null && input != null) {
				input.getElement().setId(id);
				int fieldWidth = (int)Math.round(width*.95);
				int leftMargin = (int)Math.round(width*.02);				
				lbl.setText(label);
				lbl.setWidth(fieldWidth + "px");
				input.setWidth(fieldWidth + "px");
				DOM.setStyleAttribute(lbl.getElement(), "marginLeft", leftMargin * 2 + "px");
				DOM.setStyleAttribute(input.getElement(), "marginLeft", leftMargin + "px");
				input.setStylePrimaryName(INPUT_CLASS_NAME);
				((VerticalPanel)getWidget()).add(lbl);
				((VerticalPanel)getWidget()).add(input);
				setVisible(true);
				
				validateInput();
			}
		}
		
		setHeight("50px");
		setWidth(width  + "px");
		
		switch (inputType) {
		case TEXTBOX:
			((TextBox)input).setText(defaultValue);
			break;
		case PASSWORD:
			((TextBox)input).setText(defaultValue);
		}
	
		if (label != null && input != null) {
			setVisible(true);
			validateInput();
			
			// Initialise validation handler
			if (validationStr != null && (inputType == EnumFormInputType.TEXTBOX || inputType == EnumFormInputType.TEXTAREA || inputType == EnumFormInputType.PASSWORD)) {
				registerHandler(input.addDomHandler(this, KeyUpEvent.getType()));
				registerHandler(input.addDomHandler(this, BlurEvent.getType()));
				registerHandler(input.addDomHandler(this, FocusEvent.getType()));
			}
		}
	}

	@Override
	public void onUpdate(int width, int height) {
		int fieldWidth = (int)Math.round(width*.95);
		int leftMargin = (int)Math.round(width*.02);
		this.width = width;
		setWidth(width + "px");
		input.setWidth(fieldWidth + "px");
		lbl.setWidth(fieldWidth + "px");
		DOM.setStyleAttribute(lbl.getElement(), "marginLeft", leftMargin * 2 + "px");
		DOM.setStyleAttribute(input.getElement(), "marginLeft", leftMargin + "px");
	}
	
	@Override
	public void onKeyUp(KeyUpEvent event) {
		validateInput();
	}

	@Override
	public void onBlur(BlurEvent event) {
		validateInput();
	}

	@Override
	public void onFocus(FocusEvent event) {
		// Force cursor to end of input
		if (input instanceof TextBox) {
			TextBox inputBox = (TextBox)input;
			inputBox.setCursorPos(inputBox.getText().length());
		}
	}
}
