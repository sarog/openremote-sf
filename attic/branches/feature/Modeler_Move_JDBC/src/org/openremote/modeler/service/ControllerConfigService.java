/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.modeler.service;

import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;

/**
 * The Interface ControllerConfigService for operating controller configurations.
 * 
 * @author javen
 */
public interface ControllerConfigService {
   
   public static final String CONTROLLER_CONFIG_XML_FILE = "controller-config-2.0-M7.xml";

   
   /**
    * Get all the configuration item under a category for a account. 
    * @param categoryName The name of category 
    * @param account The current account. 
    * @return all configuration item under a category. 
    */
   Set<ControllerConfig> listAllConfigsByCategoryNameForAccount(String categoryName,Account account);
   
   /**
    * Save all the configuration 
    * We can use this method to create a controller configuration for a user.  
    * @param configs The configurations you want to save. 
    * @return The configuration s you have saved. 
    */
   Set<ControllerConfig> saveAll(Set<ControllerConfig> configs);
   
   /**
    * This method is used to get all missing configurations under a 
    * category. 
    * Sometime there are some new configurations may be added, 
    * but always all the configurations will be saved to user's 
    * account as soon as this user is created. Therefore there some 
    * configurations which are not saved to user if the default 
    * configurations is changed. 
    * @param categoryName
    * @return
    */
   Set<ControllerConfig> listMissedConfigsByCategoryName(String categoryName);
   
   /**
    * This method is used to get all missing controller configurations 
    * without caring about configuration's category.  
    * 
    * Sometime there are some new configurations may be added, 
    * but always all the configurations will be saved to user's 
    * account as soon as this user is created. Therefore there some 
    * configurations which are not saved to user if the default 
    * configurations is changed. 
    * @return
    */
   Set<ControllerConfig> listAllMissingConfigs();
   
   Set<ControllerConfig> listAllexpiredConfigs();
   
   Set<ControllerConfig> listAllConfigsByCategory(String categoryName);
   
   Set<ControllerConfig> listAllConfigs();
   
   Set<ControllerConfig> listAllByAccount(Account account);
   
   Set<ConfigCategory> listAllCategory();
   
   
}
