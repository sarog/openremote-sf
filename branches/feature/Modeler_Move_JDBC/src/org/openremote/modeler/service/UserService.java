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

import java.util.List;

import org.openremote.modeler.domain.Account;
import org.openremote.modeler.domain.User;

/**
 * The service for User.
 * 
 * @author Dan 2009-7-14
 */
public interface UserService {
   
   /**
    * Initialize the user groups in crowd, as the user role in modeler is according to crowd's group.
    * There are three groups: ADMIN, MODELER and DESIGNER.
    */
   void initRoles();

   /**
    * Save the new user and account into database, no .
    * 
    * @param user the user
    */
   void saveUser(User user);
   
   /**
    * Update the user with database.
    * 
    * @param user the user
    */
   void updateUser(User user);
   
   User getUserById(long id);
   
   Account getAccount();
   
   /**
    * Creates the user and its account, set disabled by default.
    * 
    * @param username the username
    * @param password the password
    * 
    * @return true, if successful
    */
   boolean createUserAccount(String username, String password, String email);
   
   /**
    * Activate user, then user can login.
    * 
    * @param userOid
    *           user oid
    * @param aid
    *           activation id
    * @return true if success
    */
   boolean activateUser(String userOid, String aid);
   
   /**
    * Send register activation email.
    * 
    * @param user
    *           the user
    * 
    * @return true, if success
    */
   boolean sendRegisterActivationEmail(User user, String email, String passwords);
   
   /**
    * Checks if is username can be create in crowd, as the username is unique.
    * 
    * @param username the username
    * 
    * @return true, if is username available
    */
   boolean isUsernameAvailable(String username);
   
   User getCurrentUser();
   
   /**
    * Invite a user to share an account, and the account has a specified role.
    * 
    * @param email the email
    * @param role the role
    * @param currentUser the current user
    * 
    * @return the user
    */
   User inviteUser(String email, String role, User currentUser);

   boolean sendInvitation(User invitee, User currentUser);
   
   /**
    * Check invitation info.
    * 
    * @param userOid the user oid
    * @param hostOid the host oid
    * @param aid the aid
    * 
    * @return true, if successful
    */
   boolean checkInvitation(String userOid, String hostOid, String aid);
   
   /**
    * Accept the invition and set the user password.
    * 
    * @param userOid the user oid
    * @param username the username
    * @param password the password
    * @param email the email
    * 
    * @return true, if successful
    */
   boolean createInviteeAccount(String userOid, String username, String password, String email);
   
   /**
    * Gets the pending invitees by current account.
    * 
    * @param currentUser the current user
    * 
    * @return the pending invitees by account
    */
   List<User> getPendingInviteesByAccount(User currentUser);
   
   /**
    * Update invited or pending user roles by the user id.
    * 
    * @param uid the uid
    * @param roles the roles
    * 
    * @return the user
    */
   User updateUserRoles(long uid, String roles, boolean pending);
   
   void deleteUser(long uid, boolean isPending);
   
   /**
    * Gets the users who can access the current account.
    * 
    * @param currentUser the current user
    * 
    * @return the account access users
    */
   List<User> getAccountAccessUsers(User currentUser);
   
   /**
    * The user forget password, send to the email that the user have registered.
    * 
    * @param username the username
    * 
    * @return the user
    */
   String forgetPassword(String username);
   
   /**
    * Check password token from the url which forward from the user email.
    * 
    * @param uid the uid
    * @param passwordToken the password token
    * 
    * @return the user
    */
   User checkPasswordToken(long uid, String passwordToken);
   
   /**
    * Change the user password if the passwordToken is equals to the database stored token.
    * 
    * @param uid the uid
    * @param password the password
    * @param passwordToken the password token
    * 
    * @return true, if successful
    */
   boolean resetPassword(long uid, String password, String passwordToken);
   /**
    * create a new user account if the user had passed crowd authorisation but no account in modeler.
    * 
    * @param username the username
    */
   void initUserAccount(String username);
   
   /**
    * Creates a guest user and sends a email to the guest user.
    * 
    * The email is the username, and "guest" is the password.
    * 
    * @return the user
    * 
    */
   User createGusetUser(String email);
   
}