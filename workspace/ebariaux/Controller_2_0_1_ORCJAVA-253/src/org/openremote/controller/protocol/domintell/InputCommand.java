package org.openremote.controller.protocol.domintell;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.domintell.model.DomintellModule;
import org.openremote.controller.protocol.domintell.model.Input;
import org.openremote.controller.protocol.domintell.model.InputModule;

public class InputCommand extends DomintellCommand implements ExecutableCommand, EventListener {

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   public static DomintellCommand createCommand(String name, DomintellGateway gateway, String moduleType, DomintellAddress address, Integer output, Integer level, Float floatValue, TemperatureMode mode) {

      log.info("createCommand (" + name + "," + gateway + "," + moduleType + "," + address + "," + output + ")");

      // Check for mandatory attributes
      if (moduleType == null) {
         throw new NoSuchCommandException("Module type is required for any Domintell command");
      }
      
      if (address == null) {
        throw new NoSuchCommandException("Address is required for any Domintell command");
      }

      // TODO: not output but input, maybe rename to "element number" or "I/O number"
      
      return new InputCommand(name, gateway, moduleType, address, output);
    }

   /**
    * Number of the input this command must actuate.
    */
   private Integer input;
   
   public InputCommand(String name, DomintellGateway gateway, String moduleType, DomintellAddress address, Integer output) {
      super(name, gateway, moduleType, address);
      this.input = output;
   }

   @Override
   public void send() {
      try {
         Input inputModule = (Input) gateway.getDomintellModule(moduleType, address, InputModule.class);
         if ("BEGIN_SHORT_PUSH".equals(name)) {
           inputModule.beginShortPush(input);
         } else if ("END_SHORT_PUSH".equals(name)) {
            inputModule.endShortPush(input);
         } else if ("BEGIN_LONG_PUSH".equals(name)) {
            inputModule.beginLongPush(input);
         } else if ("END_LONG_PUSH".equals(name)) {
            inputModule.endLongPush(input);
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
            InputModule inputModule = (InputModule) gateway.getDomintellModule(moduleType, address, InputModule.class);
            if (inputModule == null) {
              // This should never happen as above command is supposed to create device
              log.warn("Gateway could not create a Input module we're receiving feedback for (" + address + ")");
            }

            // Register ourself with the Relay so it can propagate update when received
            inputModule.addCommand(this);
            addSensor(sensor);

            // Trigger a query to get the initial value
            inputModule.queryState(input);
         } catch (DomintellModuleException e) {
            log.error("Impossible to get module", e);
         }
      } else {
         addSensor(sensor);
      }
   }

   @Override
   public void stop(Sensor sensor) {
      stop(sensor, InputModule.class);
   }

   @Override
   protected void updateSensor(DomintellModule module, Sensor sensor) {
      InputModule inputModule = (InputModule)module;
      if (sensor instanceof SwitchSensor) {
         sensor.update(inputModule.getState(input) ? "on" : "off");
       } else {
          log.warn("Query Input status for incompatible sensor type (" + sensor + ")");
       }
   }

}
