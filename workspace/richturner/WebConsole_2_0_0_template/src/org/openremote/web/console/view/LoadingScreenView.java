package org.openremote.web.console.view;

import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.AbsolutePanelComponent;
import org.openremote.web.console.widget.HTMLComponent;
import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.SliderComponent;
import org.openremote.web.console.widget.TabBarComponent;
import org.openremote.web.console.widget.ext.SpinnerComponent;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoadingScreenView extends ScreenViewImpl {
	public static final String LOADING_MESSAGE = "LOADING....";
	
	public LoadingScreenView() {
		// Create a spinning HTML Component
		SpinnerComponent htmlSpinner = new SpinnerComponent();
		htmlSpinner.setSize("100px");

		// Create a label component for the loading message text
		LabelComponent msgWidget = new LabelComponent();
		msgWidget.setText(LOADING_MESSAGE);
		msgWidget.getElement().setAttribute("style", "color: #245E36; text-shadow: -1px -1px 0 #4FA800, 1px -1px 0 #4FA800, -1px 1px 0 #4FA800, 1px 1px 0 #4FA800; font-weight: bold; font-size: 25px; font-family: verdana, arial, sans-serif;");

		//SliderComponent slider = new SliderComponent(250, 50);
		AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
		DOM.setStyleAttribute(absPanel.getElement(), "border", "2px solid white");
		absPanel.setHeight("200px");
		absPanel.setWidth("200px");
		absPanel.setComponent(htmlSpinner);
		
		// Add tab bar
		TabBarComponent tabBar = new TabBarComponent();
		
		// Add components to screen view
		//super.addConsoleWidget(htmlSpinner);
		super.addConsoleWidget(msgWidget);
		super.addConsoleWidget(absPanel);
		super.addConsoleWidget(tabBar);
	}
}
