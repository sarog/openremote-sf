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
package org.openremote.web.console.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.FormLayout;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.ListLayout;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.GridPanelComponent;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;
import org.openremote.web.console.widget.panel.list.ListPanelComponent;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ScreenViewService {
	private static ScreenViewService instance = null;
	Map<Integer, ScreenViewImpl> screenViewMap = new HashMap<Integer, ScreenViewImpl>();	
	
	private ScreenViewService() {}
	
	public static synchronized ScreenViewService getInstance() {
		if (instance == null) {
			instance = new ScreenViewService();
		}
		return instance;
	}
	
	public ScreenViewImpl getScreenView(int screenId) {
		ScreenViewImpl screenView;
		screenView = screenViewMap.get(screenId);
		return screenView;
	}
	
	public ScreenViewImpl getScreenView(Screen screen) {
		ScreenViewImpl screenView = null;
		if (screen != null) {
			int screenId = screen.getId();
			screenView = screenViewMap.get(screenId);
			if (screenView == null) {
				screenView = buildScreenView(screen);
				if (screenView != null) {
					screenViewMap.put(screenId, screenView);
				}
			}
		}
		return screenView;
	}
	
	
	public void reset() {
		screenViewMap.clear();
	}
	
	private ScreenViewImpl buildScreenView(Screen screen) {
		ScreenViewImpl screenView = new ScreenViewImpl();
		
		// Set background if defined
		screenView.setBackground(screen.getBackground());
		
		// Check orientation
		Boolean isLandscape = screen.getLandscape();
		if (isLandscape != null && isLandscape) {
			screenView.setIsLandscape(true);
		}
		
		// Create panel components
		try {
			List<AbsoluteLayout> absoluteElems = screen.getAbsolute();
			
			if (absoluteElems != null) {
				for (AbsoluteLayout layout : absoluteElems) {
					// Create Absolute Panel Component
					AbsolutePanelComponent absPanel = AbsolutePanelComponent.build(layout);
					screenView.addPanelComponent(absPanel);
				}
			}
			
			List<GridLayout> gridElems = screen.getGrid();
			
			if (gridElems != null) {
				for (GridLayout layout : gridElems) {
					// Create Grid Panel Component
					GridPanelComponent gridPanel = GridPanelComponent.build(layout);
					screenView.addPanelComponent(gridPanel);
				}
			}
			
			List<FormLayout> formElems = screen.getForm();
			
			if (formElems != null) {
				for (FormLayout layout : formElems) {
					FormPanelComponent formPanel = FormPanelComponent.build(layout);
					screenView.addPanelComponent(formPanel);
				}
			}
			
			List<ListLayout> listElems = screen.getList();
			
			if (listElems != null) {
				for (ListLayout layout : listElems) {
					ListPanelComponent listPanel = ListPanelComponent.build(layout);
					screenView.addPanelComponent(listPanel);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return screenView;
	}
}
