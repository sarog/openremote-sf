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
package org.openremote.controller.control.protocol.socket;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.socket.TCPSocketCommand;
import org.openremote.controller.protocol.socket.TCPSocketCommandBuilder;
/**
 * 
 * @author Javen
 *
 */
public class TCPSocketCommandBuilderTest {
   private TCPSocketCommandBuilder builder = null;

   @Before
   public void setUp() {
      builder = new TCPSocketCommandBuilder();
   }

   @Test
   public void testSocketCommandBuilder() {
      TCPSocketCommand cmd = getCommand("192.168.0.1", 9090);

      Assert.assertEquals(cmd.getIp(), "192.168.0.1");
      Assert.assertEquals(cmd.getPort(), "9090");
      Assert.assertEquals(cmd.getCommand(), "test");
      Assert.assertEquals(cmd.getName(), "testName");
   }

   private TCPSocketCommand getCommand(String address, int port) {
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocal", "tcpSocket");
      ele.setAttribute("value", "test");

      Element propName = new Element("property");
      propName.setAttribute("name", "name");
      propName.setAttribute("value", "testName");

      Element propAddr = new Element("property");
      propAddr.setAttribute("name", "ipAddress");
      propAddr.setAttribute("value", address);

      Element propPort = new Element("property");
      propPort.setAttribute("name", "port");
      propPort.setAttribute("value", port + "");

      ele.addContent(propName);
      ele.addContent(propAddr);
      ele.addContent(propPort);

      return (TCPSocketCommand) builder.build(ele);
   }
}
