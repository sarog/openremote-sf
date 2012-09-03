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
package org.openremote.modeler.service;

import java.util.List;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.dao.GenericDAO;
import org.openremote.modeler.domain.Role;
import org.openremote.modeler.domain.User;
import org.openremote.useraccount.domain.UserDTO;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * The Class UserServiceTest.
 * 
 * @author Dan Cong
 */
public class UserServiceTest {
   
   private UserService userService = (UserService) SpringTestContext.getInstance().getBean("userService");
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   private User invitee;
   
   public static final String TEST_EMAIL = "marcus@openremote.org";
   
   
   @Test
   public void createNullAccount() {
      Assert.assertFalse(userService.createUserAccount(null, null, null));
   }
   
   @Test
   public void createAccountSuccessfully() {
      Assert.assertTrue(userService.createUserAccount("dan.cong", "finalist", TEST_EMAIL));
   }
   
   @Test(dependsOnMethods = { "createAccountSuccessfully" })
   public void activateNullAccount() {
      User u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
      Assert.assertFalse(userService.activateUser(null, null));
      u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
   }
   
   @Test(dependsOnMethods = { "activateNullAccount" })
   public void activateInvalidAccount() {
      User u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
      Assert.assertFalse(userService.activateUser("a", null));
      u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
   }
   
   @Test(dependsOnMethods = { "activateInvalidAccount" })
   public void activateAccountWithoutAid() {
      User u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
      Assert.assertFalse(userService.activateUser("1", null));
      u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
   }
   
   @Test(dependsOnMethods = { "activateAccountWithoutAid" })
   public void activateAccountWithWrongAid() {
      User u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
      Assert.assertFalse(userService.activateUser("1", "asdfsd"));
      u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
   }
   
   @Test(dependsOnMethods = { "activateAccountWithWrongAid" })
   public void activateAccountSuccessfully() {
      User u = userService.getUserById(1L);
      Assert.assertFalse(u.isValid());
      String sig = new Md5PasswordEncoder().encodePassword(u.getUsername(), u.getPassword());
      Assert.assertTrue(userService.activateUser("1", sig));
      Assert.assertTrue(userService.activateUser("1", sig));
      u = userService.getUserById(1L);
      Assert.assertTrue(u.isValid());
   }
   
   @Test(dependsOnMethods = { "createAccountSuccessfully" })
   public void inviteUser() {
      User currentUser = userService.getUserById(1L);
      User newInvitee = userService.inviteUser(TEST_EMAIL, Constants.ROLE_MODELER_DISPLAYNAME, currentUser);
      Assert.assertEquals(newInvitee.getRole(), Constants.ROLE_MODELER_DISPLAYNAME);
      Assert.assertFalse(newInvitee.isValid());
      invitee = newInvitee; 
   }
   
   @Test(dependsOnMethods = { "inviteUser" })
   public void checkInvitation() {
      User currentUser = userService.getUserById(1L);
      Assert.assertTrue(userService.checkInvitation(String.valueOf(invitee.getOid()), ""+1, new Md5PasswordEncoder().encodePassword(TEST_EMAIL, currentUser.getPassword())));
   }
   
   @Test(dependsOnMethods = { "checkInvitation" })
   public void updateUserRoles() {
      User user = userService.updateUserRoles(invitee.getOid(), Constants.ROLE_MODELER_DESIGNER_DISPLAYNAME);
      Assert.assertEquals(user.getRole(), Constants.ROLE_MODELER_DESIGNER_DISPLAYNAME);
   }
   
   @Test(dependsOnMethods = { "updateUserRoles" })
   public void createInviteeAccount() {
      Assert.assertTrue(userService.createInviteeAccount(String.valueOf(invitee.getOid()), "tomsky", "hahahaha", TEST_EMAIL));
   }
   
   @Test
   public void resetPassword() {
      String username = "tomsky.wang";
      Assert.assertTrue(userService.createUserAccount(username, "firstcreate", TEST_EMAIL));
      UserDTO user = userService.forgotPassword(username);
      Assert.assertNotNull(user);
      Assert.assertTrue(userService.resetPassword(user.getOid(), "finalist", user.getToken()));
   }
}
