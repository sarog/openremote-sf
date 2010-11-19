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
package org.openremote.android.controller.command;

import java.util.Hashtable;

import org.w3c.dom.Element;
import org.openremote.android.controller.protocol.socket.TCPSocketCommandBuilder;
//import org.openremote.controller.exception.CommandBuildException;
//import org.openremote.controller.exception.NoSuchCommandBuilderException;
//import org.springframework.context.support.ApplicationObjectSupport;


/**
 * A factory for creating Command objects.
 * 
 * @author Handy.Wang 2009-10-13
 */
public class CommandFactory
{
   
   /** The command builders. */
   private Hashtable<String, CommandBuilder> commandBuilders;
   
   
   public CommandFactory()
   {
	   commandBuilders = new Hashtable();
	   commandBuilders.put("socket",new TCPSocketCommandBuilder());
   }
   /**
    * Gets the command.
    * 
    * @param element the element
    * 
    * @return the command
    */
   public Command getCommand(Element element)
   {
      if (element == null)
      {
         new Exception("Command DOM element is null.").printStackTrace();
      }

      String protocolType = element.getAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME);

      if (protocolType == null || "".equals(protocolType))
      {
         (new Exception("Protocol type is null.")).printStackTrace();
      }

      CommandBuilder builder = commandBuilders.get(protocolType);

      if (builder == null)
      {
         (new Exception("NoSuchCommandBuilderException "+ protocolType)).printStackTrace();
      }
           
      return builder.build(element);
   }
}
