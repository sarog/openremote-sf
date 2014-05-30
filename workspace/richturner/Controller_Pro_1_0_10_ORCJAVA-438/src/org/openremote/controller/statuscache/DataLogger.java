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
import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.statuscache.EventContext;
import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.statuscache.LifeCycleEvent;
import org.openremote.controller.utils.Logger;
import org.apache.http.entity.StringEntity;

/**
 * This is an event processor that tries to commit sensor values to the Data Logger
 * REST service asynchronously; failed logging attempts are recorded in the log file.
 * All settings should be defined using bean configuration. Example config: -
 * <bean id="dataLogger" class="org.openremote.controller.statuscache.DataLogger">
 *   <property name="dataLoggerURL" value="http://[Server URL]/datalogger/rest"/>
 *   <property name="writeAPIKey" value="[API KEY]"/>
 *   <property name = "sensors">
 *     <list>
 *       <value>YourSensor1</value>
 *       <value>YourSensor2</value>
 *     </list>
 *   </property>
 * </bean> 
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class DataLogger extends EventProcessor {

   private final static Logger log = Logger.getLogger(Constants.RUNTIME_EVENTPROCESSOR_LOG_CATEGORY + ".datalogger" );

   private String dataLoggerURL;
   private String writeAPIKey;
   private List<DataLoggerSensor> sensors;
   private boolean isValid = true;
   
   @Override
   public String getName() {
      return "Data Logger";
   }

   @Override
   public synchronized void push(EventContext ctx) {
      if (!isValid) return;     
      
      final String sensorName = ctx.getEvent().getSource();
      try {
         DataLoggerSensor sensor = null;
         if (sensors != null) {
            for (DataLoggerSensor s : sensors) {
               if (sensorName.equalsIgnoreCase(s.getSensorName())) {
                  sensor = s;
                  break;
               }
            }
         }
         
         if (sensor != null) {
            boolean okToLog = false;
            Date lastLogTime = sensor.getLastLogTime();
            Date now = new Date();
            int logRepeat = sensor.getLogRepeatSeconds();
            if (lastLogTime == null || logRepeat <= 0)
            {
               okToLog = true;
            } else {
               long ms = now.getTime() - lastLogTime.getTime();
               okToLog = ms > logRepeat * 1000;
            }
            sensor.setLastLogTime(now);
            
            if (!okToLog) {
               StatusCache cache = ctx.getDeviceStateCache();
               Event event = cache.queryStatus(sensorName);
               okToLog = !event.getValue().equals(ctx.getEvent().getValue());
            }
            
            if (!okToLog) {
               log.debug("Ignoring Sensor '" + sensorName + "' as value is unchanged and log repeat time has not elapsed");
               return;
            }
 
            final String sensorValue = ctx.getEvent().getValue().toString().trim();
            String xmlStr = buildXml(sensorName, sensorValue);
            String url = getDataLoggerURL();
            log.debug("Saving Sensor '" + sensorName + "' [" + sensorValue + "]");
            final CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
            httpclient.start();
            try {
               final HttpPut request = new HttpPut(url);
               request.addHeader("User-Agent", "OpenRemoteController");
               request.addHeader("X-ApiKey", getWriteAPIKey());
               request.setEntity(new StringEntity(xmlStr));

               final Future<HttpResponse> future = httpclient.execute(request, null);
               HttpResponse response = future.get();
               int statusCode = response.getStatusLine().getStatusCode();
               if (statusCode != 200) {
                  log.debug(String.format("Request '" + request.getRequestLine() +"' Failed (Sensor Name = %1$s, Value = %2$s)", sensorName, sensorValue));
               } else {
                  log.debug("Request '" + request.getRequestLine() +"' Completed successfully");
               }
            } catch (Exception e) {
               log.debug(String.format("Request Failed (Sensor Name = %1$s, Value = %2$s)", sensorName, sensorValue));
            } finally {
               httpclient.close();
            }
         }
      } catch (Exception e) {
         log.error("Unknown error", e);
      }
   }

   @Override
   public void start(LifeCycleEvent ctx) throws InitializationException {
      String url = getDataLoggerURL();
      String apiKey = getWriteAPIKey();
      List<DataLoggerSensor> sensors = getSensors();
      if (url == null || url.isEmpty() || apiKey == null || apiKey.isEmpty() || sensors == null || sensors.size() == 0) {
         log.warn("Invalid settings, please ensure dataLoggerURL, apiKey and sensor list are defined in applicationContext.xml");
         isValid = false;
      }
   }
   
   @Override
   public void stop() {
   }

   public String getDataLoggerURL() {
      return dataLoggerURL;
   }

   public void setDataLoggerURL(String dataLoggerURL) {
      this.dataLoggerURL = dataLoggerURL;
   }

   public String getWriteAPIKey() {
      return writeAPIKey;
   }

   public void setWriteAPIKey(String writeAPIKey) {
      this.writeAPIKey = writeAPIKey;
   }

   public List<DataLoggerSensor> getSensors() {
      return sensors;
   }

   public void setSensors(List<DataLoggerSensor> sensors) {
      this.sensors = sensors;
   }
   
   private String buildXml(String sensorName, String sensorValue) {
      if (sensorName == null || sensorName.isEmpty() || sensorValue == null || sensorValue.isEmpty()) {
         return null;
      }
      
      String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                      "<eeml>" +
                      "<environment>" +
                      "<data id='" + sensorName + "'>" +
                      "<current_value>" + sensorValue + "</current_value>" +
                      "</data>" +
                      "</environment>" +
                      "</eeml>";
      return xmlStr;
   }
}
