package org.openremote.web.console.widget.ext;

import org.openremote.web.console.widget.HTMLComponent;

import com.google.gwt.user.client.DOM;

public class SpinnerComponent extends HTMLComponent {
	private int size = 70;
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
	
	@Override
	public void onRender(int width, int height) {
		this.getElement().getFirstChildElement().setAttribute("style", "position: absolute; top: 50%; left: 50%; margin-top: -" + size/2 + "px; margin-left: -" + size/2 + "px; width: " + size + "px; height: " + size + "px;");
		super.onRender(width, height);
	}
	
	public void setSize(int size) {
		this.size = size;
	}
}
