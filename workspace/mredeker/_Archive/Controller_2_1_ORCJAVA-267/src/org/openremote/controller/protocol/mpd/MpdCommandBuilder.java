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
package org.openremote.controller.protocol.mpd;

import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.List;

import org.bff.javampd.exception.MPDConnectionException;
import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.russound.RussCmdEnum;
import org.openremote.controller.utils.Logger;

/**
 * Builds a MpdCommand which can be used to control MPD through LAN
 * 
 * @author Marcus Redeker
 */
public class MpdCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------

   public final static String MPD_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "MPD";

   private final static String STR_ATTRIBUTE_NAME_IP_ADDRESS = "ipAddress";
   private final static String STR_ATTRIBUTE_NAME_IP_PORT = "port";
   private final static String STR_ATTRIBUTE_NAME_COMMAND = "command";
   

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(MPD_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   
   // cached sessions for reusing session objects for different commands...
   private Hashtable<String, MpdSession> sessions = new Hashtable<String, MpdSession>();
   
   // Constructors ---------------------------------------------------------------------------------
   
   public MpdCommandBuilder() {
   }
   
   
   // Implements CommandBuilder --------------------------------------------------------------------
   
   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public Command build(Element element) {
      logger.debug("Building MPD command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      String command = null;
      String ip = null;
      String port = null;

      // read values from config xml
      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_COMMAND.equals(elementName)) {
            command = elementValue;
            logger.debug("MPD Command: command = " + command);
         } else if (STR_ATTRIBUTE_NAME_IP_ADDRESS.equals(elementName)) {
            ip = elementValue;
            logger.debug("MPD Command: ipAddress = " + ip);
         } else if (STR_ATTRIBUTE_NAME_IP_PORT.equals(elementName)) {
            port = elementValue;
            logger.debug("MPD Command: port = " + port);
         }
      }

      if (null == command || ip == null || port == null) {
         throw new NoSuchCommandException("MPD command must have 'command', 'ip' and 'port' property.");
      }

      String paramValue = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
      try {
         MpdCmdEnum cmd = MpdCmdEnum.valueOf(command.trim().toUpperCase());
         int portInt = Integer.parseInt(port);
         MpdSession session = getSession(ip.toLowerCase(), portInt);
         logger.debug("MPD Command created successfully");
         return new MpdCommand(session, cmd, paramValue);
      } catch (Exception e) {
         throw new NoSuchCommandException("Unable to create MPD Command.", e);
      }
      
   }
   
   private MpdSession getSession(String ip, int port) throws UnknownHostException, MPDConnectionException {
      String key = ip+":"+port;
      if (!sessions.containsKey(key)) {
         logger.debug("Create new MPD session for: " + key);
         sessions.put(key, new MpdSession(ip, port));
      }
      logger.debug("Use existing MPD session for: " + key);
      return sessions.get(key);
   }
   
}
