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

    StateSensor s1 = new StateSensor("single", 1, new StateReadCommand("foo"), states);

    Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(s1.getSensorID() == 1);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("single"));
    Assert.assertTrue(s1.getProperties().size() == 1);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
  }

  /**
   * A test case of a two-state sensor (similar to switch).
   */
  @Test public void testTwoState()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");

    StateSensor s1 = new StateSensor("twostate", 2, new StateReadCommand("foo", "bar"), states);

    Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(s1.getSensorID() == 2);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("twostate"));
    Assert.assertTrue(s1.getProperties().size() == 2);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
    Assert.assertTrue(s1.getProperties().values().contains("bar"));


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

    StateSensor s1 = new StateSensor("threestate", 3, new StateReadCommand("foo", "bar", "acme"), states);

    Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(s1.getSensorID() == 3);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("threestate"));
    Assert.assertTrue(s1.getProperties().size() == 3);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-3"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
    Assert.assertTrue(s1.getProperties().values().contains("bar"));
    Assert.assertTrue(s1.getProperties().values().contains("acme"));


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
        "tenstate", 4,
        new StateReadCommand("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
        states
    );

    Assert.assertTrue(s1.read().equals("one"));
    Assert.assertTrue(s1.getSensorID() == 4);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("tenstate"));
    Assert.assertTrue(s1.getProperties().size() == 10);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-3"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-4"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-5"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-6"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-7"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-8"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-9"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-10"));

    Assert.assertTrue(s1.getProperties().values().contains("one"));
    Assert.assertTrue(s1.getProperties().values().contains("two"));
    Assert.assertTrue(s1.getProperties().values().contains("three"));
    Assert.assertTrue(s1.getProperties().values().contains("four"));
    Assert.assertTrue(s1.getProperties().values().contains("five"));
    Assert.assertTrue(s1.getProperties().values().contains("six"));
    Assert.assertTrue(s1.getProperties().values().contains("seven"));
    Assert.assertTrue(s1.getProperties().values().contains("eight"));
    Assert.assertTrue(s1.getProperties().values().contains("nine"));
    Assert.assertTrue(s1.getProperties().values().contains("ten"));

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

    StateSensor s1 = new StateSensor("funky", 5, new StateReadCommand("acme", "foo", "bar"), states);

    Assert.assertTrue(s1.read().equals(Sensor.UNKNOWN_STATUS));
    Assert.assertTrue(s1.getSensorID() == 5);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("funky"));
    Assert.assertTrue(s1.getProperties().size() == 2);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
    Assert.assertTrue(s1.getProperties().values().contains("bar"));


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

    StateSensor s1 = new StateSensor("mapped", 6, new StateReadCommand("0", "1", "2"), states);

    Assert.assertTrue(s1.read().equals("Raining"));
    Assert.assertTrue(s1.getSensorID() == 6);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("mapped"));
    Assert.assertTrue(s1.getProperties().size() == 3);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-3"));
    Assert.assertTrue(s1.getProperties().values().contains("0"));
    Assert.assertTrue(s1.getProperties().values().contains("1"));
    Assert.assertTrue(s1.getProperties().values().contains("2"));


    Assert.assertTrue(s1.read().equals("Cloudy"));
    Assert.assertTrue(s1.read().equals("Sunny"));
  }


  @Test public void testConstructorArgs()
  {
    // TODO : test null args, etc
    // TODO : make sure no NPE's on object methods (such as toString) because of null field values

    Assert.fail("Not Yet Implemented. See ORCJAVA-106 -- http://jira.openremote.org/browse/ORCJAVA-106");
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

    StateSensor s1 = new StateSensor("broken", 7, new BrokenCommand(), states);

    Assert.assertTrue(s1.read().equals(Sensor.UNKNOWN_STATUS));

    Assert.assertTrue(s1.getSensorID() == 7);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("broken"));
    Assert.assertTrue(s1.getProperties().size() == 3);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-3"));
    Assert.assertTrue(s1.getProperties().values().contains("0"));
    Assert.assertTrue(s1.getProperties().values().contains("1"));
    Assert.assertTrue(s1.getProperties().values().contains("2"));
  }




  @Test public void testMixedStateMapping()
  {
    // TODO : test state mappings where only some of the states are mapped to other values..


    Assert.fail("Not Yet Implemented. See ORCJAVA-107 -- http://jira.openremote.org/browse/ORCJAVA-107");
  }

  @Test public void testMappingToNull()
  {

    // TODO : test cases where some map to nulls or empty strings...

    Assert.fail("Not Yet Implemented. See ORCJAVA-108 -- http://jira.openremote.org/browse/ORCJAVA-108");
  }


  @Test public void testArbitraryStringPassThrough()
  {
    // TODO :
    //   - Some panel behavior depends on 'CUSTOM' type sensor passing through arbitrary strings
    //     unfiltered. Make sure we're not too aggressive with validating custom state sensor
    //     output to restrict this usage and allow arbitrary strings to pass through if no state
    //     mappings are defined.

    Assert.fail("Not Yet Implemented. See ORCJAVA-109 -- http://jira.openremote.org/browse/ORCJAVA-109");
  }


  @Test public void testToString()
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");

    StateSensor s1 = new StateSensor("single", 1, new StateReadCommand("foo"), states);

    Assert.assertTrue(s1.toString().contains("single"));
    Assert.assertTrue(s1.toString().contains("foo"));


    states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");

    s1 = new StateSensor("twostate", 2, new StateReadCommand("foo", "bar"), states);

    Assert.assertTrue(s1.toString().contains("twostate"));
    Assert.assertTrue(s1.toString().contains("foo"));
    Assert.assertTrue(s1.toString().contains("bar"));


    // TODO : test behavior if null fields in the object are allowed
  }



  // Nested Classes -------------------------------------------------------------------------------

  private static class StateReadCommand extends ReadCommand
  {

    private String[] returnValue;
    private int index = 0;

    StateReadCommand(String... returnValue)
    {
      this.returnValue = returnValue;
    }

    @Override public String read(Sensor s)
    {
      if (index >= returnValue.length )
        index = 0;

      Assert.assertTrue(s instanceof StateSensor);

      List<String> vals = Arrays.asList(returnValue);

      for (int i = 1; i <= returnValue.length; ++i)
      {
        Assert.assertTrue(s.getProperties().keySet().contains("state-" + i));

        String state = s.getProperties().get("state-" + 1);

        Assert.assertTrue(vals.contains(state));
      }

      return returnValue[index++];
    }
  }

  private static class BrokenCommand extends ReadCommand
  {

    @Override public String read(Sensor s)
    {
      throw new NullPointerException("this should have been handled.");
    }
  }
}

