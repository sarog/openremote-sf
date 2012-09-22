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

import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.widget.ButtonComponent;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

public class FormButtonComponent extends ButtonComponent {
	public static final String CLASS_NAME = "formButtonComponent";
	private EnumFormButtonType buttonType = null;
	private FormPanelComponent parentForm = null;
	private EnumFormButtonAction action = null;
	
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
	
	public enum EnumFormButtonAction {
		ADD,
		DELETE,
		UPDATE;
		
	   @Override
	   public String toString() {
	      return super.toString().toLowerCase();
	   }
	   
	   public static EnumFormButtonAction enumValueOf(String submitActionTypeValue) {
	   	EnumFormButtonAction result = null;
	      try {
	         result = Enum.valueOf(EnumFormButtonAction.class, submitActionTypeValue.toUpperCase());
	      } catch (Exception e) {}
	      return result;
	   }
	}
	
	public FormButtonComponent(FormPanelComponent parent, EnumFormButtonType type) {
		this(parent, type, type.getText());
	}
	
	public FormButtonComponent(FormPanelComponent parent, EnumFormButtonType type, String name) {
		super();
		this.parentForm = parent;
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
	
	public void setAction(EnumFormButtonAction action) {
		this.action = action;
	}
	
	public EnumFormButtonAction getAction() {
		return action;
	}
	
	@Override
	public void onTap(TapEvent event) {
		FormButtonComponent btn = (FormButtonComponent)event.getSource();
		switch (btn.getType()) {
			case SUBMIT:
				if (parentForm.isValid()) {
					parentForm.onSubmit(this);
					super.onTap(event);
				} else {
					//TODO: Handle invalid submit
					Window.alert("FORM IS NOT VALID!");
				}
				break;
			default:
				super.onTap(event);
				break;
		}
	}
}
