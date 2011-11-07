package org.openremote.web.console.view;

import org.openremote.web.console.widget.AbsolutePanelComponent;
import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.ext.SpinnerComponent;

public class LoadingScreenView extends ScreenViewImpl {
	public static final String LOADING_MESSAGE = "LOADING....";
	
	public LoadingScreenView() {
		// Create a spinning HTML Component
		SpinnerComponent htmlSpinner = new SpinnerComponent();
		htmlSpinner.setSize(150);

		// Create a label component for the loading message text
		LabelComponent msgWidget = (LabelComponent) LabelComponent.build(null);
		msgWidget.setText(LOADING_MESSAGE);
		msgWidget.getElement().setAttribute("style", "color: #245E36; text-shadow: -1px -1px 0 #4FA800, 1px -1px 0 #4FA800, -1px 1px 0 #4FA800, 1px 1px 0 #4FA800; font-weight: bold; font-size: 25px; font-family: verdana, arial, sans-serif; letter-spacing: 1px; margin-top: 90px;");

		// Create container for spinner
		AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
		absPanel.setHeight("100%");
		absPanel.setWidth("100%");
		absPanel.setPosition(0,0);
		absPanel.setComponent(htmlSpinner);
		
		// Create container for label
		AbsolutePanelComponent absPanel2 = new AbsolutePanelComponent();
		absPanel2.setHeight("100%");
		absPanel2.setWidth("100%");
		absPanel2.setPosition(0,0);
		absPanel2.setComponent(msgWidget);
		
		// Add components to screen view
		super.addConsoleWidget(absPanel);
		super.addConsoleWidget(absPanel2);
	}
}
