/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.protocol.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.component.Sensor;

/**
 * The HTTP GET Command.
 *
 * @author Marcus 2009-4-26
 * @author Dan Cong
 */
public class HttpGetCommand implements ExecutableCommand, StatusCommand {

   /** The logger. */
   private static Logger logger = Logger.getLogger(HttpGetCommand.class.getName());

   /** A name to identify command in controller.xml. */
   private String name;

   /** The url to perform the http get request on */
   private String url;

   /**
    * Gets the name.
    *
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    *
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }


   /**
    * Gets the url
    * @return the url
    */
   public String getUrl() {
      return url;
   }

   /**
    * Sets the url
    * @param url the new url
    */
   public void setUrl(String url) {
      this.url = url;
   }



   /**
    * {@inheritDoc}
    */
   @Override
   public void send() {
      requestURL();
   }

   private String requestURL() {
      BufferedReader in = null;
      try {
         URL url = new URL(getUrl());
         in = new BufferedReader(new InputStreamReader(url.openStream()));
         StringBuffer result = new StringBuffer();
         String str;
         while ((str = in.readLine()) != null) {
            result.append(str);
         }
         logger.info("received message: " + result);
         return result.toString();
      } catch (Exception e) {
         logger.error("HttpGetCommand could not execute", e);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               logger.error("BufferedReader could not be closed", e);
            }
         }
      }
      return "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String read(EnumSensorType sensoryType, Map<String, String> stateMap) {
      String rawResult = requestURL();
      if (sensoryType == null || stateMap == null) {
         return rawResult;
      }
      if ("".equals(rawResult)) {
         return UNKNOWN_STATUS;
      }
      switch (sensoryType) {
      case RANGE:
         break;
      case LEVEL:
         String min = stateMap.get(Sensor.RANGE_MIN_STATE);
         String max = stateMap.get(Sensor.RANGE_MAX_STATE);
         try {
            int val = Integer.valueOf(rawResult);
            if (min != null && max != null) {
               int minVal = Integer.valueOf(min);
               int maxVal = Integer.valueOf(max);
               return String.valueOf(100 * (val - minVal)/ (maxVal - minVal));
            } 
         } catch (ArithmeticException e) {
            break;
         }
         break;
      default://NOTE: if sensor type is RANGE, this map only contains min/max states.
         for (String state : stateMap.keySet()) {
            if (rawResult.equals(stateMap.get(state))) {
               return state;
            }
         }
      }
      return rawResult;
   }

}
