package org.openremote.controller.protocol.port;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * A factory for <code>PhysicalBus</code> instances.
 */
public class PortFactory {
   /**
    * A common log category name intended to be used across all classes related to Physical Bus implementation.
    */
   public final static String PHYSICALBUS_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "knx.ip";

   private final static Logger log = Logger.getLogger(PHYSICALBUS_LOG_CATEGORY);

   public static Port createPhysicalBus(String clazz) {
      try {
         Class<?> c = Class.forName(clazz);
         if (Port.class.isAssignableFrom(c)) {
            return (Port) c.newInstance();
         } else {
            return null;
         }
      } catch (Exception e) {
         log.error("Could not instantiate PhysicalBus", e);
         return null;
      }
   }
}
