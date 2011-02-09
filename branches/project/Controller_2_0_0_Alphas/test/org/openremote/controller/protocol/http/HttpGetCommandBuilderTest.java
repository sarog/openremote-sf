/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.component.Sensor;
import org.openremote.controller.protocol.http.HttpGetCommand;
import org.openremote.controller.protocol.http.HttpGetCommandBuilder;

/**
 * HttpGetCommandBuilder Test.
 * 
 * @author Javen
 * @author Dan Cong
 *
 */
public class HttpGetCommandBuilderTest
{
   
  private HttpGetCommandBuilder builder = new HttpGetCommandBuilder();

  @Before public void setUp()
  {

  }

  
  private Command getHttpCommand(String name,String url)
  {
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



  @Test public void testHasNameAndUrl()
  {
    Command cmd = getHttpCommand("finalist","http://www.finalist.cn");
    Assert.assertTrue(cmd instanceof HttpGetCommand);
    HttpGetCommand httpCmd = (HttpGetCommand)cmd;
    Assert.assertEquals("finalist", httpCmd.getName());
    Assert.assertEquals("http://www.finalist.cn", httpCmd.getUrl());
  }


  @Test public void testHasNameAndUrlWithParam()
  {
    Command cmd = getHttpCommand("finalist","http://www.finalist.cn?command=light1_${param}");
    Assert.assertTrue(cmd instanceof HttpGetCommand);
    HttpGetCommand httpCmd = (HttpGetCommand)cmd;
    Assert.assertEquals("finalist", httpCmd.getName());
    Assert.assertEquals("http://www.finalist.cn?command=light1_255", httpCmd.getUrl());
  }



  @Test public void testReadRawStatus() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("on"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");
    Assert.assertEquals("on", cmd.read(null, null));

    server.stop();
  }



  @Test public void testSendCommand() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("on"));
    server.start();

    ExecutableCommand cmd = (ExecutableCommand) getHttpCommand("","http://127.0.0.1:9999");
    try {
       cmd.send();
    } catch (Exception e) {
       fail();//won't go here if succeed
    }

    server.stop();
  }



  @Test public void testReadSwitchStatus() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("on"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");

    Assert.assertEquals("on", cmd.read(EnumSensorType.SWITCH, null));

    server.stop();
  }



  @Test public void testReadRangeRawStatus() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("1"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");

    Assert.assertEquals("1", cmd.read(EnumSensorType.RANGE, null));

    server.stop();
  }



  @Test public void testReadLevelRawStatus() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("1"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");
    Assert.assertEquals("1", cmd.read(EnumSensorType.LEVEL, null));

    server.stop();
  }



  @Test public void testReadLevelStatusWithMinMaxValue() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("1"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");
    HashMap<String, String> map = new HashMap<String, String>();
    map.put(Sensor.RANGE_MAX_STATE, "35");
    map.put(Sensor.RANGE_MIN_STATE, "-10");

    // (1 - (-10)) / (35 - (-10)) = 24.44444% ≈ 24%
    Assert.assertEquals("24", cmd.read(EnumSensorType.LEVEL, map));

    server.stop();
  }


  @Test public void testReadColorStatus() throws Exception
  {
    Server server = new Server(9999);
    //TODO not clear what color really is.
    server.setHandler(new Handler("#000000"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");

    Assert.assertEquals("#000000", cmd.read(EnumSensorType.COLOR, null));
  //      Assert.assertEquals("black", cmd.read(EnumSensorType.COLOR, null));

    server.stop();
  }


  @Test public void testReadCustomStatus() throws Exception
  {
    Server server = new Server(9999);
    server.setHandler(new Handler("light1_on"));
    server.start();

    StatusCommand cmd = (StatusCommand) getHttpCommand("","http://127.0.0.1:9999");
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("dim0", "light1_dim0");
    map.put("dim30", "light1_dim30");
    map.put("dim50", "light1_dim50");
    map.put("dim70", "light1_dim70");
    map.put("dim100", "light1_dim100");
    map.put("on", "light1_on");
    map.put("off", "light1_off");
    Assert.assertEquals("on", cmd.read(EnumSensorType.CUSTOM, map));

    server.stop();
  }
}


/**
 * Inner Class for Jetty HTTP server handler, returns a string when calling http://127.0.0.1:{port}. 
 * Only used in this test.
 * 
 * @author Dan Cong
 *
 */
class Handler extends AbstractHandler
{

  private String responseAsString;

  public Handler(String responseAsString)
  {
    this.responseAsString = responseAsString;
  }

  public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
       throws IOException, ServletException
  {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter().println(responseAsString);
    ((Request) request).setHandled(true);
  }

  
}
