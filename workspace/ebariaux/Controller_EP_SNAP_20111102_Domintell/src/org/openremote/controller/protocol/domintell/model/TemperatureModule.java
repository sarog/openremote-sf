package org.openremote.controller.protocol.domintell.model;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.domintell.DomintellAddress;
import org.openremote.controller.protocol.domintell.DomintellCommandBuilder;
import org.openremote.controller.protocol.domintell.DomintellGateway;

public class TemperatureModule extends DomintellModule implements Temperature {

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   private float currentTemperature;
   
   public TemperatureModule(DomintellGateway gateway, String moduleType, DomintellAddress address) {
      super(gateway, moduleType, address);
   }

   @Override
   public void setSetPoint(Float setPoint) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void queryState() {
      gateway.sendCommand(moduleType + address + "%S");
   }

   // Feedback method from HomeWorksDevice ---------------------------------------------------------

   @Override
   public void processUpdate(String info) {
     try {
        // T 0.0 18.0 AUTO 18.0
        currentTemperature = Float.parseFloat(info.substring(1,5).trim());
        
        log.info("Current temperature read as >" + currentTemperature + "<");
        
        /*
        int value = Integer.parseInt(info.substring(1), 16);
        int bitmask = 1;
        for (int i = 0; i < 8; i++) {
           states[i] = (value & bitmask) == bitmask;
           bitmask = bitmask<<1;
        }
        */
     } catch (NumberFormatException e) {
       // Not understood as a scene, do not update ourself
       log.warn("Invalid feedback received " + info, e);
     }
       
     super.processUpdate(info);
   }

   public float getCurrentTemperature() {
      return currentTemperature;
   }

}
