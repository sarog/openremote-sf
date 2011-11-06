package org.openremote.web.console.widget;

import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressCancelHandler;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressEndHandler;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.press.PressStartHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class ButtonComponent extends InteractiveConsoleComponent implements PressStartHandler, PressEndHandler, PressCancelHandler, TapHandler {
	public static final String CLASS_NAME = "buttonComponent";
	private String name;
	private boolean isRendered = false;
	
	private ButtonComponent() {
		super(new Label(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(getElement(), "display", "inline-block");
	}
	
	public void setName(String name) {
		this.name = name;
		((Label)getWidget()).setText(name);
		
		if (!isRendered) {
			return;
		}
		
		// Check length of name and whether it is completely visible
		boolean textResized = false;
		String newName = name;
		setWidth("");
		int currentWidth = getOffsetWidth();
		
		while (currentWidth > width) {
			newName = newName.substring(0, newName.length()-1);
			((Label)getWidget()).setText(newName);
			textResized = true;
			currentWidth = getOffsetWidth();
		}
		
		if (textResized) {
			name = newName.substring(0, newName.length()-1);
			name += "..";
			((Label)getWidget()).setText(name);
			this.name = name;
		}
		setWidth(width + "px");
	}
	
	@Override
	public void onRender(int width, int height) {
		isRendered = true;
		setName(name);
		DOM.setStyleAttribute(getElement(), "lineHeight", height + "px");
	}

	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ButtonComponent entity) {
		ButtonComponent component = new ButtonComponent();
		if (entity == null) {
			return component;
		}
		component.setName(entity.getName());
		return component;
	}

	@Override
	public void onPressCancel(PressCancelEvent event) {
		this.removeStyleName("pressed");
	}

	@Override
	public void onPressEnd(PressEndEvent event) {
		this.removeStyleName("pressed");
	}

	@Override
	public void onPressStart(PressStartEvent event) {
		this.addStyleName("pressed");
	}

	@Override
	public void onTap(TapEvent event) {
		// TODO Auto-generated method stub
		Window.alert("TAP");
	}
}
