package org.openremote.controller.protocol.domintell;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.domintell.model.DomintellModule;
import org.openremote.controller.protocol.domintell.model.Output;
import org.openremote.controller.protocol.domintell.model.RelayModule;

public class OutputCommand extends DomintellCommand implements ExecutableCommand, EventListener {
   
   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   public static DomintellCommand createCommand(String name, DomintellGateway gateway, String moduleType, DomintellAddress address, Integer output, Integer level) {

      log.info("createCommand (" + name + "," + gateway + "," + moduleType + "," + address + "," + output + ")");

      // Check for mandatory attributes
      if (moduleType == null) {
         throw new NoSuchCommandException("Module type is required for any Domintell command");
      }
      
      if (address == null) {
        throw new NoSuchCommandException("Address is required for any Domintell command");
      }

      return new OutputCommand(name, gateway, moduleType, address, output);
    }

   // Private Instance Fields
   // ----------------------------------------------------------------------

   /**
    * Number of the output this command must actuate.
    */
   private Integer output;

   public OutputCommand(String name, DomintellGateway gateway, String moduleType, DomintellAddress address, Integer output) {
      super(name, gateway, moduleType, address);
      this.output = output;
   }

   @Override
   public void send() {
      try {
         Output relay = (Output) gateway.getDomintellModule(moduleType, address, RelayModule.class);
         if ("ON".equals(name)) {
           relay.on(output);
         } else if ("OFF".equals(name)) {
           relay.off(output);
         } else if ("TOGGLE".equals(name)) {
           relay.toggle(output);
         }
       } catch (DomintellModuleException e) {
         log.error("Impossible to get module", e);
       }
      
   }   

   @Override
   public void setSensor(Sensor sensor) {
       if (sensors.isEmpty()) {
          // First sensor registered, we also need to register ourself with the device
          try {
             RelayModule relay = (RelayModule) gateway.getDomintellModule(moduleType, address, RelayModule.class);
             if (relay == null) {
               // This should never happen as above command is supposed to create device
               log.warn("Gateway could not create a Relay module we're receiving feedback for (" + address + ")");
             }

             // Register ourself with the Relay so it can propagate update when received
             relay.addCommand(this);
             addSensor(sensor);

             // Trigger a query to get the initial value
             relay.queryState(output);
          } catch (DomintellModuleException e) {
             log.error("Impossible to get module", e);
          }
       } else {
          addSensor(sensor);
       }
   }
   
   @Override
   public void stop(Sensor sensor) {
      removeSensor(sensor);
      if (sensors.isEmpty()) {
         // Last sensor removed, we may unregister ourself from device
         try {
            RelayModule relay = (RelayModule) gateway.getDomintellModule(moduleType, address, RelayModule.class);
            if (relay == null) {
              // This should never happen as above command is supposed to create device
              log.warn("Gateway could not create a Relay module we're receiving feedback for (" + address + ")");
            }

            relay.removeCommand(this);
         } catch (DomintellModuleException e) {
            log.error("Impossible to get module", e);
         }
      }
   }

   @Override
   protected void updateSensor(DomintellModule module, Sensor sensor) {
      RelayModule relay = (RelayModule)module;
      if (sensor instanceof SwitchSensor) {
         sensor.update(relay.getState(output) ? "on" : "off");
       } else {
          log.warn("Query Relay status for incompatible sensor type (" + sensor + ")");
       }
   }
}
