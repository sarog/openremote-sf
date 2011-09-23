package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.HTML;

public class HTMLComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "htmlComponent";
	private HTML container;
	
	public HTMLComponent() {
		// Create HTML widget container
		super(new HTML());
		container = (HTML)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}
	
	public void setHTML(String html) {
		container.setHTML(html);
	}		
}
