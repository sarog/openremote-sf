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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openremote.web.console.panel.entity.Link;
import org.openremote.web.console.panel.entity.StateMap;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class Sensor {
	Integer sensorRef;
	boolean valid = false;
	private Map<String, String> map = new LinkedHashMap<String, String>();
	
	public Sensor(Link link) {
		if (link != null) {
			this.sensorRef = link.getRef();
			List<StateMap> stateMap = link.getState();
			if (stateMap != null) {
				for (StateMap state : stateMap) {
					map.put(state.getName(), state.getValue());
				}
			}
			this.valid = true;
		}
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void setIsValid(boolean valid) {
		this.valid = valid;
	}
	
	public int getSensorRef() {
		return sensorRef;
	}
	
	public Map<String, String> getStateMap() {
		return map;
	}
	
	public String getMappedValue(String name) {
		String value = "";
		if (map != null && map.containsKey(name)) {
			value = map.get(name);
		}
		return value;
	}
}
