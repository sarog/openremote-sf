package org.openremote.console.web.client.widget;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents an Activity containing a list of screens.
 * 
 * @author David Reines
 */
public class Activity {

	private final String name;
	private final VerticalPanel activityPanel = new VerticalPanel();
	private final FlexTable topNavBar = new FlexTable();
	private final List<Screen> screens;

	private int currentScreenIndex = 0;

	public Activity(String name, List<Screen> screens) {
		this.name = name;
		this.screens = screens;
		activityPanel.add(topNavBar);
		addScreensToActivityPanel(screens);
		setVisible(false);
		populateTopNavBar();
	}

	private void addScreensToActivityPanel(List<Screen> screens) {
		int index = 0;
		for (Screen screen : screens) {
			activityPanel.add(screen.asGwtWidget());
			index++;
		}
	}

	private void populateTopNavBar() {
		// TODO: find icons to use instead of nav text (i.e. right arrow instead
		// of next).
		if (currentScreenIndex == 0) {
			topNavBar.setWidget(0, 0, createBackLink());
		} else {
			topNavBar.setWidget(0, 0, createNavLink("prev", "prev", -1));
		}
		Screen currentScreen = currentScreen();
		if (currentScreen != null) {
			topNavBar.setWidget(0, 1, new Label(currentScreen.getName()));
		}
		if (currentScreenIndex < (screens.size() - 1)) {
			topNavBar.setWidget(0, 2, createNavLink("next", "next", 1));
		} else {
			topNavBar.setText(0, 2, "");
		}
	}

	public void setVisible(boolean visible) {
		this.activityPanel.setVisible(visible);
		this.topNavBar.setVisible(visible);
		if (visible) {
			showCurrentScreen();
		} else {
			for (Screen screen : screens) {
				screen.setVisible(visible);
			}
		}
	}

	public Hyperlink createNavLink(String text, String targetHistoryToken,
			final int screenIndexIncrement) {
		// TODO: determine what targetHistoryToken should be...
		Hyperlink link = new Hyperlink(text, targetHistoryToken);
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hideCurrentScreen();
				currentScreenIndex = currentScreenIndex + screenIndexIncrement;
				populateTopNavBar();
				showCurrentScreen();
			}
		});
		return link;
	}

	private Hyperlink createBackLink() {
		Hyperlink link = new Hyperlink("back to activities", "back");
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.alert("todo: navigate back to activities");
			}
		});
		return link;
	}

	private void showCurrentScreen() {
		Screen screen = currentScreen();
		if (screen != null) {
			screen.setVisible(true);
		}
	}

	private void hideCurrentScreen() {
		Screen screen = currentScreen();
		if (screen != null) {
			screen.setVisible(false);
		}
	}

	private Screen currentScreen() {
		Screen screen = null;
		if (screens.size() > currentScreenIndex) {
			screen = screens.get(currentScreenIndex);
		}
		return screen;
	}

	public Widget asGwtWidget() {
		return this.activityPanel;
	}

	public String getName() {
		return name;
	}

	public List<Screen> getScreens() {
		return screens;
	}

}
