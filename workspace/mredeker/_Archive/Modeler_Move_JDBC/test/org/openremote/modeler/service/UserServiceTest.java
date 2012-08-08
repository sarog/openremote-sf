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
package org.openremote.modeler.service;

import org.openremote.modeler.SpringTestContext;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.User;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.atlassian.crowd.integration.model.UserConstants;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClient;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClientFactory;
import com.atlassian.crowd.integration.soap.SOAPPrincipal;


/**
 * The Class UserServiceTest.
 * 
 * @author Dan Cong
 */
public class UserServiceTest {
   
   private UserService userService = (UserService) SpringTestContext.getInstance().getBean("userService");
   
   private User invitee;
   private SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
   
   private static final String TEST_USERNAME = "dan.cong";
   private static final String TEST_PASSWORD = "finalist";
   public static final String TEST_EMAIL = "openremote@163.com";
   
   @Test
   public void initRoles() {
      userService.initRoles();
      try {
         String[] allRoles = crowdClient.findAllGroupNames();
         Assert.assertEquals(allRoles.length, 3);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   @Test
   public void sendActivationEmailSuccessfully() {
      User user = new User();
      user.setId(11111);
      user.setUsername(TEST_USERNAME);
      Assert.assertTrue(userService.sendRegisterActivationEmail(user, TEST_EMAIL, TEST_PASSWORD));
   }
   
   @Test
   public void sendActivationEmailWithNullUser() {
      Assert.assertFalse(userService.sendRegisterActivationEmail(null, null, null));
   }
   
   @Test
   public void sendActivationEmailWithoutOid() {
      User user = new User();
      user.setUsername(TEST_USERNAME);
      Assert.assertFalse(userService.sendRegisterActivationEmail(user, TEST_EMAIL, TEST_PASSWORD));
   }
   
   @Test
   public void sendActivationEmailWihtoutUsername() {
      User user = new User();
      user.setId(11111);
      Assert.assertFalse(userService.sendRegisterActivationEmail(user, TEST_EMAIL, TEST_PASSWORD));
   }
   
   @Test
   public void sendActivationEmailWihtoutPassword() {
      User user = new User();
      user.setId(11111);
      user.setUsername(TEST_USERNAME);
      Assert.assertFalse(userService.sendRegisterActivationEmail(user, TEST_EMAIL, null));
   }
   
   @Test
   public void sendActivationEmailWihtoutEmail() {
      User user = new User();
      user.setId(11111);
      user.setUsername(TEST_USERNAME);
      Assert.assertFalse(userService.sendRegisterActivationEmail(user, null, TEST_PASSWORD));
   }
   
   @Test
   public void createNullAccount() {
      Assert.assertFalse(userService.createUserAccount(null, null, null));
   }
   
//   @Test(dependsOnMethods = { "initRoles" })
   public void createAccountSuccessfully() {
      Assert.assertTrue(userService.createUserAccount(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL));
   }
   
//   @Test(dependsOnMethods = { "createAccountSuccessfully" })
   public void activateNullAccount() {
      try {
         SOAPPrincipal principal = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal.isActive());
         Assert.assertNull(userService.activateUser(null, null));
         SOAPPrincipal principal2 = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal2.isActive());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
//   @Test(dependsOnMethods = { "activateNullAccount" })
   public void activateInvalidAccount() {
      try {
         SOAPPrincipal principal = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal.isActive());
         Assert.assertNull(userService.activateUser("a", null));
         SOAPPrincipal principal2 = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal2.isActive());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
//   @Test(dependsOnMethods = { "activateInvalidAccount" })
   public void activateAccountWithWrongAid() {
      try {
         SOAPPrincipal principal = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal.isActive());
         Assert.assertNull(userService.activateUser("1", "asdfsd"));
         SOAPPrincipal principal2 = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal2.isActive());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
//   @Test(dependsOnMethods = { "activateAccountWithWrongAid" })
   public void activateAccountSuccessfully() {
      try {
         SOAPPrincipal principal = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertFalse(principal.isActive());
         String sig = new Md5PasswordEncoder().encodePassword(principal.getName(), principal.getAttribute(UserConstants.EMAIL).getValues()[0]);
         Assert.assertNotNull(userService.activateUser("1", sig));
         SOAPPrincipal principal2 = crowdClient.findPrincipalByName(TEST_USERNAME);
         Assert.assertTrue(principal2.isActive());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
//   @Test(dependsOnMethods = { "createAccountSuccessfully" })
   public void inviteUser() {
      User currentUser = userService.getUserById(1L);
      User newInvitee = userService.inviteUser(TEST_EMAIL, Constants.ROLE_MODELER_DISPLAYNAME, currentUser);
      Assert.assertNotNull(newInvitee.getPendingRoleName());
      Assert.assertEquals(newInvitee.getPendingRoleName(), Constants.ROLE_MODELER_DISPLAYNAME);
      invitee = newInvitee; 
   }
   
//   @Test(dependsOnMethods = { "inviteUser" })
   public void checkInvitation() {
      User currentUser = userService.getUserById(1L);
      Assert.assertTrue(userService.checkInvitation(String.valueOf(invitee.getId()), ""+1, new Md5PasswordEncoder().encodePassword(TEST_EMAIL, currentUser.getUsername())));
   }
   
//   @Test(dependsOnMethods = { "checkInvitation" })
   public void updateUserRoles() {
      User user = userService.updateUserRoles(invitee.getId(), Constants.ROLE_MODELER_DESIGNER_DISPLAYNAME, true);
      Assert.assertEquals(user.getPendingRoleName(), Constants.ROLE_MODELER_DESIGNER_DISPLAYNAME);
   }
   
//   @Test(dependsOnMethods = { "updateUserRoles" })
   public void createInviteeAccount() {
      Assert.assertTrue(userService.createInviteeAccount(String.valueOf(invitee.getId()), "tomsky.wang", "hahahaha", TEST_EMAIL));
   }
   
//   @Test(dependsOnMethods = { "createInviteeAccount" })
   public void cleanPrincipals() {
      try {
         crowdClient.removePrincipal(TEST_USERNAME);
         crowdClient.removePrincipal("tomsky.wang");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
