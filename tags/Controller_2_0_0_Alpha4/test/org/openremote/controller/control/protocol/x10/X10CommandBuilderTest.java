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
package org.openremote.controller.control.protocol.x10;

import static junit.framework.Assert.*;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.protocol.x10.X10Command;
import org.openremote.controller.protocol.x10.X10CommandBuilder;

/**
 * X10CommandBuilder Test
 * 
 * @author Dan Cong
 *
 */
public class X10CommandBuilderTest {
   public X10CommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new X10CommandBuilder();
   }

   @Test
   public void testGetCommandByRightCmdAndAddress() {
      X10Command cmd = getCommand("testCmd", "127.0.0.1");

      assertEquals("127.0.0.1", cmd.getAddress());
      assertEquals("testCmd", cmd.getCommand());
   }
   
   @Test
   public void testGetCommandByCmdAndAddressWithParam() {
      X10Command cmd = getCommand("light1_${param}", "127.0.0.1");
      
      assertEquals("127.0.0.1", cmd.getAddress());
      assertEquals("light1_255", cmd.getCommand());
   }

   public void testGetCommandByWrongCmd() {
      try {
         getCommand("  ", "127.0.0.1");
         fail();
      } catch (CommandBuildException e) {
      };
   }

   public void testGetCommandByWrongAddress() {
      try {
         getCommand("my command ", "  ");
         fail();
      } catch (CommandBuildException e) {
      };
   }

   public X10Command getCommand(String cmd, String address) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocol", "x10");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

      Element propAddr = new Element("property");
      propAddr.setAttribute("name", "address");
      propAddr.setAttribute("value", address);
      
      Element propCommand = new Element("property");
      propCommand.setAttribute("name", "command");
      propCommand.setAttribute("value", cmd);

      ele.addContent(propAddr);
      ele.addContent(propCommand);

      return (X10Command) builder.build(ele);
   }
}
