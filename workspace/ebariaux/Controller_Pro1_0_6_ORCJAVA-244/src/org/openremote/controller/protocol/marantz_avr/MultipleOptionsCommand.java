package org.openremote.controller.protocol.marantz_avr;

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
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
      knownCommands.put("INPUT", cfg);
   }
   
   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static MultipleOptionsCommand createCommand(String name, MarantzAVRGateway gateway, String parameter) {
      // Check for mandatory attributes
/*      if (parameter == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }*/

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
      
      // TODO: tests for the sensor type ? or is this done in updateSensorsWithValue
      updateSensorsWithValue(cfg.lookupResponseParam(response.parameter));
   }
}