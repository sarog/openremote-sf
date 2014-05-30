/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.statuscache.datalogger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.statuscache.EventContext;
import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.statuscache.LifeCycleEvent;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.http.entity.StringEntity;

/**
 * This is an event processor that tries to commit sensor values to the Data Logger
 * REST service asynchronously; failed logging attempts are recorded in the log file.
 * All settings should be defined using bean configuration. Example config: -
 * <bean id="dataLogger" class="org.openremote.controller.statuscache.datalogger.DataLogger">
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
   public final static int DEFAULT_LOG_REPEAT_SECONDS = 3600;
   public final static int QUEUE_FLUSH_INTERVAL = 10000;
   private final static Logger log = Logger.getLogger(Constants.RUNTIME_EVENTPROCESSOR_LOG_CATEGORY + ".datalogger" );
   private static SimpleDateFormat dateFormatter;
   private String dataLoggerURL;
   private String writeAPIKey;
   private List<DataLoggerSensor> sensors;
   private boolean isValid = false;
   private static DataLoggerQueue queueProcessor;
   
   static {
      dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

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
            
            if (!okToLog) {
               StatusCache cache = ctx.getDeviceStateCache();
               Event event = cache.queryStatus(sensorName);
               okToLog = !event.getValue().equals(ctx.getEvent().getValue());
            }
            
            if (!okToLog) {
               log.debug("Ignoring Sensor '" + sensorName + "' as value is unchanged and log repeat time has not elapsed");
               return;
            }
            
            sensor.setLastLogTime(now); 
            String sensorValue = ctx.getEvent().getValue().toString().trim();
            SensorValue value = new SensorValue();
            value.setSensorValue(sensorValue);
            value.setTimestamp(now);
            queueProcessor.queueValue(sensorName, value);
         }
      } catch (Exception e) {
         log.error("Unknown error", e);
      }
   }

   @Override
   public void start(LifeCycleEvent ctx) throws InitializationException {
      URI configUri = getConfigUri();
      parseConfigXML(configUri);

      if (getDataLoggerURL() == null || getDataLoggerURL().isEmpty() || getWriteAPIKey() == null || getWriteAPIKey().isEmpty()) {
         throw new InitializationException("Data Logger Config error: URL and key values must be set");
      }
      
      isValid = true;
      queueProcessor = new DataLoggerQueue();
      queueProcessor.start();
   }
   
   @Override
   public void stop() {
      queueProcessor.cancel();
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
   
   private URI getConfigUri() throws InitializationException {
      ControllerConfiguration config = ServiceContext.getControllerConfiguration();
      URI resourceURI;
      try {
         resourceURI = new URI(config.getResourcePath());

         if (!resourceURI.isAbsolute()) {
            resourceURI = new File(config.getResourcePath()).toURI();
         }
      } catch (URISyntaxException e) {
         throw new InitializationException("Property 'resource.path' value ''{0}'' cannot be parsed. "
               + "It must contain a valid URI : {1}", e, config.getResourcePath(), e.getMessage());
      }
      URI uri = resourceURI.resolve("datalogger/datalogger-config.xml");
      File configFile = new File(uri);
      
      if (!configFile.exists() || !configFile.canRead()) {
         throw new InitializationException("Directory ''{0}'' does not exist or cannot be read.", uri);
      }
      return uri;
   }
   
   private void parseConfigXML(URI configUri) throws InitializationException {
      try {
         File fXmlFile = new File(configUri);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(fXmlFile);
         doc.getDocumentElement().normalize();

         String url = doc.getElementsByTagName("url").item(0).getTextContent();
         String key = doc.getElementsByTagName("key").item(0).getTextContent();
         List<DataLoggerSensor> sensors = new ArrayList<DataLoggerSensor>();
         NodeList nList = doc.getElementsByTagName("sensor");
         
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            NamedNodeMap attributes = nNode.getAttributes();
            String name = attributes.getNamedItem("name").getNodeValue();
            String logRepeat = attributes.getNamedItem("logRepeatSeconds") != null ? attributes.getNamedItem("logRepeatSeconds").getNodeValue() : null;
            int logRepeatSeconds = logRepeat != null ? Integer.parseInt(logRepeat) : DEFAULT_LOG_REPEAT_SECONDS;
            
            if (name != null && !name.isEmpty()) {
               DataLoggerSensor sensor = new DataLoggerSensor();
               sensor.setSensorName(name);
               sensor.setLogRepeatSeconds(logRepeatSeconds);
               sensors.add(sensor);
            }
         }
         
         setDataLoggerURL(url);
         setWriteAPIKey(key);
         setSensors(sensors);
      } catch (Exception e) {
         e.printStackTrace();
         throw new InitializationException("Error parsing datalogger-config.xml", e);
      }
   }

   class DataLoggerQueue extends Thread {
      private static final int TIMEOUT = 5000;
      private final HttpClient client;
      private final Map<String, List<SensorValue>> data = Collections.synchronizedMap(new HashMap<String, List<SensorValue>>());
      boolean stopped;
      private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                         "<eeml>" +
                                         "<environment>";
      private static final String XML_FOOTER = "</environment>" +
                                         "</eeml>";
      
      public DataLoggerQueue() {
         // Set connection timeout
         HttpParams params = new BasicHttpParams();
         HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
         HttpConnectionParams.setSoTimeout(params, TIMEOUT);
         client = new DefaultHttpClient(params);
      }
      
      @Override
      public void run() {
        
        while(!stopped) {
           // Add queued values to xml
           if (data.size() > 0) {
              String sensorXml = XML_HEADER;
              Map<String, List<SensorValue>> inProgressData = new HashMap<String, List<SensorValue>>();
              boolean hasData = false;
              
              synchronized (data) {
                 for (Entry<String, List<SensorValue>> sensorData : data.entrySet()) {

                    List<SensorValue> values = sensorData.getValue();
                    if (values != null && values.size() > 0)
                    {
                       hasData = true;
                       
                       sensorXml += "<data id='" + sensorData.getKey() + "'>" +
                             "<datapoints>";
                       for (SensorValue value : sensorData.getValue())
                       {
                          sensorXml += "<value at='" + dateFormatter.format(value.getTimestamp()) + "'>" + value.getSensorValue() + "</value>"; 
                       }
   
                       sensorXml += "</datapoints>" +
                                    "</data>";
                       inProgressData.put(sensorData.getKey(), new ArrayList<SensorValue>(sensorData.getValue()));
                    }
                }
              }
              
              sensorXml += XML_FOOTER;
              
              // Process this data
              if (hasData) {
                 String url = getDataLoggerURL();
                 
                 final HttpPut request = new HttpPut(url);
                 request.addHeader("User-Agent", "OpenRemoteController");
                 request.addHeader("X-ApiKey", getWriteAPIKey());
   
                 try {
                    request.setEntity(new StringEntity(sensorXml));
                    HttpResponse response = client.execute(request);                 
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                       log.error("Data Logger Service returned an invalid response");
                    } else {
                       for (Entry<String, List<SensorValue>> processedSensor : inProgressData.entrySet()) {
                          List<SensorValue> values = data.get(processedSensor.getKey());
                          values.removeAll(processedSensor.getValue());
                          if (values.size() == 0) {
                             data.remove(processedSensor.getKey());
                          }
                       }
                    }
                  } catch (Exception e) {
                     log.error("Data Logger Service returned an invalid response");
                  } finally {
                     request.abort();
                  }
              }
           }
           
           try {
              Thread.sleep(QUEUE_FLUSH_INTERVAL);
            } catch (InterruptedException e) {
              
            }
        }
      }
      
      void cancel() {
        stopped = true;
        this.interrupt();
      }
      
      void queueValue(String sensorName, SensorValue sensorValue) {
        if (sensorValue == null || sensorValue.sensorValue == null || sensorValue.sensorValue.isEmpty() || sensorValue.sensorValue.equals("N/A")) {
           return;
        }
         
         if (data.containsKey(sensorName)) {
            synchronized (data) {
               List<SensorValue> values = data.get(sensorName);
               if (values.size() < 1000) {
                  values.add(sensorValue);
               } else {
                  log.warn("Queue is full so no more values will be cached");
               }
            }
         } else {
            if (data.size() < 1000) {
               List<SensorValue> values = new ArrayList<SensorValue>();
               values.add(sensorValue);
               data.put(sensorName, values);
            } else {
               log.warn("Queue is full so no more values will be cached");
            }
         }
      }
   }

   class SensorValue {
      private String sensorValue;
      private Date timestamp;
      
      String getSensorValue() {
         return sensorValue;
      }
      
      void setSensorValue(String sensorValue) {
         this.sensorValue = sensorValue;
      }
      
      Date getTimestamp() {
         return timestamp;
      }
      
      void setTimestamp(Date timestamp) {
         this.timestamp = timestamp;
      }      
   }
}