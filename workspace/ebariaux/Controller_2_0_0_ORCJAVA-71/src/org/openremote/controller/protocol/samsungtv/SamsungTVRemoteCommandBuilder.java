/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.controller.protocol.samsungtv;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * Builds a SamsungTVRemoteCommand which can be used to control Samsung TV's through LAN
 * 
 * @author Marcus Redeker
 */
public class SamsungTVRemoteCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String SAMSUNG_TV_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "SamsungTV";

   private final static String STR_ATTRIBUTE_NAME_KEY_CODE = "keyCode";
   
   private final static int SAMSUNG_TV_REMOTE_CONTROL_PORT = 55000;
   private final static String SAMSUNG_TV_REMOTE_APPLICATION_NAME = "OpenRemote";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(SAMSUNG_TV_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   
   private SamsungTVSession session;
   private String samsungTVIp;
   
   // Constructors ---------------------------------------------------------------------------------

   public SamsungTVRemoteCommandBuilder(String samsungTVIp) {
      this.samsungTVIp = samsungTVIp;
   }
   
   
   // Implements CommandBuilder --------------------------------------------------------------------
   
   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      if (session == null) {
         session = new SamsungTVSession(samsungTVIp, SAMSUNG_TV_REMOTE_CONTROL_PORT, SAMSUNG_TV_REMOTE_APPLICATION_NAME);      
      }
      logger.debug("Building Samsung TV command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());

      String keyCode = null;

      // read values from config xml

      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_KEY_CODE.equals(elementName)) {
            keyCode = elementValue;
            logger.debug("Samsung TV Command: keyCode = " + keyCode);
         }
      }

      if (null == keyCode) {
         throw new NoSuchCommandException("Samsung TV command must have a 'keyCode' property.");
      }

      try {
         Key key = Key.valueOf(keyCode);
         logger.debug("Samsung TV Command created successfully");
         return new SamsungTVCommand(session, key);

      } catch (Exception e) {
         throw new NoSuchCommandException("Invlid keyCode: " + keyCode);
      }
      
   }
   
}
