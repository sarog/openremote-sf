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
package org.openremote.controller.control.protocol.knx;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.knx.KNXCommand;
import org.openremote.controller.protocol.knx.KNXCommandBuilder;
import org.openremote.controller.protocol.knx.KNXCommandType;

/**
 * Basic unit tests for parsing XML elements in
 * {@link org.openremote.controller.protocol.knx.KNXCommandBuilder}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen
 *
 */
public class KNXCommandBuilderTest
{

  // Test Setup -----------------------------------------------------------------------------------

  private KNXCommandBuilder builder = null;

  @Before public void setUp() {
    builder = new KNXCommandBuilder();
  }


  // Tests ----------------------------------------------------------------------------------------

  /**
   * Test KNX command parsing with "ON" as the command string and 1/1/1 as group address.
   */
  @Test public void testKNXOn() 
  {
    Command cmd = getCommand("ON", "1/1/1");

    assertTrue(cmd instanceof KNXCommand);
  }

  /**
   * Test KNX command parsing with "on", "On" and "oN" as the command string and 1/1/1 as
   * group address.
   */
  @Test public void testKNXOnMixedCase()
  {
    Command cmd1 = getCommand("on", "1/1/1");

    assertTrue(cmd1 instanceof KNXCommand);

    Command cmd2 = getCommand("On", "1/1/1");

    assertTrue(cmd2 instanceof KNXCommand);

    Command cmd3 = getCommand("oN", "1/1/1");

    assertTrue(cmd3 instanceof KNXCommand);
  }



  @Test public void testKNXOff()
  {
    Command cmd = getCommand("OFF", "testGroupAddress");
    assertTrue(cmd instanceof KNXCommand);
  }

   @Test
   public void testKNXStatus() {
      Command cmd = getCommand("STATUS", "testGroupAddress");
      assertTrue(cmd instanceof KNXCommand);
   }

   @Test
   public void testNoSuchCommand() {
      try {
         getCommand(" ", "testGroupAddress");
         fail();
      } catch (NoSuchCommandException e) {
      }
   }


  // Helpers --------------------------------------------------------------------------------------
  
   private Command getCommand(String cmd, String groupAddress) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocol", "knx");
      //ele.setAttribute("value", cmd);

      Element propAddr = new Element("property");
      propAddr.setAttribute("name", KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
      propAddr.setAttribute("value", groupAddress);

      ele.addContent(propAddr);

      Element propAddr2 = new Element("property");
      propAddr2.setAttribute("name", "command");
      propAddr2.setAttribute("value", cmd);

      ele.addContent(propAddr2);

      return builder.build(ele);
   }
}
