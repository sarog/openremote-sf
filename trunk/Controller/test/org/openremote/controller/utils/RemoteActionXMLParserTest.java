package org.openremote.controller.utils;

import java.util.List;

import junit.framework.TestCase;

import org.openremote.controller.domain.Event;
import org.openremote.controller.spring.SpringContext;

public class RemoteActionXMLParserTest extends TestCase {
   private RemoteActionXMLParser remoteActionXMLParser = (RemoteActionXMLParser) SpringContext.getInstance().getBean(
         "remoteActionXMLParser");

   public void testFindIREventByButtonID(){
      List<Event> events= remoteActionXMLParser.findEventsByButtonID("4");
      assertEquals(1, events.size());
   }
   
}
