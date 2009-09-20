/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import java.util.List;

import junit.framework.TestCase;

import org.openremote.controller.event.Event;
import org.openremote.controller.event.RemoteActionXMLParser;
import org.openremote.controller.protocol.infrared.IREvent;
import org.openremote.controller.spring.SpringContext;


/**
 * The Class RemoteActionXMLParserTest.
 * 
 * @author Dan 2009-4-3
 */
public class RemoteActionXMLParserTest extends TestCase {
   
   /** The remote action xml parser. */
   private RemoteActionXMLParser remoteActionXMLParser = (RemoteActionXMLParser) SpringContext.getInstance().getBean(
         "remoteActionXMLParser");

   /**
    * Test find ir event by button id.
    */
   public void testFindIREventByButtonID(){
//      List<Event> list= remoteActionXMLParser.findEventsByButtonID("8");
//      System.out.println(((IREvent)list.get(0)).getName());
//      assertEquals(1, list.size());
   }
   
}
