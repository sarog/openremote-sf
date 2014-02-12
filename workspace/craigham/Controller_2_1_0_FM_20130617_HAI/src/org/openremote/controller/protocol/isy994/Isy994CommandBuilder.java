package org.openremote.controller.protocol.isy994;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.protocol.isy994.model.ISYCommandBuilder;
import org.openremote.controller.utils.Logger;

public class Isy994CommandBuilder implements CommandBuilder {

   public final static String ISY99_XMLPROPERTY_ADDRESS = "address";

   public final static String ISY99_XMLPROPERTY_COMMAND = "command";

   /**
    * Implicit name property for all commands that were introduced in Designer 2.13.x and later. This property should be
    * eventually provided by the API, at which point this constant can be replaced.
    */
   public final static String COMMAND_XMLPROPERTY_NAME = "name";
   private static Logger logger = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ISY994");

   private String mUrl;
   private String mUserString;
   private String mPassword;
   private IsyConnectionClient mClient = null;

   public Isy994CommandBuilder(String url, String userName, String password) {
      // TODO get rid of hard coding
      // create a thread which will maintain a connection to device
      logger.severe("starting ISY994 CommandBuilder");
      mUrl = url;
      mUserString = userName;
      mPassword = password;

      logger.info("Createing new client");
      mClient = new IsyConnectionClient();
      mClient.start();
      logger.severe("starting ISY994 CommandBuilder");
   }

   @Override
   public Command build(Element element) {
      // create the Command object

      @SuppressWarnings("unchecked")
      List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

      String address = null;
      String command = null;
      String commandName = null; // Currently unused but useful for logging [JPL]

      for (Element el : propertyElements) {
         String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
         String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

         if (ISY99_XMLPROPERTY_ADDRESS.equalsIgnoreCase(propertyName)) {
            address = propertyValue;
         } else if (ISY99_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName)) {
            command = propertyValue;
         }

         else if (COMMAND_XMLPROPERTY_NAME.equalsIgnoreCase(propertyName)) {
            commandName = propertyValue;
         }

         else {
            logger.warn("Unknown ISY-99 property '<" + XML_ELEMENT_PROPERTY + " " + XML_ATTRIBUTENAME_NAME + " = \""
                  + propertyName + "\" " + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
         }
      }
      String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      InsteonDeviceAddress insteonAddress = new InsteonDeviceAddress(address);
      return ISYCommandBuilder.createCommand(mClient, insteonAddress, command, paramValue);
      
   }

}
