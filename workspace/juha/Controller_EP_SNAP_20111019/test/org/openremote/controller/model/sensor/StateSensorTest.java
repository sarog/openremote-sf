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
import org.junit.Before;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;

/**
 * Base tests for {@link org.openremote.controller.model.sensor.StateSensor} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StateSensorTest
{

  /* share the same cache across all sensor tests */
  private StatusCache cache = null;


  @Before
  public void setup()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain echain = new EventProcessorChain();

    cache = new StatusCache(cst, echain);
  }


  /**
   * Simple test case of a one-state sensor.
   */
  @Test public void testSingleState() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");

    StateSensor s1 = new StateSensor("single", 1, cache, new StateReadCommand("foo"), states);

    cache.registerSensor(s1);  // TODO : redundant -- should be in Sensor's constructor

    s1.start();

    //Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(getSensorValueFromCache(1).equals("foo"));

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
   *
   * @throws Exception if test fails
   */
  @Test public void testTwoState() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");

    final int SENSOR_ID = 2;
    StateReadCommand readCommand = new StateReadCommand("foo", "bar");

    StateSensor s1 = new StateSensor("twostate", SENSOR_ID, cache, readCommand, states);

    cache.registerSensor(s1);     // TODO : redundant -- should be in Sensor's constructor

    s1.start();

    //Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("foo"));

    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("twostate"));
    Assert.assertTrue(s1.getProperties().size() == SENSOR_ID);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
    Assert.assertTrue(s1.getProperties().values().contains("bar"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("bar"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("bar"));
  }


  /**
   * Test case of a three-state sensor
   *
   * @throws Exception if test fails
   */
  @Test public void testThreeState() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");
    states.addState("acme");

    final int SENSOR_ID = 3;
    StateReadCommand readCommand = new StateReadCommand("foo", "bar", "acme");

    StateSensor s1 = new StateSensor("threestate", SENSOR_ID, cache, readCommand, states);

    cache.registerSensor(s1);   // TODO : redundant -- should be in Sensor's constructor

    s1.start();

    //Assert.assertTrue(s1.read().equals("foo"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("foo"));

    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("threestate"));
    Assert.assertTrue(s1.getProperties().size() == SENSOR_ID);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-3"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
    Assert.assertTrue(s1.getProperties().values().contains("bar"));
    Assert.assertTrue(s1.getProperties().values().contains("acme"));


    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("bar"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("bar"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("acme"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("acme"));
  }


  /**
   * Test case of a sensor with ten distinct states.
   *
   * @throws Exception if test fails
   */
  @Test public void testTenState() throws Exception
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

    final int SENSOR_ID = 4;
    StateReadCommand readCommand = new StateReadCommand(
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"
    );

    StateSensor s1 = new StateSensor("tenstate", 4, cache, readCommand, states);

    cache.registerSensor(s1);  // TODO : redundant -- should be in Sensor's constructor

    s1.start();

    //Assert.assertTrue(s1.read().equals("one"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("one"));

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

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("two"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("two"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("three"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("three"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("four"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("four"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("five"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("five"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("six"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("six"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("seven"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("seven"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("eight"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("eight"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("nine"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("nine"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("ten"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("ten"));
  }


  /**
   * Test sensor behavior when event producer returns a value that has not been added to this
   * state sensor's configuration.
   *
   * @throws Exception if test fails
   */
  @Test public void testFalseReturn() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("bar");
    states.addState("foo");

    final int SENSOR_ID = 5;
    StateReadCommand readCommand = new MixedStateReadCommand("acme", "foo", "bar");
    StateSensor s1 = new StateSensor("funky", SENSOR_ID, cache, readCommand, states);

    cache.registerSensor(s1);   // TODO : redundant -- should be in Sensor constructor
    s1.start();

    //Assert.assertTrue(s1.read().equals(Sensor.UNKNOWN_STATUS));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals(Sensor.UNKNOWN_STATUS));

    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s1.isPolling());
    Assert.assertTrue(s1.getName().equals("funky"));
    Assert.assertTrue(s1.getProperties().size() == 2);
    Assert.assertTrue(s1.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s1.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s1.getProperties().values().contains("foo"));
    Assert.assertTrue(s1.getProperties().values().contains("bar"));


    readCommand.nextValue();

    //String readVal = s1.read();
    String readVal = getSensorValueFromCache(SENSOR_ID);

    Assert.assertTrue("Expected 'foo', got : " + readVal, readVal.equals("foo"));

    readCommand.nextValue();

    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("bar"));

  }


  /**
   * Test mapping of state string from event producers to translated forms.
   *
   * @throws Exception if test fails
   */
  @Test public void testStateMapping() throws Exception
  {
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addStateMapping("0", "Raining");
    states.addStateMapping("1", "Cloudy");
    states.addStateMapping("2", "Sunny");

    final int SENSOR_ID = 6;
    StateReadCommand readCommand = new StateReadCommand("0", "1", "2");

    StateSensor s1 = new StateSensor("mapped", SENSOR_ID, cache, readCommand, states);

    cache.registerSensor(s1); // TODO : redundant -- should be in Sensor's constructor

    s1.start();

    //Assert.assertTrue(s1.read().equals("Raining"));
    Assert.assertTrue(
        "Expected 'Raining', got " + getSensorValueFromCache(SENSOR_ID),
        getSensorValueFromCache(SENSOR_ID).equals("Raining")
    );

    Assert.assertTrue(s1.getSensorID() == SENSOR_ID);
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


    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("Cloudy"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("Cloudy"));

    readCommand.nextValue();

    //Assert.assertTrue(s1.read().equals("Sunny"));
    Assert.assertTrue(getSensorValueFromCache(SENSOR_ID).equals("Sunny"));
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

    StateSensor s1 = new StateSensor("broken", 7, cache, new BrokenCommand(), states);

    cache.registerSensor(s1);   // TODO : redundant -- should be in sensor constructor

    s1.start();

    //Assert.assertTrue(s1.read().equals(Sensor.UNKNOWN_STATUS));
    Assert.assertTrue(cache.queryStatusBySensorId(7).equals(Sensor.UNKNOWN_STATUS));
    
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

    StateSensor s1 = new StateSensor("single", 1, cache, new StateReadCommand("foo"), states);

    Assert.assertTrue(s1.toString().contains("single"));
    Assert.assertTrue(s1.toString().contains("foo"));


    states = new StateSensor.DistinctStates();
    states.addState("foo");
    states.addState("bar");

    s1 = new StateSensor("twostate", 2, cache, new StateReadCommand("foo", "bar"), states);

    Assert.assertTrue(s1.toString().contains("twostate"));
    Assert.assertTrue(s1.toString().contains("foo"));
    Assert.assertTrue(s1.toString().contains("bar"));


    // TODO : test behavior if null fields in the object are allowed
  }



  // Helpers --------------------------------------------------------------------------------------

  private String getSensorValueFromCache(int sensorID) throws Exception
  {
    // sleep here to give the polling mechanism enough time to push the event value to cache...

    Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);

    return cache.queryStatusBySensorId(sensorID);
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class StateReadCommand extends ReadCommand
  {

    protected String[] returnValue;
    protected int index = 0;

    protected StateReadCommand(String... returnValue)
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

        String state = s.getProperties().get("state-" + i);

        Assert.assertTrue(vals.contains(state));
      }

      //return returnValue[index++];
      return returnValue[index];
    }

    public void nextValue()
    {
      index++;
    }
  }

  private static class MixedStateReadCommand extends StateReadCommand
  {
    MixedStateReadCommand(String... values)
    {
      super(values);
    }

    @Override public String read(Sensor s)
    {
      if (index >= returnValue.length )
        index = 0;

      Assert.assertTrue(s instanceof StateSensor);

      List<String> vals = Arrays.asList(returnValue);

      return returnValue[index];
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

