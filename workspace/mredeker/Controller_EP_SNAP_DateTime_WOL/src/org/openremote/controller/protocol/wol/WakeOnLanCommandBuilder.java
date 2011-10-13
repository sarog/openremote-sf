package org.openremote.controller.protocol.wol;

import java.util.List;

import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.utils.CommandUtil;

/**
 * 
 * @author Marcus Redeker
 */
public class WakeOnLanCommandBuilder implements CommandBuilder {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
   public Command build(Element element) {

		WakeOnLanCommand cmd = new WakeOnLanCommand();

		List<Element> propertyElements = element.getChildren(CommandBuilder.XML_ELEMENT_PROPERTY, element.getNamespace());
		for (Element el : propertyElements) {
			if ("macAddress".equals(el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME))) {
				cmd.setMacAddress(CommandUtil.parseStringWithParam(element, el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE)));
			}
         if ("broadcastIp".equals(el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME))) {
            cmd.setBroadcastIp(CommandUtil.parseStringWithParam(element, el.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE)));
         }
		}
		return cmd;
	}

}
