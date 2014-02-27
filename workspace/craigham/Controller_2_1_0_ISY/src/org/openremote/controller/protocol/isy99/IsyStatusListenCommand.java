/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.isy99;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;

/**
 * Command which handles status updates from the ISY soap subscription. ISY devices typically have range of 0-255, but
 * to maintain compatiblity with existing ISY command, this scales that range from 0 to 100
 * 
 * @author craigh
 * 
 */
public class IsyStatusListenCommand implements EventListener, StatusChangeListener {

   public enum StatusType {
      GET_POWER, GET_LEVEL;
      public static StatusType fromString(String type) {
         if ("get-power".equals(type)) return GET_POWER;
         else if ("status".equals(type)) return GET_LEVEL;
         else
            throw new IllegalArgumentException("Cannot convert '" + type + "' into StatusType");
      }
   };

   StatusType mType;

   public IsyStatusListenCommand(String type) {
      mType = StatusType.fromString(type);
   }

   private List<Sensor> mSensors = new ArrayList<Sensor>();

   @Override
   public void setSensor(Sensor sensor) {
      mSensors.add(sensor);
   }

   @Override
   public void stop(Sensor sensor) {
      mSensors.remove(sensor);
   }

   @Override
   public void update(String value) {
      for (Sensor sensor : mSensors) {
         if (mType.equals(StatusType.GET_POWER)) {
            if (Integer.parseInt(value) > 0) sensor.update("on");
            else
               sensor.update("off");
         } else {
            sensor.update(Integer.toString(Integer.parseInt(value) * 100 / 255));
         }
      }
   }
}