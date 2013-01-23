package org.openremote.controller.protocol.marantz_avr.commands;

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.marantz_avr.CommandConfig;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRCommand;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRCommandBuilder;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRGateway;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRGateway.MarantzResponse;
import org.openremote.controller.utils.Logger;

public class BooleanCommand extends MarantzAVRCommand implements ExecutableCommand, EventListener {

   private final static Map<String, CommandConfig> knownCommands = new HashMap<String, CommandConfig>();

   static {
      CommandConfig cfg = new CommandConfig("PW");
      cfg.addParameter("ON", "ON");
      cfg.addParameter("OFF", "STANDBY");
      cfg.addParameter("STATUS", "?");
      knownCommands.put("POWER", cfg);
      cfg = new CommandConfig("MU");
      cfg.addParameter("ON", "ON");
      cfg.addParameter("OFF", "OFF");
//      cfg.addParameter("TOGGLE", "TG");
      cfg.addParameter("STATUS", "?");
      knownCommands.put("MUTE", cfg);
      knownCommands.put("INPUT", cfg);
   }
   
   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static BooleanCommand createCommand(String name, MarantzAVRGateway gateway, String parameter) {
      // Check for mandatory attributes
/*      if (parameter == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }*/

      return new BooleanCommand(name, gateway, parameter);
    }

   public BooleanCommand(String name, MarantzAVRGateway gateway, String parameter) {
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
      
      // TODO: add check for valid command / parameter
      
     CommandConfig cfg = knownCommands.get(name);
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
      
      // TODO: have generic, this is just for testing
      if ("OFF".equals(cfg.lookupResponseParam(response.parameter))) {
         updateSensorsWithValue("off");
      } else {
         updateSensorsWithValue("on");
      }
   }
}
 