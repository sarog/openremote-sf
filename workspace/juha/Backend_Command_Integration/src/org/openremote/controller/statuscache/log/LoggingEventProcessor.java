package org.openremote.controller.statuscache.log;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openremote.controller.Constants;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.service.StatusCacheService;
import org.openremote.controller.statuscache.EventProcessor;

public class LoggingEventProcessor extends EventProcessor {

   /** The logger for sensor values */
   private Logger valueLogger = Logger.getLogger(Constants.SENSOR_LOG_CATEGORY);

   private StatusCacheService statusCacheService;

   /**
    * Logs the new value for this sensor if it is different from the previous value
    */
   @Override
   public Event push(Event event) {
      String val = event.getValue() != null ? event.getValue().toString() : "";
      String previousValue = statusCacheService.getStatusBySensorId(event.getSourceID());
      if(!StringUtils.isEmpty(val)
            && !val.equals(previousValue)){
         valueLogger.info(event.getSource()+((char) 0x1E)+val);
      }
      
      return event;
   }

   public void setStatusCacheService(StatusCacheService statusCacheService) {
      this.statusCacheService = statusCacheService;
   }

}
