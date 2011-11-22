package org.openremote.controller.protocol.domintell.model;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.domintell.DomintellAddress;
import org.openremote.controller.protocol.domintell.DomintellCommandBuilder;
import org.openremote.controller.protocol.domintell.DomintellGateway;

public class RelayModule extends DomintellModule implements Output {

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   private boolean[] states = new boolean[8];
   
   public RelayModule(DomintellGateway gateway, String moduleType, DomintellAddress address) {
      super(gateway, moduleType, address);
   }
   
   @Override
   public void on(Integer output) {
      // moduleType is supposed to be BIR
      // address should be in hex, formatted on 6 characters
      
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(output) + "%I");
   }

   @Override
   public void off(Integer output) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(output) + "%O");
   }

   @Override
   public void toggle(Integer output) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(output));
   }

   @Override
   public void queryState(Integer output) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(output) + "%S");
   }
   
   // Feedback method from HomeWorksDevice ---------------------------------------------------------

   @Override
   public void processUpdate(String info) {
     try {
        // O00
        int value = Integer.parseInt(info.substring(1), 16);
        int bitmask = 1;
        for (int i = 0; i < 8; i++) {
           states[i] = (value & bitmask) == bitmask;
           bitmask = bitmask<<1;
        }
     } catch (NumberFormatException e) {
       // Not understood as a scene, do not update ourself
       log.warn("Invalid feedback received " + info, e);
     }
       
     super.processUpdate(info);
   }

   public boolean getState(int output) {
      return states[output - 1];
   }
}
