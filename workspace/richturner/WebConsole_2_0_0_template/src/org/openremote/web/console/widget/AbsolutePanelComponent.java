package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AbsolutePanelComponent extends PassiveConsoleComponent {
	private static final String CLASS_NAME = "absolutePanelComponent";
	private SimplePanel container;
	private ConsoleComponent component;
	private HorizontalPanel componentContainer;
	
	public AbsolutePanelComponent() {
		super(new SimplePanel());
		container = (SimplePanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		
		componentContainer = new HorizontalPanel();
		componentContainer.setWidth("100%");
		componentContainer.setHeight("100%");
		
		componentContainer.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		componentContainer.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		
		container.add(componentContainer);
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		if (component != null) {
			component.onAdd();
		}
	}
	
	public void setComponent(ConsoleComponent component) {
		componentContainer.add((Widget)component);
		this.component = component;
	}
	
	public ConsoleComponent getComponent() {
		return (ConsoleComponent)this.componentContainer.getWidget(0);
	}
}
