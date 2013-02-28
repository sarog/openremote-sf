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
package org.openremote.controller.control.protocol.infrared;

import junit.framework.Assert;
import static org.junit.Assert.fail;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.exception.CommandBuildException;
import org.openremote.controller.protocol.infrared.IRCommand;
import org.openremote.controller.protocol.infrared.IRCommandBuilder;
/**
 * 
 * @author Javen
 *
 */
public class IRCommandBuilderTest {
   private IRCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new IRCommandBuilder();
   }

   @Test
   public void testRightIRCommandBuilder() {
      IRCommand cmd = getCommand("testName", "VL++");

      Assert.assertEquals(cmd.getName(), "testName");
      Assert.assertEquals(cmd.getCommand(), "VL++");
   }

   @Test
   public void testBuildWithInvalidName() {
      try {
         getCommand("   ", "VL++");
         fail();
      } catch (CommandBuildException e) {
      }
   }

   @Test
   public void testBuildWithInvalidValue() {
      try {
         getCommand("testName", " ");
         fail();
      } catch (CommandBuildException e) {
      }
   }

   private IRCommand getCommand(String name, String value) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocal", "ir");
      ele.setAttribute("value", value);

      Element propName = new Element("property");
      propName.setAttribute("name", "name");
      propName.setAttribute("value", name);

      ele.addContent(propName);
      return (IRCommand) builder.build(ele);
   }
}
