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
package org.openremote.controller.protocol.upnp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.ReadCommand;

/**
 * UPnP Event class
 * 
 * @author Mathieu Gallissot
 */
public class UPnPCommand extends ReadCommand implements ExecutableCommand, DeviceChangeListener, EventListener, org.cybergarage.upnp.event.EventListener {

   private String deviceUDN;
   private String action;
   private Map<String, String> args;
   private ControlPoint controlPoint;
   private String service;
   private Device device;
   private List<Sensor> sensors = new LinkedList<Sensor>();
   protected String cache = "";
   
   private static Logger logger = Logger.getLogger(UPnPCommandBuilder.UPNP_PROTOCOL_LOG_CATEGORY);

   /**
    * Constructor of the UPnP event.
    * 
    * @param controlPoint
    *           An UPnP control point already started
    * @param device
    *           Device id, preferably the uuid of the device, but friendly name can be accepted too.
    * @param action
    *           Action to invoke on the remote device. This action must be available on the remote device, external
    *           tools may be used in order to discover devices capabilities.
    * @param args
    *           Arguments to pass onto the action. These are mandatory, and action specific. An external tool may be
    *           used in order to discover actions requirements.
    */
   public UPnPCommand(ControlPoint controlPoint, String device, String service, String action, Map<String, String> args) {
      this.deviceUDN = device;
      this.service = service;
      this.action = action;
      this.args = args;
      this.controlPoint = controlPoint;
      this.controlPoint.addDeviceChangeListener(this);
      if(this.controlPoint.getDevice(this.deviceUDN) != null) {
         this.deviceAdded(this.controlPoint.getDevice(this.deviceUDN));
      }
   }

   @Override
   public void send() {
      // First, let's grab the device corresponding to the event's id
      if (this.device == null) {
         Device dev = this.controlPoint.getDevice(this.deviceUDN);
         if (dev != null) {
            this.device = dev;
         } else {
            logger.warn("Device not found :" + this.deviceUDN + " , event aborted.");
            return;
         }
      }

      Service serv = device.getService(this.service);
      if (serv == null) {
         logger.warn("Service not found :" + this.service + " , event aborted.");
         return;
      }

      // then, let's find the action in the device.
      Action act = serv.getAction(this.action);
      if (act == null) {
         logger.warn("Action not found :" + this.action + " , event aborted.");
         return;
      }

      // Filling the arguments
      Iterator<String> i = args.keySet().iterator();
      while (i.hasNext()) {
         String argName = i.next();
         act.setArgumentValue(argName, this.args.get(argName));
      }

      // Post the action
      if(!act.postControlAction()) {
         logger.warn("Sending the command failed with status " + act.getControlStatus().getDescription());
         return;
      }
      logger.info("command " + this.action + " successfully send to " + this.device.getFriendlyName());
      
      
      //Processing results, if any
      Iterator<Argument> j = act.getArgumentList().iterator();
      while (j.hasNext()) {
         Argument arg = j.next();
         if(arg.isOutDirection()) {
            this.cache = arg.getValue();
            logger.info("action returned for argument \"" + arg.getName() + "\" value: " + this.cache);
            for (Sensor sensor : this.sensors) {
               sensor.update(this.cache);
            }
            logger.debug("sensors updated with new value");
         }
         
      }
   }


   // Methods to manage if the device has been yet discovered or not
   @Override
   public void deviceAdded(Device dev) {
      if (dev.getUDN().equals(this.deviceUDN)) {
         this.device = dev;
         logger.info("Device \"" + dev.getFriendlyName() + "\" is online");
         this.controlPoint.addEventListener(this);
         if(this.controlPoint.subscribe(this.device.getService(this.service))) {
            logger.info("succesfully subscribed to " + dev.getFriendlyName() + "/" + this.service);
         } else {
            logger.warn("failed to subscribe to " + dev.getFriendlyName() + "/" + this.service);
            this.controlPoint.removeEventListener(this);
         }
      }
   }

   @Override
   public void deviceRemoved(Device dev) {
      if (dev.getUDN().equals(this.deviceUDN)) {
         logger.info("Device \"" + dev.getFriendlyName() + "\" is offline");
         /* if(this.controlPoint.unsubscribe(this.device.getService(this.service))) {
            logger.info("succesfully unsubscribed to " + this.device.getFriendlyName() + "/" + this.service);
         } else {
            logger.warn("failed to unsubscribe to " + this.device.getFriendlyName() + "/" + this.service);
         }*/
         this.controlPoint.removeEventListener(this);
         this.device = null;
      }
   }

   @Override
   public void setSensor(Sensor sensor) {
      this.sensors.add(sensor);
   }

   @Override
   public void stop(Sensor sensor) {
      this.sensors.remove(sensor);
   }

   @Override
   public String read(Sensor sensor) {
      return this.cache;
   }

   @Override
   public void eventNotifyReceived(String uuid, long seq, String varName, String value) {
      logger.info("received event from " + uuid + " for var " + varName + " with value " + value);
      this.cache = value;
      for (Sensor sensor : this.sensors) {
         sensor.update(value);
      }
   }

}
