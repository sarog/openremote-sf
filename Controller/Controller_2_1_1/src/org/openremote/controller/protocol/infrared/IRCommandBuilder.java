/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.infrared;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.utils.CommandUtil;


/**
 * The IREvent Builder which can build a IREvent from a DOM Element in controller.xml.
 * 
 * @author Dan 2009-4-3
 */
public class IRCommandBuilder implements CommandBuilder
{

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override public Command build(Element element)
  {
    IRCommand irCommand = new IRCommand();
    String command = "";
    List<Element> propertyEles = element.getChildren("property", element.getNamespace());
    String remotename = "";

    int namesFound = 0;

    for(Element ele : propertyEles)
    {
      if("remotename".equals(ele.getAttributeValue("name")))
      {
        remotename = ele.getAttributeValue("value");
      }

      else if ("command".equals(ele.getAttributeValue("name")))
      {
        command = ele.getAttributeValue("value");
      }

      else if ("name".equals(ele.getAttributeValue("name")))
      {
        namesFound++;

        if (namesFound > 1)
        {
          System.out.println(
              "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n" +
              "  Your configuration contains an incompatible LIRC \n" +
              "  command definition. Please contact us to have your\n" +
              "  account data updated.\n" +
              "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n"
          );
        }
      }
    }

    if ("".equals(command.trim()) || "".equals(remotename.trim()))
    {
        throw new CommandBuildException(
            "Cannot build a IREvent with empty property : command=" +  command +
            ",remotename=" + remotename
        );
    }

    else
    {
       irCommand.setCommand(CommandUtil.parseStringWithParam(element, command));
       irCommand.setRemotename(remotename);
    }

    return irCommand;
  }

}

