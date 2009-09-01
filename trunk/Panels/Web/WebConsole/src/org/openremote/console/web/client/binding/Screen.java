/**
 * 
 */
package org.openremote.console.web.client.binding;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Screen parsed from the iphone.xml.
 * 
 * @author David Reines
 */
public class Screen {

	private final Integer id;

	private final String name;

	private final Integer row;

	private final Integer column;

	private final List<Button> buttons = new ArrayList<Button>();

	public Screen(Integer id, String name, Integer row, Integer column) {
		super();
		this.id = id;
		this.name = name;
		this.row = row;
		this.column = column;
	}

	public void addButton(Button button) {
		buttons.add(button);
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getRow() {
		return row;
	}

	public Integer getColumn() {
		return column;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	@Override
	public String toString() {
		return "Screen [buttons=" + buttons + ", column=" + column + ", id="
				+ id + ", name=" + name + ", row=" + row + "]";
	}

}
