package org.openremote.web.console.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;

public class LabelComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "labelComponent";
	private Label container;
	
	public LabelComponent() {
		super(new Label());
		container = (Label)this.getWidget();
		setStylePrimaryName(CLASS_NAME);
		this.setWidth("20px");
		this.setHeight("20px");
		DOM.setStyleAttribute(this.getElement(), "border", "2px solid white");
	}
	
	public void setText(String text) {
		container.setText(text);
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

}
