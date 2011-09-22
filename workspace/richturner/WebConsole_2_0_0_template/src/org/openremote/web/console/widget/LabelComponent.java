package org.openremote.web.console.widget;

import com.google.gwt.user.client.ui.Label;

public class LabelComponent extends PassiveConsoleWidget {
	private Label container;
	
	public LabelComponent() {
		container = new Label();
	}
	
	public void setText(String text) {
		container.setText(text);
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

}
