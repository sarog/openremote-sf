/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.statuscache.emoncms;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openremote.controller.Constants;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.statuscache.EventContext;
import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.statuscache.LifeCycleEvent;
import org.openremote.controller.utils.Logger;

/**
 * This is an EventProcessor that tries to convert each sensor value into a double 
 * and uploads the reult to an EmonCMS account. The EmonCMD API URL has to be configured 
 * in config.properties
 * 
 * @author marcus
 *
 */
public class EmonDataLogger extends EventProcessor {

   private final static Logger log = Logger.getLogger(Constants.RUNTIME_EVENTPROCESSOR_LOG_CATEGORY + ".emonlogger" );

   
   private String emonURL;
   private List<String> sensors;
   
   @Override
   public String getName() {
      return "EmonCMS Data Logger";
   }

   @Override
   public synchronized void push(EventContext ctx) {
      String sensorName = ctx.getEvent().getSource();
      try {
         if (sensors.contains(sensorName)) {
            double value = Double.parseDouble(ctx.getEvent().getValue().toString().trim());
            String url = emonURL+ URLEncoder.encode("{"+sensorName+":"+value+"}");
            log.debug("Trying to log value '" + value + "' for sensor '" + sensorName + "' to EmonCMS");
            DefaultHttpClient client = new DefaultHttpClient();
            HttpUriRequest request = new HttpGet(url);
            request.addHeader("User-Agent", "OpenRemoteController");
            client.execute(request);
         }
      } catch (NumberFormatException nfe) {
         log.debug("The value: '" + ctx.getEvent().getValue().toString().trim() + "' could not be converted into a double.");
      } catch (Exception e) {
         log.error("Unknown error", e);
      }
   }

   @Override
   public void start(LifeCycleEvent ctx) throws InitializationException {
   }
   
   @Override
   public void stop() {
   }

   public String getEmonURL() {
      return emonURL;
   }

   public void setEmonURL(String emonURL) {
      this.emonURL = emonURL;
   }

   public List<String> getSensors() {
      return sensors;
   }

   public void setSensors(List<String> sensors) {
      this.sensors = sensors;
   }
   
   
}
