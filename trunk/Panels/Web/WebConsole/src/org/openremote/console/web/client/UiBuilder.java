/**
 * 
 */
package org.openremote.console.web.client;

import java.util.ArrayList;
import java.util.List;

import org.openremote.console.web.client.def.ActivityDef;
import org.openremote.console.web.client.def.ButtonDef;
import org.openremote.console.web.client.def.ScreenDef;
import org.openremote.console.web.client.def.UiDef;
import org.openremote.console.web.client.widget.Activities;
import org.openremote.console.web.client.widget.Activity;
import org.openremote.console.web.client.widget.Screen;

/**
 * Responsible for bulding the web console ui from the UiDef binding result.
 * 
 * @author David Reines
 */
public class UiBuilder {

	public Activities buildActivities(UiDef uiDef) {

		Activities activities = new Activities();
		activities.setTitle("Activities");
		for (ActivityDef activityDef : uiDef.getActivityDefs()) {
			List<Screen> screens = buildScreens(activityDef);
			Activity activity = new Activity(activityDef.getName(), screens);
			activities.addActivity(activity);
		}
		return activities;

	}

	private List<Screen> buildScreens(ActivityDef activityDef) {
		List<Screen> screens = new ArrayList<Screen>(activityDef
				.getScreenDefs().size());
		for (ScreenDef screenDef : activityDef.getScreenDefs()) {
			Screen screen = new Screen(screenDef.getName());
			addButtons(screen, screenDef);
			screens.add(screen);
		}
		return screens;
	}

	private void addButtons(Screen screen, ScreenDef screenDef) {
		for (ButtonDef buttonDef : screenDef.getButtonDefs()) {
			screen.addButton(buttonDef.getLabel(), buttonDef.getX(), buttonDef.getY(), buttonDef
					.getWidth(), buttonDef.getHeight());
		}
	}

}
