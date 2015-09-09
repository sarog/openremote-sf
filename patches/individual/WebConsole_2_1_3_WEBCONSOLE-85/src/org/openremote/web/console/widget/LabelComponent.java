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

import java.util.List;

import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.panel.entity.Link;
import org.openremote.web.console.panel.entity.StateMap;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class LabelComponent extends PassiveConsoleComponent implements SensorChangeHandler {
	public static final String CLASS_NAME = "labelComponent";
	private Link sensorLink;
	
	public LabelComponent() {
		super(new Label(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(getElement(), "display", "inline-block");
	}
	
	public void setText(String text) {
		((Label)getWidget()).setText(text);
	}
	
	public void setColor(String color) {
		getElement().getStyle().setProperty("color", color);
	}
	
	public void setFontSize(int size) {
		getElement().getStyle().setProperty("fontSize", size + "px");
	}

	public Link getSensorLink() {
		return sensorLink;
	}

	public void setSensorLink(Link sensorLink) {
		this.sensorLink = sensorLink;
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		DOM.setStyleAttribute(getElement(), "lineHeight", height + "px");
	}
	
	@Override
	public void onUpdate(int width, int height) {
		DOM.setStyleAttribute(getElement(), "lineHeight", height + "px");
	}
	
	@Override
	public void onSensorAdd() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sensorChanged(String newValue) {
		String newStr = newValue;
		
		// Look for state map for this new value
		Link link = getSensorLink();
		
		if (link != null)
		{
			List<StateMap> states = link.getState();
	
			if (states != null)
			{
				for (StateMap state : states)
				{
					if (state.getName().equalsIgnoreCase(newValue))
					{
						newStr = state.getValue();
						break;
					}
				}
			}
		}
		
		setText(newStr);
	}

	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.LabelComponent entity) {
		LabelComponent component = new LabelComponent();
		if (entity == null) {
			return component;
		}
		component.setSensor(new Sensor(entity.getLink()));
		component.setSensorLink(entity.getLink());
		component.setId(entity.getId());
		component.setText(entity.getText());
		component.setColor(entity.getColor());
		component.setFontSize(entity.getFontSize());
		return component;
	}
}
