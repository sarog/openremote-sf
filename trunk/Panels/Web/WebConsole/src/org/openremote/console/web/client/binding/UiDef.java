/**
 * 
 */
package org.openremote.console.web.client.binding;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results of the iphone.xml parsing. The UiDef consists of
 * multiple Activity(s) and is used to build the UI.
 * 
 * @author David Reines
 */
public class UiDef {

	private final List<Activity> activities = new ArrayList<Activity>();

	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	public List<Activity> getActivities() {
		return activities;
	}

	@Override
	public String toString() {
		return "UiDef [activities=" + activities + "]";
	}

}
