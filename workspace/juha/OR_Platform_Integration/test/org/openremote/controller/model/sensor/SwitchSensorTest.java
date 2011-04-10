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
package org.openremote.controller.model.sensor;


import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * Base tests for {@link org.openremote.controller.model.sensor.SwitchSensor}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchSensorTest
{

  /**
   * Test 'on' state.
   */
  @Test public void testSwitchOnState()
  {
    SwitchSensor s1 = new SwitchSensor("switch on", 1, new SwitchReadCommand("switch on", 1, "on"));

    Assert.assertTrue(s1.read().equals("on"));
    Assert.assertTrue(s1.getSensorID() == 1);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("switch on"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }

  /**
   * Test 'off' state.
   */
  @Test public void testSwitchOffState()
  {
    SwitchSensor s1 = new SwitchSensor("switch off", 2, new SwitchReadCommand("switch off", 2, "off"));

    Assert.assertTrue(s1.read().equals("off"));
    Assert.assertTrue(s1.getSensorID() == 2);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("switch off"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }


  /**
   * Test switch when event producer gives a non-compliant value.
   */
  @Test public void testSwitchUnknownState()
  {
    SwitchSensor s1 = new SwitchSensor("unknown", 3, new SwitchReadCommand("unknown", 3, "foo"));

    Assert.assertTrue(s1.read().equals(StatusCommand.UNKNOWN_STATUS));
    Assert.assertTrue(s1.getSensorID() == 3);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("unknown"));
    Assert.assertTrue(s1.getProperties().size() == 0);
  }


  /**
   * Test translations of switch states.
   */
  @Test public void testSwitchStateMapping()
  {
    SwitchSensor.DistinctStates mapping = new SwitchSensor.DistinctStates();
    mapping.addStateMapping("off", "close");
    mapping.addStateMapping("on", "open");

    
    SwitchSensor s1 = new SwitchSensor("door sensor", 4, new SwitchReadCommand("door sensor", 4, "on"), mapping);

    Assert.assertTrue(s1.read().equals("open"));
    Assert.assertTrue(s1.getSensorID() == 4);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("door sensor"));
    Assert.assertTrue(s1.getProperties().size() == 0);



    SwitchSensor s2 = new SwitchSensor("door", 5, new SwitchReadCommand("door", 5, "off"), mapping);

    Assert.assertTrue(s2.read().equals("close"));
    Assert.assertTrue(s2.getSensorID() == 5);
    Assert.assertTrue(s2.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s2.isPolling());
    Assert.assertTrue(s2.getName().equals("door"));
    Assert.assertTrue(s2.getProperties().size() == 0);



    SwitchSensor s3 = new SwitchSensor("unknown", 6, new SwitchReadCommand("unknown", 6, "bar"), mapping);

    Assert.assertTrue(s3.read().equals(StatusCommand.UNKNOWN_STATUS));
    Assert.assertTrue(s3.getSensorID() == 6);
    Assert.assertTrue(s3.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s3.isPolling());
    Assert.assertTrue(s3.getName().equals("unknown"));
    Assert.assertTrue(s3.getProperties().size() == 0);

  }


  /**
   * Test switch behavior against a broken event producer.
   */
  @Test public void testBrokenCommand()
  {
    SwitchSensor s1 = new SwitchSensor("broken", 7, new BrokenCommand());

    Assert.assertTrue(s1.read().equals(Sensor.UNKNOWN_STATUS));
    Assert.assertTrue(s1.getSensorID() == 7);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("broken"));
    Assert.assertTrue(s1.getProperties().size() == 0);

  }


  @Test public void testMixedMapping()
  {

    // test mapping when only one of the two states is mapped

    Assert.fail("Not Yet Implemented.");
  }


  @Test public void testNullArgs()
  {

    // test null args on constructor etc

    Assert.fail("Not Yet Implemented.");
  }




  // Nested Classes -------------------------------------------------------------------------------

  private static class SwitchReadCommand extends ReadCommand
  {

    private String returnValue;
    private String name;
    private int id;

    SwitchReadCommand(String name, int id, String returnValue)
    {
      this.returnValue = returnValue;
      this.name = name;
      this.id = id;
    }

    public String read(Sensor s)
    {
      Assert.assertTrue(s instanceof SwitchSensor);
      Assert.assertTrue(s.getName().equals(name));
      Assert.assertTrue(s.getSensorID() == id);
      Assert.assertTrue(s.getProperties().size() == 0);

      return returnValue;
    }
  }

  private static class BrokenCommand extends ReadCommand
  {

    @Override public String read(Sensor s)
    {
      throw new NullPointerException("shouldve been handled");
    }
  }
}

