/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.onkyoeiscp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openremote.controller.command.Command;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.protocol.onkyoeiscp.OnkyoeISCPGateway.OnkyoResponse;
import org.openremote.controller.utils.Logger;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 * 
 */
public abstract class OnkyoeISCPCommand implements Command {

   
   // Class Members --------------------------------------------------------------------------------

   /**
    * Onkyo eISCP logger. Uses a common category for all Onkyo eISCP related logging.
    */
   private final static Logger log = Logger.getLogger(OnkyoeISCPCommandBuilder.ONKYO_EISCP_LOG_CATEGORY);

   // Keep a list of all the command strings we receive from OR Console and the associated command class to handle that command
   private static HashMap<String, Class<? extends OnkyoeISCPCommand>> commandClasses = new HashMap<String, Class<? extends OnkyoeISCPCommand>>();

   static {
      commandClasses.put("POWER", SimpleCommand.class);
      commandClasses.put("MUTE", SimpleCommand.class);
   }

   /**
    * Factory method for creating AMX NI command instances based on a
    * human-readable configuration strings.
    * 
    * @return new AMX NI command instance
    */
   static OnkyoeISCPCommand createCommand(String name, OnkyoeISCPGateway gateway, String parameter) {
    log.debug("Received request to build command with name " + name);
    
      name = name.trim().toUpperCase();
      Class<? extends OnkyoeISCPCommand> commandClass = commandClasses.get(name);

      log.debug("This command maps to the command class " + commandClass);

      if (commandClass == null) {
         throw new NoSuchCommandException("Unknown command '" + name + "'.");
      }
      OnkyoeISCPCommand cmd = null;
      try {
         Method method = commandClass.getMethod("createCommand", String.class, OnkyoeISCPGateway.class, String.class);
         log.debug("Got the creation method " + method + ", will call it");
         
         cmd = (OnkyoeISCPCommand) method.invoke(null, name, gateway, parameter);
         log.debug("Creation successfull, got command " + cmd);
      } catch (SecurityException e) {
         // TODO: should this be logged, check other source code
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
      } catch (NoSuchMethodException e) {
         // TODO: should this be logged, check other source code
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
      } catch (IllegalArgumentException e) {
         // TODO: should this be logged, check other source code
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
      } catch (IllegalAccessException e) {
         // TODO: should this be logged, check other source code
         throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
      } catch (InvocationTargetException e) {
        if (e.getCause() instanceof NoSuchCommandException) {       
        // This means method threw an exception, re-throw it as is
        throw (NoSuchCommandException)e.getCause();
        } else {
          throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
        }
      // TODO: should this be logged, check other source code
      }
      return cmd;
   }
   
   // Instance Fields ------------------------------------------------------------------------------

   /**
    * Gateway to be used to transmit this command.
    */
   protected OnkyoeISCPGateway gateway;

   /**
    * Name of the command
    */
   protected String name;
   
   /**
    * Sensors we are a command of
    */
   protected List<Sensor> sensors;
   
   // Constructors ---------------------------------------------------------------------------------

   /**
    * Constructs a AMX NI command with a given gateway.
    * 
    * @param gateway AMX NI gateway instance used for transmitting this command
    */
   public OnkyoeISCPCommand(String name, OnkyoeISCPGateway gateway) {
      this.name = name;
      this.gateway = gateway;
      this.sensors = new ArrayList<Sensor>();
   }
   
   /**
    * Add a sensor to update on value change.
    * 
    * @param sensor Sensor to add
    */
   public void addSensor(Sensor sensor) {
      sensors.add(sensor);
   }
   
   /**
    * Remove a sensor to update on value change.
    * 
    * @param sensor Sensor to remove
    */
   public void removeSensor(Sensor sensor) {
      sensors.remove(sensor);
   }
   
   abstract protected void updateWithResponse(OnkyoResponse response);   
   
   /**
    * Update all registered sensors with provided value).
    */
   public void updateSensorsWithValue(String value) {
      for (Sensor sensor : sensors) {
         updateSensorWithValue(sensor, value);
      }     
   }

   /**
    * Update the specified sensor with provided value.
    * 
    * @param sensor Sensor to update
    */
   protected void updateSensorWithValue(Sensor sensor, String value) {
      if (sensor instanceof StateSensor) { // Note this includes SwitchSensor
         sensor.update(value);
      } else if (sensor instanceof RangeSensor) {
         try {
            Integer parsedValue = Integer.parseInt(value);
            if (sensor instanceof LevelSensor) {
               sensor.update(Integer.toString(Math.min(100, Math.max(0, parsedValue))));
            } else {
               sensor.update(Integer.toString(parsedValue));
            }
         } catch (NumberFormatException e){
            log.warn("Received value (" + value + ") invalid, cannot be converted to integer");
         }
      } else{
         log.warn("Query level value for incompatible sensor type (" + sensor + ")");
      }
   }

}
