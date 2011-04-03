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
package org.openremote.controller.protocol.meta;

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.service.ControlCommandService;
import org.jdom.Element;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class MetaCommandBuilder implements CommandBuilder
{

  private static int currentIndex = 0;
  private final static Boolean lock = true;

  public static int shiftAndReturnCurrentIndex(boolean next)
  {
    synchronized (lock)
    {
      if (currentIndex == 0)
        currentIndex = 1;
      else
        currentIndex = 0;

      return currentIndex;
    }
  }

  public static int getCurrentIndex()
  {
    synchronized (lock)
    {
      return currentIndex;
    }
  }


  private ControlCommandService ccs;

  public void setControlCommandService(ControlCommandService ccs)
  {
    this.ccs = ccs;
  }


  public Command build(Element element)
  {
    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

    String commandString = "<undefined>";
    String knxid = "-999";
    String plcid = "-999";

    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

      if ("command".equalsIgnoreCase(propertyName))
      {
        commandString = propertyValue;
      }

      else if ("knxid".equalsIgnoreCase(propertyName))
      {
        knxid = propertyValue.trim();
      }

      else if ("plcid".equalsIgnoreCase(propertyName))
      {
        plcid = propertyValue.trim();
      }
    }


    MetaCommand cmd = new MetaCommand(ccs, commandString, plcid, knxid);
/*
    cmd.setKNXOn();
    cmd.setKNXOff();
    cmd.setPLCOn();
    cmd.setPLCOff();
*/
    return cmd;
  }
}

