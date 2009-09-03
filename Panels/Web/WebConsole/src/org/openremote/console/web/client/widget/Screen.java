package org.openremote.console.web.client.widget;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a single screen.
 * 
 * @author David Reines
 */
public class Screen {

	private final FlexTable flexTable = new FlexTable();
	private final String name;

	public Screen(String name) {
		super();
		this.name = name;
		flexTable.setText(0, 0, this.name);
		setVisible(false);
	}

	public void setVisible(boolean visible) {
		this.flexTable.setVisible(visible);
	}

	public String getName() {
		return name;
	}

	public Widget asGwtWidget() {
		return this.flexTable;
	}

}
