package org.openremote.controller.protocol.marantz_avr.commands;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.marantz_avr.CommandConfig;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRCommand;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRCommandBuilder;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRGateway;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRGateway.MarantzResponse;
import org.openremote.controller.utils.Logger;

public class MultipleOptionsCommand extends MarantzAVRCommand implements ExecutableCommand, EventListener {

   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static MultipleOptionsCommand createCommand(CommandConfig commandConfig, String name, MarantzAVRGateway gateway, String parameter) {
      // Check for mandatory attributes
      if (commandConfig == null) {
         throw new NoSuchCommandException("No configuration provided for " + name + " command.");
      }
      if (parameter == null) {
         throw new NoSuchCommandException("A parameter is always required for " + name + " command.");
      }

      return new MultipleOptionsCommand(commandConfig, name, gateway, parameter);
    }

   public MultipleOptionsCommand(CommandConfig commandConfig, String name, MarantzAVRGateway gateway, String parameter) {
      super(name, gateway);
      this.parameter = parameter;
      this.commandConfig = commandConfig;
   }

   // Private Instance Fields ----------------------------------------------------------------------

   /**
    * Configuration defining this command.
    */
   private CommandConfig commandConfig;
   
   /**
    * Parameter used by this command.
    */
   private String parameter;

   // Implements ExecutableCommand -----------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   public void send() {
      if (commandConfig.getParameter(parameter) == null) {
         throw new NoSuchCommandException("Invalid parameter (" + parameter + ") for command " + name);
      }
     gateway.sendCommand(commandConfig.getValue(), commandConfig.getParameter(parameter));
   }

   // Implements EventListener -------------------------------------------------------------------

   @Override
   public void setSensor(Sensor sensor) {
       if (sensors.isEmpty()) {
          
          // First sensor registered, we also need to register ourself with the gateway
          gateway.registerCommand(commandConfig.getValue(), this);
          addSensor(sensor);

          // Trigger a query to get the initial value
          send();
       } else {
          addSensor(sensor);
       }
   }
   
   @Override
   public void stop(Sensor sensor) {
      removeSensor(sensor);
      if (sensors.isEmpty()) {
         // Last sensor removed, we may unregister ourself from gateway
         gateway.unregisterCommand(commandConfig.getValue(), this);
      }
   }
   
   @Override
   protected void updateWithResponse(MarantzResponse response)
   {
      updateSensorsWithValue(commandConfig.lookupResponseParam(response.parameter));
   }
   
   @Override
   protected void updateSensorWithValue(Sensor sensor, Object value) {
      if (sensor instanceof SwitchSensor) {
         log.warn("Switch sensor type is not supported by this command (sensor: " + sensor + ")");
      } else if (sensor instanceof StateSensor) { // Note this includes SwitchSensor
         sensor.update((String)value);
      } else {
         log.warn("Query value for incompatible sensor type (" + sensor + ")");
      }
   }

}