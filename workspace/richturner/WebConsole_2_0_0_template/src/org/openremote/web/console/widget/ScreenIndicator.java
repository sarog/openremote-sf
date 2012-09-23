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
package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.ui.ScreenViewChangeEvent;
import org.openremote.web.console.event.ui.ScreenViewChangeHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ScreenIndicator extends PassiveConsoleComponent implements ScreenViewChangeHandler {
	private static final String CLASS_NAME = "screenIndicatorComponent";
	private static final String ITEM_CLASS_NAME = "screenIndicatorItem";
	private static final int INDICATOR_SIZE = 5;
	private static final int INDICATOR_SPACING = 8;
	private List<Integer> screenIds;
	private List<Widget> screenIndicators;
	
	public ScreenIndicator(List<Integer> screenIds) {
		super(new HorizontalPanel(), CLASS_NAME);
		HorizontalPanel container = (HorizontalPanel)getWidget();
		DOM.setIntStyleAttribute(getElement(), "zIndex", 1000 );
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.screenIds = screenIds;
		screenIndicators = new ArrayList<Widget>();
		
		if (screenIds != null) {
			width = INDICATOR_SIZE * screenIds.size() + (INDICATOR_SPACING * (screenIds.size()-1));
			setWidth(width + "px");
			setHeight(INDICATOR_SIZE + "px");
			height = INDICATOR_SIZE;
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onAdd(int width, int height) {
		onRender(width, height);
		setVisible(true);
		isInitialised = true;
	}

	@Override
	public void onRender(int width, int height) {
		// Add a simple div for each screen
		HorizontalPanel container = (HorizontalPanel)getWidget();
		for (int i=0; i<screenIds.size(); i++) {
			int cellWidth = INDICATOR_SIZE;
			cellWidth = i != screenIds.size()-1 ? cellWidth + INDICATOR_SPACING : cellWidth;
			Widget screenIndicator = new HTML();
			screenIndicator.setWidth(INDICATOR_SIZE + "px");
			screenIndicator.setHeight(INDICATOR_SIZE + "px");
			screenIndicator.setStylePrimaryName(ITEM_CLASS_NAME);
			screenIndicators.add(screenIndicator);
			container.add(screenIndicator);
			container.setCellWidth(screenIndicator, cellWidth + "px");
		}
		
		// Register screen change handler
		registerHandler(ConsoleUnitEventManager.getInstance().getEventBus().addHandler(ScreenViewChangeEvent.getType(),this));
	}
	
	@Override
	public void onUpdate(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScreenViewChange(ScreenViewChangeEvent event) {
		if (!handlersRegistered) {
			return;
		}
		// Cycle through indicators to find this screen id
		for (int i=0; i<screenIds.size(); i++) {
			if (screenIds.get(i) == event.getNewScreenId()) {
				screenIndicators.get(i).addStyleName("selected");
			} else {
				screenIndicators.get(i).removeStyleName("selected");
			}
		}
	}
}
