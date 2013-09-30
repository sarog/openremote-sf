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
package org.openremote.datalogger.connector;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import org.openremote.datalogger.exception.DataConnectorException;
import org.openremote.datalogger.exception.DataSecurityException;
import org.openremote.datalogger.model.SensorValue;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public interface DataConnector {
	// Initialise the data connector	
	void init() throws DataConnectorException;
	
	// Set the current value for a particular sensor ID and API Key should also add a sensor value record to the sensor values
	void setSensorCurrentValue(String apiKey, String sensorName, String currentValue) throws DataSecurityException, DataConnectorException;
	
	// Set multiple values for a particular sensor ID and API Key
	void addSensorValues(String apiKey, String sensorName, Set<SensorValue> values, String currentValue) throws DataSecurityException, DataConnectorException;
	
	float getAverageSensorValue(String apiKey, String sensorName, Date fromTime, Date toTime) throws DataSecurityException, DataConnectorException;
	
	// Cleanup the data connector
	void destroy();
}
