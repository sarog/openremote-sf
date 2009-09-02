/**
 * 
 */
package org.openremote.console.web.client.def;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an activity parsed from the iphone.xml.
 * 
 * @author David Reines
 */
public class ActivityDef {

	private final String name;

	private final Integer id;

	private final List<ScreenDef> screenDefs = new ArrayList<ScreenDef>();

	public ActivityDef(Integer id, String name) {
		this.name = name;
		this.id = id;
	}

	public void addScreenDef(ScreenDef screenDef) {
		screenDefs.add(screenDef);
	}

	public List<ScreenDef> getScreenDefs() {
		return screenDefs;
	}

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", name=" + name + ", screenDefs="
				+ screenDefs + "]";
	}

}
