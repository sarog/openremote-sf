/**
 * 
 */
package org.openremote.console.web.client.binding;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an activity parsed from the iphone.xml.
 * 
 * @author David Reines
 */
public class Activity {

	private final String name;

	private final Integer id;

	private final List<Screen> screens = new ArrayList<Screen>();

	public Activity(Integer id, String name) {
		this.name = name;
		this.id = id;
	}

	public void addScreen(Screen screen) {
		screens.add(screen);
	}

	public List<Screen> getScreens() {
		return screens;
	}

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", name=" + name + ", screens=" + screens
				+ "]";
	}

}
