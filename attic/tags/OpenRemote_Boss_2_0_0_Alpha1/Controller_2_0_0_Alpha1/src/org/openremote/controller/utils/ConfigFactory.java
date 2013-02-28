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
package org.openremote.controller.utils;

import org.openremote.controller.Configuration;
import org.openremote.controller.RoundRobinConfig;
import org.openremote.controller.spring.SpringContext;

/**
 * A factory for creating Configuration objects.
 * 
 * @author Dan 2009-6-9
 */
public class ConfigFactory {

   /**
    * Gets the config.
    * 
    * @return the config
    */
   public static Configuration getConfig(){
      return (Configuration) SpringContext.getInstance().getBean("configuration");
   }
   
   public static RoundRobinConfig getRoundRobinConfig() {
      return (RoundRobinConfig) SpringContext.getInstance().getBean("roundRobinConfig");
   }
   
}
