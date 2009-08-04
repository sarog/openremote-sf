/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

package org.openremote.modeler.server;

import org.apache.log4j.Logger;
import org.openremote.modeler.client.rpc.ConfigurationRPCService;

import java.io.IOException;
import java.util.Properties;

/**
 * Gets the system configuration 
 */
public class ConfigurationController extends BaseGWTSpringController implements ConfigurationRPCService {
   
   /** The logger. */
   private static Logger logger = Logger.getLogger(ConfigurationController.class);

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.ConfigurationRPCService#beehiveRESTUrl()
    */
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
