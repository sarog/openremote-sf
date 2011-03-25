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

import java.util.Map;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * Base tests for {@link org.openremote.controller.model.sensor.StateSensor} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StateSensorTest
{
  /**
   * Simple test case of a one-state sensor.
   */
  @Test public void testSingleState()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");

    StateSensor s1 = new StateSensor(1, new StateReadCommand("foo"), states);

    Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(s1.getSensorID() == 1);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
  }

  /**
   * A test case of a two-state sensor (similar to switch).
   */
  @Test public void testTwoState()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");

    StateSensor s1 = new StateSensor(2, new StateReadCommand("foo", "bar"), states);

    Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(s1.getSensorID() == 2);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());


    Assert.assertTrue(s1.read().equals("bar"));

  }


  /**
   * Test case of a three-state sensor
   */
  @Test public void testThreeState()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");
    states.addState("acme");

    StateSensor s1 = new StateSensor(3, new StateReadCommand("foo", "bar", "acme"), states);

    Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(s1.getSensorID() == 3);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());


    Assert.assertTrue(s1.read().equals("bar"));
    Assert.assertTrue(s1.read().equals("acme"));
  }


  /**
   * Test case of a sensor with ten distinct states.
   */
  @Test public void testTenState()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("one");
    states.addState("two");
    states.addState("three");
    states.addState("four");
    states.addState("five");
    states.addState("six");
    states.addState("seven");
    states.addState("eight");
    states.addState("nine");
    states.addState("ten");

    StateSensor s1 = new StateSensor(
        4,
        new StateReadCommand("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
        states
    );

    Assert.assertTrue(s1.read().equals("one"));
    Assert.assertTrue(s1.getSensorID() == 4);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());


    Assert.assertTrue(s1.read().equals("two"));
    Assert.assertTrue(s1.read().equals("three"));
    Assert.assertTrue(s1.read().equals("four"));
    Assert.assertTrue(s1.read().equals("five"));
    Assert.assertTrue(s1.read().equals("six"));
    Assert.assertTrue(s1.read().equals("seven"));
    Assert.assertTrue(s1.read().equals("eight"));
    Assert.assertTrue(s1.read().equals("nine"));
    Assert.assertTrue(s1.read().equals("ten"));
  }


  /**
   * Test sensor behavior when event producer returns a value that has not been added to this
   * state sensor's configuration.
   */
  @Test public void testFalseReturn()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("bar");
    states.addState("foo");

    StateSensor s1 = new StateSensor(5, new StateReadCommand("acme", "foo", "bar"), states);

    Assert.assertTrue(s1.read().equals(ReadCommand.UNKNOWN_STATUS));
    Assert.assertTrue(s1.getSensorID() == 5);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());


    String readVal = s1.read();
    Assert.assertTrue("Expected 'foo', got : " + readVal, readVal.equals("foo"));
    Assert.assertTrue(s1.read().equals("bar"));

  }


  /**
   * Test mapping of state string from event producers to translated forms.
   */
  @Test public void testStateMapping()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addStateMapping("0", "Raining");
    states.addStateMapping("1", "Cloudy");
    states.addStateMapping("2", "Sunny");

    StateSensor s1 = new StateSensor(6, new StateReadCommand("0", "1", "2"), states);

    Assert.assertTrue(s1.read().equals("Raining"));
    Assert.assertTrue(s1.getSensorID() == 6);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());


    Assert.assertTrue(s1.read().equals("Cloudy"));
    Assert.assertTrue(s1.read().equals("Sunny"));
  }


  @Test public void testConstructorArgs()
  {
    // test null args, etc

    Assert.fail("Not Yet Implemented.");
  }


  /**
   * Test sensor behavior when the event producer throws an exception.
   */
  @Test public void testBrokenCommand()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addStateMapping("0", "Raining");
    states.addStateMapping("1", "Cloudy");
    states.addStateMapping("2", "Sunny");

    StateSensor s1 = new StateSensor(7, new BrokenCommand(), states);

    Assert.assertTrue(s1.read().equals(ReadCommand.UNKNOWN_STATUS));

    Assert.assertTrue(s1.getSensorID() == 7);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
  }




  @Test public void testMixedStateMapping()
  {
    // test state mappings where only some of the states are mapped to other values..


    Assert.fail("Not Yet Implemented.");
  }

  @Test public void testMappingToNull()
  {

    // test cases where some map to nulls or empty strings...

    Assert.fail("Not Yet Implemented.");
  }



  // Nested Classes -------------------------------------------------------------------------------

  private static class StateReadCommand implements ReadCommand
  {

    private String[] returnValue;
    private int index = 0;

    StateReadCommand(String... returnValue)
    {
      this.returnValue = returnValue;
    }

    @Override public String read(EnumSensorType type, Map<String, String> properties)
    {
      if (index >= returnValue.length )
        index = 0;

      Assert.assertTrue(type == EnumSensorType.CUSTOM);

      List<String> vals = Arrays.asList(returnValue);

      for (int i = 1; i <= returnValue.length; ++i)
      {
        Assert.assertTrue(properties.keySet().contains("state-" + i));

        String state = properties.get("state-" + 1);

        Assert.assertTrue(vals.contains(state));
      }

      return returnValue[index++];
    }
  }

  private static class BrokenCommand implements ReadCommand
  {

    @Override public String read(EnumSensorType type, Map<String, String> properties)
    {
      throw new NullPointerException("this should have been handled.");
    }
  }
}

