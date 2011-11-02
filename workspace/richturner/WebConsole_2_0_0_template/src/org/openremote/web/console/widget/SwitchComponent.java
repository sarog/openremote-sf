package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.Button;

public class SwitchComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "buttonComponent";
	private Button container;
	
	private SwitchComponent() {
		super(new Button());
		container = (Button)this.getWidget();
		setStylePrimaryName(CLASS_NAME);
		container.setHeight("100%");
		container.setWidth("100%");
		container.setText("OFF");
	}
	
	public void setName(String name) {
		container.setText(name);
	}
	
	@Override
	public void onRender(int width, int height) {
		container.setWidth(width + "px");
		container.setHeight(height + "px");
	}

	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.SwitchComponent entity) {
		SwitchComponent component = new SwitchComponent();
		return component;
	}

}
