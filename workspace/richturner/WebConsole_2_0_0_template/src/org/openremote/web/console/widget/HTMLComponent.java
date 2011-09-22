package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.HTML;

public class HTMLComponent extends PassiveConsoleWidget {
	private HTML container;
	
	public HTMLComponent() {
		// Create HTML widget container
		container = new HTML();
		container.setStylePrimaryName("htmlComponent");
		this.initWidget(container);
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}
	
	public void setHTML(String html) {
		container.setHTML(html);
	}		
}
