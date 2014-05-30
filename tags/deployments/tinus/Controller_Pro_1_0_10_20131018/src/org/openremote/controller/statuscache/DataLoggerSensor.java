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
package org.openremote.controller.statuscache;

import java.util.Date;

/**
 * Simple bean for defining the name of sensor to be logged and the
 * log repeat time (i.e. if sensor value has not changed but N seconds
 * have passed then log the value anyway)
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class DataLoggerSensor {
   String sensorName;
   int logRepeatSeconds;
   Date lastLogTime;
   
   public String getSensorName() {
      return sensorName;
   }
   public void setSensorName(String sensorName) {
      this.sensorName = sensorName;
   }
   public int getLogRepeatSeconds() {
      return logRepeatSeconds;
   }
   public void setLogRepeatSeconds(int logRepeatSeconds) {
      this.logRepeatSeconds = logRepeatSeconds;
   }
   public Date getLastLogTime() {
      return lastLogTime;
   }
   public void setLastLogTime(Date lastLogTime) {
      this.lastLogTime = lastLogTime;
   }   
}
