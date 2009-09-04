package org.openremote.console.web.client.widget;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a single screen.
 * 
 * @author David Reines
 */
public class Screen {

	private final AbsolutePanel screenPanel = new AbsolutePanel();
	private final String name;

	public Screen(String name) {
		super();
		this.name = name;
		setVisible(false);
	}

	public void addButton(String label, int x, int y, int width, int height) {
		PushButton button = new PushButton();
		button.setText(label);
		// TODO: determine why buttons are not displaying outside of GWT browser
		button.setWidth(width + "px");
		button.setHeight(height + "px");
		screenPanel.add(button, x, y);
	}

	public void setVisible(boolean visible) {
		this.screenPanel.setVisible(visible);
	}

	public String getName() {
		return name;
	}

	public Widget asGwtWidget() {
		return this.screenPanel;
	}

}
