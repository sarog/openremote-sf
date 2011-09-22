package org.openremote.web.console.view;

import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.HTMLComponent;
import org.openremote.web.console.widget.LabelComponent;
import org.openremote.web.console.widget.TabBarComponent;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoadingScreenView extends ScreenViewImpl {
	public static final String LOADING_MESSAGE = "LOADING....";
	public static final String SPINNER_HTML_CODE = 
		"<div class=\"spinner\">" +
		"<div class=\"bar1\"></div>" +
		"<div class=\"bar2\"></div>" +
		"<div class=\"bar3\"></div>" +
		"<div class=\"bar4\"></div>" +
		"<div class=\"bar5\"></div>" +
		"<div class=\"bar6\"></div>" +
		"<div class=\"bar7\"></div>" +
		"<div class=\"bar8\"></div>" +
		"<div class=\"bar9\"></div>" +
		"<div class=\"bar10\"></div>" +
		"<div class=\"bar11\"></div>" +
   	"<div class=\"bar12\"></div>" +
   	"</div>";
	
	public LoadingScreenView() {
		// Create a spinning HTML Component
		HTMLComponent htmlSpinner = new HTMLComponent();
		htmlSpinner.setHTML(SPINNER_HTML_CODE);
		
		// Create a label component for the loading message text
		LabelComponent msgWidget = new LabelComponent();
		msgWidget.setText(LOADING_MESSAGE);
	
		// Add tab bar
		//TabBarComponent tabBar = new TabBarComponent();
		
		// Add components to screen view
		
	}
}
