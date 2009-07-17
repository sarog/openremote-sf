package org.openremote.modeler.server;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.rpc.ConfigurationService;

public class ConfigurationController extends BaseGWTSpringController implements ConfigurationService{
   
   private static Logger logger = Logger.getLogger(ConfigurationController.class);

   public String beehiveRESTUrl() {
      Properties properties = new Properties();
      try {
         properties.load(getClass().getResourceAsStream("/config.properties"));
      } catch (IOException e) {
         logger.error("Read config error",e);
      }
       
      return properties.getProperty("beehive.REST.Url");
   }

}
