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
package org.openremote.controller.protocol.upnp;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.UPnP;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.jdom.Element;

/**
 * UPnPCommandBuilder is responsible for parsing the XML model from controller.xml and create
 * appropriate Command objects for it. <p>
 *
 * The structure of a UPnP command XML snippet from controller.xml is shown below:
 *
 * <pre>{@code
 * <command protocol = "upnp" >
 *   <property name = "device" value = "xxx"/>
 *   <property name = "action" value = "yyy"/>
 *   <property name = "argname1" value = "..."/>
 *   <property name = "argname2" value = "..."/>
 *   <property name = "argnameX" value = "..."/>
 * </command>
 * }</pre>
 *
 * Properties 'device' and 'action' are mandatory. Any other property name is considered
 * an UPnP Event argument.
 *
 * @author Mathieu Gallissot
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UPnPCommandBuilder implements CommandBuilder
{

  /*
   * NOTE:
   *
   *   UPnP implementation has not been tested completely.
   *                                                          [JPL]
   */


	private ControlPoint controlPoint;

	/**
	 * Constructor of the UPnP Event. It initialize the UPnP stack by :
   *
	 * <ul>
	 * <li>Initializing a proper parser (UPnPParser.class) and setting this
	 * parser for the stack</li>
	 * <li>Initializing the UPnP control point and starting it. It will start
	 * the discovery of compatible devices on the network</li>
	 * </ul>
   *
	 * No other configuration parameters are handled here.
	 */
	public UPnPCommandBuilder()
  {
		UPnP.setXMLParser(new UPnPParser());
		this.controlPoint = new ControlPoint();
		this.controlPoint.start();
	}

  /**
   * Parses the UPnP command XML snippets and builds a corresponding UPnP command instance.  <p>
   *
   * The expected XML structure is:
   *
   * <pre>{@code
   * <command protocol = "upnp" >
   *   <property name = "device" value = "xxx"/>
   *   <property name = "action" value = "yyy"/>
   *   <property name = "argname1" value = "..."/>
   *   <property name = "argname2" value = "..."/>
   *   <property name = "argnameX" value = "..."/>
   * </command>
   * }</pre>
   *
   * Properties other than 'device' and 'action' are passed on as UPnP event arguments.
   *
   * @see UPnPEvent
   * @see org.openremote.controller.command.CommandBuilder#build(org.w3c.dom.Element)
   *
   * @throws org.openremote.controller.exception.NoSuchCommandException
   *            if the UPnP command instance cannot be constructed from the XML snippet
   *            for any reason
   *
   * @return an immutable UPnP command instance with configured properties set
   */
	public Command build(Element element)
  {

    final String UPNP_PROPERTY_DEVICE = "device";
    final String UPNP_PROPERTY_ACTION = "action";


    String upnpDevice = null;
    String upnpAction = null;
    Map<String, String> upnpEventArguments = new HashMap<String, String>(3);


    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY, element.getNamespace());

    for (Element el : propertyElements)
    {
      String upnpPropertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String upnpPropertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

      if (UPNP_PROPERTY_DEVICE.equalsIgnoreCase(upnpPropertyName))
      {
        upnpDevice = upnpPropertyValue;
      }
      else if (UPNP_PROPERTY_ACTION.equalsIgnoreCase(upnpPropertyName))
      {
        upnpAction = upnpPropertyValue;
      }
      else
      {
        upnpEventArguments.put(upnpPropertyName, upnpPropertyValue);
      }
    }

		return new UPnPEvent(this.controlPoint, upnpDevice, upnpAction, upnpEventArguments);
	}

}
