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
package org.openremote.controller.control.protocol.knx;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.knx.KNXCommandBuilder;
import org.openremote.controller.protocol.knx.KNXCommandType;
import org.openremote.controller.protocol.knx.KNXExecutableCommand;
import org.openremote.controller.protocol.knx.KNXStatusCommand;

/**
 * 
 * @author Javen
 *
 */
public class KNXCommandBuilderTest {
   private KNXCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new KNXCommandBuilder();
   }

   @Test
   public void testKNXOn() {
      Command cmd = getCommand("ON", "testGroupAddress");
      assertTrue(cmd instanceof KNXExecutableCommand);
      KNXExecutableCommand exeCmd = (KNXExecutableCommand) cmd;
      assertEquals(exeCmd.getGroupAddress(), "testGroupAddress");
      assertEquals(exeCmd.getKnxCommandType(), KNXCommandType.SWITCH_ON);
   }

   @Test
   public void testKNXOff() {
      Command cmd = getCommand("OFF", "testGroupAddress");
      assertTrue(cmd instanceof KNXExecutableCommand);
      KNXExecutableCommand exeCmd = (KNXExecutableCommand) cmd;
      assertEquals(exeCmd.getGroupAddress(), "testGroupAddress");
      assertEquals(exeCmd.getKnxCommandType(), KNXCommandType.SWITCH_OFF);
   }

   @Test
   public void testKNXStatus() {
      Command cmd = getCommand("STATUS", "testGroupAddress");
      assertTrue(cmd instanceof KNXStatusCommand);
      KNXStatusCommand exeCmd = (KNXStatusCommand) cmd;
      assertEquals(exeCmd.getGroupAddress(), "testGroupAddress");
      assertEquals(exeCmd.getKnxCommandType(), null);
   }

   @Test
   public void testNoSuchCommand() {
      try {
         getCommand(" ", "testGroupAddress");
         fail();
      } catch (NoSuchCommandException e) {
      }
   }

   private Command getCommand(String cmd, String groupAddress) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocal", "knx");
      ele.setAttribute("value", cmd);

      Element propAddr = new Element("property");
      propAddr.setAttribute("name", KNXCommandBuilder.GROUP_ADDRESS_XML_ATTRIBUTE);
      propAddr.setAttribute("value", groupAddress);

      ele.addContent(propAddr);

      return builder.build(ele);
   }
}
