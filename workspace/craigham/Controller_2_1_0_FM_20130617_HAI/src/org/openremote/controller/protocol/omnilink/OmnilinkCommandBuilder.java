package org.openremote.controller.protocol.omnilink;

import java.util.List;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;
//import org.openremote.devicediscovery.domain.DiscoveredDeviceAttrDTO;
//import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;

public class OmnilinkCommandBuilder implements CommandBuilder{

   // Constants ------------------------------------------------------------------------------------
   public final static String OMNILINK_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "Omnilink";

   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
   private final static String STR_ATTRIBUTE_NAME_PARAMETER1 = "parameter1";
   private final static String STR_ATTRIBUTE_NAME_PARAMETER2 = "parameter2";
   private final static int WAIT_FOR_LOAD_TIME = 1000 * 60;

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(OMNILINK_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------

   private OmnilinkClient client;

   // Constructors ---------------------------------------------------------------------------------
   public OmnilinkCommandBuilder(String address, int port, String key1, String key2) {
      
      try {
         logger.info("Createing new client");
         client = new OmnilinkClient(address, port, key1, key2);
         
         int maxTries = 10;
         int tries = 0;
         while(client.isRunning()) {
            logger.info("Checking for connections");
            if(!client.isConnected() && tries < maxTries )
               Thread.sleep(1000);
            else
               break;
         }
         logger.info("connected ? " + client.isConnected());
         
      } catch (Exception e) {
         logger.error("Could not start omnilink client: ", e);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      if ((this.client == null) || (!this.client.isRunning())) {
         throw new NoSuchCommandException("omnilink client is not started");
      }
         
      logger.debug("Building omnilink command");
      
      OmniLinkCmd command = null;
      
      int parameter1 = 0;
      int parameter2 = 0;
      
      String paramString1 = null;
      String paramString2 = null;
      
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      
      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
   
         if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName)) {
            command = OmniLinkCmd.getCommand(elementValue);
            
            if(command == null)
               throw new NoSuchCommandException("Command " + elementValue + " not found ");
            
            logger.debug("omnilink command: command = " + elementValue);
            
         }
         
         if (STR_ATTRIBUTE_NAME_PARAMETER1.equals(elementName)) {
            paramString1 = elementValue;
         }
         
         if (STR_ATTRIBUTE_NAME_PARAMETER2.equals(elementName)) {
            paramString2 = elementValue;
         }
      }
      
      
      if (command == null ) {
         throw new NoSuchCommandException("Unable to create omnilink command, missing configuration parameter(s)");
      }
      
      if(paramString1 != null) {
         try {
            parameter1 = Integer.parseInt(paramString1);
         } catch (NumberFormatException e) {
            throw new NoSuchCommandException("parameter 1 is not an integer: " + paramString1);
         }
      } else {
         String slider = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
         if (slider != null && !slider.equals("")) {
            try {
               parameter1 = Integer.parseInt(slider);
            } catch (NumberFormatException e) {
               throw new NoSuchCommandException("dynamic slider is not an integer: " + slider);
            }
         }
         
      }
      
      if(paramString2 != null) {
         try {
            parameter2 = Integer.parseInt(paramString2);
         } catch (NumberFormatException e) {
            throw new NoSuchCommandException("parameter 2 is not an integer: " + paramString2);
         }
      }
      
      return new OmniLinkCommand(command, parameter1, parameter2, client);
   }
   
//   private synchronized void waitForLoadedDevice() {
//      while()
//      Collection<VeraDevice> devices = client.startVeraClient().values();
//      if ((devices != null) && (!devices.isEmpty())) {
//         List<DiscoveredDeviceDTO> list = prepareDiscoveredDevices(devices);
//         if (!list.isEmpty() && (deployer != null)) {
//            deployer.announceDiscoveredDevices(list);
//         }
//      }
//   }
//
//   private List<DiscoveredDeviceDTO> prepareDiscoveredDevices(Collection<VeraDevice> list) {
//      List<DiscoveredDeviceDTO> result = new ArrayList<DiscoveredDeviceDTO>();
//      for (VeraDevice device : list) {
//         DiscoveredDeviceDTO a = new DiscoveredDeviceDTO();
//         a.setModel("N/A");
//         a.setName(device.getName());
//         a.setProtocol("vera");
//         a.setType(device.getCategory().name());
//         a.setUsed(false);
//         List<DiscoveredDeviceAttrDTO> deviceAttrs = new ArrayList<DiscoveredDeviceAttrDTO>();
//         DiscoveredDeviceAttrDTO b = new DiscoveredDeviceAttrDTO();
//         b.setName("id");
//         b.setValue("" + device.getId());
//         deviceAttrs.add(b);
//         a.setDeviceAttrs(deviceAttrs);
//         result.add(a);
//      }
//      return result;
//   }
}
