package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.Label;

public class LabelComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "labelComponent";
	private Label container;
	
	private LabelComponent() {
		super(new Label());
		container = (Label)this.getWidget();
		setStylePrimaryName(CLASS_NAME);
		//this.setWidth("20px");
		//this.setHeight("20px");
		//DOM.setStyleAttribute(this.getElement(), "border", "2px solid white");
	}
	
	public void setText(String text) {
		container.setText(text);
	}
	
	public void setColor(String color) {
		container.getElement().getStyle().setProperty("color", color);
	}
	
	public void setFontSize(int size) {
		container.getElement().getStyle().setProperty("fontSize", size + "px");
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.LabelComponent entity) {
		LabelComponent component = new LabelComponent();
		if (entity == null) {
			return component;
		}
		component.setText(entity.getText());
		component.setColor(entity.getColor());
		component.setFontSize(entity.getFontSize());
		return component;
	}

}
