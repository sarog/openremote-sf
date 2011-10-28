package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.Button;

public class ButtonComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "buttonComponent";
	private Button container;
	
	private ButtonComponent() {
		super(new Button());
		container = (Button)this.getWidget();
		setStylePrimaryName(CLASS_NAME);
		container.setHeight("100%");
		container.setWidth("100%");
	}
	
	public void setName(String name) {
		container.setText(name);
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
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
