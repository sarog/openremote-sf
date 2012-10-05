package org.openremote.controller.protocol.domintell.model;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.domintell.DomintellAddress;
import org.openremote.controller.protocol.domintell.DomintellCommandBuilder;
import org.openremote.controller.protocol.domintell.DomintellGateway;

public class InputModule extends DomintellModule implements Input {

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   private boolean[] states = new boolean[8];

   public InputModule(DomintellGateway gateway, String moduleType, DomintellAddress address) {
      super(gateway, moduleType, address);
   }

   @Override
   public void beginShortPush(Integer input) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(input) + "%P1");
   }

   @Override
   public void endShortPush(Integer input) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(input) + "%P2");
   }

   @Override
   public void beginLongPush(Integer input) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(input) + "%P3");
   }

   @Override
   public void endLongPush(Integer input) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(input) + "%P4");
   }

   @Override
   public void queryState(Integer input) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(input) + "%S");
   }

   // Feedback method from Domintell system ---------------------------------------------------------

   @Override
   public void processUpdate(String info) {
     try {
        // I00
        int value = Integer.parseInt(info.substring(1), 16);
        int bitmask = 1;
        for (int i = 0; i < 8; i++) {
           states[i] = (value & bitmask) == bitmask;
           bitmask = bitmask<<1;
        }
     } catch (NumberFormatException e) {
       // Not understood as an input feedback, do not update ourself
       log.warn("Invalid feedback received " + info, e);
     }
       
     super.processUpdate(info);
   }

   public boolean getState(int output) {
      return states[output - 1];
   }
}
