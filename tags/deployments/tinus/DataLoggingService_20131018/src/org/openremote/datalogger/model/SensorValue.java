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
package org.openremote.datalogger.model;

import java.util.Date;

import javax.xml.bind.annotation.*;
import javax.persistence.*;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="value")
@Entity
@Table(name="sensorValues")
public class SensorValue {
	@XmlTransient
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long Id;
	
	@XmlValue
	@Column(name="value", nullable=false)
	private String value;

	@XmlAttribute(name="at")
	@Column(name="timestamp", nullable=false)
	private Date timestamp;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name="sensorId", nullable = false)
	private Sensor sensor;
	
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
}
