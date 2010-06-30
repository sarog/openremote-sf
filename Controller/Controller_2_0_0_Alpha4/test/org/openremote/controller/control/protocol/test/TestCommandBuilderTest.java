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
package org.openremote.controller.control.protocol.test;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.test.TestCommandBuilder;

/**
 * 
 * @author Javen
 *
 */
public class TestCommandBuilderTest {
   private TestCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new TestCommandBuilder();
   }

   @Test
   public void testOn() {
      Command cmd = getCommand("ON");
      Assert.assertTrue(cmd instanceof ExecutableCommand);
   }

   @Test
   public void testOFF() {
      Command cmd = getCommand("OFF");
      Assert.assertTrue(cmd instanceof ExecutableCommand);
   }

   @Test
   public void testStatus() {
      Command cmd = getCommand("STATUS");
      Assert.assertTrue(cmd instanceof StatusCommand);
   }

   @Test
   public void testRightNumber() {
      Command cmd = getCommand("12");
      Assert.assertTrue(cmd instanceof ExecutableCommand);
   }

   @Test
   public void testWrongNumber1() {
      try {
         getCommand("-11");
         fail();
      } catch (NoSuchCommandException nse) {
      }
   }

   @Test
   public void testWrongNumber2() {
      try {
         getCommand("-36");
         fail();
      } catch (NoSuchCommandException nse) {
      }
   }

   @Test
   public void testErrorCommand() {
      try {
         getCommand("xxx");
         fail();
      } catch (NumberFormatException nfe) {
      }
   }

   private Command getCommand(String cmdString) {
      Element ele = new Element("command");
      ele.setAttribute("id", "1234");
      ele.setAttribute("protocol", "test");
      ele.setAttribute("value", cmdString);

      return builder.build(ele);
   }
}
