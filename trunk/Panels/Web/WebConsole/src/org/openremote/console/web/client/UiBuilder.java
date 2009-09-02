/**
 * 
 */
package org.openremote.console.web.client;

import org.openremote.console.web.client.def.ActivityDef;
import org.openremote.console.web.client.def.UiDef;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * Responsible for bulding the web console ui from the UiDef binding result.
 * 
 * @author David Reines
 */
public class UiBuilder {

	private ActivitiesTable activitiesTable;

	public void buildUi(UiDef uiDef) {

		this.activitiesTable = new ActivitiesTable();
		activitiesTable.setTitle("Activities");
		for (ActivityDef activityDef : uiDef.getActivityDefs()) {
			activitiesTable.addActivityDef(activityDef);
		}
		RootPanel.get("console").add(activitiesTable.asWidget());

	}

}
