/**
 * 
 */
package org.openremote.controller.protocol.upnp;

import java.util.HashMap;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.UPnP;
import org.openremote.controller.event.Event;
import org.openremote.controller.event.EventBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * UPnPEventBuilder class
 * @author Mathieu Gallissot
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
		String device = element.getAttribute("device");
		String action = element.getAttribute("action");
		HashMap<String, String> args = new HashMap<String, String>();
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node child = element.getChildNodes().item(i);
			if (child.getNodeName().equals("argument")) {
				args.put(child.getAttributes().getNamedItem("name")
						.getNodeValue(), child.getAttributes().getNamedItem(
						"value").getNodeValue());
			}
		}

		return new UPnPEvent(this.controlPoint, device, action, args);
	}

}
