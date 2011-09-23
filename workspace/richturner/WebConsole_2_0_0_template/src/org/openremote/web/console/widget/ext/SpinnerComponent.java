package org.openremote.web.console.widget.ext;

import org.openremote.web.console.widget.HTMLComponent;

import com.google.gwt.user.client.DOM;

public class SpinnerComponent extends HTMLComponent {
	private static final String STATIC_STYLE = "position: relative; display: inline-block;";
	private static final String SPINNER_HTML_CODE = 
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
	
	public SpinnerComponent() {
		super();
		super.setHTML(SPINNER_HTML_CODE);
		this.addStyleName("spinnerComponent");
	}
	
	// Ignore attempt to set HTML
	@Override
	public void setHTML(String html) {}
	
	public void setSize(String size) {
		this.setWidth(size);
		this.setHeight(size);
		this.getElement().getFirstChildElement().setAttribute("style", STATIC_STYLE + "width: " + size + "; height: " + size + ";");
	}
}
