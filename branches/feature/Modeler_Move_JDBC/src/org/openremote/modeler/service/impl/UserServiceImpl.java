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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.BeehiveJDBCException;
import org.openremote.modeler.exception.NotAuthenticatedException;
import org.openremote.modeler.exception.UserInvitationException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.utils.JsonGenerator;
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

import flexjson.JSONDeserializer;

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
      boolean hasGuestRole = false;
      
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
            } else if (groupName.equals(Constants.GUEST)) {
            	hasGuestRole = true;
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
         if (!hasGuestRole) {
        	 SOAPGroup group = new SOAPGroup();
        	 group.setActive(true);
        	 group.setDescription("openremote guest");
        	 group.setName(Constants.GUEST);
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
      return getUserFromBeehive("get/" + id);
   }
   
   /**
    * {@inheritDoc}
    */
    public Account getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByName(username).getAccount();
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
         return sendRegisterActivationEmail(saveUser(user), email, password);
      } else {
         return false;
      }
   }

    /**
    * {@inheritDoc}
    */
    public User saveUser(User user) {
       HttpClient httpClient = new DefaultHttpClient();
       HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTManageUserUrl() + "create");
       String[] includes = {"user","username","token","pendingRoleName"};
       String[] excludes = {"account","email","role"};
       httpPost.setHeader("Content-Type", "application/json"); 
       httpPost.addHeader("Accept", "application/json");
       String json = JsonGenerator.deepSerializerObjectInclude(user, includes, excludes);
       try {
          httpPost.setEntity(new StringEntity(json,"UTF-8"));
          HttpResponse response = httpClient.execute(httpPost);
          if (response.getStatusLine().getStatusCode() == 200) {
             String userJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<User>().use(null, User.class).deserialize(userJson);
          }
       } catch (UnsupportedEncodingException e) {
          throw new UserInvitationException("Save user to beehive failed");
       } catch (ClientProtocolException e) {
          throw new UserInvitationException("Save user to beehive failed");
       } catch (IOException e) {
          throw new UserInvitationException("Save user to beehive failed");
       }
       return null;
    }
    /**
     * {@inheritDoc}
     */
    public void updateUser(User user) {
       HttpClient httpClient = new DefaultHttpClient();
       HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTManageUserUrl() + "update");
       String[] includes = {"user","username","token","pendingRoleName"};
       String[] excludes = {"account","email","role"};
       httpPost.setHeader("Content-Type", "application/json"); 
       String json = JsonGenerator.deepSerializerObjectInclude(user, includes, excludes);
       try {
          httpPost.setEntity(new StringEntity(json,"UTF-8"));
          httpClient.execute(httpPost);
       } catch (UnsupportedEncodingException e) {
          throw new BeehiveJDBCException("Update user to beehive failed");
       } catch (ClientProtocolException e) {
          throw new BeehiveJDBCException("Update user to beehive failed");
       } catch (IOException e) {
          throw new BeehiveJDBCException("Update user to beehive failed");
       }
    }
    
    private void setDefaultConfigsForAccount(Account account){
       Set<ConfigCategory> categories = new HashSet<ConfigCategory>();
       Set<ControllerConfig> allDefaultConfigs = new HashSet<ControllerConfig>();
       XmlParser.initControllerConfig(categories, allDefaultConfigs);
       String[] excludes = {"*.class","*.hint","*.validation","*.options"};
       String json = JsonGenerator.serializerObjectExcludeWithRoot(allDefaultConfigs, excludes, "controllerConfigs");
       
       HttpClient httpClient = new DefaultHttpClient();
       HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTControllerCongigUrl() + "savedefault/" + account.getId());
       httpPost.setHeader("Content-Type", "application/json");
       
       try {
          httpPost.setEntity(new StringEntity(json,"UTF-8"));
          httpClient.execute(httpPost);
       } catch (UnsupportedEncodingException e) {
          throw new BeehiveJDBCException("Can't save default controllerConfigs to account.");
       } catch (ClientProtocolException e) {
          throw new BeehiveJDBCException("Can't save default controllerConfigs to account.");
       } catch (IOException e) {
          throw new BeehiveJDBCException("Can't save default controllerConfigs to account.");
       }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean sendRegisterActivationEmail(final User user, final String email, final String password) {
       if (user == null || user.getId() == 0 || StringUtils.isEmpty(email)
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
   public User activateUser(String userOid, String aid) {
      long id = 0;
      try {
         id = Long.valueOf(userOid);
      } catch (NumberFormatException e) {
         return null;
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
               return user;
            }
            
         } catch (Exception e) {
            log.error("Can't active user " + username +" in crowd", e);
         }
      }
      return null;
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
      return getUserByName(getCurrentUsername());
   }

   public User inviteUser(String email, String role, User currentUser) {
      User invitee = getPendingUserbyName(email);
      if (invitee == null) {
         invitee = new User();
         invitee.setUsername(email);
         invitee.setPendingRoleName(role);
         invitee = saveInvitee(invitee, currentUser.getAccount().getId());
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

   private User saveInvitee(User invitee, long accountId) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(configuration.getBeehiveRESTManageUserUrl() + "createinvitee/" + accountId);
      String[] includes = {"user","username","token","pendingRoleName"};
      String[] excludes = {"account","email","role"};
      httpPost.setHeader("Content-Type", "application/json"); 
      httpPost.addHeader("Accept", "application/json");
      String json = JsonGenerator.deepSerializerObjectInclude(invitee, includes, excludes);
      addAuthentication(httpPost);
      try {
         httpPost.setEntity(new StringEntity(json,"UTF-8"));
         HttpResponse response = httpClient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
            String userJson = IOUtils.toString(response.getEntity().getContent());
            return new JSONDeserializer<User>().use(null, User.class).deserialize(userJson);
         } else if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else {
            throw new BeehiveJDBCException("Failed save device to beehive.");
         }
      } catch (IOException e) {
         log.error("Save user to beehive failed", e);
         throw new BeehiveJDBCException("Failed save device to beehive.");
      }
   }
   
   private User getPendingUserbyName(String email) {
      User user = getUserByName(email);
      if (user != null && user.getPendingRoleName() != null) {
         return user;
      }
      return null;
   }
   
   public boolean sendInvitation(final User invitee, final User currentUser) {
       if (invitee == null || invitee.getId() == 0 || StringUtils.isEmpty(invitee.getUsername())) {
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
             model.put("uid", invitee.getId());
             model.put("role", invitee.getPendingRoleName());
             model.put("cid", currentUser.getId());
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
      List<User> sameAccountUsers = loadUsersByAccount(currentUser.getAccount().getId());
      List<User> pendingUsers = new ArrayList<User>();
      for (User user : sameAccountUsers) {
         if (user.getPendingRoleName() != null) {
            pendingUsers.add(user);
         }
      }
      return pendingUsers;
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
            throw new UserInvitationException("Can't update user roles.");
         }
      }
      return user;
   }

   private List<User> loadUsersByAccount(long accountId) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTManageUserUrl() + "loadall/" + accountId);
      httpGet.addHeader("Accept", "application/json");
      addAuthentication(httpGet);
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
            String usersJson = IOUtils.toString(response.getEntity().getContent());
            UserList result = new JSONDeserializer<UserList>().use(null, UserList.class).use("users",
                  ArrayList.class).deserialize(usersJson);
            return result.getUsers();
         } else if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
         } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            return new ArrayList<User>();
         } else {
            throw new BeehiveJDBCException("Failed load account users from beehive.");
         }
      } catch (IOException e) {
         log.error("Can't load account users from beehive.", e);
         throw new BeehiveJDBCException("Can't load account users from beehive.");
      }
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
      if (rolestr.indexOf(Constants.ROLE_GUEST_DISPLAYNAME) != -1) {
    	  roles.add(Constants.GUEST);
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
      HttpClient httpClient = new DefaultHttpClient();
      HttpDelete httpDelete = new HttpDelete(configuration.getBeehiveRESTManageUserUrl() + "delete/" + uid);
      addAuthentication(httpDelete);
      try {
         HttpResponse response = httpClient.execute(httpDelete);
         if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
               throw new NotAuthenticatedException("User " + getCurrentUsername() + " not authenticated! ");
            }
            throw new BeehiveJDBCException("Failed delete user in beehive.");
         }
      } catch (IOException e) {
         log.error("Failed delete user in beehive.", e);
         throw new BeehiveJDBCException("Failed delete user in beehive.");
      }
   }

   public List<User> getAccountAccessUsers(User currentUser) {
      List<User> accessUsers = new ArrayList<User>();
      List<User> sameAccountUsers = loadUsersByAccount(currentUser.getAccount().getId());
      SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
      SearchRestriction[] searchRestrictions = new SearchRestriction[0];
      try {
         SOAPGroup[] groups = crowdClient.searchGroups(searchRestrictions);
         initUserProperties(currentUser, crowdClient, groups);
         accessUsers.add(currentUser);
         for (User accessUser : sameAccountUsers) {
            if (currentUser.getId() == accessUser.getId()) continue;
            if (accessUser.getPendingRoleName() == null) {
               initUserProperties(accessUser, crowdClient, groups);
               accessUsers.add(accessUser);
            }
         }
      } catch (Exception e) {
         log.error("Can't find groups in crowd when initialize user properties.", e);
         throw new UserInvitationException("Can't find groups in crowd when initialize user properties.");
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
         } else if(roleStrs.contains(Constants.GUEST)) {
        	 user.setRole(Constants.ROLE_GUEST_DISPLAYNAME);
         }
         
      } catch (Exception e) {
         log.error("Can't find user " + username + " in crowd when initialize user properties.", e);
         throw new UserInvitationException("Can't find user " + username + " in crowd when initialize user properties.");
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
      final User user = getUserByName(username);   
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
            model.put("uid", user.getId());
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

   private User getUserByName(String username) {
      return getUserFromBeehive("getbyname/" + username);
   }
   
   /**
    * Gets the user from beehive.
    * 
    * @param url the url can be "getbyname/username" or "get/userId".
    * 
    * @return the user from beehive
    */
   private User getUserFromBeehive(String url) {
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet(configuration.getBeehiveRESTManageUserUrl() + url);
      httpGet.addHeader("Accept", "application/json");
      try {
         HttpResponse response = httpClient.execute(httpGet);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == HttpServletResponse.SC_OK) {
             String userJson = IOUtils.toString(response.getEntity().getContent());
             return new JSONDeserializer<User>().use(null, User.class).deserialize(userJson);
          } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
             return null;
          }
      } catch (ClientProtocolException e) {
         throw new BeehiveJDBCException("Can't get user by username from beehive.");
      } catch (IOException e) {
         throw new BeehiveJDBCException("Can't get user by username from beehive.");
      }
      return null;
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
      if (getUserByName(username) == null) {
         User user = new User();
         user.setUsername(username);
         saveUser(user);
      }
   }

   public User createGusetUser(String email) {
      if (!isUsernameAvailable(email)) {
         throw new UserInvitationException("Failed to create guest user.");
      }
      User currentUser = getCurrentUser();
      final String host = currentUser.getUsername();
      final User user = new User(currentUser.getAccount());
      final String rawPassword = "guest";
      user.setUsername(email);
      user.setEmail(email);
      MimeMessagePreparator preparator = new MimeMessagePreparator() {
         @SuppressWarnings("unchecked")
         public void prepare(MimeMessage mimeMessage) throws Exception {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setSubject("OpenRemote Guest User");
            message.setTo(user.getEmail());
            message.setFrom(mailSender.getUsername());
            Map model = new HashMap();
            model.put("host", host);
            model.put("username", user.getUsername());
            model.put("password", rawPassword);
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                 Constants.CREATE_GUEST_EMAIL_VM_NAME, "UTF-8", model);
            message.setText(text, true);
         }
      };
      try {
         this.mailSender.send(preparator);
         log.info("Sent 'OpenRemote Guest User' email to " + email);
         
         SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
         
         /**
          * Create a new principal, its name is equals the username.
          */
         SOAPPrincipal principal = new SOAPPrincipal();
         principal.setActive(false);
         principal.setName(email);
         
         /**
          * Create principal attributes.
          * The email, firstname and lastname are required.
          */
         SOAPAttribute[] soapAttributes = new SOAPAttribute[4];
         soapAttributes[0] = buildAttribute(UserConstants.EMAIL, email);
         soapAttributes[1] = buildAttribute(UserConstants.FIRSTNAME, email);
         soapAttributes[2] = buildAttribute(UserConstants.LASTNAME, email);
         soapAttributes[3] = buildAttribute(UserConstants.DISPLAYNAME, email);
         
         principal.setAttributes(soapAttributes);
         
         /**
          * Create the principal's password credentials, it represent the password.
          */
         PasswordCredential credentials = new PasswordCredential("guest");
         try {
            principal = crowdClient.addPrincipal(principal, credentials);
            crowdClient.addPrincipalToGroup(email, Constants.GUEST);
         } catch (Exception e) {
            log.error("Can't create user " + email + " with role 'GUEST'.", e);
            throw new UserInvitationException("Failed to create guest user.");
         }
         User dbUser = saveInvitee(user, currentUser.getAccount().getId());
         dbUser.setEmail(email);
         dbUser.setRole(Constants.ROLE_GUEST_DISPLAYNAME);
         return dbUser;
      } catch (MailException e) {
         log.error("Can't send 'OpenRemote Guest User' email", e);
         throw new UserInvitationException("Failed to create guest user.");
      }
   }
   
}
