package org.openremote.controller.protocol.russound;

import org.jdom.Element;
import org.openremote.controller.event.Event;
import org.openremote.controller.event.EventBuilder;

/**
 * 
 * @author Marcus Redeker
 *
 */
public class RussoundEventBuilder implements EventBuilder {

    /**
     * {@inheritDoc}
     */
    public Event build(Element element) {
       RussoundEvent russoundEvent = new RussoundEvent();
       russoundEvent.setCommand(element.getAttributeValue("command"));
       return russoundEvent;
    }

}
