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
package org.openremote.modeler.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;

/**
 * The service for User.
 * 
 * @author Dan 2009-7-14
 */
public class UserServiceImpl extends BaseAbstractService<User> implements UserService {
   
   public void initRoles() {
      boolean hasDesignerRole = false;
      boolean hasModelerRole = false;
      List<Role> allRoles = genericDAO.loadAll(Role.class);
      for (Role r : allRoles) {
         if (r.getName().equals(Role.ROLE_DESIGNER)) {
            hasDesignerRole = true;
         } else if (r.getName().equals(Role.ROLE_MODELER)) {
            hasModelerRole = true;
         }
      }
      if (!hasDesignerRole) {
         Role r = new Role();
         r.setName(Role.ROLE_DESIGNER);
         genericDAO.save(r);
      }
      if (!hasModelerRole) {
         Role r = new Role();
         r.setName(Role.ROLE_MODELER);
         genericDAO.save(r);
      }
      
   }
    /**
     * Gets the current account.
     * 
     * @return the account
     */
    public Account getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return genericDAO.getByNonIdField(User.class, "username", username).getAccount();
    }
    
    /**
     * Creates the account.
     * 
     * @param username the username
     * @param password the password
     * @param roleStr the role string
     * 
     * @return true, if successful
     */
    public boolean createAccount(String username, String password, String roleStr) {
      User user = new User();
      user.setUsername(username);
      user.setPassword(new Md5PasswordEncoder().encodePassword(password, username));
      if (genericDAO.getByNonIdField(User.class, "username", username) == null) {
         List<Role> allRoles = genericDAO.loadAll(Role.class);
         for (Role r : allRoles) {
            if (r.getName().equals(Role.ROLE_DESIGNER) && roleStr.indexOf("role_ud") != -1) {
               user.addRole(r);
            } else if (r.getName().equals(Role.ROLE_MODELER) && roleStr.indexOf("role_bm") != -1) {
               user.addRole(r);
            }
         }
         Account acc = new Account();
         acc.setUser(user);
         user.setAccount(acc);
         genericDAO.save(user);
         setDefaultConfigsForAccount(acc);
         return true;
      } else {
         return false;
      }
   }

    /**
     * {@inheritDoc}
    * @see org.openremote.modeler.client.rpc.UserRPCService#saveUser(org.openremote.modeler.domain.User)
    */
    public void saveUser(User user) {
        genericDAO.save(user);
    }
    
    private void setDefaultConfigsForAccount(Account account){
       Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
       Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
       XmlParser.initControllerConfig(categories, allDefaultConfigs);
       for(ControllerConfig cfg : allDefaultConfigs){
          cfg.setAccount(account);
       }
       genericDAO.getHibernateTemplate().saveOrUpdateAll(allDefaultConfigs);
    }
}
