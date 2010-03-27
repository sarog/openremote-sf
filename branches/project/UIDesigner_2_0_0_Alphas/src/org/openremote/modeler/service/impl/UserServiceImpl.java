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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.XmlParser;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * The service implementation for UserService.
 * 
 * @author Dan 2009-7-14
 */
public class UserServiceImpl extends BaseAbstractService<User> implements UserService {
   
   
   private static Logger log = Logger.getLogger(UserServiceImpl.class);
   
   private JavaMailSenderImpl mailSender;
   
   private VelocityEngine velocityEngine;
   
   private Configuration configuration;
   
   /**
    * {@inheritDoc}
    */
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
    * {@inheritDoc}
    */
   public User getUserById(long id) {
      return genericDAO.getById(User.class, id);
   }
   
   /**
    * {@inheritDoc}
    */
    public Account getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return genericDAO.getByNonIdField(User.class, "username", username).getAccount();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean createUserAccount(String username, String password, String email, String roleStr) {
      if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)
            || StringUtils.isEmpty(roleStr)) {
         return false;
      }
      User user = new User();
      user.setUsername(username);
      user.setRawPassword(password);
      user.setPassword(new Md5PasswordEncoder().encodePassword(password, username));
      user.setEmail(email);
      if (isUsernameAvailable(username)) {
         List<Role> allRoles = genericDAO.loadAll(Role.class);
         for (Role r : allRoles) {
            if (r.getName().equals(Role.ROLE_DESIGNER) && roleStr.indexOf("role_ud") != -1) {
               user.addRole(r);
            } else if (r.getName().equals(Role.ROLE_MODELER) && roleStr.indexOf("role_bm") != -1) {
               user.addRole(r);
            }
         }
         saveUser(user);
         return sendRegisterActivationEmail(user);
      } else {
         return false;
      }
   }

    /**
    * {@inheritDoc}
    */
    public void saveUser(User user) {
        genericDAO.save(user);
    }
    /**
     * {@inheritDoc}
     */
    public void updateUser(User user) {
       genericDAO.update(user);
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
    
    /**
     * {@inheritDoc}
     */
    public boolean sendRegisterActivationEmail(final User user) {
       if (user == null || user.getOid() == 0 || StringUtils.isEmpty(user.getEmail())
            || StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
         return false;
       }
       
       MimeMessagePreparator preparator = new MimeMessagePreparator() {
          @SuppressWarnings("unchecked")
          public void prepare(MimeMessage mimeMessage) throws Exception {
             MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
             message.setSubject("OpenRemote Boss 2.0 Account Registration Confirmation");
             message.setTo(user.getEmail());
             message.setFrom(mailSender.getUsername());
             Map model = new HashMap();
             model.put("user", user);
             model.put("webapp", configuration.getWebappServerRoot());
             model.put("aid", new Md5PasswordEncoder().encodePassword(user.getUsername(), user.getPassword()));
             String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                  Constants.REGISTRATION_ACTIVATION_EMAIL_VM_NAME, "UTF-8", model);
             message.setText(text, true);
          }
       };
       try {
          this.mailSender.send(preparator);
          log.info("Sent 'Modeler Account Registration Confirmation' email to " + user.getEmail());
          return true;
       } catch (MailException e) {
          log.error("Can't send 'Modeler Account Registration Confirmation' email", e);
          return false;
       }
   }

    /**
    * {@inheritDoc}
    */
   @Override
   public boolean activateUser(String userOid, String aid) {
      long id = 0;
      try {
         id = Long.valueOf(userOid);
      } catch (NumberFormatException e) {
         return false;
      }
      User user = getUserById(id);
      if (user != null && aid != null) {
         if (new Md5PasswordEncoder().encodePassword(user.getUsername(), user.getPassword()).equals(aid)) {
            user.setValid(true);
            updateUser(user);
            setDefaultConfigsForAccount(user.getAccount());
            return true;
         }
      }
      return false;
   }
   
   public boolean isUsernameAvailable(String username) {
      return genericDAO.getByNonIdField(User.class, "username", username) == null;
   }

   public void setMailSender(JavaMailSenderImpl mailSender) {
      this.mailSender = mailSender;
   }

   public void setVelocityEngine(VelocityEngine velocityEngine) {
      this.velocityEngine = velocityEngine;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }
   
}
