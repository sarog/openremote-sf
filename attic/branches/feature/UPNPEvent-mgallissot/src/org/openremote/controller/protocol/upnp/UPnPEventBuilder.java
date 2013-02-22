/**
 * 
 */
package org.openremote.controller.protocol.upnp;

import java.util.HashMap;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.UPnP;
import org.openremote.controller.event.Event;
import org.openremote.controller.event.EventBuilder;
import org.jdom.Element;

/**
 * UPnPEventBuilder class
 *
 * @author Mathieu Gallissot
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class UPnPEventBuilder implements EventBuilder {

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
	public UPnPEventBuilder() {
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
	public Event build(Element element) {
		String device = element.getAttributeValue("device");
		String action = element.getAttributeValue("action");
		HashMap<String, String> args = new HashMap<String, String>();
		for (int i = 0; i < element.getChildren().size(); i++) {
			Element child = (Element)element.getChildren().get(i);
			if (child.getName().equals("upnpEventArgument")) {
				args.put(child.getAttributeValue("name"), child.getAttributeValue("value"));
			}
		}

		return new UPnPEvent(this.controlPoint, device, action, args);
	}

}
