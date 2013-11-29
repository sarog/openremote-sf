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
import org.openremote.web.console.util.BrowserUtils;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ScreenIndicator extends PassiveConsoleComponent implements ScreenViewChangeHandler {
	private static final String CLASS_NAME = "screenIndicatorComponent";
	private static final String ITEM_CLASS_NAME = "screenIndicatorItem";
	private static final String SPACER_CLASS_NAME = "screenIndicatorSpacer";
	private static int INDICATOR_SIZE = 6;
	private List<Integer> screenIds;
	private List<Widget> screenIndicators;
	
	static {
		int[] size;
		size = BrowserUtils.getSizeFromStyle(ITEM_CLASS_NAME);
		INDICATOR_SIZE = size[1] == 0 ? INDICATOR_SIZE : size[1];
	}
	
	public ScreenIndicator(List<Integer> screenIds) {
		super(new HorizontalPanel(), CLASS_NAME);
		HorizontalPanel container = (HorizontalPanel)getWidget();
		DOM.setIntStyleAttribute(getElement(), "zIndex", 1000 );
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		container.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.screenIds = screenIds;
		screenIndicators = new ArrayList<Widget>();
		
		if (screenIds != null) {
			width = INDICATOR_SIZE * ((screenIds.size()*2) - 1);
			setWidth(width + "px");
			setHeight(INDICATOR_SIZE + "px");
			height = INDICATOR_SIZE;
			
			// Add a simple div for each screen
			for (int i=0; i<screenIds.size(); i++) {
				int cellWidth = INDICATOR_SIZE;
				//cellWidth = i != screenIds.size()-1 ? cellWidth + INDICATOR_SPACING : cellWidth;
				Widget screenIndicator = new SimplePanel();
				screenIndicator.setWidth(INDICATOR_SIZE + "px");
				screenIndicator.setHeight(INDICATOR_SIZE + "px");
				screenIndicator.setStylePrimaryName(ITEM_CLASS_NAME);
				screenIndicators.add(screenIndicator);
				container.add(screenIndicator);
				container.setCellWidth(screenIndicator, cellWidth + "px");
				
				if (i<screenIds.size()-1) {
					// Add a spacer
					Widget spacer = new SimplePanel();
					spacer.setWidth(INDICATOR_SIZE + "px");
					spacer.setHeight(INDICATOR_SIZE + "px");
					spacer.setStylePrimaryName(SPACER_CLASS_NAME);
					container.add(spacer);
					container.setCellWidth(spacer, cellWidth + "px");
				}
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return INDICATOR_SIZE;
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
		if (!isInitialised)
		{
			// Register screen change handler
			registerHandler(ConsoleUnitEventManager.getInstance().getEventBus().addHandler(ScreenViewChangeEvent.getType(),this));
		}
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
