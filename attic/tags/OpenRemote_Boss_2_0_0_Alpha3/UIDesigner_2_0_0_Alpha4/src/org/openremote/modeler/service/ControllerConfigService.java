package org.openremote.modeler.service;

import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.ConfigCategory;

public interface ControllerConfigService {
   
   public static final String CONTROLLER_CONFIG_XML_FILE = "controller-config-2.0-M7.xml";

   
   /**
    * Get all the configuration item under a category for a account. 
    * @param categoryName The name of category 
    * @param account The current account. 
    * @return all configuration item under a category. 
    */
   Set<ControllerConfig> listAllConfigByCategoryNameForAccouont(String categoryName,Account account);
   
   /**
    * Update a configuration . 
    * @param config The configuration you want to update
    * @return A configuration after being updated. 
    */
   ControllerConfig update(ControllerConfig config);
   
   /**
    * Save all the configuration 
    * We can use this method to create a controller configuration for a user.  
    * @param configs The configurations you want to save. 
    * @return The configuration s you have saved. 
    */
   Set<ControllerConfig> saveAll(Set<ControllerConfig> configs);
   
   Set<ControllerConfig> listAllConfigByCategoryForCurrentAccount(String categoryName);
   
   Set<ControllerConfig> listAllForCurrentAccount();
   
   Set<ControllerConfig> listAllByAccount(Account account);
   
   Set<ConfigCategory> listAllCategory();
   
}
