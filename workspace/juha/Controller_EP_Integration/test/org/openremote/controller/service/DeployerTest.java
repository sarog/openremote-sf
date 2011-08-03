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
package org.openremote.controller.service;

import java.util.Properties;
import java.util.Iterator;
import java.net.URI;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.virtual.VirtualCommandBuilder;
import org.openremote.controller.model.xml.ObjectBuilder;
import org.openremote.controller.model.xml.SensorBuilder;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.jdom.Element;

/**
 * Basic unit tests for {@link org.openremote.controller.service.Deployer Deployer} service.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class DeployerTest
{

  private final static String deployerName = "Deployer for " + DeployerTest.class.getSimpleName();


  // Constructor Tests ----------------------------------------------------------------------------


  /**
   * Test valid constructor execution.
   */
  @Test public void construction()
  {
    StatusCache sc = new StatusCache();
    ControllerConfiguration cc = new ControllerConfiguration();

    new Deployer(deployerName, sc, cc);
  }

  /**
   * Test invalid construction execution with null args.
   */
  @Test (expected = IllegalArgumentException.class)
  public void constructionNullArgs()
  {
    new Deployer(null, null, null);
  }

  /**
   * Test invalid construction execution with null configuration.
   */
  @Test (expected = IllegalArgumentException.class)
  public void constructionNullArgs2()
  {
    new Deployer(null, new StatusCache(), null);
  }

  /**
   * Test invalid construction execution with null status cache.
   */
  @Test (expected = IllegalArgumentException.class)
  public void constructionNullArgs3()
  {
    new Deployer(null, null, new ControllerConfiguration());
  }

  /**
   * Test construction with null name
   */
  public void constructionNullArgs4()
  {
    new Deployer(null, new StatusCache(), new ControllerConfiguration());
  }


  // RegisterObjectBuilder Tests ------------------------------------------------------------------

  @Test public void testRegisterOB()
  {
    StatusCache sc = new StatusCache();
    ControllerConfiguration cc = new ControllerConfiguration();

    Deployer d = new Deployer(deployerName, sc, cc);

    ObjectBuilder ob = new TestOB(d);
    
  }


  @Test public void testRegisterBrokenOB()
  {
    StatusCache sc = new StatusCache();
    ControllerConfiguration cc = new ControllerConfiguration();

    Deployer d = new Deployer(deployerName, sc, cc);

    ObjectBuilder ob = new BrokenOB(d);
  }




  // GetSensor Tests ------------------------------------------------------------------------------

  @Test public void testGetSensor()
  {
    StatusCache sc = new StatusCache();

    Sensor s1 = new SwitchSensor("Sensor 1", 1, new TestEventListener());
    Sensor s2 = new RangeSensor("Sensor 2", 2, new TestEventListener(), 0, 10);
    Sensor s3 = new LevelSensor("Sensor 3", 3, new TestEventListener());
    Sensor s4 = new StateSensor("Sensor 4", 4, new TestEventListener(),
                                new StateSensor.DistinctStates());

    sc.registerSensor(s1);
    sc.registerSensor(s2);
    sc.registerSensor(s3);
    sc.registerSensor(s4);

    ControllerConfiguration cc = new ControllerConfiguration();

    Deployer d = new Deployer(deployerName, sc, cc);

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull(sensor1);
    Assert.assertNotNull(sensor1.equals(s1));
    Assert.assertNotNull(s1.equals(sensor1));
    Assert.assertTrue(s1.getName().equals(sensor1.getName()));
    Assert.assertTrue(s1.getSensorID() == sensor1.getSensorID());
    Assert.assertTrue(sensor1.getSensorID() == 1);


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertNotNull(sensor2.equals(s2));
    Assert.assertNotNull(s2.equals(sensor2));
    Assert.assertTrue(s2.getName().equals(sensor2.getName()));
    Assert.assertTrue(s2.getSensorID() == sensor2.getSensorID());
    Assert.assertTrue(sensor2.getSensorID() == 2);


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertNotNull(sensor3.equals(s3));
    Assert.assertNotNull(s3.equals(sensor3));
    Assert.assertTrue(s3.getName().equals(sensor3.getName()));
    Assert.assertTrue(s3.getSensorID() == sensor3.getSensorID());
    Assert.assertTrue(sensor3.getSensorID() == 3);


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertNotNull(sensor4.equals(s4));
    Assert.assertNotNull(s4.equals(sensor4));
    Assert.assertTrue(s4.getName().equals(sensor4.getName()));
    Assert.assertTrue(s4.getSensorID() == sensor4.getSensorID());
    Assert.assertTrue(sensor4.getSensorID() == 4);
  }


  /**
   * Test retrieving sensor ID that is not registered with status cache. This currently
   * returns a null pointer.
   */
  @Test public void testGetSensorUnknownID()
  {
    StatusCache sc = new StatusCache();
    ControllerConfiguration cc = new ControllerConfiguration();

    Deployer d = new Deployer(deployerName, sc, cc);

    Sensor s = d.getSensor(0);

    Assert.assertTrue(s == null);
  }



  // SoftRestart Tests ----------------------------------------------------------------------------


  /**
   * Use softRestart to load new controller definition once.
   *
   * @throws Exception if test fails
   */
  @Test public void testSoftRestart() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");
    cc.setResourcePath(deploymentURI.getPath());

    ChangedStatusTable cst = new ChangedStatusTable();
    StatusCache sc = new StatusCache();
    sc.setChangedStatusTable(cst);

    Deployer d = new Deployer("Deployer for " + deploymentURI, sc, cc);

    CommandFactory cf = new CommandFactory();
    Properties p = new Properties();
    p.put("virtual", VirtualCommandBuilder.class.getName());

    cf.setCommandBuilders(p);

    SensorBuilder sb = new SensorBuilder(d);
    sb.setCommandFactory(cf);
    
    d.softRestart();

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());

    Iterator it = sc.listSensors();
    int count = 0;

    while(it.hasNext())
    {
      count++;
      it.next();
    }

    Assert.assertTrue(count == 4);

  }


  /**
   * Test soft restart to load one controller definition and then override it with
   * a new one.
   *
   * @throws Exception if test fails
   */
  @Test public void testSoftRestart2() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");
    cc.setResourcePath(deploymentURI.getPath());

    ChangedStatusTable cst = new ChangedStatusTable();
    StatusCache sc = new StatusCache();
    sc.setChangedStatusTable(cst);

    Deployer d = new Deployer("Deployer2 for " + deploymentURI, sc, cc);

    CommandFactory cf = new CommandFactory();
    Properties p = new Properties();
    p.put("virtual", VirtualCommandBuilder.class.getName());

    cf.setCommandBuilders(p);

    SensorBuilder sb = new SensorBuilder(d);
    sb.setCommandFactory(cf);

    d.softRestart();

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());

    Iterator it = sc.listSensors();
    int count = 0;

    while(it.hasNext())
    {
      count++;
      it.next();
    }

    Assert.assertTrue(count == 4);


    // do the change...

    deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly2");
    cc.setResourcePath(deploymentURI.getPath());

    d.softRestart();

    Assert.assertFalse(sensor4.isRunning());
    Assert.assertFalse(sensor3.isRunning());
    Assert.assertFalse(sensor2.isRunning());
    Assert.assertFalse(sensor1.isRunning());

    Assert.assertTrue(d.getSensor(1) == null);
    Assert.assertTrue(d.getSensor(2) == null);
    Assert.assertTrue(d.getSensor(3) == null);
    Assert.assertTrue(d.getSensor(4) == null);


    it = sc.listSensors();
    count = 0;

    while(it.hasNext())
    {
      count++;
      it.next();
    }

    Assert.assertTrue(count == 5);


    Sensor sensor5 = d.getSensor(5);

    Assert.assertNotNull("got null sensor", sensor5);
    Assert.assertTrue(sensor5.getName().equals("Sensor 5"));
    Assert.assertTrue(sensor5.getSensorID() == 5);
    Assert.assertTrue(sensor5 instanceof StateSensor);
    Assert.assertTrue(sensor5.isRunning());


    Sensor sensor6 = d.getSensor(6);

    Assert.assertNotNull(sensor6);
    Assert.assertTrue(sensor6.getName().equals("Sensor 6"));
    Assert.assertTrue(sensor6.getSensorID() == 6);
    Assert.assertTrue(sensor6 instanceof RangeSensor);
    Assert.assertTrue(sensor6.isRunning());


    Sensor sensor7 = d.getSensor(7);

    Assert.assertNotNull(sensor7);
    Assert.assertTrue(sensor7.getName().equals("Sensor 7"));
    Assert.assertTrue(sensor7.getSensorID() == 7);
    Assert.assertTrue(sensor7 instanceof LevelSensor);
    Assert.assertTrue(sensor7.isRunning());


    Sensor sensor8 = d.getSensor(8);

    Assert.assertNotNull(sensor8);
    Assert.assertTrue(sensor8.getName().equals("Sensor 8"));
    Assert.assertTrue(sensor8.getSensorID() == 8);
    Assert.assertTrue(sensor8 instanceof SwitchSensor);
    Assert.assertTrue(sensor8.isRunning());


    Sensor sensor9 = d.getSensor(9);

    Assert.assertNotNull(sensor9);
    Assert.assertTrue(sensor9.getName().equals("Sensor 9"));
    Assert.assertTrue(sensor9.getSensorID() == 9);
    Assert.assertTrue(sensor9 instanceof StateSensor);
    Assert.assertTrue(sensor9.isRunning());


    // finish by deploying empty...

    deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/doesnotexist");
    cc.setResourcePath(deploymentURI.getPath());

    try
    {
      d.softRestart();

      Assert.fail("Expected an exception here.");
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      // expected...
    }

    Assert.assertFalse(sensor5.isRunning());
    Assert.assertFalse(sensor6.isRunning());
    Assert.assertFalse(sensor7.isRunning());
    Assert.assertFalse(sensor8.isRunning());
    Assert.assertFalse(sensor9.isRunning());

    Assert.assertTrue("expected sensor 5 to be cleared", d.getSensor(5) == null);
    Assert.assertTrue("expected sensor 6 to be cleared", d.getSensor(6) == null);
    Assert.assertTrue("expected sensor 7 to be cleared", d.getSensor(7) == null);
    Assert.assertTrue("expected sensor 8 to be cleared", d.getSensor(8) == null);
    Assert.assertTrue("expected sensor 9 to be cleared", d.getSensor(9) == null);


    it = sc.listSensors();
    count = 0;

    while(it.hasNext())
    {
      count++;
      it.next();
    }

    Assert.assertTrue(count == 0);

    Assert.assertTrue(cst.getRecordList().isEmpty());
  }


  /**
   * Test restart with redeploying the same controller definition over itself.
   *
   * @throws Exception if test fails
   */
  @Test public void redeployItself() throws Exception
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/sensorsonly");
    cc.setResourcePath(deploymentURI.getPath());

    ChangedStatusTable cst = new ChangedStatusTable();
    StatusCache sc = new StatusCache();
    sc.setChangedStatusTable(cst);

    Deployer d = new Deployer("Deployer3 for " + deploymentURI, sc, cc);

    CommandFactory cf = new CommandFactory();
    Properties p = new Properties();
    p.put("virtual", VirtualCommandBuilder.class.getName());

    cf.setCommandBuilders(p);

    SensorBuilder sb = new SensorBuilder(d);
    sb.setCommandFactory(cf);

    d.softRestart();

    Sensor sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    Sensor sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    Sensor sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    Sensor sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());

    Iterator it = sc.listSensors();
    int count = 0;

    while(it.hasNext())
    {
      count++;
      it.next();
    }

    Assert.assertTrue(count == 4);


    // redeploy

    d.softRestart();

    Assert.assertFalse(sensor4.isRunning());
    Assert.assertFalse(sensor3.isRunning());
    Assert.assertFalse(sensor2.isRunning());
    Assert.assertFalse(sensor1.isRunning());

    it = sc.listSensors();
    count = 0;

    while(it.hasNext())
    {
      count++;
      it.next();
    }

    Assert.assertTrue(count == 4);

    sensor1 = d.getSensor(1);

    Assert.assertNotNull("got null sensor", sensor1);
    Assert.assertTrue(sensor1.getName().equals("Sensor 1"));
    Assert.assertTrue(sensor1.getSensorID() == 1);
    Assert.assertTrue(sensor1 instanceof SwitchSensor);
    Assert.assertTrue(sensor1.isRunning());


    sensor2 = d.getSensor(2);

    Assert.assertNotNull(sensor2);
    Assert.assertTrue(sensor2.getName().equals("Sensor 2"));
    Assert.assertTrue(sensor2.getSensorID() == 2);
    Assert.assertTrue(sensor2 instanceof LevelSensor);
    Assert.assertTrue(sensor2.isRunning());


    sensor3 = d.getSensor(3);

    Assert.assertNotNull(sensor3);
    Assert.assertTrue(sensor3.getName().equals("Sensor 3"));
    Assert.assertTrue(sensor3.getSensorID() == 3);
    Assert.assertTrue(sensor3 instanceof RangeSensor);
    Assert.assertTrue(sensor3.isRunning());


    sensor4 = d.getSensor(4);

    Assert.assertNotNull(sensor4);
    Assert.assertTrue(sensor4.getName().equals("Sensor 4"));
    Assert.assertTrue(sensor4.getSensorID() == 4);
    Assert.assertTrue(sensor4 instanceof StateSensor);
    Assert.assertTrue(sensor4.isRunning());

  }


  /**
   * Test restart when configured to non-existent controller.xml path. <p>
   *
   * At the moment, no exception is propagated, the error is logged but the runtime
   * stays operational although not executing any functions.
   */
  @Test public void testSoftRestartNoXMLDoc()
  {
    ControllerConfiguration cc = new ControllerConfiguration();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("deployment/doesntexist");
    cc.setResourcePath(deploymentURI.getPath());

    
    ChangedStatusTable cst = new ChangedStatusTable();
    StatusCache sc = new StatusCache();
    sc.setChangedStatusTable(cst);
    
    Deployer d = new Deployer("Deployer4 for " + deploymentURI, sc, cc);

    try
    {
      d.softRestart();

      Assert.fail("Expected an exception here..");
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      // expected...
    }
  }


  // TODO : check test API usage of startController() vs softRestart()
  // TODO : test URI/abs file/relative file support in controllerconfiguration.getResource()


  @Test public void testStartController()
  {
    Assert.fail("Not Yet Implemented. See ORCJAVA-160");
  }

  @Test public void testIsPaused()
  {
    Assert.fail("Not Yet Implemented. See ORCJAVA-161");  
  }

  @Test public void testAutoAndRedeployment()
  {
    // These should be integration tests with external test container

    Assert.fail("Not Yet Implemented. See ORCJAVA-162");  
  }

  @Test public void testSlowUnresponsiveSensorOnStop()
  {
    // test deployer behavior when stopping and sensor won't respond to request to stop

    Assert.fail("Not Yet Implemented. See ORCJAVA-163");
  }

  @Test public void testRestartOnIncorrectXML()
  {
    // test restart behavior when the xml is structurally correct but
    // semantically broken (such as linking to non-existent elements, etc).

    Assert.fail("Not Yet Implemented. See ORCJAVA-164");   
  }





  // Nested Classes -------------------------------------------------------------------------------


  private static class TestEventListener implements EventListener
  {
    @Override public void setSensor(Sensor s)
    {

    }

    @Override public void stop(Sensor s)
    {

    }
  }


  private static class TestOB extends ObjectBuilder
  {
    private TestOB(Deployer d)
    {
      super(d);
    }

    @Override public Object build(Element e)
    {
      return null;
    }

    @Override public Deployer.XMLSegment getRootSegment()
    {
      return Deployer.XMLSegment.SENSORS;
    }

    @Override public Deployer.ControllerSchemaVersion getSchemaVersion()
    {
      return Deployer.ControllerSchemaVersion.VERSION_2_0;
    }
  }

  private static class BrokenOB extends TestOB
  {
    private BrokenOB(Deployer d)
    {
      super(d);
    }

    @Override public Deployer.XMLSegment getRootSegment()
    {
      throw new Error("testing broken object builder");
    }
  }
}

