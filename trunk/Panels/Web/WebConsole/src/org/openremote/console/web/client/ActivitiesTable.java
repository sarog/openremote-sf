/**
 * 
 */
package org.openremote.console.web.client;

import org.openremote.console.web.client.def.ActivityDef;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * Table containing the list of activities.
 * 
 * @author David Reines
 */
public class ActivitiesTable {

	private final FlexTable flexTable;

	public ActivitiesTable() {
		this.flexTable = new FlexTable();
	}

	/**
	 * Sets the title of the activity.
	 */
	public void setTitle(String title) {
		flexTable.setText(0, 0, title);
	}

	/**
	 * Adds the activity to the last row in the underlying FlexTable.
	 */
	public void addActivityDef(ActivityDef activityDef) {
		Hyperlink link = new Hyperlink(activityDef.getName(), "activities");
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				flexTable.setVisible(false);
			}
		});
		flexTable.setWidget(flexTable.getRowCount(), 0, link);
	}

	public Widget asWidget() {
		return this.flexTable;
	}

}
