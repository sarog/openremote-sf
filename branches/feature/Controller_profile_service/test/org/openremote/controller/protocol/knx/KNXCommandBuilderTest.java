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
package org.openremote.controller.protocol.knx;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Javen
 *
 */
public class KNXCommandBuilderTest {
   
   private KNXCommandBuilder builder ;
   @Before
   public void setUp(){
      builder = new KNXCommandBuilder();
   }
   @Test
   public void testKNXON(){
      KNXCommand cmd = getCommand("192.168.1.1","ON");
      
      Assert.assertEquals(cmd.getGroupAddress(), "192.168.1.1");
      Assert.assertEquals(cmd.getKnxCommandType(), KNXCommandType.SWITCH_ON);
   }
   public KNXCommand getCommand(String ipAddr,String value){
      Element ele = new Element("command");
      ele.setAttribute("id","testId");
      ele.setAttribute("name","testName");
      ele.setAttribute("protocal","knx");
      
      
      ele.setAttribute("value",value);
      
      Element propAddr = new Element("property");
      propAddr.setAttribute("name","groupAddress");
      propAddr.setAttribute("value",ipAddr);
      
      ele.addContent(propAddr);
      
      return (KNXCommand) builder.build(ele);
   }
}
