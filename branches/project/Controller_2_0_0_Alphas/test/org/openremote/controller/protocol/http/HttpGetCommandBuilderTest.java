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
package org.openremote.controller.protocol.http;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.http.HttpGetCommand;
import org.openremote.controller.protocol.http.HttpGetCommandBuilder;
/**
 * HttpGetCommandBuilder Test
 * 
 * @author Javen
 *
 */
public class HttpGetCommandBuilderTest {
   private HttpGetCommandBuilder builder = null;
   @Before
   public void setUp(){
      builder = new HttpGetCommandBuilder();
   }
   
   private Command getHttpCommand(String name,String url){
      Element ele = new Element("command");
      ele.setAttribute("id", "test");
      ele.setAttribute("protocol","httpGet");
      ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");
      
      Element propName = new Element("property");
      propName.setAttribute("name","name");
      propName.setAttribute("value",name);
      
      Element propUrl = new Element("property");
      propUrl.setAttribute("name","url");
      propUrl.setAttribute("value",url);
      
      ele.addContent(propName);
      ele.addContent(propUrl);
      
      return builder.build(ele);
   }
   @Test
   public void testHasNameAndUrl(){
      Command cmd = getHttpCommand("finalist","http://www.finalist.cn");
      Assert.assertTrue(cmd instanceof HttpGetCommand);
      HttpGetCommand httpCmd = (HttpGetCommand)cmd;
      Assert.assertEquals("finalist", httpCmd.getName());
      Assert.assertEquals("http://www.finalist.cn", httpCmd.getUrl());
   }
   
   @Test
   public void testHasNameAndUrlWithParam(){
      Command cmd = getHttpCommand("finalist","http://www.finalist.cn?command=light1_${param}");
      Assert.assertTrue(cmd instanceof HttpGetCommand);
      HttpGetCommand httpCmd = (HttpGetCommand)cmd;
      Assert.assertEquals("finalist", httpCmd.getName());
      Assert.assertEquals("http://www.finalist.cn?command=light1_255", httpCmd.getUrl());
   }
   
}
