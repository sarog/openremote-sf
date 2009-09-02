/**
 * 
 */
package org.openremote.console.web.client.def;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Screen parsed from the iphone.xml.
 * 
 * @author David Reines
 */
public class ScreenDef {

	private final Integer id;

	private final String name;

	private final Integer row;

	private final Integer column;

	private final List<ButtonDef> buttonDefs = new ArrayList<ButtonDef>();

	public ScreenDef(Integer id, String name, Integer row, Integer column) {
		super();
		this.id = id;
		this.name = name;
		this.row = row;
		this.column = column;
	}

	public void addButtonDef(ButtonDef buttonDef) {
		buttonDefs.add(buttonDef);
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

	public List<ButtonDef> getButtonDefs() {
		return buttonDefs;
	}

	@Override
	public String toString() {
		return "Screen [buttonDefs=" + buttonDefs + ", column=" + column
				+ ", id=" + id + ", name=" + name + ", row=" + row + "]";
	}

}
