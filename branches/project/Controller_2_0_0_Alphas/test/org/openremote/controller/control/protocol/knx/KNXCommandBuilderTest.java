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

import static junit.framework.Assert.assertTrue;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.knx.KNXCommand;
import org.openremote.controller.protocol.knx.KNXCommandBuilder;

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


  /**
   * Test KNX command parsing with "OFF" as the command string and 1/1/1 as group address.
   */
  @Test public void testKNXOff()
  {
    Command cmd = getCommand("OFF", "1/1/1");

    assertTrue(cmd instanceof KNXCommand);
  }

  /**
   * Test KNX command parsing with "off", "Off" and "oFf" as the command string and 1/1/1 as
   * group address.
   */
  @Test public void testKNXOffMixedCase()
  {
    Command cmd1 = getCommand("off", "1/1/1");

    assertTrue(cmd1 instanceof KNXCommand);

    Command cmd2 = getCommand("Off", "1/1/1");

    assertTrue(cmd2 instanceof KNXCommand);

    Command cmd3 = getCommand("oFf", "1/1/1");

    assertTrue(cmd3 instanceof KNXCommand);
  }


  /**
   * Test KNX command parsing with "STATUS" as the command string and 1/1/1 as
   * group address.
   */
  @Test public void testKNXStatus()
  {
    Command cmd = getCommand("STATUS", "1/1/1");
    assertTrue(cmd instanceof KNXCommand);
  }

  /**
   * Test KNX command parsing with "Status", "status" and "STatus" as the command string and
   * 1/1/1 as group address.
   */
  @Test public void testKNXStatusMixedCase()
  {
    Command cmd1 = getCommand("status", "1/1/1");
    assertTrue(cmd1 instanceof KNXCommand);

    Command cmd2 = getCommand("Status", "1/1/1");
    assertTrue(cmd2 instanceof KNXCommand);

    Command cmd3 = getCommand("STatus", "1/1/1");
    assertTrue(cmd3 instanceof KNXCommand);
  }

  /**
   * Test KNX command parsing with invalid command name and 1/2/2 as group address.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testNoSuchCommand()
  {
    getCommand(" ", "1/2/2");
  }

  /**
   * Test KNX command with invalid group address value. These could/should be validated
   * defensively.
   *
   * NOT YET IMPLEMENTED.
   */
  @Test(expected=Error.class)
  public void invalidGroupAddress()
  {
    // TODO : not yet implemented

    getCommand("STatus", "gargabe should not work");

    // We should be defensive and do some basic validation on obviously wrong group addresses.
  }

  /**
   * Test KNX command parsing with XML snippets containing superfluous properties.
   */
  @Test public void testKNXSuperfluousProperties()
  {
    Command cmd = getCommandWithExtraProperties("on", "1/1/1") ;
    assertTrue(cmd instanceof KNXCommand);
  }

  /**
   * Test KNX command parsing with a missing mandatory command property.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testKNXWithMissingCommandProperty()
  {
    getCommandMissingCommandProperty("1/1/1") ;
  }


  /**
   * Test KNX command parsing with a missing mandatory groupAddress property.
   */
  @Test(expected = NoSuchCommandException.class)
  public void testKNXWithMissingGroupAddressProperty()
  {
    getCommandMissingGroupAddressProperty("on") ;
  }


  /**
   * Test KNX command parsing with arbitrary property order
   */
  @Test public void testKNXWithArbitraryPropertyOrder()
  {
    Command cmd = getCommandArbitraryPropertyOrder("on", "1/1/1");
    assertTrue(cmd instanceof KNXCommand);
  }


  // Helpers --------------------------------------------------------------------------------------
  
  private Command getCommand(String cmd, String groupAddress)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "knx");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);

    return builder.build(ele);
  }

  private Command getCommandArbitraryPropertyOrder(String cmd, String groupAddress)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "knx");


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);


    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandMissingGroupAddressProperty(String cmd)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "knx");

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);

    return builder.build(ele);
  }

  private Command getCommandMissingCommandProperty(String groupAddress)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "knx");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    return builder.build(ele);
  }

  private Command getCommandWithExtraProperties(String cmd, String groupAddress)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "knx");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                          KNXCommandBuilder.KNX_XMLPROPERTY_GROUPADDRESS);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                          groupAddress);

    ele.addContent(propAddr);

    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,
                           KNXCommandBuilder.KNX_XMLPROPERTY_COMMAND);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE,
                           cmd);

    ele.addContent(propAddr2);

    // empty properties..

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME,"");
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "");

    ele.addContent(propAddr3);

    // unknown properties

    Element propAddr4 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "test");
    propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "unknown");

    ele.addContent(propAddr4);
    return builder.build(ele);
  }

}
