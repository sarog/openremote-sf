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

import java.util.List;
import java.util.Set;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelSize;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public interface PanelService {
	Integer getDefaultGroupId();
	
	Screen getDefaultScreen(Integer groupId);
	
	Panel getCurrentPanel();
	
	void setCurrentPanel(Panel currentPanel);
	
	Screen getScreenById(Integer screenId);
	
	Screen getScreenById(Integer screenId, Integer groupId);
	
	Screen getScreenByName(String name);
	
	Screen getNextScreen(Integer groupId, Integer screenId);
	
	Screen getPreviousScreen(Integer groupId, Integer screenId);
	
	TabBar getTabBar(Integer groupId);
	
	Screen getInverseScreen(Integer screenId);
	
	boolean isInitialized();
	
	PanelSize getPanelSize();

	List<Integer> getGroupScreenIds(Integer groupId);
	
	List<Integer> getGroupScreenIdsWithSameOrientation(Integer screenId, Integer groupId);

	String[] getPanelImageUrls();
}
