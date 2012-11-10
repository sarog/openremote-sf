/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.unit;

import java.util.List;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.InteractiveConsoleComponent;
import org.openremote.web.console.widget.ScreenIndicator;
import org.openremote.web.console.widget.TabBarComponent;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
/**
 * This is the container where content will actually be displayed
 * An absolute panel is used as a wrapper to ease with repositioning
 * the display within the console unit during orientation change
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ConsoleDisplay extends InteractiveConsoleComponent implements TouchMoveHandler, MouseMoveHandler, MouseOutHandler {
	public static final String CLASS_NAME = "consoleDisplay";
	private AbsolutePanel display;
	private String currentOrientation = "portrait";
	private TabBarComponent currentTabBar;
	private ScreenIndicator currentScreenIndicator;
	private ScreenViewImpl currentScreen;
	
	public ConsoleDisplay() {
		super(new AbsolutePanel(), CLASS_NAME);
		getElement().setId("consoleDisplayWrapper");
		
		// Create display panel where screen is actually loaded
		display = new AbsolutePanel();
		display.addStyleName("portrait");
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
		this.width = width;
		this.height = height;
		
		// Resize the display wrapper
		setWidth(width + "px");
		setHeight(height + "px");
		
		
		if (getOrientation().equalsIgnoreCase("portrait")) {
			display.setWidth(width + "px");
			display.setHeight(height + "px");
		} else {
			display.setWidth(height + "px");
			display.setHeight(width + "px");
		}
		
		// Update display position and Screen indicator position
		setDisplayPosition();
		updateScreenIndicator();
		
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
		
	   display.setWidth(width + "px");
	   display.setHeight(height + "px");
	   
	   setDisplayPosition();
	}
	
	public String getOrientation() {
		return this.currentOrientation;
	}
	
//	public boolean getIsVertical() {
//		boolean response = false;
//		if (currentOrientation.equalsIgnoreCase("portrait")) {
//			response = true;
//		}
//		return response;
//	}
	
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
	
	private void setDisplayPosition() {
		if (currentOrientation.equals("portrait")) {
		   display.removeStyleName("landscape");
		   display.addStyleName("portrait");
		   DOM.setStyleAttribute(display.getElement(), "left", "0px");
		} else {
		   display.removeStyleName("portrait");
		   display.addStyleName("landscape");
		   DOM.setStyleAttribute(display.getElement(), "left", width + "px");
		}
	}
	
	/**
	 * Completely clear the display
	 */
	protected void clearDisplay() {
		if (currentTabBar != null) {
			removeComponent(currentTabBar);
		}
		unloadScreenView();
	}
	
	protected void setScreenView(ScreenViewImpl screen, List<DataValuePairContainer> data) {
		if (currentScreen != screen) {

			unloadScreenView();
			
			BrowserUtils.showLoadingMsg("Loading Screen");
			
			// Adjust display orientation if necessary
			if (screen.isLandscape()) {
				setOrientation("landscape");
			} else {
				setOrientation("portrait");
			}
			// Have to set this before onAdd as if exception is thrown then at least
			// any components added to the display will be removed during screen removal
			currentScreen = screen;
			
			display.add(screen, 0, 0);
			
			BrowserUtils.hideLoadingMsg();
			
			screen.onAdd(getWidth(), getHeight(), data);
		}
	}
	
	protected void unloadScreenView() {
		if (currentScreen != null) {
			currentScreen.setVisible(false);
			removeComponent(currentScreen);
			currentScreen = null;
		}
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
	
	protected void setScreenIndicator(ScreenIndicator screenIndicator) {		
		if (screenIndicator == null) {
			return;
		}
		
		if (screenIndicator != currentScreenIndicator) {
			if (currentScreenIndicator != null) {
				removeComponent(currentScreenIndicator);
				currentScreenIndicator = null;
			}
			addComponent(screenIndicator, (int)Math.round((((double)getWidth() - screenIndicator.getWidth())/2)), getHeight() - 55);
			currentScreenIndicator = screenIndicator;
		}
	}
	
	protected void removeScreenIndicator() {
		if (currentScreenIndicator != null) {
			removeComponent(currentScreenIndicator);
			currentScreenIndicator = null;
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
			currentTabBar.onRefresh(getWidth(), getHeight());
			display.setWidgetPosition(currentTabBar, 0, getHeight() - currentTabBar.getHeight());
		}
	}
	
	private void updateScreenView() {
		if (currentScreen != null) {
			currentScreen.onRefresh(getWidth(), getHeight());
			display.setWidgetPosition(currentScreen, 0, 0);
		}
	}
	
	private void updateScreenIndicator() {
		if (currentScreenIndicator != null) {
			display.setWidgetPosition(currentScreenIndicator, (int)Math.round((((double)getWidth() - currentScreenIndicator.getWidth())/2)), getHeight() - 55);
		}
	}
	
//	protected void highlightTabBarItem(int screenId) {
//		if (currentTabBar != null) {
//			currentTabBar.onScreenViewChange(screenId);
//		}
//	}
	
	protected void doResize(int width, int height) {
		display.setWidth(width + "px");
		display.setHeight(height + "px");
	}
	
	public TabBarComponent getTabBar() {
		return currentTabBar;
	}
	
	public ScreenViewImpl getScreen() {
		return currentScreen;
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
	}
	
	@Override
	public void onUpdate(int width, int height) {
		display.setWidth(width + "px");
		display.setHeight(height + "px");
	}
}
