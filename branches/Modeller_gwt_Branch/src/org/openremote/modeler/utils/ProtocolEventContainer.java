package org.openremote.modeler.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.UIButtonEvent;
import org.openremote.modeler.protocol.ProtocolContainer;
import org.openremote.modeler.service.ProtocolParser;

public class ProtocolEventContainer {
   
   private Map<String, List<UIButtonEvent>> protocolEvents = new HashMap<String, List<UIButtonEvent>>();
   
   public ProtocolEventContainer() {
      if (ProtocolContainer.getInstance().getProtocols().size() == 0) {
         ProtocolParser parser = new ProtocolParser();
         ProtocolContainer.getInstance().setProtocols(parser.parseXmls());
      }      
      Set<String> protocolTypes = ProtocolContainer.getInstance().getProtocols().keySet();
      for (String protocolType : protocolTypes) {
         protocolEvents.put(protocolType, new ArrayList<UIButtonEvent>());
      }
   }
   
   public void addUIButtonEvent(UIButtonEvent uiButtonEvent) {
      Set<String> protocolTypes = protocolEvents.keySet();
      for (String protocolType : protocolTypes) {
         if (protocolType.equals(uiButtonEvent.getType())) {
            List<UIButtonEvent> uiButtonEvents = protocolEvents.get(protocolType);
            for (UIButtonEvent uiBtnEvent : uiButtonEvents) {
               Map<String, String> protocolAttrs = uiBtnEvent.getProtocolAttrs();
               Set<String> protocolAttrKeySet = protocolAttrs.keySet();
               boolean flag = true; // find the same uiButtonEvent?
               for (String key : protocolAttrKeySet) {
                  if (!protocolAttrs.get(key).equals(uiButtonEvent.getProtocolAttrs().get(key))) {
                     flag = false;
                     break;
                  }
               }
               if (flag) {
                  uiButtonEvent.setId(uiBtnEvent.getId());
                  return;
               }               
            }
            protocolEvents.get(protocolType).add(uiButtonEvent);
         }
      }
   }
   
   public Map<String, List<UIButtonEvent>> getProtocolEvents () {
      return protocolEvents;
   }
   
   public List<UIButtonEvent> getUIButtonEvents(String protocolType) {
      if (protocolEvents.containsKey(protocolType)) {
         return protocolEvents.get(protocolType);
      } else {
         return new ArrayList<UIButtonEvent>();
      }
   }
   
   public String generateUIButtonEventsXml () {
      StringBuffer uiButtonEventXml = new StringBuffer();
      Set<String> protocolTypes = protocolEvents.keySet();
      uiButtonEventXml.append("  <events>\n");
      for (String protocolType : protocolTypes) {
         String eventsTagName = (Constants.INFRARED_TYPE.equals(protocolType)) ? "irEvents" : protocolType + "Events";
         uiButtonEventXml.append("    <" + eventsTagName + ">\n");
         for(UIButtonEvent uiButtonEvent : protocolEvents.get(protocolType)) {
            String eventTagName = (Constants.INFRARED_TYPE.equals(protocolType)) ? "irEvent" : protocolType + "Event";
            uiButtonEventXml.append("      <" + eventTagName + " id=\"" + uiButtonEvent.getId() + "\"");
            Set<String> protocolAttrKeySet = uiButtonEvent.getProtocolAttrs().keySet();
            for (String attrKey : protocolAttrKeySet) {
               uiButtonEventXml.append(" " + attrKey + "=\"" + uiButtonEvent.getProtocolAttrs().get(attrKey) + "\"");
            }
            uiButtonEventXml.append(" />\n");
         }
         uiButtonEventXml.append("    </" + eventsTagName + ">\n");
      }
      uiButtonEventXml.append("</events>\n");
      return uiButtonEventXml.toString();
   }
   
   public void clear() {
      protocolEvents.clear();
   }
}
