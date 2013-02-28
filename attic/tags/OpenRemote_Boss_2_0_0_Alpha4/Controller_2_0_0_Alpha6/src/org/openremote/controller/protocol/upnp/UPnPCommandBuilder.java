/* OpenRemote, the Home of the Digital Home.
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
 * UPnPEventBuilder class
 *
 * @author Mathieu Gallissot
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UPnPCommandBuilder implements CommandBuilder {

	private ControlPoint controlPoint;

	/**
	 * Constructor of the UPnP Event. It initialize the UPnP stack by :
	 * <ul>
	 * <li>Initializing a proper parser (UPnPParser.class) and setting this
	 * parser for the stack</li>
	 * <li>Initializing the UPnP control point and starting it. It will start
	 * the discovery of compatible devices on the network</li>
	 * </ul>
	 * No other configuration parameters are handled here.
	 */
	public UPnPCommandBuilder() {
		UPnP.setXMLParser(new UPnPParser());
		this.controlPoint = new ControlPoint();
		this.controlPoint.start();
	}

  /*
    * (non-Javadoc)
    *
    * @see
    * org.openremote.controller.event.EventBuilder#build(org.w3c.dom.Element)
    */
	@Override
	public Command build(Element element) {

    /*
     * NOTE:
     *    ported the API from Boss 1.0 to Boss 2.0 which changes the XML schema.
     *
     *    An example UPnP configuration in controller.xml looks like this:
     *
     *    <command protocol = "upnp" >
     *      <property name = "device" value = "xxx"/>
     *      <property name = "action" value = "yyy"/>
     *      <property name = "argname1" value = "..."/>
     *      <property name = "argname2" value = "..."/>
     *      <property name = "argnameX" value = "..."/>
     *    </command>
     *
     * Note that this is structured quite different from 1.0 API. Also, any property name
     * that is *not* "device" or "action" is considered as an UPnP Event argument.
     *
     * Actual implementation is so far untested.
     *                                                          [JPL]
     */

    final String XML_ELEMENT_PROPERTY = "property";

    final String XML_ATTRIBUTENAME_NAME = "name";
    final String XML_ATTRIBUTENAME_VALUE = "value";

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
