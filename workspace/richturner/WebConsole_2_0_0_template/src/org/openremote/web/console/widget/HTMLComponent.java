package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.HTML;

public class HTMLComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "htmlComponent";
	private HTML container;
	
	protected HTMLComponent() {
		// Create HTML widget container
		super(new HTML());
		container = (HTML)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		this.setWidth("100%");
		this.setHeight("100%");
	}

	@Override
	public void onRender(int width, int height) {
		container.setWidth(width + "px");
		container.setHeight(height + "px");
	}
	
	public void setHTML(String html) {
		container.setHTML(html);
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.HTMLComponent entity) {
		HTMLComponent component = new HTMLComponent(); 
		if (entity == null) {
			return component;
		}
		component.setHTML(entity.getHtml());
		return component;
	}
}
