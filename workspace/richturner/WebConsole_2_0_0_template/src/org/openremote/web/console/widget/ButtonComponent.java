package org.openremote.web.console.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ButtonComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "buttonComponent";
	private Label container;
	private String name;
	
	private ButtonComponent() {
		super(new Label());
		container = (Label)this.getWidget();
		setStylePrimaryName(CLASS_NAME);
		DOM.setStyleAttribute(container.getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(container.getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(container.getElement(), "display", "inline-block");
		//DOM.setStyleAttribute(container.getElement(), "padding", "9px");
		//DOM.setStyleAttribute(container.getElement(), "fontSize", "15px");
	}
	
	public void setName(String name) {
		this.name = name;
		container.setText(name);
	}
	
	@Override
	public void onRender(int width, int height) {
		// Check length of name and whether it is completely visible
		boolean textResized = false;
		
		while (container.getOffsetWidth() > width) {
			name = name.substring(0, name.length()-1);
			container.setText(name);
			textResized = true;
		}
		
		if (textResized) {
			name = name.substring(0, name.length()-1);
			name += "..";
			container.setText(name);
		}
		
		container.setWidth(width + "px");
		container.setHeight(height + "px");
		DOM.setStyleAttribute(container.getElement(), "lineHeight", height + "px");
	}

	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ButtonComponent entity) {
		ButtonComponent component = new ButtonComponent();
		if (entity == null) {
			return component;
		}
		component.setName(entity.getName());
		return component;
	}
}
