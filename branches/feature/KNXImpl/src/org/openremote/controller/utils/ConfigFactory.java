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
package org.openremote.controller.utils;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.openremote.controller.Configuration;
import org.openremote.controller.RoundRobinConfig;
import org.openremote.controller.CustomConfiguration;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.spring.SpringContext;

/**
 * A factory for creating Basic or RoundRobin Configuration objects.
 * 
 * @author Dan 2009-6-9
 */
public class ConfigFactory
{
   




  public static Configuration readControllerConfiguration()
  {
    Map<String, String> attrMap = CustomConfiguration.parseCustomConfigAttrMap();
    Configuration config = CustomConfiguration.getConfig();
    config.setCustomAttrMap(attrMap);
    return config;
  }

   
  public static RoundRobinConfig readRoundRobinConfiguration()
  {
    Map<String, String> attrMap = CustomConfiguration.parseCustomConfigAttrMap();

    RoundRobinConfig config = CustomConfiguration.getRoundRobinConfig();
    config.setCustomAttrMap(attrMap);

    return config;
  }





  public static Map<String, String> pullAllCustomConfigs(Element element)
  {
    Map<String, String> attrMap = new HashMap<String, String>();

    for (Object o : element.getChildren())
    {
      Element e = (Element) o;
      String name = e.getAttributeValue("name");
      String value = e.getAttributeValue("value");
      attrMap.put(name, value);
    }

    return attrMap;
  }


  // Private Class Members ------------------------------------------------------------------------





}
