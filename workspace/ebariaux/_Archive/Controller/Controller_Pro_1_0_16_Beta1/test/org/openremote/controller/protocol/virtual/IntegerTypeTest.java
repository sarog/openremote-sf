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
package org.openremote.controller.protocol.virtual;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.protocol.virtual.VirtualCommandBuilder.Type;
import org.jdom.Element;

/**
 * Test 'range' sensor state reads and writes on OpenRemote virtual room/device protocol.
 *
 * @see org.openremote.controller.protocol.virtual.VirtualCommand
 *
 * @author <a href="mailto:wbalcaen@tinsys.com">Juha Lindfors</a>
 */
public class IntegerTypeTest
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Reference to the command builder we can use to build command instances.
   */
  private VirtualCommandBuilder builder = null;



  // Test Setup -----------------------------------------------------------------------------------

  @Before public void setUp()
  {
    builder = new VirtualCommandBuilder();
  }



  // Tests ----------------------------------------------------------------------------------------

  /**
   * Tests protocol read command behavior for 'Range' sensor type when no explict command to
   * set state has been sent yet. Expecting a 'Range' sensor to return '0' in such a case.
   */
  @Test public void testStatusDefaultValueInteger()
  {
    StatusCommand cmd = getReadCommand("test range with integer default value", null, null, null, null);

    String value = cmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value.equals("0"));
  }



  /**
   * Tests 'range' sensor read/write behavior.
   */
  @Test public void testRangeStateWithoutRangeLimitInteger()
  {
    final String ADDRESS = "range read/write with integer type tests";

    // Read command in uninitialized state, should return '0'...

    StatusCommand readCmd = getReadCommand(ADDRESS, "INCREMENT 0", Type.Integer.toString(), null, null);

    String value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equalsIgnoreCase("0"));


    // Send INCREMENT 10 to the same address...

    ExecutableCommand writeRange1 = getWriteCommand(ADDRESS, "INCREMENT 10", Type.Integer, null, null);

    writeRange1.send();


    // Read state, should return '10'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value.equals("10"));


    // Send write command '80' to the same address...

    ExecutableCommand writeRange80 = getWriteCommand(ADDRESS, "SET 80", Type.Integer, null, null);

    writeRange80.send();


    // Read state, should return '80'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value.equals("80"));
    
    
    // Send write command 'DECREMENT -10' to the same address...

    ExecutableCommand writeRange100negative = getWriteCommand(ADDRESS, "SET -100", Type.Integer, null, null);

    writeRange100negative.send();

    // Read state, should return '-100'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("-100"));
    
    
    // Send write command 'DECREMENT' to the same address...

    ExecutableCommand writeRangeDecrement = getWriteCommand(ADDRESS, "DECREMENT", Type.Integer, null, null);

    writeRangeDecrement.send();

    // Read state, should return '-101'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("-101"));

    
    // Send write command 'INCREMENT' to the same address...

    ExecutableCommand writeRangeIncrement = getWriteCommand(ADDRESS, "INCREMENT", Type.Integer, null, null);

    writeRangeIncrement.send();

    // Read state, should return '-100'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("-100"));

    
    // Send write command 'DECREMENT 10' to the same address...

    ExecutableCommand writeRangeDecrement10 = getWriteCommand(ADDRESS, "DECREMENT 10", Type.Integer, null, null);

    writeRangeDecrement10.send();

    // Read state, should return '-110'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("-110"));
    
  }




  /**
   * Tests 'range' sensor read/write behavior with out of bounds values.
   */
  @Test public void testRangeOutOfBoundsStateInteger()
  {
    final String ADDRESS = "range out of bounds integer tests";

    // Read command in uninitialized state, should return '0'...

    StatusCommand readCmd = getReadCommand(ADDRESS, "INCREMENT 1", Type.Integer.toString(), 0, 80);

    String value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equalsIgnoreCase("0"));


    // Send write command '-1' to the same address...

    ExecutableCommand writeRangeNeg1 = getWriteCommand(ADDRESS, "INCREMENT -1", Type.String, 0, 80);

    writeRangeNeg1.send();


    // Read state, should return '0'...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value.equals("0"));


    // Send write command 'INCREMENT 91' to the same address...

    ExecutableCommand writeRange91 = getWriteCommand(ADDRESS, "INCREMENT 91", Type.Integer, 0, 90);

    writeRange91.send();



    // Read state, should return '0' because out of range...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("0"));
    
    
    // Send write command 'SET -91' to the same address...

    ExecutableCommand writeRangeMinus91 = getWriteCommand(ADDRESS, "SET 91", Type.Integer, 0, 90);

    writeRangeMinus91.send();



    // Read state, should return '0' because out of range...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("0"));
 
  
    //Send write command 'SET 90' to the same address...

    ExecutableCommand writeRangeSET90 = getWriteCommand(ADDRESS, "SET 90", Type.Integer, 0, 90);

    writeRangeSET90.send();  

    // Read state, should return '90' 

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("90"));
    
    
  //Send write command 'DECREMENT 100' to the same address...

    ExecutableCommand writeRangeDECREMENT100 = getWriteCommand(ADDRESS, "DECREMENT 100", Type.Integer, -20, 90);

    writeRangeDECREMENT100.send();  

    // Read state, should return '-10' ...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("-10"));
    
  //Send write command 'DECREMENT 125' to the same address out of bounds...

    ExecutableCommand writeRangeDECREMENT125 = getWriteCommand(ADDRESS, "DECREMENT 125", Type.Integer, -20, 90);

    writeRangeDECREMENT125.send();  

    // Read state, should return '-10' ...

    value = readCmd.read(EnumSensorType.RANGE, new HashMap<String, String>());

    Assert.assertTrue(value,value.equals("-10"));
}


  // Helpers --------------------------------------------------------------------------------------

  /**
   * Returns a read ('status') command.
   *
   * @param address   arbitrary string address
   *
   * @return  status command instance for the given address
   */
  private StatusCommand getReadCommand(String address,String command, String type, Integer  minValue, Integer maxValue)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    ele.setAttribute(CommandBuilder.PROTOCOL_ATTRIBUTE_NAME, "virtual");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "address");
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "command");
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "status");

    ele.addContent(propAddr2);

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "type");
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, "String");

    ele.addContent(propAddr3);

    Command cmd = builder.build(ele);

    if (!(cmd instanceof StatusCommand))
    {
      Assert.fail("Was expecting a read command (StatusCommand) type, got " + cmd.getClass());

      return null;
    }
    else
    {
      return (StatusCommand)cmd;
    }
  }


  /**
   * Creates a write command with given command value hacked into an XML element attribute.
   *
   * @param address   arbitrary address string
   * @param cmd       arbitrary command name
   * @param value     command value
   *
   * @return  write command instance with given parameters
   */
  private ExecutableCommand getWriteCommand(String address, String cmd, Type type, Integer minValue, Integer maxValue)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");

    Element propAddr = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "address");
    propAddr.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, address);

    ele.addContent(propAddr);


    Element propAddr2 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "command");
    propAddr2.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, cmd);

    ele.addContent(propAddr2);

    Element propAddr3 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "type");
    propAddr3.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, type.toString());

    ele.addContent(propAddr3);

    if (minValue != null)
    {
      Element propAddr4 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
      propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "minValue");
      propAddr4.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, minValue.toString());
      
      ele.addContent(propAddr4);      
    }

    if (maxValue != null)
    {
      Element propAddr5 = new Element(CommandBuilder.XML_ELEMENT_PROPERTY);
      propAddr5.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_NAME, "maxValue");
      propAddr5.setAttribute(CommandBuilder.XML_ATTRIBUTENAME_VALUE, maxValue.toString());
      ele.addContent(propAddr5);
    }
    
    Command command = builder.build(ele);

    if (!(command instanceof ExecutableCommand))
    {
      Assert.fail("Was expecting a write command (ExecutableCommand) type, got " + command.getClass());

      return null;
    }
    else
    {
      return (ExecutableCommand)command;
    }
  }

}

