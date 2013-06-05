/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.utils.Logger;

import com.sun.mail.smtp.SMTPTransport;

public class EmailCommand implements ExecutableCommand  {
   
   // Constants ------------------------------------------------------------------------------------
   
   public final static String EMAIL_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "email";
   
   private final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(EMAIL_PROTOCOL_LOG_CATEGORY);
   
   // Instance Fields
   // ----------------------------------------------------------

   private String recipient;
   private String subject;
   private String message;
   
// Implements ExecutableCommand ---------------------------------------------------------------------
   
   public EmailCommand(String recipient, String subject, String message) {
      this.recipient = recipient;;
      this.subject = subject;
      this.message = message;
   }
   
   @Override
   public void send() {
      String username = "lawrie.griffiths";
      String password = "password";

      Properties props = System.getProperties();
      props.setProperty("mail.smtps.host", "smtp.gmail.com");
      props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
      props.setProperty("mail.smtp.socketFactory.fallback", "false");
      props.setProperty("mail.smtp.port", "465");
      props.setProperty("mail.smtp.socketFactory.port", "465");
      props.setProperty("mail.smtps.auth", "true");
      props.setProperty("mail.smtps.quitwait", "false");

      Session session = Session.getInstance(props, null);
      MimeMessage msg = new MimeMessage(session);

      try {
         msg.setFrom(new InternetAddress(username + "@gmail.com"));
         msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));
         msg.setSubject(subject);
         msg.setText(message, "utf-8");
         msg.setSentDate(new Date());

         SMTPTransport tr = (SMTPTransport)session.getTransport("smtps");

         tr.connect("smtp.gmail.com", username, password);
         tr.sendMessage(msg, msg.getAllRecipients());      
         tr.close();
      } catch (AddressException e) {
         logger.error("Email: Invalid address");
      } catch (MessagingException e) {
         logger.error("Email: Messaging exception");
      }
   }  
}
