package org.openremote.controller.protocol.ping;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.utils.Logger;

/**
 * Builds a PingCommand which can be used to detect if a device is available.
 * 
 * @author Simon Vincent
 */
public class PingCommandBuilder implements CommandBuilder {
   
   private final static Logger logger = Logger.getLogger(PingCommandBuilder.class.getName());

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      PingCommand pingCommand = new PingCommand();
      
      logger.debug("Building ping command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String ipAddr = "";

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if ("ipAddress".equals(elementName)) {
            ipAddr = elementValue;
            logger.debug("Ping Command: ipAddress= " + ipAddr);
         }
      }

      if ("".equals(ipAddr.trim())) {
         throw new CommandBuildException("Ping command must have a ipAddress property.");
      }else
      {
         pingCommand.setIPAddr(ipAddr);
      }

      logger.debug("Ping Command created successfully");
      
      return pingCommand;
   }

}
