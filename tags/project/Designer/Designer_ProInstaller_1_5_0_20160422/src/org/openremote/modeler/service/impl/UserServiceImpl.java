/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.ControllerConfig;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.UserChangePasswordException;
import org.openremote.modeler.exception.UserInvitationException;
import org.openremote.modeler.service.BaseAbstractService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.service.UserService.UsernamePassword;
import org.openremote.modeler.utils.XmlParser;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.openremote.useraccount.domain.AccountDTO;
import org.openremote.useraccount.domain.RoleDTO;
import org.openremote.useraccount.domain.UserDTO;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * TODO
 * 
 * @author Dan 2009-7-14
 * @author <a href="mailto:marcus@openremote.org">Marcus Redeker</a>
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UserServiceImpl extends BaseAbstractService<User> implements UserService {


  public final static String MAILER_LOG_CATEGORY = "OpenRemote.Designer.Mail";
  public final static String ACTIVATION_MAIL_LOG_CATEGORY = MAILER_LOG_CATEGORY + ".Activate";
  public final static String SHARED_ACCOUNT_MAIL_LOG_CATEGORY = MAILER_LOG_CATEGORY + ".Share.Account";


   private static Logger log = Logger.getLogger(UserServiceImpl.class);
   
   private Configuration configuration;

   /**
    * {@inheritDoc}
    */
    @Override
   public UsernamePassword getCurrentUsernamePassword() {
     return new UsernamePassword(SecurityContextHolder.getContext().getAuthentication().getName(),
         SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
   }

   /**
    * {@inheritDoc}
    */
    @Override
    public User getUserById(long id) {
      return genericDAO.getById(User.class, id);
    }

   public UserDTO getUserDTOById(long id) {
     ClientResource cr = null;
     String str = "";
     try {
       cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "user/" + id);
       cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, configuration.getUserAccountServiceRESTUsername(), configuration.getUserAccountServiceRESTPassword());
       Representation r = cr.get();
       try { 
         str = r.getText();
       } catch (IOException e)
       {
         throw new RuntimeException(e);
       }
     } finally {
       if (cr != null) {
	     cr.release();
       }
     }
     GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", UserDTO.class).deserialize(str);
     if (res.getErrorMessage() != null) {
       throw new RuntimeException(res.getErrorMessage());
     }
     return (UserDTO)res.getResult(); 
   }
   
   /**
    * {@inheritDoc}
    */
    @Override
    public Account getAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return genericDAO.getByNonIdField(User.class, "username", username).getAccount();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createUserAccount(String username, String password, String email) {
      if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(email)) {
         return false;
      }
      if (isUsernameAvailable(username)) {
         UserDTO user = new UserDTO();
         user.setUsername(username);
         user.setPassword(new Md5PasswordEncoder().encodePassword(password, username));
         user.setEmail(email);
         user.setRegisterTime(new Timestamp(System.currentTimeMillis()));
         user.setAccount(new AccountDTO());
         user.addRole(new RoleDTO(Role.ROLE_ADMIN));
         saveUserDTO(user);
         return true;
      } else {
         return false;
      }
   }

    /**
     * {@inheritDoc}
     */
	 @Override
     @Transactional public void saveUser(User user) {
         genericDAO.save(user.getAccount());
         genericDAO.save(user);
     }
     
    /**
    * {@inheritDoc}
    */
    private void saveUserDTO(UserDTO user) {
      ClientResource cr = null;
      String str = "";
      try {
        cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "user");
        cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, configuration.getUserAccountServiceRESTUsername(), configuration.getUserAccountServiceRESTPassword());
        Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(user));
        Representation r = cr.post(rep);
        try
        {
          str = r.getText();
        } catch (IOException e)
        {
          throw new RuntimeException(e);
        }
      } finally {
    	if (cr != null) {
    	  cr.release();
        }
      }
      GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", Long.class).deserialize(str); 
      if (res.getErrorMessage() != null) {
        throw new RuntimeException(res.getErrorMessage());
      }
      Long addedUserOID = (Long)res.getResult();
      user.setOid(addedUserOID);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUser(UserDTO user) {
      ClientResource cr = null;
      String str = "";
      try {
        cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "user");
        cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, configuration.getUserAccountServiceRESTUsername(), configuration.getUserAccountServiceRESTPassword());
        Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class", "registerTimeAsString").deepSerialize(user));
        Representation r = cr.put(rep);
        try
        {
          str = r.getText();
        } catch (IOException e)
        {
          e.printStackTrace();
        }
      } finally {
    	if (cr != null) {
          cr.release();
    	}
      }
      GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", UserDTO.class).deserialize(str); 
      if (res.getErrorMessage() != null) {
        throw new RuntimeException(res.getErrorMessage());
      }
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
   @Override
   public boolean activateUser(String userOid, String aid) {
      long id = 0;
      try {
         id = Long.valueOf(userOid);
      } catch (NumberFormatException e) {
         return false;
      }
      UserDTO user = getUserDTOById(id);
      if (user != null && aid != null) {
         if (new Md5PasswordEncoder().encodePassword(user.getUsername(), user.getPassword()).equals(aid)) {
            user.setValid(true);
            updateUser(user);
            Account account = getUserByUsername(user.getUsername()).getAccount();
            setDefaultConfigsForAccount(account);
            return true;
         }
      }
      return false;
   }
   
   @Override
   public boolean isUsernameAvailable(String username) {
      return getUserByUsername(username) == null;
   }

   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   @Override
   public User getCurrentUser() {
      String username = SecurityContextHolder.getContext().getAuthentication().getName();      
      User user = getUserByUsername(username);
      return user;
   }
   
   private User getUserByUsername(String username) {
      return genericDAO.getByNonIdField(User.class, "username", username);
    }

   @Override
   public UserDTO inviteUser(String email, String roleDisplayName, User currentUser) throws UserInvitationException {
     StringBuffer url = new StringBuffer("user/" + currentUser.getOid() + "/inviteUser");
     url.append("?inviteeEmail=" + email);
     url.append("&inviteeRoles=" + convertRoleDisplayStringToRoleString(roleDisplayName));
     ClientResource cr = null;
     String str = "";
     try {
       cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + url.toString());
       cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, configuration.getUserAccountServiceRESTUsername(), configuration.getUserAccountServiceRESTPassword());
       Representation r = cr.post(null);
       try { 
         str = r.getText();
       } catch (IOException e)
       {
         throw new RuntimeException(e);
       }
     } finally {
       if (cr != null) {
    	 cr.release();
       }
     }
     GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", UserDTO.class).deserialize(str);
     if (res.getErrorMessage() != null) {
       throw new UserInvitationException(res.getErrorMessage());
     }
     UserDTO inviteeDTO = (UserDTO)res.getResult();
     return inviteeDTO; 
   }

   @Override
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
      UserDTO user = getUserDTOById(uid);
      UserDTO hostUser = getUserDTOById(hid);
      if (user != null && hostUser != null && aid != null) {
         if (new Md5PasswordEncoder().encodePassword(user.getEmail(), hostUser.getPassword()).equals(aid)) {
            return true;
         }
      }
      return false;
   }

   @Override
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
         UserDTO user = getUserDTOById(id);
         user.setValid(true);
         user.setUsername(username);
         user.setPassword(new Md5PasswordEncoder().encodePassword(password, username));
         user.setEmail(email);
         user.setRegisterTime(new Timestamp(System.currentTimeMillis()));
         updateUser(user);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public List<User> getPendingInviteesByAccount(User currentUser) {
      List<User> invitees = new ArrayList<User>();
      List<User> sameAccountUsers = currentUser.getAccount().getUsers();
      sameAccountUsers.remove(currentUser);
      for (User invitee : sameAccountUsers) {
         if(!invitee.isValid()) {
            Hibernate.initialize(invitee.getRoles());
            invitees.add(invitee);
         }
      }
      return invitees;
   }

   @Override
   public UserDTO updateUserRoles(long uid, String roles) {
     UserDTO user = getUserDTOById(uid);
     user.getRoles().clear();
     convertRoleStringToRole(roles, user, genericDAO.loadAll(Role.class));
     updateUser(user);
     return user;
   }

   private void convertRoleStringToRole(String roles, UserDTO user, List<Role> allRoles) {
      for (Role role : allRoles) {
         if(role.getName().equals(Role.ROLE_ADMIN) && roles.indexOf(RoleDTO.ROLE_ADMIN_DISPLAYNAME) != -1) {
            user.addRole(new RoleDTO(Role.ROLE_ADMIN, role.getOid()));
         } else if (role.getName().equals(Role.ROLE_MODELER) && roles.indexOf(RoleDTO.ROLE_MODELER_DISPLAYNAME) != -1) {
            user.addRole(new RoleDTO(Role.ROLE_MODELER, role.getOid()));
         } else if (role.getName().equals(Role.ROLE_DESIGNER) && roles.indexOf(RoleDTO.ROLE_DESIGNER_DISPLAYNAME) != -1) {
            user.addRole(new RoleDTO(Role.ROLE_DESIGNER, role.getOid()));
         }
      }
   }
   
   private String convertRoleDisplayStringToRoleString(String roleDisplayName) {
     StringBuffer roleNames = new StringBuffer();
     List<Role> allRoles = genericDAO.loadAll(Role.class);
     for (Role role : allRoles) {
        if(role.getName().equals(Role.ROLE_ADMIN) && roleDisplayName.indexOf(RoleDTO.ROLE_ADMIN_DISPLAYNAME) != -1) {
           roleNames.append(role.getName()).append(",");
        } else if (role.getName().equals(Role.ROLE_MODELER) && roleDisplayName.indexOf(RoleDTO.ROLE_MODELER_DISPLAYNAME) != -1) {
          roleNames.append(role.getName()).append(",");
        } else if (role.getName().equals(Role.ROLE_DESIGNER) && roleDisplayName.indexOf(RoleDTO.ROLE_DESIGNER_DISPLAYNAME) != -1) {
          roleNames.append(role.getName()).append(",");
        }
     }
     roleNames.deleteCharAt(roleNames.length()-1);
     return roleNames.toString();
  }

   @Override
   public void deleteUser(long uid) {
      ClientResource cr = null;
      String str = "";
      try {
    	cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "user/" + uid);
        cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, configuration.getUserAccountServiceRESTUsername(), configuration.getUserAccountServiceRESTPassword());
        Representation result = cr.delete();
        try
        {
          str = result.getText();
        } catch (IOException e)
        {
          throw new RuntimeException(e);
        }
      } finally {
    	if (cr != null) {
  	      cr.release();
    	}
      }
      GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", String.class).deserialize(str);
      if (res.getErrorMessage() != null) {
        throw new RuntimeException(res.getErrorMessage());
      }
   }

   @Override
   public List<User> getAccountAccessUsers(User currentUser) {
      List<User> accessUsers = new ArrayList<User>();
      List<User> sameAccountUsers = currentUser.getAccount().getUsers();
      sameAccountUsers.remove(currentUser);
      accessUsers.add(currentUser);
      Hibernate.initialize(currentUser.getRoles());
      for (User accessUser : sameAccountUsers) {
         if(accessUser.isValid()) {
            Hibernate.initialize(accessUser.getRoles());
            accessUsers.add(accessUser);
         }
      }
      return accessUsers;
   }


   @Override
   public UserDTO forgotPassword(String username)
   {
     ClientResource cr = null;
     String str = "";
     try {
       cr = new ClientResource(configuration.getUserAccountServiceRESTRootUrl() + "user/" + username + "/forgotPassword");
       cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, configuration.getUserAccountServiceRESTUsername(), configuration.getUserAccountServiceRESTPassword());
       Representation r = cr.get();
       try { 
         str = r.getText();
       } catch (IOException e)
       {
         throw new RuntimeException(e);
       }
     } finally {
       if (cr != null) {
         cr.release();
       }
     }
     GenericResourceResultWithErrorMessage res =new JSONDeserializer<GenericResourceResultWithErrorMessage>().use(null, GenericResourceResultWithErrorMessage.class).use("result", UserDTO.class).deserialize(str);
     if (res.getErrorMessage() != null) {
       throw new RuntimeException(res.getErrorMessage());
     }
     return (UserDTO)res.getResult(); 
   }

   @Override
   public UserDTO checkPasswordToken(long uid, String passwordToken) {
      UserDTO user = getUserDTOById(uid);
      if (user != null && passwordToken.equals(user.getToken())) {
         return user;
      }
      return null;
   }

   @Override
   public boolean resetPassword(long uid, String password, String passwordToken) {
      UserDTO user = getUserDTOById(uid);
      if (user != null && passwordToken.equals(user.getToken())) {
         user.setPassword(new Md5PasswordEncoder().encodePassword(password, user.getUsername()));
         user.setToken(null);
         updateUser(user);
         return true;
      }
      return false;
   }

  @Override
  public void changePassword(String oldPassword, String newPassword) throws UserChangePasswordException
  {
    User currentUser = getCurrentUser();
    UserDTO user = getUserDTOById(currentUser.getOid());
    String oldPasswordEnc = new Md5PasswordEncoder().encodePassword(oldPassword, user.getUsername());
    if (!oldPasswordEnc.equals(user.getPassword())) {
      throw new UserChangePasswordException("The old password does not match!");
    }
    try
    {
      user.setPassword(new Md5PasswordEncoder().encodePassword(newPassword, user.getUsername()));
      updateUser(user);
    } catch (Exception e)
    {
      throw new UserChangePasswordException(e.getMessage());
    }
  }

   
}
