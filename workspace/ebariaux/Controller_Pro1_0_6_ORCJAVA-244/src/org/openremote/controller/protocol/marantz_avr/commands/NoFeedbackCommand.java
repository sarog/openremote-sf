package org.openremote.controller.protocol.marantz_avr.commands;

import java.util.HashMap;
import java.util.Map;

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

public class NoFeedbackCommand extends MarantzAVRCommand implements ExecutableCommand, EventListener {

   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static NoFeedbackCommand createCommand(CommandConfig commandConfig, String name, MarantzAVRGateway gateway, String parameter) {
      // Check for mandatory attributes
      if (commandConfig == null) {
         throw new NoSuchCommandException("No configuration provided for " + name + " command.");
      }

      // parameter is optional

      return new NoFeedbackCommand(commandConfig, name, gateway, parameter);
    }

   public NoFeedbackCommand(CommandConfig commandConfig, String name, MarantzAVRGateway gateway, String parameter) {
      super(name, gateway);
      this.commandConfig = commandConfig;
      this.parameter = parameter;
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
      // Parameter is not mandatory, if no mapping is provided, just pass value as is
      String parameterValue = "";
      if (parameter != null) {
         if (commandConfig.getParameter(parameter) != null) {
            parameterValue = commandConfig.getParameter(parameter);
         }
      }
     gateway.sendCommand(commandConfig.getValue(), parameterValue);
   }

   // Implements EventListener -------------------------------------------------------------------

   @Override
   public void setSensor(Sensor sensor) {
      throw new NoSuchCommandException("This command can not be associated with a Sensor");
   }
   
   @Override
   public void stop(Sensor sensor) {
      // Don't do anything, no sensor should be registered
   }
   
   @Override
   protected void updateWithResponse(MarantzResponse response) {
      // Don't do anything, no sensor should be registered
   }
   
}