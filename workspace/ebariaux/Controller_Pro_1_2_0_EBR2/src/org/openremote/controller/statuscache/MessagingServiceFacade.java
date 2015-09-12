/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.statuscache;

import java.util.ArrayList;
import java.util.List;

import org.openremote.controller.Constants;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.Deployer.PasswordException;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.utils.Logger;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONSerializer;

/**
 * Facade to provide access to the OpenRemote Messaging service from within the rules.
 * 
 * @author Eric Bariaux <a href="mailto:eric@openremote.org"/>
 */
public class MessagingServiceFacade extends EventFacade {

   /**
    * Common status cache logging category on operations that occur during runtime (not part
    * of lifecycle start/stop operations).
    */
   private final static Logger log = Logger.getLogger(Constants.RUNTIME_STATECACHE_LOG_CATEGORY);

   public void message(String recipient, String message) {
      log.debug("Sending '" + message + "' to " + recipient);
      
      Deployer deployer = ServiceContext.getDeployer();
      
      String username = deployer.getUserName();
      
      String password = null;
      try {
         password = deployer.getPassword(username);
      } catch (PasswordException e) {
         log.warn("Failed to get password for user", e);
      }

      if (username == null || password == null) {
         log.warn("Missing credential, messaging service not called");
      }
      
      log.debug("Authenticated as " + username);
      
      ClientResource cr = null;
      
      try {
        // For now, have direct communication with the REST service.
        // In a future version, this should go through a central communication service between controller and Beehive.
        // This service should then also ensure communication with backend is performed in the background.

        // TODO: this URL must come from some configuration file
         
        // TODO: account oid must come from controller configuration (account controller is linked to)

        cr = new ClientResource("http://54.195.247.29/openremote-messaging-0.1.0/v1/accounts/123/SMSMessages");
        cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
        Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(new SMSMessage(recipient, message)));
        cr.post(rep);
        
        // TODO: retrieve return value and log if error
        
      } finally {
        if (cr != null) {
          cr.release();
        }
      }
   }
   
   public void email(String fromEmail, String fromName, String toEmail, String subject, String message) {
      log.debug("Sending '" + message + "' to " + toEmail);
      
      Deployer deployer = ServiceContext.getDeployer();
      
      String username = deployer.getUserName();
      
      String password = null;
      try {
         password = deployer.getPassword(username);
      } catch (PasswordException e) {
         log.warn("Failed to get password for user", e);
      }

      if (username == null || password == null) {
         log.warn("Missing credential, messaging service not called");
      }
      
      log.debug("Authenticated as " + username);
      
      ClientResource cr = null;
      
      try {
         // For now, have direct communication with the REST service.
         // In a future version, this should go through a central communication service between controller and Beehive.
         // This service should then also ensure communication with backend is performed in the background.

         // TODO: this URL must come from some configuration file
          
         // TODO: account oid must come from controller configuration (account controller is linked to)

         cr = new ClientResource("http://54.195.247.29/openremote-messaging-0.1.0/v1/accounts/123/EMailMessages");
         cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
         Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(new EMailMessage(toEmail, subject, message, fromEmail, fromName)));
         cr.post(rep);
         
         // TODO: retrieve return value and log if error
         
       } finally {
         if (cr != null) {
           cr.release();
         }
       }
   }
   
   // For now, have our own version of the "payload" class.
   // This is a good candidate to share in an object model with the backend service.

   // For now, have our own version of the "payload" class.
   // This is a good candidate to share in an object model with the backend
   // service.

   private class BaseMessage
   {
     protected List<String> recipients;
     protected String message;

     @SuppressWarnings("unused")
     public List<String> getRecipients()
     {
       return recipients;
     }

     @SuppressWarnings("unused")
     public String getMessage()
     {
       return message;
     }

   }

   private class SMSMessage extends BaseMessage
   {
     public SMSMessage(String recipient, String message)
     {
       this.recipients = new ArrayList<String>();
       this.recipients.add(recipient);
       this.message = message;
     }
   }

   private class EMailMessage extends BaseMessage
   {
     private String subject;
     private From from;

     public EMailMessage(String recipient, String subject, String message, String fromEmail, String fromName)
     {
       this.recipients = new ArrayList<String>();
       this.recipients.add(recipient);
       this.subject = subject;
       this.message = message;
       this.from = new From(fromEmail, fromName);
     }

     @SuppressWarnings("unused")
     public String getSubject()
     {
       return subject;
     }

     @SuppressWarnings("unused")
     public From getFrom()
     {
       return from;
     }

   }
   
   private class From
   {
     String email;
     String name;

     public From(String email, String name)
     {
       super();
       this.email = email;
       this.name = name;
     }

     @SuppressWarnings("unused")
     public String getEmail()
     {
       return email;
     }

     @SuppressWarnings("unused")
     public String getName()
     {
       return name;
     }

   }
}
