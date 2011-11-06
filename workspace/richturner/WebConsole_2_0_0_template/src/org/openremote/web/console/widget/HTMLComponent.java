package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.HTML;

public class HTMLComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "htmlComponent";
	
	protected HTMLComponent() {
		super(new HTML(), CLASS_NAME);
	}

	@Override
	public void onRender(int width, int height) {

	}
	
	public void setHTML(String html) {
		((HTML)getWidget()).setHTML(html);
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
