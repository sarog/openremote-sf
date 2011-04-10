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
package org.openremote.controller.component;


import java.util.Map;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.StateSensor;

/**
 * Basic tests on the {@link Sensor} superclass. Specific sensor implementations have their
 * own tests in their respective test classes.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SensorTest
{

  /**
   * Test sensor equality based on sensor ID.
   */
  @Test public void testEquals()
  {
    EventProducer ep1 = new TestEventProducer1();
    EventProducer ep2 = new TestEventProducer2();

    Sensor s1 = new RangeSensor("range", 1, ep1, -50, 50);
    Sensor s2 = new LevelSensor("level", 1, ep2);
    Sensor s3 = new RangeSensor("range", 1, ep1, -20, 20);

    // there should be only one sensor with the same id which is the only one that matters in
    // equals comparison...

    Assert.assertTrue(s1.equals(s1));
    Assert.assertTrue(s1.equals(s2));
    Assert.assertTrue(s2.equals(s1));
    Assert.assertTrue(s1.equals(s3));
    Assert.assertTrue(s3.equals(s1));
    Assert.assertTrue(s3.equals(s2));
    Assert.assertTrue(s2.equals(s3));
    Assert.assertTrue(s2.equals(s2));
    Assert.assertTrue(s3.equals(s3));

    // And they all need to return same hash...

    Assert.assertTrue(s1.hashCode() == s2.hashCode());
    Assert.assertTrue(s2.hashCode() == s3.hashCode());

    // And different ID should not be equals...

    Sensor s4 = new RangeSensor("range", 2, ep1, -50, 50);

    Assert.assertFalse(s1.equals(s4));
    Assert.assertFalse(s4.equals(s1));
    Assert.assertFalse(s2.equals(s4));
    Assert.assertFalse(s4.equals(s2));
    Assert.assertFalse(s3.equals(s4));
    Assert.assertFalse(s4.equals(s3));
  }


  /**
   * Check defensive argument processing with nulls.
   */
  @Test public void testNullProducerInConstructor()
  {
    try
    {
      Sensor s1 = new SwitchSensor("switch", 4, null);

      Assert.fail("should not reach here");
    }
    catch (IllegalArgumentException e)
    {
      // expected..
    }

    try
    {
      Sensor s1 = new RangeSensor("range", 54, null, 0, 0);

      Assert.fail("should not reach here");
    }
    catch (IllegalArgumentException e)
    {
      // expected..
    }

    try
    {
      Sensor s1 = new StateSensor("state", 455, null, null);

      Assert.fail("should not reach here");
    }
    catch (IllegalArgumentException e)
    {
      // expected..
    }
  }

  /**
   * Ensure sensor state gets correctly initialized.
   */
  @Test public void testSensorInitialization()
  {
    Sensor s1 = new SwitchSensor("switch", 9, new EventProducer() {});

    Assert.assertTrue(s1.getSensorID() == 9);
    Assert.assertTrue(s1.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s1.getName().equals("switch"));
    Assert.assertTrue(s1.getProperties().size() == 0);


    RangeSensor s2 = new RangeSensor("range", 99, new EventProducer() {}, 0, 0);

    Assert.assertTrue(s2.getSensorID() == 99);
    Assert.assertTrue(s2.getMaxValue() == 0);
    Assert.assertTrue(s2.getMinValue() == 0);
    Assert.assertTrue(s2.getSensorType() == EnumSensorType.RANGE);
    Assert.assertTrue(s2.getName().equals("range"));
    Assert.assertTrue(s2.getProperties().size() == 2);


    StateSensor.DistinctStates states = new StateSensor.DistinctStates();
    states.addState("one");
    states.addState("two");

    Sensor s3 = new StateSensor("state", 444, new EventProducer() {}, states);

    Assert.assertTrue(s3.getSensorID() == 444);
    Assert.assertTrue(s3.getSensorType() == EnumSensorType.CUSTOM);
    Assert.assertTrue(s3.getName().equals("state"));
    Assert.assertTrue(s3.getProperties().size() == 2);
    Assert.assertTrue(s3.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s3.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s3.getProperties().values().contains("one"));
    Assert.assertTrue(s3.getProperties().values().contains("two"));

    
    LevelSensor s4 = new LevelSensor("level", 993, new EventProducer() {});

    Assert.assertTrue(s4.getSensorID() == 993);
    Assert.assertTrue(s4.getSensorType() == EnumSensorType.LEVEL);
    Assert.assertTrue(s4.getName().equals("level"));
    Assert.assertTrue(s4.getProperties().size() == 0);
    Assert.assertTrue(s4.getMaxValue() == 100);
    Assert.assertTrue(s4.getMinValue() == 0);
  }


  /**
   * Test that the binding of event producer to sensor works correctly.
   */
  @Test public void testSensorRead()
  {
    Sensor s1 = new SwitchSensor("switch", 84, new SwitchRead(EnumSensorType.SWITCH));

    String returnValue = s1.read();

    Assert.assertTrue(returnValue.equals("on"));



    Sensor s2 = new RangeSensor("range", 33, new RangeRead(EnumSensorType.RANGE), 0, 0);

    returnValue = s2.read();

    Assert.assertTrue(returnValue.equals("0"));
  }


  /**
   * Test the distinction between polling read commands and listeners as event producers.
   */
  @Test public void testPollingVsListener()
  {
    Sensor s1 = new SwitchSensor("switch", 4555, new Listener(4555));

    Assert.assertTrue(s1.isEventListener());
    Assert.assertFalse(s1.isPolling());

    Sensor s2 = new SwitchSensor("switch", 9933, new SwitchRead(EnumSensorType.SWITCH));

    Assert.assertTrue(s2.isPolling());
    Assert.assertFalse(s2.isEventListener());
    
  }






  
  // Nested Classes -------------------------------------------------------------------------------


  private static class SwitchRead implements ReadCommand
  {
    private EnumSensorType type;

    SwitchRead(EnumSensorType expectedType)
    {
      this.type = expectedType;
    }

    public String read(EnumSensorType type, Map<String, String> properties)
    {
      Assert.assertTrue(type == this.type);

      return "on";
    }
  }


  private static class RangeRead implements ReadCommand
  {
    private EnumSensorType type;

    RangeRead(EnumSensorType expectedType)
    {
      this.type = expectedType;
    }

    public String read(EnumSensorType type, Map<String, String> properties)
    {
      Assert.assertTrue(type == this.type);

      return "0";
    }
  }


  private static class Listener implements EventListener
  {
    private int id;

    Listener(int expectedID)
    {
      this.id = expectedID;
    }

    public void setSensorID(int sensorID)
    {
      Assert.assertTrue(sensorID == id);
    }
  }





  private static class TestEventProducer1 implements EventProducer
  {

  }

  private static class TestEventProducer2 implements EventProducer
  {

  }
}

