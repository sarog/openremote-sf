package org.openremote.console.web.client.widget;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a single screen.
 * 
 * @author David Reines
 */
public class Screen {

	private final VerticalPanel parentPanel = new VerticalPanel();
	private final AbsolutePanel titlePanel = new AbsolutePanel();
	private final AbsolutePanel panel = new AbsolutePanel();
	private final String name;

	public Screen(String name) {
		super();
		this.name = name;
		titlePanel.add(new Label(this.name));
		parentPanel.add(titlePanel);
		parentPanel.add(panel);
		setVisible(false);
	}

	public void addButton(String label, int x, int y, int width, int height) {
		PushButton button = new PushButton();
		button.setText(label);
		button.setWidth(width + "px");
		button.setHeight(height + "px");
		panel.add(button, x, y);
	}

	public void setVisible(boolean visible) {
		this.parentPanel.setVisible(visible);
	}

	public String getName() {
		return name;
	}

	public Widget asGwtWidget() {
		return this.parentPanel;
	}

}
