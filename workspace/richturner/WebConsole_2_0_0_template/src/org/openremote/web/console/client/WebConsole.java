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
package org.openremote.web.console.client;

import org.openremote.web.console.panel.entity.PanelSizeInfo;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.LocalDataService;
import org.openremote.web.console.service.LocalDataServiceImpl;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * This class configures the Window for mobile and desktop environments
 * and determines the size of the window we have to work with:
 * windowHeight = Longest window dimension (i.e. portrait)
 * windowWidth = Shortest window dimension
 */
public class WebConsole implements EntryPoint {
	private static ConsoleUnit consoleUnit;
	private static final String DEFAULT_PANEL_SIZE_INFO = "{\"panelSizeType\": \"fixed\", \"panelSizeWidth\": 320, \"panelSizeHeight\": 480}";
	public void onModuleLoad() {
		// Export method to hide alert window from native JS
		BrowserUtils.exportStaticMethod();
		
		BrowserUtils.initWindow();
		
		// Initialise the Console Unit - if mobile or Setting defined to load in fullscreen mode
		if (BrowserUtils.isMobile) {
			consoleUnit = new ConsoleUnit(true);
		} else {
			// Check preferences
			LocalDataService dataService = LocalDataServiceImpl.getInstance();
			PanelSizeInfo sizeInfo = null;

			String panelSizeInfo = dataService.getObjectString("panelSizeInfo");
			if (panelSizeInfo == null) {
				// Generate defaults
				dataService.setObject("panelSizeInfo", DEFAULT_PANEL_SIZE_INFO);
				sizeInfo = AutoBeanService.getInstance().fromJsonString(PanelSizeInfo.class, DEFAULT_PANEL_SIZE_INFO).as();
			} else {
				sizeInfo = AutoBeanService.getInstance().fromJsonString(PanelSizeInfo.class, panelSizeInfo).as();
			}
			
			if (sizeInfo != null) {
				// Get stored size info
				if (sizeInfo.getPanelSizeType().equals("fullscreen")) {
					consoleUnit = new ConsoleUnit(true);
				} else {
					consoleUnit = new ConsoleUnit(sizeInfo.getPanelSizeWidth(), sizeInfo.getPanelSizeHeight());
				}
			} else {				
				consoleUnit = new ConsoleUnit();
			}
			
			SlidingToolbar.initialise(sizeInfo);
		}
		
		if (consoleUnit != null) {
			RootPanel.get().add(consoleUnit, 0, 0);
			getConsoleUnit().onAdd();
		} else {
			Window.alert("Failed to create Console Unit!");
		}
	}
	
	public static ConsoleUnit getConsoleUnit() {
		return consoleUnit;
	}
}
