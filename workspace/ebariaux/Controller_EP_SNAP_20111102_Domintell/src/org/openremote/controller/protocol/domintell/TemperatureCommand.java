package org.openremote.controller.protocol.domintell;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.domintell.model.DomintellModule;
import org.openremote.controller.protocol.domintell.model.Temperature;

public class TemperatureCommand extends DomintellCommand implements ExecutableCommand, EventListener {

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

      if ("FADE".equalsIgnoreCase(name) && level == null) {
         throw new NoSuchCommandException("Level is required for a dimmer Fade command");
       }

      return new DimmerCommand(name, gateway, moduleType, address, output, level);
    }
   
   // Private Instance Fields
   // ----------------------------------------------------------------------

   /**
    * Set point.
    */

   private Float setPoint;

   public TemperatureCommand(String name, DomintellGateway gateway, String moduleType, DomintellAddress address, Float floatValue) {
      super(name, gateway, moduleType, address);
      this.setPoint = floatValue;
   }


   @Override
   protected void updateSensor(DomintellModule module, Sensor sensor) {
      // TODO Auto-generated method stub

   }

   @Override
   public void setSensor(Sensor sensor) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void stop(Sensor sensor) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void send() {
      // TODO Auto-generated method stub
      
   }

}
