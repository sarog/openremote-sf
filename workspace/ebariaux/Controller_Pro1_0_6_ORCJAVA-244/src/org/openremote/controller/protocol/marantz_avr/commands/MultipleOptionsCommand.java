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

public class MultipleOptionsCommand extends MarantzAVRCommand implements ExecutableCommand, EventListener {

   private final static Map<String, CommandConfig> knownCommands = new HashMap<String, CommandConfig>();

   static {
      CommandConfig cfg = new CommandConfig("SI");
      cfg.addParameter("PHONO", "PHONO");
      cfg.addParameter("CD", "CD");
      cfg.addParameter("DVD", "DVD");
      cfg.addParameter("BD", "BD");
      cfg.addParameter("TV", "TV");
      cfg.addParameter("SAT/CBL", "SAT/CBL");
      cfg.addParameter("VCR", "VCR");
      cfg.addParameter("GAME", "GAME");
      cfg.addParameter("V.AUX", "V.AUX");
      cfg.addParameter("AUX1", "AUX1");
      cfg.addParameter("AUX2", "AUX2");
      cfg.addParameter("STATUS", "?");
      knownCommands.put("INPUT", cfg);
      cfg = new CommandConfig("MS");
      cfg.addParameter("MOVIE", "MOVIE");
      cfg.addParameter("MUSIC", "MUSIC");
      cfg.addParameter("GAME", "GAME");
      cfg.addParameter("DIRECT", "DIRECT");
      cfg.addParameter("PURE DIRECT", "PURE DIRECT");
      cfg.addParameter("STEREO", "STEREO");
      cfg.addParameter("AUTO", "AUTO");
      cfg.addParameter("NEURAL", "NEURAL");
      cfg.addParameter("STANDARD", "STANDARD");
      cfg.addParameter("DOLBY", "DOLBY");
      cfg.addParameter("DTS", "DTS");
      cfg.addParameter("MCH STEREO", "MCH STEREO");
      cfg.addParameter("MATRIX", "MATRIX");
      cfg.addParameter("VIRTUAL", "VIRTUAL");
      cfg.addParameter("LEFT", "LEFT");
      cfg.addParameter("RIGHT", "RIGHT");
      cfg.addParameter("STATUS", "?");
      knownCommands.put("SURROUND_MODE", cfg);
   }
   
   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static MultipleOptionsCommand createCommand(String name, MarantzAVRGateway gateway, String parameter) {
      // Check for mandatory attributes
      if (parameter == null) {
         throw new NoSuchCommandException("A parameter is always required for " + name + " command.");
      }

      return new MultipleOptionsCommand(name, gateway, parameter);
    }

   public MultipleOptionsCommand(String name, MarantzAVRGateway gateway, String parameter) {
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
      if (cfg.getParameter(parameter) == null) {
         throw new NoSuchCommandException("Invalid parameter (" + parameter + ") for command " + name);
      }
     gateway.sendCommand(cfg.getCommand(), cfg.getParameter(parameter));
   }

   // Implements EventListener -------------------------------------------------------------------

   @Override
   public void setSensor(Sensor sensor) {
       if (sensors.isEmpty()) {
          
          // First sensor registered, we also need to register ourself with the gateway
          CommandConfig cfg = knownCommands.get(name);
          gateway.registerCommand(cfg.getCommand(), this);
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
         CommandConfig cfg = knownCommands.get(name);
         gateway.unregisterCommand(cfg.getCommand(), this);
      }
   }
   
   @Override
   protected void updateWithResponse(MarantzResponse response)
   {
      System.out.println("updateWithResponse >" + response.command + "<->" + response.parameter + "<");
      CommandConfig cfg = knownCommands.get(name);
      System.out.println("Lookup " + cfg.lookupResponseParam(response.parameter));
      
      updateSensorsWithValue(cfg.lookupResponseParam(response.parameter));
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