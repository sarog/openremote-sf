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
package org.openremote.modeler.service.impl;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.UserInvitationException;
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

import com.atlassian.crowd.integration.authentication.PasswordCredential;
import com.atlassian.crowd.integration.exception.InvalidAuthorizationTokenException;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.model.UserConstants;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClient;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClientFactory;
import com.atlassian.crowd.integration.soap.SOAPAttribute;
import com.atlassian.crowd.integration.soap.SOAPGroup;
import com.atlassian.crowd.integration.soap.SOAPPrincipal;
import com.atlassian.crowd.integration.soap.SearchRestriction;

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
      boolean hasAdminRole = false;
      
      SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
      try {
         String[] groupNames = crowdClient.findAllGroupNames();
         for (String groupName : groupNames) {
            if (groupName.equals(Constants.DESIGNER)) {
               hasDesignerRole = true;
            } else if (groupName.equals(Constants.MODELER)) {
               hasModelerRole = true;
            } else if (groupName.equals(Constants.ADMIN)) {
               hasAdminRole = true;
            }
         }
         // If there is no group in crowd, add it.
         if (!hasDesignerRole) {
            SOAPGroup group = new SOAPGroup();
            group.setActive(true);
            group.setDescription("openremote designer");
            group.setName(Constants.DESIGNER);
            crowdClient.addGroup(group);
         }
         if (!hasModelerRole) {
            SOAPGroup group = new SOAPGroup();
            group.setActive(true);
            group.setDescription("openremote modeler");
            group.setName(Constants.MODELER);
            crowdClient.addGroup(group);
         }
         if (!hasAdminRole) {
            SOAPGroup group = new SOAPGroup();
            group.setActive(true);
            group.setDescription("openremote admin");
            group.setName(Constants.ADMIN);
            crowdClient.addGroup(group);
         }
      } catch (Exception e) {
         log.error("Can't init role in crowd", e);
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
    public boolean createUserAccount(String username, String password, String email) {
      if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
         return false;
      }
      if (isUsernameAvailable(username)) {
         SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
         
         /**
          * Create a new principal, its name is equals the username.
          */
         SOAPPrincipal principal = new SOAPPrincipal();
         principal.setActive(false);
         principal.setName(username);
         
         /**
          * Create principal attributes.
          * The email, firstname and lastname are required.
          */
         SOAPAttribute[] soapAttributes = new SOAPAttribute[4];
         soapAttributes[0] = buildAttribute(UserConstants.EMAIL, email);
         soapAttributes[1] = buildAttribute(UserConstants.FIRSTNAME, username);
         soapAttributes[2] = buildAttribute(UserConstants.LASTNAME, username);
         soapAttributes[3] = buildAttribute(UserConstants.DISPLAYNAME, username);
         
         principal.setAttributes(soapAttributes);
         
         /**
          * Create the principal's password credentials, it represent the password.
          */
         PasswordCredential credentials = new PasswordCredential(password);
         try {
            principal = crowdClient.addPrincipal(principal, credentials);
            crowdClient.addPrincipalToGroup(username, Constants.ADMIN);
         } catch (Exception e) {
            log.error("Can't create user " + username + " with role 'ADMIN'.", e);
            return false;
         }
         User user = new User();
         user.setUsername(username);
         saveUser(user);
         return sendRegisterActivationEmail(user, email, password);
      } else {
         return false;
      }
   }

    /**
    * {@inheritDoc}
    */
    public void saveUser(User user) {
        genericDAO.save(user.getAccount());
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
    public boolean sendRegisterActivationEmail(final User user, final String email, final String password) {
       if (user == null || user.getOid() == 0 || StringUtils.isEmpty(email)
             || StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(password)) {
          return false;
       }
       
       MimeMessagePreparator preparator = new MimeMessagePreparator() {
          @SuppressWarnings("unchecked")
          public void prepare(MimeMessage mimeMessage) throws Exception {
             MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
             message.setSubject("OpenRemote Boss 2.0 Account Registration Confirmation");
             message.setTo(email);
             message.setFrom(mailSender.getUsername());
             Map model = new HashMap();
             model.put("user", user);
             model.put("registerTime", new Timestamp(System.currentTimeMillis()).toString().replaceAll("\\.\\d+", ""));
             String rpwd = password;
             StringBuffer maskPwd = new StringBuffer();
             maskPwd.append(rpwd.substring(0, 1));
             for (int i = 0; i < rpwd.length() - 2; i++) {
                maskPwd.append("*");
             }
             maskPwd.append(rpwd.substring(rpwd.length() - 1));
             model.put("maskPassword", maskPwd.toString());
             model.put("webapp", configuration.getWebappServerRoot());
             model.put("aid", new Md5PasswordEncoder().encodePassword(user.getUsername(), email));
             String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                   Constants.REGISTRATION_ACTIVATION_EMAIL_VM_NAME, "UTF-8", model);
             message.setText(text, true);
          }
       };
       try {
          this.mailSender.send(preparator);
          log.info("Sent 'Modeler Account Registration Confirmation' email to " + email);
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
         String username = user.getUsername();
         SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
         try {
            SOAPPrincipal principal = crowdClient.findPrincipalByName(username);
            if (new Md5PasswordEncoder().encodePassword(username, principal.getAttribute(UserConstants.EMAIL).getValues()[0]).equals(aid)) {
               SOAPAttribute soapAttribute = new SOAPAttribute();
               soapAttribute.setValues(new String[1]);
               soapAttribute.setName(UserConstants.ACTIVE);
               soapAttribute.getValues()[0] = Boolean.toString(true);
               crowdClient.updatePrincipalAttribute(username, soapAttribute);
               setDefaultConfigsForAccount(user.getAccount());
               return true;
            }
            
         } catch (Exception e) {
            log.error("Can't active user " + username +" in crowd", e);
         }
      }
      return false;
   }
   
   public boolean isUsernameAvailable(String username) {
      SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
      try {
         if (crowdClient.findPrincipalByName(username) == null){
            return true;
         }
      } catch (RemoteException e) {
         e.printStackTrace();
      } catch (InvalidAuthorizationTokenException e) {
         e.printStackTrace();
      } catch (ObjectNotFoundException e) {
         return true;
      }
      return false;
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

   public User getCurrentUser() {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      return genericDAO.getByNonIdField(User.class, "username", username);
   }

   public User inviteUser(String email, String role, User currentUser) {
      User invitee = getPendingUserbyName(email);
      if (invitee == null) {
         invitee = new User(currentUser.getAccount());
         invitee.setUsername(email);
         invitee.setPendingRoleName(role);
         genericDAO.save(invitee);
         if (!sendInvitation(invitee, currentUser)) {
            throw new UserInvitationException("Failed to send invitation.");
         }
         return invitee;
      } else {
         if (!sendInvitation(invitee, currentUser)) {
            throw new UserInvitationException("Failed to send invitation.");
         }
         return null;
      }
   }

   private User getPendingUserbyName(String email) {
      User user = genericDAO.getByNonIdField(User.class, "username", email);
      if (user != null && user.getPendingRoleName() != null) {
         return user;
      }
      return null;
   }
   
   public boolean sendInvitation(final User invitee, final User currentUser) {
       if (invitee == null || invitee.getOid() == 0 || StringUtils.isEmpty(invitee.getUsername())) {
         return false;
       }
       
       MimeMessagePreparator preparator = new MimeMessagePreparator() {
          @SuppressWarnings("unchecked")
          public void prepare(MimeMessage mimeMessage) throws Exception {
             MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
             message.setSubject("Invitation to Share an OpenRemote Boss 2.0 Account");
             message.setTo(invitee.getUsername());
             message.setFrom(mailSender.getUsername());
             Map model = new HashMap();
             model.put("uid", invitee.getOid());
             model.put("role", invitee.getPendingRoleName());
             model.put("cid", currentUser.getOid());
             model.put("host", currentUser.getUsername());
             model.put("webapp", configuration.getWebappServerRoot());
             model.put("aid", new Md5PasswordEncoder().encodePassword(invitee.getUsername(), currentUser.getUsername()));
             String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                  Constants.REGISTRATION_INVITATION_EMAIL_VM_NAME, "UTF-8", model);
             message.setText(text, true);
          }
       };
       try {
          this.mailSender.send(preparator);
          log.info("Sent 'Modeler Account Invitation' email to " + invitee.getEmail());
          return true;
       } catch (MailException e) {
          log.error("Can't send 'Modeler Account Invitation' email", e);
          return false;
       }
   }

   public boolean checkInvitation(String userOid, String hostOid, String aid) {
      long uid = 0;
      long hid = 0;
      try {
         uid = Long.valueOf(userOid);
      } catch (NumberFormatException e) {
         return false;
      }
      try {
         hid = Long.valueOf(hostOid);
      } catch (NumberFormatException e) {
         return false;
      }
      User user = getUserById(uid);
      User hostUser = getUserById(hid);
      if (user != null && hostUser != null && aid != null) {
         if (new Md5PasswordEncoder().encodePassword(user.getUsername(), hostUser.getUsername()).equals(aid)) {
            return true;
         }
      }
      return false;
   }

   public boolean createInviteeAccount(String userOid, String username, String password, String email) {
      if (StringUtils.isEmpty(userOid) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
         return false;
      }
      long id = 0;
      try {
         id = Long.valueOf(userOid);
      } catch (NumberFormatException e) {
         return false;
      }
      if (isUsernameAvailable(username)) {
         SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
         SOAPPrincipal principal = new SOAPPrincipal();
         
         principal.setActive(true);
         principal.setName(username);
         
         SOAPAttribute[] soapAttributes = new SOAPAttribute[4];
         
         soapAttributes[0] = buildAttribute(UserConstants.EMAIL, email);
         soapAttributes[1] = buildAttribute(UserConstants.FIRSTNAME, username);
         soapAttributes[2] = buildAttribute(UserConstants.LASTNAME, username);
         soapAttributes[3] = buildAttribute(UserConstants.DISPLAYNAME, username);
         
         principal.setAttributes(soapAttributes);
         
         // our password
         PasswordCredential credentials = new PasswordCredential(password);
         User user = getUserById(id);
         // have the security server add it
         try {
            principal = crowdClient.addPrincipal(principal, credentials);
            List<String> roles = convertDisplayRoleStringToRoleList(user.getPendingRoleName());
            for (String role : roles) {
               crowdClient.addPrincipalToGroup(username, role);
            }
         } catch (Exception e) {
            log.error("Can't create invited user " + username + ".", e);
            return false;
         }
         
         user.setUsername(username);
         user.setPendingRoleName(null);
         updateUser(user);
         return true;
      } else {
         return false;
      }
   }

   public List<User> getPendingInviteesByAccount(User currentUser) {
      List<User> invitees = new ArrayList<User>();
      List<User> sameAccountUsers = currentUser.getAccount().getUsers();
      sameAccountUsers.remove(currentUser);
      for (User invitee : sameAccountUsers) {
         if(invitee.getPendingRoleName() != null) {
            invitees.add(invitee);
         }
      }
      return invitees;
   }

   public User updateUserRoles(long uid, String roles, boolean isPending) {
      User user = getUserById(uid);
      if (isPending) {
         user.setPendingRoleName(roles);
         updateUser(user);
      } else {
         String username = user.getUsername();
         List<String> newGroups = convertDisplayRoleStringToRoleList(roles);
         SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
         SearchRestriction[] searchRestrictions = new SearchRestriction[0];
         try {
            SOAPGroup[] soapgroups = crowdClient.searchGroups(searchRestrictions);
            for (SOAPGroup soapgroup : soapgroups) {
               List<String> members = new ArrayList<String>(Arrays.asList(soapgroup.getMembers()));
               if (members.contains(username)) {
                  crowdClient.removePrincipalFromGroup(username, soapgroup.getName());
               }
            }
            for (String group : newGroups) {
               crowdClient.addPrincipalToGroup(username, group);
            }
            user.setRole(roles);
         } catch (Exception e) {
            log.error("Can't update user roles.", e);
         }
      }
      return user;
   }

   /**
    * Convert the display role name to role list.
    * e.g.: "Building Modeler & UI Designer" to (MODELER,DESIGNER). 
    * 
    * @param rolestr the rolestr
    * 
    * @return the list< string>
    */
   private List<String> convertDisplayRoleStringToRoleList(String rolestr) {
      List<String> roles = new ArrayList<String>();
      if (rolestr == null) {
         return roles;
      }
      if (rolestr.indexOf(Constants.ROLE_ADMIN_DISPLAYNAME) != -1) {
         roles.add(Constants.ADMIN);
      }
      if (rolestr.indexOf(Constants.ROLE_MODELER_DISPLAYNAME) != -1) {
         roles.add(Constants.MODELER);
      }
      if (rolestr.indexOf(Constants.ROLE_DESIGNER_DISPLAYNAME) != -1) {
         roles.add(Constants.DESIGNER);
      }
      return roles;
   }

   public void deleteUser(long uid, boolean isPending) {
      User user = getUserById(uid);
      if (!isPending) {
         SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
         try {
            crowdClient.removePrincipal(user.getUsername());
         } catch (Exception e) {
            log.error("Can't remove user " + user.getUsername() + " in crowd.", e);
            return;
         }
      }
      genericDAO.delete(user);
   }

   public List<User> getAccountAccessUsers(User currentUser) {
      List<User> accessUsers = new ArrayList<User>();
      List<User> sameAccountUsers = currentUser.getAccount().getUsers();
      sameAccountUsers.remove(currentUser);
      SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
      SearchRestriction[] searchRestrictions = new SearchRestriction[0];
      try {
         SOAPGroup[] groups = crowdClient.searchGroups(searchRestrictions);
         initUserProperties(currentUser, crowdClient, groups);
         accessUsers.add(currentUser);
         for (User accessUser : sameAccountUsers) {
            if (accessUser.getPendingRoleName() == null) {
               initUserProperties(accessUser, crowdClient, groups);
               accessUsers.add(accessUser);
            }
         }
      } catch (Exception e) {
         log.error("Can't find groups in crowd when initialize user properties.", e);
      }
      return accessUsers;
   }

   /**
    * Inits the user's email and role properties from crowd.
    * 
    * @param user the user
    * @param crowdClient the crowd client
    * @param groups the groups
    */
   private void initUserProperties(User user, SecurityServerClient crowdClient, SOAPGroup[] groups) {
      String username = user.getUsername();
      try {
         SOAPPrincipal principal = crowdClient.findPrincipalByName(username);
         user.setEmail(principal.getAttribute(UserConstants.EMAIL).getValues()[0]);
         List<String> roleStrs = new ArrayList<String>();
         for (SOAPGroup group : groups) {
            List<String> members = new ArrayList<String>(Arrays.asList(group.getMembers()));
            if (members.contains(username)) {
               roleStrs.add(group.getName());
            }
         }
         if (roleStrs.contains(Constants.ADMIN)) {
            user.setRole(Constants.ROLE_ADMIN_DISPLAYNAME);
         } else if(roleStrs.contains(Constants.MODELER) && roleStrs.contains(Constants.DESIGNER)) {
            user.setRole(Constants.ROLE_MODELER_DESIGNER_DISPLAYNAME);
         } else if (roleStrs.contains(Constants.MODELER)) {
            user.setRole(Constants.ROLE_MODELER_DISPLAYNAME);
         } else if(roleStrs.contains(Constants.DESIGNER)) {
            user.setRole(Constants.ROLE_DESIGNER_DISPLAYNAME);
         }
         
      } catch (Exception e) {
         log.error("Can't find user " + username + " in crowd when initialize user properties.", e);
      }
   }
   
   public String forgetPassword(String username) {
      SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
      String sendTo = "";
      try {
         SOAPPrincipal principal = crowdClient.findPrincipalByName(username);
         sendTo = principal.getAttribute(UserConstants.EMAIL).getValues()[0];
      } catch (Exception e) {
         log.error("Can't get " + username + "'s email from crowd when forget password.", e);
         return null;
      }
      final User user = genericDAO.getByNonIdField(User.class, "username", username);
      final String paswordToken = UUID.randomUUID().toString();
      final String email = sendTo;
      if ("".equals(email)) {
         return null;
      }
      
      MimeMessagePreparator preparator = new MimeMessagePreparator() {
         @SuppressWarnings("unchecked")
         public void prepare(MimeMessage mimeMessage) throws Exception {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject("OpenRemote Password Assistance");
            message.setTo(email);
            message.setFrom(mailSender.getUsername());
            Map model = new HashMap();
            model.put("webapp", configuration.getWebappServerRoot());
            model.put("username", user.getUsername());
            model.put("uid", user.getOid());
            model.put("aid", paswordToken);
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                 Constants.FORGET_PASSWORD_EMAIL_VM_NAME, "UTF-8", model);
            message.setText(text, true);
         }
      };
      try {
         this.mailSender.send(preparator);
         log.info("Sent 'Reset password' email to " + email);
         user.setToken(paswordToken);
         updateUser(user);
         return email;
      } catch (MailException e) {
         log.error("Can't send 'Reset password' email", e);
         return null;
      }
   }

   public User checkPasswordToken(long uid, String passwordToken) {
      User user = getUserById(uid);
      if (user != null && passwordToken.equals(user.getToken())) {
         return user;
      }
      return null;
   }

   public boolean resetPassword(long uid, String password, String passwordToken) {
      User user = getUserById(uid);
      if (user != null && passwordToken.equals(user.getToken())) {
         PasswordCredential credentials = new PasswordCredential(password);
         try {
            SecurityServerClientFactory.getSecurityServerClient().updatePrincipalCredential(user.getUsername(), credentials);
            user.setToken(null);
            updateUser(user);
            return true;
         } catch (Exception e) {
            log.error("Reset password error.", e);
         }
      }
      return false;
   }
   
   private SOAPAttribute buildAttribute(String key, String value) {
      SOAPAttribute attribute = new SOAPAttribute();

      attribute.setName(key);
      attribute.setValues(new String[1]);
      attribute.getValues()[0] = value;

      return attribute;
   }

   public void initUserAccount(String username) {
      if (genericDAO.getByNonIdField(User.class, "username", username) == null) {
         User user = new User();
         user.setUsername(username);
         saveUser(user);
      }
   }
}
