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
import org.openremote.controller.model.xml.SensorBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.command.CommandFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Attribute;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ComponentBuilderTest
{
  private static Namespace ns = Namespace.getNamespace(Constants.OPENREMOTE_WEBSITE);
  private static Document document;

  static
  {
    try
    {
      initXML();
    }
    catch (Throwable t)
    {
      StringWriter stringBuffer = new StringWriter();
      PrintWriter writer = new PrintWriter(stringBuffer);

      t.printStackTrace(writer);

      Assert.fail(
        "\n\n" +
        "**************************************************************************" +
        "\n" +
        " Cannot initialize tests: \n" +
        " " + t.getMessage() +
        "\n\n" +
        " " + stringBuffer.getBuffer() + 
        "**************************************************************************\n\n"
      );
    }
  }


  
  private ComponentBuilder componentBuilder;
  private SensorBuilder sensorBuilder;



  @Before public void setUp()
  {
    componentBuilder = new TestComponentBuilder();

    // Setup the component builder with one protocol implementation we can use with components...

    CommandFactory cf = new CommandFactory();
    Properties props = new Properties();
    props.setProperty("virtual", "virtualCommandBuilder");
    cf.setCommandBuilders(props);

    // Configure the dependencies...

    componentBuilder.setCommandFactory(cf);
    componentBuilder.setRemoteActionXMLParser(ServiceContext.getControllerXMLParser());


    ControllerConfiguration cc = new ControllerConfiguration();
    StatusCache sc = new StatusCache();
    Deployer deployer = new Deployer(sc, cc);

    // Setup the sensor builder with same dependencies

    sensorBuilder = new SensorBuilder(deployer);
    sensorBuilder.setCommandFactory(cf);
  }

  @Test public void testParseSensorOnSwitch1() throws Exception
  {
    Element switch1 = getComponentByID(1);

    Assert.assertNotNull(switch1);
    Assert.assertTrue(switch1.getAttribute("id").getIntValue() == 1);

    Element include = switch1.getChild("include", ns);

    Assert.assertNotNull(include);
    Assert.assertNotNull(include.getAttribute("type"));
    Assert.assertNotNull(include.getAttribute("ref"));

    
    Assert.assertTrue(include.getAttribute("type").getValue().equals("sensor"));
    Assert.assertTrue(include.getAttribute("ref").getValue().equals("1001"));

    SensorBuilder builder = (SensorBuilder) ServiceContext.getXMLBinding("sensor");
    Sensor sensor = builder.buildFromComponentInclude(include);

    Assert.assertTrue(sensor.getSensorID() == 1001);
    Assert.assertTrue(sensor.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(sensor instanceof SwitchSensor);
    Assert.assertTrue(sensor.isRunning());
  }


  @Test public void testParseSensorBUG() throws Exception
  {
    Element switch1 = getComponentByID(1);

    Element include = switch1.getChild("include", ns);

    SensorBuilder builder = (SensorBuilder) ServiceContext.getXMLBinding("sensor");
    Sensor sensor = builder.buildFromComponentInclude(include);
    
    Assert.assertTrue(sensor.getSensorID() == 1001);
    Assert.assertTrue(sensor.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(sensor instanceof SwitchSensor);
    Assert.assertTrue(sensor.isRunning());
  }


  @Test public void testSensorBuilderOnSwitchSensor() throws Exception
  {
    Sensor s = sensorBuilder.build(getSensorByID(1001));

    Assert.assertTrue(s.getSensorID() == 1001);
    Assert.assertTrue(s.getSensorType() == EnumSensorType.SWITCH);
    Assert.assertTrue(s instanceof SwitchSensor);
    Assert.assertTrue(s.isRunning());
  }


  @Test public void testParseSensorOnSlider() throws Exception
  {
    Element slider = getComponentByID(8);

    Assert.assertNotNull(slider);
    Assert.assertTrue(slider.getAttribute("id").getIntValue() == 8);

    Element include = slider.getChild("include", ns);

    Assert.assertNotNull(include);
    Assert.assertNotNull(include.getAttribute("type"));
    Assert.assertNotNull(include.getAttribute("ref"));


    Assert.assertTrue(include.getAttribute("type").getValue().equals("sensor"));
    Assert.assertTrue(include.getAttribute("ref").getValue().equals("1008"));

    SensorBuilder builder = (SensorBuilder) ServiceContext.getXMLBinding("sensor");
    Sensor sensor = builder.buildFromComponentInclude(include);
    Assert.assertTrue(sensor.getSensorID() == 1008);

//  TODO :
//   - this is a bug, shown in the test above -- sensor type is never set.

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



  @Test public void testParseSensorOnSliderBUG() throws Exception
  {
    Element slider = getComponentByID(8);
    Element include = slider.getChild("include", ns);

    SensorBuilder builder = (SensorBuilder) ServiceContext.getXMLBinding("sensor");
    Sensor sensor = builder.buildFromComponentInclude(include);

    Assert.assertNotNull(sensor.getProperties());
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MAX_STATE));
    Assert.assertTrue(sensor.getProperties().containsKey(Sensor.RANGE_MIN_STATE));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MAX_STATE).equals("100"));
    Assert.assertTrue(sensor.getProperties().get(Sensor.RANGE_MIN_STATE).equals("-20"));
  }



  @Test public void testSensorBuilderOnRangeSensor() throws Exception
  {
    RangeSensor s = (RangeSensor)sensorBuilder.build(getSensorByID(1008));

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

  

  // Helpers --------------------------------------------------------------------------------------

  private static Map<Integer, Element> components;
  private static Map<Integer, Element> sensors;

  private static void initXML() throws Exception
  {
    String path = AllTests.getFixtureFile("ComponentsAndSensors-controller.xml");
    document = AllTests.getJDOMDocument(new File(path));

    Element root = document.getRootElement();

    Element componentElements = root.getChild("components", ns);

    Assert.assertNotNull("components is null", componentElements);

    List<Element> componentList = componentElements.getChildren();
    Map<Integer, Element> componentMap = new HashMap<Integer, Element>();

    for (Element component : componentList)
    {
      Attribute attrID = component.getAttribute("id");

      Assert.assertNotNull(attrID);

      Integer componentID = attrID.getIntValue();

      componentMap.put(componentID, component);
    }

    components = componentMap;



    Element sensorElements = root.getChild("sensors", ns);
    List<Element> sensorsList = sensorElements.getChildren();
    Map<Integer, Element> sensorMap = new HashMap<Integer, Element>();

    for (Element sensor : sensorsList)
    {
      Attribute attrID = sensor.getAttribute("id");
      Integer sensorID = attrID.getIntValue();
      sensorMap.put(sensorID, sensor);
    }

    sensors = sensorMap;

  }

  private static Element getComponentByID(int id)
  {
    return components.get(id);
  }

  private static Element getSensorByID(int id)
  {
    return sensors.get(id);
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

