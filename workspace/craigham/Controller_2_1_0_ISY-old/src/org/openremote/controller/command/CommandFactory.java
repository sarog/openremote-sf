/* 
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.command;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.openremote.controller.exception.ConfigurationException;


/**
 * TODO:
 *
 *   - ORCJAVA-209 : merge command models -- expecting this implementation to be further
 *                   reduced or removed altogether as part of 209 refactoring
 *
 *
 * @author <a href="mailto:juha@openremote.org>Juha Lindfors</a>
 * @author Handy.Wang 2009-10-13
 */
public class CommandFactory
{

  private Map<String, CommandBuilder> commandBuilders = new HashMap<String, CommandBuilder>();


  public CommandFactory(Map<String, CommandBuilder> commandBuilders)
  {
   if (commandBuilders == null)
   {
     return;
   }

   this.commandBuilders = commandBuilders;
  }


  /**
   *
   * @deprecated    Additional use of this method should be avoided -- implementations should
   *                delegate to in-memory object model for commands instead (see
   *                {@link org.openremote.controller.model.Command} and
   *                {@link org.openremote.controller.deployer.Version20CommandBuilder}
   *                implementations). Expecting this method to be refactored/removed as part
   *                of ORCJAVA-209.
   */
  @Deprecated public Command getCommand(Element element) throws ConfigurationException
  {
    if (element == null)
    {
       throw new ConfigurationException("Null reference trying to create a protocol command.");
    }

    String protocolType = element.getAttributeValue(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME);

    if (protocolType == null || protocolType.equals(""))
    {
       throw new ConfigurationException(
           "Protocol attribute is missing in {0}",
           new XMLOutputter().outputString(element)
       );
    }

    CommandBuilder builder = commandBuilders.get(protocolType);

    if (builder == null)
    {
      throw new ConfigurationException(
          "No device protocol builders registered with protocol type ''{0}''.", protocolType
      );
    }

    return builder.build(element);
  }
}
