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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="value")
public class DataPoint {
	@XmlAttribute
	private Date at;
	
	@XmlValue
	private String val;

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public Date getAt() {
		return at;
	}

	public void setAt(Date at) {
		this.at = at;
	}
	

}
