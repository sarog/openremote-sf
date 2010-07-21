package org.openremote.controller.protocol.test.mockup;


import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.http.HttpGetCommand;
import org.openremote.controller.protocol.test.mockup.MockupCommandBuilder;

public class MockupCommandBuilderTest {
   private MockupCommandBuilder builder = null;

   @Before
   public void setUp() throws Exception {
      builder = new MockupCommandBuilder();
   }
   
   private Command getMockupCommand(String name,String url){
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
      Command cmd = getMockupCommand("finalist","http://www.finalist.cn");
      Assert.assertTrue(cmd instanceof HttpGetCommand);
      HttpGetCommand httpCmd = (HttpGetCommand)cmd;
      Assert.assertEquals("finalist", httpCmd.getName());
      Assert.assertEquals("http://www.finalist.cn", httpCmd.getUrl());
   }

}
