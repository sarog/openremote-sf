/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.cbus;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;

/**
 * CBus is a lighting and automation protocol created by Clipsal (now owned by Schneider)
 * It's functionality is similar to other lighting protocols like Lutron
 * 
 * The following commands are available at the moment:
 * - STATUS (returns the current status/level of the CBus address)
 * - ON (turn a CBus address to ON)
 * - OFF (turn a CBus address to OFF)
 * - SETVALUE (set a CBus address to a specific value immediately)
 * - PULSE (set a CBus address to ON for a certain period of time and then set it to OFF)
 * - DIM (similar to SETVALUE, but mainly used for lighting and adds the ability to set how slowly you want the value to change)
 * 
 * @author Jamie Turner
 */
public class CBusCommand implements ExecutableCommand, EventListener
{
      
   /**
    * The logger
    */
   private final static Logger log = Logger.getLogger(CBusCommandBuilder.CBUS_LOG_CATEGORY);

   /**
    * Gateway to be used to transmit this command.
    */
   protected CBusGateway gateway;

   /**
    * Sensors we are a command of
    */
   protected List<Sensor> sensors;
   
   /** CBus network for command */
   protected String network;
   
   /** CBus application for command */
   protected String application;
   
   /** CBus group for command */
   protected String group;
   
   /** type of CBus command */
   private CBusCommandType command;
   
   /** parameter for the CBus command - parameters must be pipe-delimited '|'*/
   private String commandValue;
   
   
   /**
    * Create a command object
    * 
    * @param command
    *           The type of command
    *           
    * @param projectAddress
    *           The CBus project address
    *           
    * @param network
    *           The CBus network address
    *           
    * @param application
    *           The CBus application address
    *           
    * @param group
    *           The CBus group address
    *           
    * @param commandValue
    *           The parameters for the command (pipe-delimited if more than one)
    *           
    * @param gateway
    *           The CBus gateway to use to send the command
    *           
    * @return The created command object
    */
   static CBusCommand createCommand(CBusCommandType command, String network, 
         String application, String group, String commandValue, CBusGateway gateway) 
   {
      
      log.debug("Creating CBus command " + command);
      
      CBusCommand cmd = null;

      switch(command)
      {
      case STATUS:  
         cmd = new CBusCommand(CBusCommandType.STATUS, network, application, group, null, gateway);
         break;

      default:
         cmd = new CBusCommand(command, network, application, group, commandValue, gateway);
         break;
      }      

      return cmd;
   }
   
   /**
    * Constructor
    * 
    * @param command
    *           Command type
    *           
    * @param network
    *           The CBus network name
    *           
    * @param application
    *           The CBus application
    *           
    * @param group
    *           The CBus group
    *           
    * @param commandValue
    *           The parameters for the command (pipe-delimited if more than one)
    *           
    * @param gateway
    *           The CBus gateway to use to send the command
    */
   public CBusCommand(CBusCommandType command, String network, String application, String group, String commandValue, CBusGateway gateway)
   {
      this.gateway = gateway;
      this.command = command;
      this.network = network;
      this.application = application;
      this.group = group;
      this.commandValue = commandValue;
      
      this.sensors = new ArrayList<Sensor>();
   }
   
