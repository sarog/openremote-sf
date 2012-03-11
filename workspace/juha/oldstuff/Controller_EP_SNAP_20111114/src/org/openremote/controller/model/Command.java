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
package org.openremote.controller.model;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.openremote.controller.service.ControlCommandService;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.protocol.ReadCommand;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Command
{

  private String protocolType;
  private int id;
  private Map<String, String> properties;
  private CommandFactory commandFactory;
  private Element commandElement;

  public Command(CommandFactory commandFactory, int id, String type,
                 Map<String, String> properties, Element element)
  {
    this.id = id;
    this.protocolType = type;
    this.properties = properties;

    this.commandFactory = commandFactory;

    this.commandElement = element;
  }



  public String getType()
  {
    return protocolType;
  }

  public int getID()
  {
    return id;
  }

  public String getName()
  {
    String name = properties.get("name");

    if (name == null || name.equals(""))
    {
      return "<no name>";
    }

    return name;
  }

  public Iterator<String> getPropertyNames()
  {
    return properties.keySet().iterator();
  }

  public String getProperty(String name)
  {
    return properties.get(name);
  }

  public void execute()
  {


    try
    {
      org.openremote.controller.command.Command cmd = commandFactory.getCommand(commandElement);

      System.out.println("\n\n\n"+cmd);



      if (cmd instanceof ExecutableCommand)
      {
        ExecutableCommand writeCommand = (ExecutableCommand)cmd;

        writeCommand.send();
      }

      else
      {
        // TODO log
      }
    }

    catch (ConfigurationException e)
    {
      e.printStackTrace();    // todo
    }

    catch (Throwable t)
    {
      t.printStackTrace();    // todo
    }
  }

  @Override public String toString()
  {
    XMLOutputter output = new XMLOutputter();

    return "Command( ID = " + id + ", Type = " + protocolType +
           ", Properties : " + properties + ")\n" + output.outputString(commandElement);
  }
}

