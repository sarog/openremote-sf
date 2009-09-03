package org.openremote.console.web.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents the list of activities for the web console.
 * 
 * @author David Reines
 */
public class Activities {

	private final FlexTable flexTable = new FlexTable();
	private final List<Activity> activities = new ArrayList<Activity>();

	/**
	 * Sets the title of the activity.
	 */
	public void setTitle(String title) {
		flexTable.setText(0, 0, title);
	}

	/**
	 * Adds the activity to the last row in the underlying FlexTable.
	 */
	public void addActivity(Activity activity) {

		activities.add(activity);
		addActivityLink(activity);

	}

	private void addActivityLink(final Activity activity) {
		Hyperlink link = new Hyperlink(activity.getName(), "activity");
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				flexTable.setVisible(false);
				activity.show();
			}
		});
		flexTable.setWidget(flexTable.getRowCount(), 0, link);
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public Widget asGwtWidget() {
		return this.flexTable;
	}

}
