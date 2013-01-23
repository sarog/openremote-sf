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

   private final static Map<String, CommandConfig> knownCommands = new HashMap<String, CommandConfig>();

   static {
      knownCommands.put("UP", new CommandConfig("MNCUP"));
      knownCommands.put("DOWN", new CommandConfig("MNCDN"));
      knownCommands.put("LEFT", new CommandConfig("MNCLT"));
      knownCommands.put("RIGHT", new CommandConfig("MNCRT"));
      knownCommands.put("ENTER", new CommandConfig("MNENT"));
      knownCommands.put("RETURN", new CommandConfig("MNRTN"));
   }
   
   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static NoFeedbackCommand createCommand(String name, MarantzAVRGateway gateway, String parameter) {
      // parameter is optional

      return new NoFeedbackCommand(name, gateway, parameter);
    }

   public NoFeedbackCommand(String name, MarantzAVRGateway gateway, String parameter) {
      super(name, gateway);
      this.parameter = parameter;
   }

   // Private Instance Fields ----------------------------------------------------------------------

   /**
    * Parameter used by this command.
    */
   private String parameter;

   // Implements ExecutableCommand -----------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   public void send() {
      CommandConfig cfg = knownCommands.get(name);
      if (cfg == null) {
         throw new NoSuchCommandException("Invalid command " + name);
      }
      // Parameter is not mandatory, if no mapping is provided, just pass value as is
      String parameterValue = "";
      if (parameter != null) {
         if (cfg.getParameter(parameter) != null) {
            parameterValue = cfg.getParameter(parameter);
         }
      }
     gateway.sendCommand(cfg.getCommand(), parameterValue);
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