package org.openremote.web.console.views;

import org.openremote.web.console.utils.BrowserUtils;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoadingScreenView extends ConsoleScreenView {

	public static final String CLASS_NAME = "loadingScreen";

	public static final String LOADING_MESSAGE = "LOADING....";

	public static final String SPINNER_HTML_CODE = 
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
		super();
		this.setStylePrimaryName(CLASS_NAME);
		
		// Create Loading Message Widget and wrap in simple panel to  align vertically
		VerticalPanel loadingMsgPanel = new VerticalPanel();
		DOM.setElementAttribute(loadingMsgPanel.getElement(), "id", "loadingMsgPanel");
		loadingMsgPanel.setStylePrimaryName("msgPanel");
		loadingMsgPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		loadingMsgPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		// Create HTML widget frame for loading message spinner
		HTML spinnerWidget = new HTML();
		DOM.setElementAttribute(spinnerWidget.getElement(), "id", "loadingMsgSpinner");
		spinnerWidget.setStylePrimaryName("spinner");		
		spinnerWidget.setHTML(SPINNER_HTML_CODE);		
		
		// Create a label widget for the loading message text
		Label msgWidget = new Label();
		DOM.setElementAttribute(msgWidget.getElement(), "id", "loadingMsgText");
		msgWidget.setStylePrimaryName("msgText");
		msgWidget.setText(LOADING_MESSAGE);
		
		// Add widgets to msg panel only add spinner if using a webkit browser
		if (BrowserUtils.isWebkit) {
			loadingMsgPanel.add(spinnerWidget);
		}
		loadingMsgPanel.add(msgWidget);
		
		// Add panel to screen and position in centre middle
		HorizontalPanel panelWrapper = new HorizontalPanel();
		panelWrapper.setWidth("100%");
		panelWrapper.setHeight("100%");
		panelWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panelWrapper.add(loadingMsgPanel);
		this.add(panelWrapper);
		
	}
}
