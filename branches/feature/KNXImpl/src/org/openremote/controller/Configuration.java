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
package org.openremote.controller;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.command.RemoteActionXMLParser;

/**
 * Custom Configuration accepts a map to prefer custom attribute values by name.
 * 
 * @author Dan Cong
 */
public class Configuration
{


   
  /** custom attributes map, will use these values first, other than default values from config.properties */
  private Map<String, String> customAttrMap = new HashMap<String, String>();



  public static Map<String, String> parseCustomConfigAttrMap()
  {
    Element element = null;

    try
    {
      element = getControllerXMLParser().queryElementFromXMLByName("config");
    }

    catch (Exception e)
    {
      return null;
    }

    return pullAllCustomConfigs(element);
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



  public Map<String, String> getCustomAttrMap()
  {
    return customAttrMap;
  }

  public void setCustomAttrMap(Map<String, String> customAttrMap)
  {
    if (customAttrMap == null)
    {
       return;
    }

    this.customAttrMap = customAttrMap;
  }

  protected String preferAttrCustomValue(String attrName, String defaultValue)
  {
    return customAttrMap.containsKey(attrName) ? customAttrMap.get(attrName) : defaultValue;
  }

  protected boolean preferAttrCustomValue(String attrName, boolean defaultValue)
  {
    return customAttrMap.containsKey(attrName) ? Boolean.valueOf(customAttrMap.get(attrName)) : defaultValue;
  }

  protected int preferAttrCustomValue(String attrName, int defaultValue)
  {
    return customAttrMap.containsKey(attrName) ? Integer.valueOf(customAttrMap.get(attrName)) : defaultValue;
  }

  protected long preferAttrCustomValue(String attrName, long defaultValue)
  {
    return customAttrMap.containsKey(attrName) ? Long.valueOf(customAttrMap.get(attrName)) : defaultValue;
  }

  protected String[] preferAttrCustomValue(String attrName, String[] defaultValue)
  {
    return customAttrMap.containsKey(attrName) ? customAttrMap.get(attrName).split(",") : defaultValue;
  }




  // ----------------------------------------------------------------------------------------------
  //
  // Isolating Spring library references here -- eventually this should be abstracted away
  // with a service interface that is more portable to smaller (Android) runtimes
  //
  // ----------------------------------------------------------------------------------------------

  public static ControllerConfiguration getConfig()
  {
    return (ControllerConfiguration) SpringContext.getInstance().getBean("configuration");
  }

  public static RoundRobinConfiguration getRoundRobinConfig()
  {
    return (RoundRobinConfiguration) SpringContext.getInstance().getBean("roundRobinConfig");
  }

  private static RemoteActionXMLParser getControllerXMLParser()
  {
    return (RemoteActionXMLParser) SpringContext.getInstance().getBean("remoteActionXMLParser");
  }

}
