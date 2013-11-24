package org.openremote.controller.protocol.domintell.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openremote.controller.protocol.domintell.DomintellAddress;
import org.openremote.controller.protocol.domintell.DomintellCommandBuilder;
import org.openremote.controller.protocol.domintell.DomintellGateway;

public class DimmerModule extends DomintellModule implements Dimmer {

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   private int[] levels = new int[8];
   
   public DimmerModule(DomintellGateway gateway, String moduleType, DomintellAddress address) {
      super(gateway, moduleType, address);
   }

   @Override
   public void on(Integer output) {
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

   @Override
   public void setLevel(Integer output, int level) {
      gateway.sendCommand(moduleType + address + "-" + Integer.toString(output) + "%D" + StringUtils.right("00" + Integer.toString(level) , 2));
   }
   
   // Feedback method from HomeWorksDevice ---------------------------------------------------------

   @Override
   public void processUpdate(String info) {
     try {
        // D 064 0 0 0 0 0 0
        for (int i = 0; i < 8; i++) {
           levels[i] = Integer.parseInt(info.substring(1 + i * 2, 3 + i *2).trim(), 16);
        }
     } catch (NumberFormatException e) {
       // Not understood as a scene, do not update ourself
       log.warn("Invalid feedback received " + info, e);
     }
       
     super.processUpdate(info);
   }

   public int getLevel(int output) {
      return levels[output - 1];
   }
}
