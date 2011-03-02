/* 
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway;

import java.util.Properties;

import org.jdom.Element;
import org.openremote.controller.exception.ProtocolBuildException;
import org.openremote.controller.exception.NoSuchProtocolBuilderException;
import org.springframework.context.support.ApplicationObjectSupport;


/**
 * A factory for creating protocol objects.
 * 
 * @author Rich Turner 2011-02-11
 */
public class ProtocolFactory extends ApplicationObjectSupport
{
   
   /** The protocol builders. */
   private Properties protocolBuilders;
   
   /**
    * Gets the command.
    * 
    * @param element the element
    * 
    * @return the protocol
    */
   public Protocol getProtocol(Element element)
   {
      if (element == null)
      {
         throw new ProtocolBuildException("Protocol Builder: Invalid Gateway DOM element.");
      }

      String protocolType = element.getAttributeValue(ProtocolBuilder.PROTOCOL_ATTRIBUTE_NAME);

      if (protocolType == null || "".equals(protocolType))
      {
         throw new ProtocolBuildException("Protocol type is null.");
      }

      String builder = protocolBuilders.getProperty(protocolType);

      if (builder == null)
      {
         throw new NoSuchProtocolBuilderException("Cannot find '" + protocolType + "Builder' by '" + protocolType + "' protocol.");
      }

      ProtocolBuilder protocolBuilder = (ProtocolBuilder) getApplicationContext().getBean(builder);

      return protocolBuilder.build(element);
   }

   /**
    * Sets the protocol builders.
    * 
    * @param protocolBuilders the new protocol builders
    */
   public void setProtocolBuilders(Properties protocolBuilders)
   {
      this.protocolBuilders = protocolBuilders;
   }
}