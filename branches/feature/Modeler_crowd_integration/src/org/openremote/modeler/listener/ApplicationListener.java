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
package org.openremote.modeler.listener;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openremote.modeler.SpringContext;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.service.UserService;
import org.springframework.context.ApplicationEvent;

import com.atlassian.crowd.integration.authentication.PasswordCredential;
import com.atlassian.crowd.integration.exception.ApplicationAccessDeniedException;
import com.atlassian.crowd.integration.exception.ApplicationPermissionException;
import com.atlassian.crowd.integration.exception.ExpiredCredentialException;
import com.atlassian.crowd.integration.exception.InactiveAccountException;
import com.atlassian.crowd.integration.exception.InvalidAuthenticationException;
import com.atlassian.crowd.integration.exception.InvalidAuthorizationTokenException;
import com.atlassian.crowd.integration.exception.InvalidCredentialException;
import com.atlassian.crowd.integration.exception.InvalidUserException;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.http.HttpAuthenticator;
import com.atlassian.crowd.integration.http.HttpAuthenticatorFactory;
import com.atlassian.crowd.integration.model.UserConstants;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClient;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClientFactory;
import com.atlassian.crowd.integration.soap.SOAPAttribute;
import com.atlassian.crowd.integration.soap.SOAPGroup;
import com.atlassian.crowd.integration.soap.SOAPPrincipal;
import com.atlassian.crowd.integration.soap.SearchRestriction;


/**
 * Init application when web server is started.
 * It make sure the resource folder("modeler_tmp") be created.
 * 
 * @see ApplicationEvent
 * @author Tomsky, Dan
 */
public class ApplicationListener implements ServletContextListener {
   
   private UserService userService = (UserService) SpringContext.getInstance().getBean("userService");

   public void contextDestroyed(ServletContextEvent event) {
      ;//do nothing
   }

   public void contextInitialized(ServletContextEvent event) {
      // set web root, eg: "E:\apache-tomcat-5.5.28\webapps\modeler\".
      PathConfig.WEBROOTPATH = event.getServletContext().getRealPath("/");
      File tempFolder = new File(PathConfig.WEBROOTPATH + File.separator + PathConfig.RESOURCEFOLDER);
      if (!tempFolder.exists()) {
         tempFolder.mkdirs();
      }
      userService.initRoles();
//      verify();
      SecurityServerClient crowdClient = SecurityServerClientFactory.getSecurityServerClient();
//      removePrincipalFromGroup(crowdClient, "test", "ADMIN");
//      getGroups("wangzt", crowdClient);
//      addGroup(crowdClient);
//      addUser(crowdClient);
//      getPricipal("dancyd", crowdClient);
   }

   public boolean addGroup(SecurityServerClient crowdClient) {
      
      try {
         String name = "ADMIN";
         String description = "modeler admin";
         if (doValidation(name, crowdClient)) {
            SOAPGroup group = new SOAPGroup();
            group.setActive(true);
            group.setDescription(description);
            group.setName(name);
            group = crowdClient.addGroup(group);
         }

         return true;

      } catch (Exception e) {
         return false;
      }
   }

   protected boolean doValidation(String name, SecurityServerClient securityServerClient) {
      if (name != null && !name.equals("")) {
         try {
            SOAPGroup group = securityServerClient.findGroupByName(name);
            if (group == null) {
               return true;
            }
         } catch (Exception e) {
            // ignore
            return true;
         }
      }
      return false;
   }
   
   public void addUser(SecurityServerClient crowdClient) {
      String name = "dancyd";
      String password = "123456";
      if (validatePricipal(name, crowdClient)) {
         SOAPPrincipal principal = new SOAPPrincipal();
         
         principal.setActive(true);
         principal.setName(name);
         
         SOAPAttribute[] soapAttributes = new SOAPAttribute[4];
         
         soapAttributes[0] = buildAttribute(UserConstants.EMAIL, "tomsky.wang@finalist.hk");
         soapAttributes[1] = buildAttribute(UserConstants.FIRSTNAME, "dan");
         soapAttributes[2] = buildAttribute(UserConstants.LASTNAME, "dan");
         soapAttributes[3] = buildAttribute(UserConstants.DISPLAYNAME, name + " " + name);
         
         principal.setAttributes(soapAttributes);
         
         // our password
         PasswordCredential credentials = new PasswordCredential(password, true);
         // have the security server add it
         try {
            principal = crowdClient.addPrincipal(principal, credentials);
            
            crowdClient.addPrincipalToGroup(name, "ADMIN");
         } catch (RemoteException e) {
            e.printStackTrace();
         } catch (InvalidUserException e) {
            e.printStackTrace();
         } catch (ApplicationPermissionException e) {
            e.printStackTrace();
         } catch (InvalidAuthorizationTokenException e) {
            e.printStackTrace();
         } catch (InvalidCredentialException e) {
            e.printStackTrace();
         } catch (ObjectNotFoundException e) {
            e.printStackTrace();
         }
      }
      
   }
   
   public SOAPAttribute buildAttribute(String key, String value) {
      SOAPAttribute attribute = new SOAPAttribute();

      attribute.setName(key);
      attribute.setValues(new String[1]);
      attribute.getValues()[0] = value;

      return attribute;
  }
   
  public boolean validatePricipal(String name, SecurityServerClient crowdClient) {
     try {
      if (crowdClient.findPrincipalByName(name) == null) {
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
  
  public void getPricipal(String name, SecurityServerClient crowdClient) {
    try {
      SOAPPrincipal principal = crowdClient.findPrincipalByName(name);
      
      SOAPAttribute attribute = principal.getAttribute(UserConstants.EMAIL);
      System.out.println(attribute.getName()+" : " + attribute.getValues()[0]);
   } catch (RemoteException e) {
      e.printStackTrace();
   } catch (InvalidAuthorizationTokenException e) {
      e.printStackTrace();
   } catch (ObjectNotFoundException e) {
      e.printStackTrace();
   }
  }
  
  public void verify() {
      HttpAuthenticator authenticator = HttpAuthenticatorFactory.getHttpAuthenticator();
      try {
         authenticator.verifyAuthentication("wangzt", "nihaoma");
         System.out.println("verify success");
         return;
      } catch (Exception e) {
         e.printStackTrace();
         System.out.println("verify error");
         return;
      }
   }
  
  public void getGroups(String name, SecurityServerClient crowdClient) {
     SearchRestriction[] searchRestrictions = new SearchRestriction[0];
     try {
        SOAPGroup[] groups = crowdClient.searchGroups(searchRestrictions);
        for (SOAPGroup group : groups) {
           List<String> members = new ArrayList<String>(Arrays.asList(group.getMembers()));
           if (members.contains(name)) {
              System.out.println("group:" + group.getName());
           }
           
        }
   } catch (Exception e) {
      e.printStackTrace();
   }
  }
  
  public void removePrincipalFromGroup(SecurityServerClient crowdClient, String principal, String group) {
     try {
      crowdClient.removePrincipalFromGroup(principal, group);
   } catch (RemoteException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
   } catch (ApplicationPermissionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
   } catch (InvalidAuthorizationTokenException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
   } catch (ObjectNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
   }
  }
}
