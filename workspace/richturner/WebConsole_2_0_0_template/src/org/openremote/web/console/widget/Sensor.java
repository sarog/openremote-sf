package org.openremote.web.console.widget;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openremote.web.console.panel.entity.Link;
import org.openremote.web.console.panel.entity.StateMap;

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
	
	public String getMappedValue(String name) {
		String value = "";
		if (map != null && map.containsKey(name)) {
			value = map.get(name);
		}
		return value;
	}
}
