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
package org.openremote.datalogger.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="data")
public class Data {
	@XmlAttribute
	private String id;
	
	@XmlElementWrapper(name = "datapoints")
	@XmlElement(name="value")
	private List<DataPoint> dataPoints;
	
	@XmlElement(name="current_value")
	private String currentValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<DataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setValues(ArrayList<DataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

}
