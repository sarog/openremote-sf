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

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.model.xml.SensorBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.command.CommandFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Attribute;


/**
 * TODO :
 *   - most of these tests belong to their particular component builder tests but are
 *     left here until new builder implementations are ready for all component types
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ComponentBuilderTest
{
  private static Namespace ns = Namespace.getNamespace(Constants.OPENREMOTE_WEBSITE);

  //private ComponentBuilder componentBuilder;
  private SensorBuilder sensorBuilder;
  private Deployer deployer;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    //componentBuilder = new TestComponentBuilder();

    // Setup command factory with one protocol implementation we can use with components...

    CommandFactory commandFactory = new CommandFactory();
    Properties props = new Properties();
    props.setProperty("virtual", "virtualCommandBuilder");
    commandFactory.setCommandBuilders(props);

    //componentBuilder.setCommandFactory(commandFactory);
    //componentBuilder.setRemoteActionXMLParser(ServiceContext.getControllerXMLParser());


    ControllerConfiguration cc = new ControllerConfiguration();
    cc.setResourcePath(AllTests.getAbsoluteFixturePath().resolve("component").getPath());

    ChangedStatusTable cst = new ChangedStatusTable();

    StatusCache sc = new StatusCache();
    sc.setChangedStatusTable(cst);

    deployer = new Deployer(sc, cc);

    sensorBuilder = new SensorBuilder(deployer);
    sensorBuilder.setCommandFactory(commandFactory);

    deployer.softRestart();
  }


  // Tests ----------------------------------------------------------------------------------------


  /**
   * Test constructing a sensor reference through a {@code <switch>} component
   * sensor reference:
   *
   * <pre>{@code
   *
   * <switch id = "1">
   *  <on>
   *   <include type = "command" ref = "501" />
   *  </on>
   *  <off>
   *   <include type = "command" ref = "502" />
   *  </off>
   *
   *  <include type = "sensor" ref = "1001" />
   * </switch>
   *
   * }</pre>
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseSensorOnSwitch1() throws Exception
  {
    Element switch1 = deployer.queryElementById(1);

    Assert.assertNotNull(switch1);
    Assert.assertTrue(switch1.getAttribute("id").getIntValue() == 1);

    Element include = switch1.getChild("include", ns);

    Assert.assertNotNull(include);
    Assert.assertNotNull(include.getAttribute("type"));
    Assert.assertNotNull(include.getAttribute("ref"));

    Assert.assertTrue(include.getAttribute("type").getValue().equals("sensor"));
    Assert.assertTrue(include.getAttribute("ref").getValue().equals("1001"));

    // Test...

    Sensor sensor = sensorBuilder.buildFromComponentInclude(include);


    Assert.assertTrue(sensor.getSensorID() == 1001);
    Assert.assertTrue(sensor.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(sensor instanceof SwitchSensor);
    Assert.assertTrue(sensor.isRunning());
  }


  /**
   * Redundant regression test of {@link #testParseSensorOnSwitch1()} on a bug
   * that has since been fixed. Leaving this in place for now.
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseSensorBUG() throws Exception
  {
    Element switch1 = deployer.queryElementById(1);
    Element include = switch1.getChild("include", ns);


    // Test...

    Sensor sensor = sensorBuilder.buildFromComponentInclude(include);


    Assert.assertTrue(sensor.getSensorID() == 1001);
    Assert.assertTrue(sensor.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(sensor instanceof SwitchSensor);
    Assert.assertTrue(sensor.isRunning());
  }


  /**
   * Tests parsing of a switch sensor definition with redundant {@code <state>} elements.
   *
   * <pre>{@code
   *
   * <sensor id = "1001" name = "lampA power sensor" type = "switch">
   *  <include type = "command" ref = "98" />
   *
   *   <state name = "on" value = "on" />
   *   <state name = "off" value = "off" />
   * </sensor>
   *
   * }</pre>
   *
   * @throws Exception    if test fails
   */
  @Test public void testSensorBuilderOnSwitchSensor() throws Exception
  {
    Element sensor1001 = deployer.queryElementById(1001);


    // Test...

    Sensor s = sensorBuilder.build(sensor1001);


    Assert.assertTrue(s.getSensorID() == 1001);
    Assert.assertTrue(s.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.isRunning());

    // although there are distinct states in the sensor, they should not show up as
    // properties for the protocol implementors -- the state values are automatically
    // mapped by the controller

    Assert.assertTrue(s.getProperties().size() == 0);
  }


  /**
   * Test constructing a sensor reference through a {@code <slider>} component
   * sensor reference:
   *
   * <pre>{@code
   *
   * <slider id = "8">
   *   <setValue>
   *     <include type = "command" ref = "507" />
   *   </setValue>
   *
   *   <include type = "sensor" ref = "1008" />
   * </slider>
   *
   * }</pre>
   *
   * @throws Exception      if test fails
   */
  @Test public void testParseSensorOnSlider() throws Exception
  {
    Element slider = deployer.queryElementById(8);

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.getAttribute("id").getIntValue() == 8);

    Element include = slider.getChild("include", ns);

    Assert.assertNotNull(include);
    Assert.assertNotNull(include.getAttribute("type"));
    Assert.assertNotNull(include.getAttribute("ref"));

    Assert.assertTrue(include.getAttribute("type").getValue().equals("sensor"));
    Assert.assertTrue(include.getAttribute("ref").getValue().equals("1008"));


    // Test...

    Sensor sensor = sensorBuilder.buildFromComponentInclude(include);


    Assert.assertTrue(sensor.getSensorID() == 1008);

    Assert.assertTrue(sensor.getSensorType() == EnumSensorType.RANGE);
    Assert.assertTrue(sensor instanceof RangeSensor);
    Assert.assertTrue(sensor.isRunning());

    RangeSensor range = (RangeSensor)sensor;

    Assert.assertTrue(
        "Expected 100, got " + range.getMaxValue(),
        range.getMaxValue() == 100
    );

    Assert.assertTrue(
        "Expected -20, got " + range.getMinValue(),
        range.getMinValue() == -20
    );

    Assert.assertNotNull(sensor.getProperties());
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }


  /**
   * Redundant regression test of {@link #testParseSensorOnSlider()} on a bug
   * that has since been fixed. Leaving this in place for now.
   *
   * @throws Exception  if test fails
   */
  @Test public void testParseSensorOnSliderBUG() throws Exception
  {
    Element slider = deployer.queryElementById(8);
    Element include = slider.getChild("include", ns);


    // Test...

    Sensor sensor = sensorBuilder.buildFromComponentInclude(include);


    Assert.assertNotNull(sensor.getProperties());
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }


  /**
   * Tests parsing of a range sensor definition with redundant min/max elements.
   *
   * <pre>{@code
   *
   *  <sensor id = "1008" name = "range sensor" type = "range">
   *   <include type = "command" ref = "96" />
   *
   *    <min value = "-20" />
   *    <max value = "100" />
   *  </sensor>
   *
   * }</pre>
   *
   * @throws Exception      if test fails
   */
  @Test public void testSensorBuilderOnRangeSensor() throws Exception
  {
    Element sensor1008 = deployer.queryElementById(1008);

    // Test...

    RangeSensor s = (RangeSensor)sensorBuilder.build(sensor1008);


    Assert.assertTrue(s.getSensorID() == 1008);
    Assert.assertTrue(s.getSensorType() == EnumSensorType.RANGE);
    Assert.assertTrue(s.getMaxValue() == 100);
    Assert.assertTrue(s.getMinValue() == -20);
    Assert.assertNotNull(s.getProperties());
    Assert.assertTrue(s.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(s.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(s.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(s.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }

  


  // Nested Classes -------------------------------------------------------------------------------

  private final static class TestComponentBuilder extends ComponentBuilder
  {
    @Override public Component build(org.jdom.Element el, String commandParam)
    {
      return new MyComponent();
    }
  }

  private final static class MyComponent extends Component
  {
    public List<String> getAvailableActions()
    {
      return new ArrayList<String>();
    }
  }
}

