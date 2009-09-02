/**
 * 
 */
package org.openremote.console.web.client.def;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results of the iphone.xml parsing. The UiDef consists of
 * multiple Activity(s) and is used to build the UI.
 * 
 * @author David Reines
 */
public class UiDef {

	private final List<ActivityDef> activityDefs = new ArrayList<ActivityDef>();

	public void addActivityDef(ActivityDef activityDef) {
		activityDefs.add(activityDef);
	}

	public List<ActivityDef> getActivityDefs() {
		return activityDefs;
	}

	@Override
	public String toString() {
		return "UiDef [activityDefs=" + activityDefs + "]";
	}

}