   /**
    * ExecutableCommand interface
    */
   @Override
   public void send() 
   {
     CBusAddress a = gateway.getSystem().getCBusAddress(network, application, group);
     if(command.equals(CBusCommandType.DIM))
     {
        CBusCommand.log.debug("DIM values are " + commandValue);
        
        String[] dimValues = separateCommandElements();
        if(dimValues == null)
           CBusCommand.log.error("*CBUS ERROR* An error while trying to dim with no command parameters: " + commandValue);
        else if (dimValues.length < 1)
           CBusCommand.log.error("*CBUS ERROR* An error while trying to dim with the wrong command parameters: " + commandValue + ", only " +
             dimValues.length + " parameters - level is required");
        else if(dimValues.length == 1)
           a.dim(dimValues[0], "1s");
        else
           a.dim(dimValues[0], dimValues[1]);        
     }
     else if(command.equals(CBusCommandType.ON) || command.equals(CBusCommandType.OFF))
     {
        a.turnOnOff(command.equals(CBusCommandType.ON));
     }
     else if(command.equals(CBusCommandType.SETVALUE))
     {
        a.setValue(commandValue);
     }
     else if(command.equals(CBusCommandType.PULSE))
     {
        try
        {
           if(commandValue == null || commandValue.length() < 1)
              CBusCommand.log.error("*CBUS ERROR* No command parameters supplied for pulse command");
           else
           {
              int seconds = Integer.parseInt(commandValue);
              a.pulse(seconds);
           }
        }
        catch(NumberFormatException nfe)
        {
           CBusCommand.log.error("*CBUS ERROR* Trying to pulse a group value with an invalid duration (seconds): " + commandValue);
        }
        
     }
      //else the command is the status command and will use the EventListener interface      
   }
   
   /**
    * Utility class to separate parameters provided in commandValue
    * 
    * @return The individual parameters as an array
    */
   private String[] separateCommandElements()
   {
      if(commandValue.indexOf("|") >= 0)
         return commandValue.split("|");
      else
         return new String[] {commandValue};      
   }
   
  /**
   * EventListener interface    
   */
  @Override
  public void setSensor(Sensor sensor) {
     log.debug("setting CBUS sensor");
          
     if (sensors.isEmpty()) 
     {
        // First sensor registered, we also need to register ourself with the device
        CBusAddress device = ((CBusGateway) gateway).getSystem().getCBusAddress(network, application, group);
        if (device == null) 
        {
           // This should never happen as above command is supposed to create device
           log.warn("Gateway could not create a CBus device we're receiving feedback for (" + 
                 "//" + network + "/" + application + "/" + group + ")");
        }

        // Register ourself with the Keypad so it can propagate update when received
        device.addCommand(this);
        addSensor(sensor);

        // Trigger a query to get the initial value
        device.queryCBusLevel();

     } 
     else 
     {
        addSensor(sensor);
     }
  }
  
  @Override
  public void stop(Sensor sensor)
  {
     removeSensor(sensor);
     if (sensors.isEmpty())
     {
        // Last sensor removed, we may unregister ourself from device

        CBusAddress device = gateway.getSystem().getCBusAddress(network, application, group);
        if (device == null)
        {
           // This should never happen because above command is supposed to create device
           log.warn("Gateway could not create a CBus device we're receiving feedback for (" + 
                 "//" + network + "/" + application + "/" + group + ")");
        }

        device.removeCommand(this);
     }
  }

  /**
   * Add a sensor to update on value change.
   * 
   * @param sensor Sensor to add
   */
  public void addSensor(Sensor sensor)
  {
     sensors.add(sensor);
  }
  
  /**
   * Remove a sensor to update on value change.
   * 
   * @param sensor Sensor to remove
   */
  public void removeSensor(Sensor sensor)
  {
     sensors.remove(sensor);
  }
  
  /**
   * Update all registered sensor (because of value change on given device).
   * 
   * @param device CBusAddress that triggers the update
   */
  protected synchronized void updateSensors(CBusAddress device) 
  {
     for (Sensor sensor : sensors) 
     {
        updateSensor(device, sensor);
     }     
  }

  /**
   * Update the specified sensor based on value of given device.
   * 
   * @param device CBusAddress that triggers the update
   * @param sensor Sensor to update
   */
  private void updateSensor(CBusAddress device, Sensor sensor) 
  { 
     log.debug("Updating CBUS sensor");
     
     if (sensor instanceof SwitchSensor) 
     {
        sensor.update((device.isOn()) ? "on" : "off");
     } 
     else if (sensor instanceof RangeSensor) 
     {
         sensor.update(device.getValue(true, false));
     } 
     else if (sensor instanceof LevelSensor)
     {
        sensor.update(device.getValue(false, false));
     } 
     else 
     {
        log.warn("Query CBus status for incompatible sensor type (" + sensor + ")");
     }
  }
   
   
}
