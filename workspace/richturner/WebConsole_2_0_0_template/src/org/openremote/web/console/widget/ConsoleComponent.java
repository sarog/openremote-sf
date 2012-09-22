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

public interface ConsoleComponent {
	// Called when widget added to console display
	public void onAdd(int width, int height);
	
	// Called when screen size changes
	public void onRefresh(int width, int height);
	
	// Configures the widget based on the values supplied
	public void onRender(int width, int height);
	
	// Called when screen size changes
	public void onUpdate(int width, int height);
	
	// Called when widget removed from console display
	public void onRemove();
	
	// Sets the visibility of the widget
	public void setVisible(boolean visible);
	
	// Sets the sensor info associated with this component
	public void setSensor(Sensor sensor);
	
	// Gets the sensor info associated with this component
	public Sensor getSensor();
}
