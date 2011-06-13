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
package org.openremote.controller.model.xml;


import junit.framework.Assert;


import org.jdom.Element;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.command.RemoteActionXMLParser;

/**
 * TODO: Sensor Builder Test.
 *
 * fixture: org/openremote/controller/fixture/controller.xml
 * 
 * @author Dan Cong 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SensorBuilderTest
{

  private static SensorBuilder sensorBuilder;
  private static RemoteActionXMLParser controllerXMLParser;



  // Test Lifecycle -------------------------------------------------------------------------------

  @BeforeClass public static void beforeTests()
  {
    try
    {
      sensorBuilder = (SensorBuilder) ServiceContext.getXMLBinding("sensor");
      controllerXMLParser = ServiceContext.getControllerXMLParser();

      AllTests.deleteControllerXML();
      AllTests.restoreControllerXML();

      AllTests.replaceControllerXML("SensorBuilderTests.xml");
    }

    catch (Throwable t)
    {
      Assert.fail("Cannot initialize tests: " + t.getMessage());  
    }
  }

  @AfterClass public static void afterTests()
  {
    AllTests.restoreControllerXML();
  }

  // Tests ----------------------------------------------------------------------------------------

  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1008" name="range sensor" type="range">
   *   <include type="command" ref="96" />
   *   <min value="-20" />
   *   <max value="100" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if the test fails
   */
  @Test public void testCreateRangeSensor() throws Exception
  {
    RangeSensor s = (RangeSensor)getSensor(SensorType.RANGE);
    Assert.assertEquals(EnumSensorType.RANGE, s.getSensorType());
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(-20, s.getMinValue());
    Assert.assertTrue(s.getName().equals("range sensor"));
    Assert.assertTrue(s.getProperties().size() == 2);
  }


  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1001" name="lampA power sensor" type="switch">
   *   <include type="command" ref="98" />
   *   <state name="on" value="on" />
   *   <state name="off" value="off" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testCreateSwitchSensor() throws Exception
  {
    Sensor s = getSensor(SensorType.SWITCH);
    Assert.assertEquals(EnumSensorType.SWITCH, s.getSensorType());
    Assert.assertTrue(s.getName().equals("lampA power sensor"));
    Assert.assertTrue(s.getProperties().size() == 0);
  }


  /**
   * Parse the following sensor when deployed through a complete controller.xml document.
   *
   * <pre>{@code
   * <sensor id="1010" name="range sensor" type="level">
   *   <include type="command" ref="96" />
   *   <min value="0" />
   *   <max value="100" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testCreateLevelSensor() throws Exception
  {
    LevelSensor s = (LevelSensor)getSensor(SensorType.LEVEL);
    Assert.assertEquals(EnumSensorType.LEVEL, s.getSensorType());
    Assert.assertEquals(100, s.getMaxValue());
    Assert.assertEquals(0, s.getMinValue());
    Assert.assertTrue(s.getName().equals("range sensor"));
    Assert.assertTrue(s.getProperties().size() == 0);
  }


  /**
   * Parse the following sensor configuration:
   *
   * <pre>{@code
   *
   * Two-state CUSTOM sensor configuration.
   *
   * Read command return value 'on' is mapped to 'open'
   * Read command return value 'off' is mapped to 'close'
   *
   * <sensor id = "1009" name = "Door power sensor" type = "custom">
   *   <include type = "command" ref = "98" />
   *   <state name = "open" value = "on" />
   *   <state name = "close" value = "off" />
   * </sensor>
   * }</pre>
   *
   * @throws Exception if test fails
   */
  @Test public void testCreateCustomSensor() throws Exception
  {
    Sensor s = getSensor(SensorType.CUSTOM);
    Assert.assertEquals(EnumSensorType.CUSTOM, s.getSensorType());
    Assert.assertTrue(s.getName().equals("Door power sensor"));
    Assert.assertTrue(s.getProperties().size() == 2);
    Assert.assertTrue(s.getProperties().keySet().contains("state-1"));
    Assert.assertTrue(s.getProperties().keySet().contains("state-2"));
    Assert.assertTrue(s.getProperties().values().contains("on"));
    Assert.assertTrue(s.getProperties().values().contains("off"));

    Assert.assertTrue(s instanceof StateSensor);

    StateSensor state = (StateSensor)s;

    Assert.assertTrue(state.processEvent("on").equals("open"));
    Assert.assertTrue(state.processEvent("off").equals("close"));
    Assert.assertTrue(state.processEvent("foo").equals(Sensor.UNKNOWN_STATUS));
  }


  @Test public void testInvalidConfigs()
  {
    // TODO : ORCJAVA-72
    //
    // need more tests to make sure different configuration variants are initialized correctly
    // and also test some error handling...
    
    Assert.fail("Not Yet Implemented (see ORCJAVA-72 -- http://jira.openremote.org/browse/ORCJAVA-72)");
  }


  /**
   * Test against what could be qualified as a bug that has now become a feature and we need to
   * make sure we don't regress on it unintentionally should we try to fix the bug again. <p>
   *
   * Current tooling generates a style of switch sensors in XML that makes very little sense:
   *
   * <pre>{@code
   *
   * <sensor id = "717" name = "se" type = "switch">
   *   <include type = "command" ref = "96" />
   *   <state name = "on" />
   *   <state name = "off" />
   * </sensor>
   * }</pre>
   *
   * It makes no sense because switch can only ever return on/off as states and no mapping is
   * provided, making the state declarations redundant. But because tooling does generate this,
   * we need to make sure we correctly parse it. <p>
   *
   * See http://jira.openremote.org/browse/ORCJAVA-73
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchStateMappingWithNoValue() throws Exception
  {
    Sensor s = getSensorByID(717);

    Assert.assertEquals(EnumSensorType.SWITCH,  s.getSensorType());
    Assert.assertTrue(s.getName().equals("se"));
    Assert.assertTrue(s.getSensorID() == 717);

    // switch sensor states should not show up as properties, even if mapped...

    Assert.assertTrue(s.getProperties().size() == 0);


    Assert.assertTrue(s.read().equals("off"));
    Assert.assertTrue(s.read().equals("on"));

    Assert.assertTrue(s instanceof SwitchSensor);

    StateSensor state = (StateSensor)s;

    // check that states are in place despite funky XML model...

    Assert.assertTrue(state.processEvent("on").equals("on"));
    Assert.assertTrue(state.processEvent("off").equals("off"));
    Assert.assertTrue(state.processEvent("foo").equals(Sensor.UNKNOWN_STATUS));


    Assert.assertTrue(s.isPolling());
    Assert.assertFalse(s.isEventListener());
  }


  /**
   * Same as {@link #testSwitchStateMappingWithNoValue} above, just uses
   * an event listener instead of polling sensor command.
   *
   * See http://jira.openremote.org/browse/ORCJAVA-73
   *
   * @throws Exception if test fails
   */
  @Test public void testSwitchStateMappingWithNoValueAndListener() throws Exception
  {
    Sensor s = getSensorByID(727);

    s.start();
    
    Assert.assertEquals(EnumSensorType.SWITCH,  s.getSensorType());
    Assert.assertTrue(s.getName().equals("se2"));
    Assert.assertTrue(s.getSensorID() == 727);

    // switch sensor states should not show up as properties, even if mapped...

    Assert.assertTrue(s.getProperties().size() == 0);


    // should get either one depending what the state of the listener is

    Assert.assertTrue(s.read().equals("off") || s.read().equals("on"));
    

    Assert.assertTrue(s instanceof SwitchSensor);

    StateSensor state = (StateSensor)s;

    // check that states are in place despite funky XML model...

    Assert.assertTrue(state.processEvent("on").equals("on"));
    Assert.assertTrue(state.processEvent("off").equals("off"));
    Assert.assertTrue(state.processEvent("foo").equals(Sensor.UNKNOWN_STATUS));

    String status = ServiceContext.getDeviceStateCache().queryStatus(727);

    // should have something since its a listener...

    Assert.assertFalse(status.equals(Sensor.UNKNOWN_STATUS));

    Assert.assertTrue(s.isEventListener());
    Assert.assertFalse(s.isPolling());
  }


  // TODO : test Sensor.update
  // TODO : test Sensor.start

  

  // Helpers --------------------------------------------------------------------------------------

  private Sensor getSensor(SensorType type) throws Exception
  {

    Element ele = null;

    switch (type)
    {
      case RANGE:
        ele = controllerXMLParser.queryElementFromXMLById("1008");
        break;

      case LEVEL:
        ele = controllerXMLParser.queryElementFromXMLById("1010");
        break;

      case SWITCH:
        ele = controllerXMLParser.queryElementFromXMLById("1001");
        break;

      case CUSTOM:
        ele = controllerXMLParser.queryElementFromXMLById("1009");
        break;

      default:
        break;
    }
    
    return sensorBuilder.build(ele);
  }


  private Sensor getSensorByID(int id) throws Exception
  {
    Element el = controllerXMLParser.queryElementFromXMLById(Integer.toString(id));

    return sensorBuilder.build(el);
  }


  enum SensorType { RANGE, LEVEL, SWITCH, CUSTOM }

}
