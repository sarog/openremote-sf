/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
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

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.utils.Logger;

/**
 * CommandBuilder for sending email.
 * 
 * @author Lawrie Griffiths
 *
 */
public class EmailCommandBuilder implements CommandBuilder {

   // Constants ------------------------------------------------------------------------------------
   public final static String EMAIL_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "datetime";

   private final static String STR_ATTRIBUTE_NAME_RECIPIENT = "recipient";
   private final static String STR_ATTRIBUTE_NAME_SUBJECT = "subject";
   private final static String STR_ATTRIBUTE_NAME_MESSAGE = "message";
   
   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(EMAIL_PROTOCOL_LOG_CATEGORY);
   
   // Instance Fields ------------------------------------------------------------------------------
   
   private String username;
   private String password;
   private String domain;
   private String protocol;
   private String charset;
   private String host;
   private String factoryClass;
   private String fallback;
   private String port;
   private String factoryPort;
   private String auth;
   private String quitwait;
   
   
   // Constructors ---------------------------------------------------------------------------------
   
   public EmailCommandBuilder(String username, String password, String domain, String protocol, String charset,
                              String host, String factoryClass, String fallback, String port, String factoryPort,
                              String auth, String quitwait) {
      this.username = username;
      this.password = password;
      this.domain = domain;
      this.protocol = protocol;
      this.charset = charset;
      this.host = host;
      this.factoryClass = factoryClass;
      this.fallback = fallback;
      this.port = port;
      this.factoryPort = factoryPort;
      this.auth = auth;
      this.quitwait = quitwait;
   }

   // Implements CommandBuilder --------------------------------------------------------------------

   @SuppressWarnings("unchecked")
   @Override
   public Command build(Element element) {
      logger.debug("Building Email command");
      List<Element> propertyEles = element.getChildren("property", element.getNamespace());
      
      String recipient = null;
      String subject = null;
      String message = null;
      
      for (Element ele : propertyEles) {
         String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
         String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

         if (STR_ATTRIBUTE_NAME_RECIPIENT.equals(elementName)) {
            recipient = elementValue;
            logger.debug("Email command: recipient = " + recipient);
         }
         
         if (STR_ATTRIBUTE_NAME_SUBJECT.equals(elementName)) {
            subject = elementValue;
            logger.debug("Email command: recipient = " + subject);
         }
         
         if (STR_ATTRIBUTE_NAME_MESSAGE.equals(elementName)) {
            message = elementValue;
            logger.debug("Email command: recipient = " + message);
         }
       }
      
      logger.debug("Email command created successfully");
      return new EmailCommand(recipient, subject, message, username, password, domain, protocol, charset,
                              host, factoryClass, fallback, port, factoryPort, auth, quitwait);
   }

}
