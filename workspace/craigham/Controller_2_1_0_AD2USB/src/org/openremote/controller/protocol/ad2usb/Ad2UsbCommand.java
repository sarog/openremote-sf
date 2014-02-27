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
package org.openremote.controller.protocol.ad2usb;

import org.openremote.controller.Constants;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

public class Ad2UsbCommand implements EventListener {

   private final static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ad2usb");
   private String mCommand;
   private Ad2UsbGateway mGateway = null;

   public Ad2UsbCommand(String address, Ad2UsbGateway client) {
      mCommand = address;
      mGateway = client;
   }

   @Override
   public void setSensor(Sensor sensor) {
      mGateway.registerSensor(sensor);
   }

   @Override
   public void stop(Sensor sensor) {
      mGateway.removeSensor(sensor);
   }

}
