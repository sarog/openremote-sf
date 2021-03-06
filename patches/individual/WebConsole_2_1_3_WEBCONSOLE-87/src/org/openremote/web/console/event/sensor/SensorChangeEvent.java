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
package org.openremote.web.console.event.sensor;

import com.google.gwt.event.shared.GwtEvent;
/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class SensorChangeEvent extends GwtEvent<SensorChangeHandler> {
	private static final Type<SensorChangeHandler> TYPE = new Type<SensorChangeHandler>();
	private int sensorId;
	private String sensorValue;
	
	public SensorChangeEvent(int sensorId, String sensorValue) {
		this.sensorId = sensorId;
		this.sensorValue = sensorValue;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SensorChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SensorChangeHandler handler) {
		handler.onSensorChange(this);
	}

	public static Type<SensorChangeHandler> getType() {
		return TYPE;
	}
	
	public int getSensorId() {
		return sensorId;
	}
	
	public String getNewValue() {
		return sensorValue;
	}
}
