package org.openremote.web.console.widget;

import org.openremote.web.console.event.sensor.SensorChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;

public class LabelComponent extends PassiveConsoleComponent implements SensorChangeHandler {
	public static final String CLASS_NAME = "labelComponent";
	
	public LabelComponent() {
		super(new Label(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(getElement(), "display", "inline-block");
	}
	
	public void setText(String text) {
		((Label)getWidget()).setText(text);
	}
	
	public void setColor(String color) {
		getElement().getStyle().setProperty("color", color);
	}
	
	public void setFontSize(int size) {
		getElement().getStyle().setProperty("fontSize", size + "px");
	}
	
	@Override
	public void onRender(int width, int height) {
		DOM.setStyleAttribute(getElement(), "lineHeight", height + "px");
	}
	
	@Override
	public void onUpdate(int width, int height) {
		this.width = width;
		this.height = height;
		setWidth(width + "px");
		setHeight(height + "px");
		DOM.setStyleAttribute(getElement(), "lineHeight", height + "px");
	}
	
	@Override
	public void onSensorAdd() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sensorChanged(String newValue) {
		// TODO Auto-generated method stub
		setText(newValue);
	}

	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.LabelComponent entity) {
		LabelComponent component = new LabelComponent();
		if (entity == null) {
			return component;
		}
		component.setSensor(new Sensor(entity.getLink()));
		component.setId(entity.getId());
		component.setText(entity.getText());
		component.setColor(entity.getColor());
		component.setFontSize(entity.getFontSize());
		return component;
	}
}
