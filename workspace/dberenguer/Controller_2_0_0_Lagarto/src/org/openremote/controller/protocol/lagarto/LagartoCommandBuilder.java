/**
 * Copyright (c) 2012 Daniel Berenguer <dberenguer@usapiens.com>
 *
 * This file is part of the lagarto project.
 *
 * lagarto  is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * lagarto is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panLoader; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 *  @author Daniel Berenguer
 *  @date   2012-09-13
 */

package org.openremote.controller.protocol.lagarto;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.CommandUtil;


/**
 * CommandBuilder subclass for Lagarto command
 */
public class LagartoCommandBuilder implements CommandBuilder
{

  public final static String LAGARTO_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "lagarto";
  private final static Logger logger = Logger.getLogger(LAGARTO_PROTOCOL_LOG_CATEGORY);


  /**
   * Class constructor
   */
  public LagartoCommandBuilder()
  {
    logger.debug("First try to log something");
  }

  /**
  * {@inheritDoc}
  */
  @SuppressWarnings("unchecked")
  public Command build(Element element)
  {
    logger.debug("Second try to log something");
    List<Element> propertyEles = element.getChildren("property", element.getNamespace());
    String networkName = null;
    String epId = null;
    String epValue = null;

    // read values from config xml
    for (Element ele : propertyEles)
    {
      String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
      String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);
      if ("network".equals(elementName))
        networkName = elementValue;
      else if ("epid".equals(elementName))
        epId = elementValue;
      else if ("value".equals(elementName))
        epValue = CommandUtil.parseStringWithParam(element, elementValue);
    }

    LagartoCommand cmd = new LagartoCommand(networkName, epId, epValue);

    return cmd;
  }
}
