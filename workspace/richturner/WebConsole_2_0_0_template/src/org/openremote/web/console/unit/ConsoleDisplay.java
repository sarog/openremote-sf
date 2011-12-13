package org.openremote.web.console.unit;

import java.util.List;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.service.ScreenViewService;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.view.LoadingScreenView;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.InteractiveConsoleComponent;
import org.openremote.web.console.widget.TabBarComponent;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * This is the container where content will actually be displayed
 * An absolute panel is used as a wrapper to ease with repositioning
 * the display within the console unit during orientation change
 * @author rich
 *
 */
public class ConsoleDisplay extends InteractiveConsoleComponent implements TouchMoveHandler, MouseMoveHandler, MouseOutHandler {
	public static final String CLASS_NAME = "consoleDisplay";
	private AbsolutePanel display;
	private String currentOrientation = "portrait";
	private ScreenViewImpl loadingScreen;
	private TabBarComponent currentTabBar;
	private ScreenViewImpl currentScreen;
	
	public ConsoleDisplay() {
		super(new AbsolutePanel(), CLASS_NAME);
		getElement().setId("consoleDisplayWrapper");
		
		// Create display panel where screen is actually loaded
		display = new AbsolutePanel();
		display.setStylePrimaryName("portraitDisplay");
		display.getElement().setId("consoleDisplay");
		
		// Add display to the wrapper
		((AbsolutePanel)getWidget()).add(display, 0, 0);
				
		// Add move handlers which are only used on this display component
		if(BrowserUtils.isMobile) {
			this.addDomHandler(this, TouchMoveEvent.getType());
		} else {
			this.addDomHandler(this, MouseMoveEvent.getType());
			this.addDomHandler(this, MouseOutEvent.getType());
		}
	}
	
	public void setSize(int width, int height) {
		if (this.width == width && this.height == height) {
			return;
		}
		if (getOrientation().equalsIgnoreCase("portrait")) {
			this.width = width;
			this.height = height;
			setWidth(width + "px");
			setHeight(height + "px");
			display.setWidth(width + "px");
			display.setHeight(height + "px");
		} else {
			this.width = height;
			this.height = width;
			setWidth(height + "px");
			setHeight(width + "px");
			display.setWidth(height + "px");
			display.setHeight(width + "px");
		}
		
		// Resize the screen view and tab bar
		updateScreenView();
		updateTabBar();
	}
	
	/**
	 * Set the display orientation which changes the CSS class causing a
	 * rotate transform to be applied, have to also adjust the display
	 * size and position within the wrapper
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		if (currentOrientation.equalsIgnoreCase(orientation)) {
			return;
		}
		
		this.currentOrientation = orientation;
		
		int width = getWidth();
		int height = getHeight();
		
		if ("portrait".equals(orientation)) {
			((AbsolutePanel)getWidget()).setWidgetPosition(display,0,0);
		   display.setStylePrimaryName("portraitDisplay");
		} else {
			((AbsolutePanel)getWidget()).setWidgetPosition(display, (height/2)-(width/2), (width/2)-(height/2));
			display.setStylePrimaryName("landscapeDisplay");
		}
		
	   display.setWidth(width + "px");
	   display.setHeight(height + "px");
	}
	
	public String getOrientation() {
		return this.currentOrientation;
	}
	
	public boolean getIsVertical() {
		boolean response = false;
		if (currentOrientation.equalsIgnoreCase("portrait")) {
			response = true;
		}
		return response;
	}
	
	public int getWidth() {
		int value = 0; 
		if ("portrait".equals(currentOrientation)) {
			value = this.width;
		} else {
			value = this.height;
		}
		return value;
	}
	
	public int getHeight() {
		int value = 0; 
		if ("portrait".equals(currentOrientation)) {
			value = this.height;
		} else {
			value = this.width;
		}
		return value;
	}
	
	/**
	 * Completely clear the display
	 */
	protected void clearDisplay() {
		if (currentTabBar != null) {
			removeComponent(currentTabBar);
		}
		if (currentScreen != null) {
			removeComponent(currentScreen);
		}
		showLoadingScreen();
	}
	
	private void showLoadingScreen() {
		if (currentScreen != null) {
			currentScreen.setVisible(false);
			removeComponent(currentScreen);
		}
		if (currentTabBar != null) {
			currentTabBar.setVisible(false);
		}
		currentScreen = loadingScreen;
		loadingScreen.setVisible(true);
	}
	
	private void hideLoadingScreen() {
		loadingScreen.setVisible(false);
		if (currentTabBar != null) {
			currentTabBar.setVisible(true);
		}
	}
	
	protected boolean setScreenView(ScreenViewImpl screen, List<DataValuePair> data) {
		boolean screenChanged = false;
		
		if (screen == null) {
			return screenChanged;
		}
		
		if (currentScreen != screen) {
			hideLoadingScreen();
			if (currentScreen != null) {
				removeComponent(currentScreen);
				currentScreen = null;
			}
			// Adjust display orientation if necessary
			if (screen.isLandscape()) {
				setOrientation("landscape");
			} else {
				setOrientation("portrait");
			}
			display.add(screen, 0, 0);
			screen.onAdd(getWidth(), getHeight(), data);
			
			currentScreen = screen;
			screenChanged = true;
		}
		return screenChanged;
	}
	
	protected boolean setTabBar(TabBarComponent tabBar) {
		boolean tabBarChanged = false;
		
		if (tabBar == null) {
			return tabBarChanged;
		}
		
		if (currentTabBar != tabBar) {
			if (currentTabBar != null) {
				removeComponent(currentTabBar);
				currentTabBar = null;
			}
			addComponent(tabBar, 0, getHeight() - tabBar.getHeight());
			currentTabBar = tabBar;
			tabBarChanged = true;
		}
		return tabBarChanged;
	}
	
	protected void removeTabBar() {
		if (currentTabBar != null) {
			removeComponent(currentTabBar);
			currentTabBar = null;
		}
	}
	
	private void addComponent(ConsoleComponentImpl component, int left, int top) {
		display.add(component, left, top);
		component.onAdd(component.getOffsetWidth(), component.getOffsetHeight());
	}
	
	private void removeComponent(ConsoleComponentImpl component) {
		component.onRemove();
		display.remove(component);
	}
	
	protected void updateTabBar() {
		if (currentTabBar != null) {
			currentTabBar.refresh();
			display.setWidgetPosition(currentTabBar, 0, getHeight() - currentTabBar.getHeight());
		}
	}
	
	private void updateScreenView() {
		if (currentScreen != null) {
			currentScreen.onRefresh(getWidth(), getHeight());
			display.setWidgetPosition(currentScreen, 0, 0);
		}
	}
	
	protected void highlightTabBarItem(int screenId) {
		if (currentTabBar != null) {
			currentTabBar.onScreenViewChange(screenId);
		}
	}
	
	protected void doResize(int width, int height) {
		display.setWidth(width + "px");
		display.setHeight(height + "px");
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		lastMoveEvent = new PressMoveEvent(event);
		ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(lastMoveEvent);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		lastMoveEvent = new PressMoveEvent(event);
		ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(lastMoveEvent);
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		event.stopPropagation();
		ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new PressCancelEvent(event));
		reset();
	}

	@Override
	public void onRender(int width, int height) {
		display.setWidth(width + "px");
		display.setHeight(height + "px");
		loadingScreen = new LoadingScreenView();
		display.add(loadingScreen, 0, 0);
		loadingScreen.onAdd(width, height, null);
		showLoadingScreen();		
	}
}
