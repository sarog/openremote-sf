package org.openremote.controller.commander;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openremote.irbuilder.domain.IREvent;

public class IRCommander extends Commander {

   private static Logger logger = Logger.getLogger(IRCommander.class.getName());
   
   @Override
   public void execute() {
      IREvent irEvent = (IREvent)getEvent();
      String cmd = "irsend send_once " + irEvent.getName() + " " + irEvent.getCommand();
      try {
         Process pro = Runtime.getRuntime().exec(cmd);
         logger.info(cmd);
         pro.waitFor();
      } catch (InterruptedException e) {
         logger.error(cmd+" was interrupted.",e);
      } catch (IOException e) {
         logger.error(cmd+" failed.",e);
      }
   }
   
}
