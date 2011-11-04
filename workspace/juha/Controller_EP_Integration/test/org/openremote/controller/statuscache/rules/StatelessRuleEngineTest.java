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
package org.openremote.controller.statuscache.rules;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.model.event.Level;
import org.openremote.controller.protocol.Event;

/**
 * Basic tests for {@link StatelessRuleEngine}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StatelessRuleEngineTest
{


  // Instance Fields ------------------------------------------------------------------------------


  private String controllerResourcePath;
  private ControllerConfiguration config;



  // Test Life Cycle ------------------------------------------------------------------------------


  /**
   * Saves the original (default) resource path setting for each test. Initializes the
   * service context if hasn't been already done so by previous tests.
   */
  @Before public void initTest()
  {
    AllTests.initServiceContext();

    config = ServiceContext.getControllerConfiguration();

    controllerResourcePath = config.getResourcePath();
  }

  /**
   * Restores the original resource path after each test.
   */
  @After public void restore()
  {
    config.setResourcePath(controllerResourcePath);
  }



  // Tests ----------------------------------------------------------------------------------------


  /**
   * Test basic decision table deployment and operation to modify the processed events.
   *
   * @throws Exception  if test fails
   */
  @Test public void testDecisionTable() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/dtable/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    sre.start();

    Switch sw = new Switch(555, "test555", "on", Switch.State.ON);

    Event evt = sre.push(sw);

    Assert.assertTrue(evt.getSourceID() == 5555);

    sw = new Switch(666, "test666", "on", Switch.State.ON);

    evt = sre.push(sw);

    Assert.assertTrue(evt.getSourceID() == 6666);

    sw = new Switch(777, "test777", "on", Switch.State.ON);

    evt = sre.push(sw);

    Assert.assertTrue(evt.getSourceID() == 7777);

  }



  /**
   * Test rule engine initialization in case of a broken CSV file.
   *
   * @throws Exception  if test fails
   */
  @Test public void testDecisionTableNoRuleSet() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/dtable-noruleset/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    sre.start();
  }


  /**
   * Test rule engine initialization in case of a incorrectly defined decision table.
   *
   * @throws Exception    if test fails
   */
  @Test public void testDecisionTableIncorrectDefinition() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/dtable-incorrect-definition/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    sre.start();
  }


  /**
   * Test a simple event modification on a DRL file.
   *
   * @throws Exception  if test fails
   */
  @Test public void testEventModRule() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/java/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    sre.start();

    Switch sw = new Switch(123, "test", "on", Switch.State.ON);

    Event evt = sre.push(sw);

    Assert.assertTrue(evt.getSourceID() == 321);
    Assert.assertTrue(evt.getSource().equals("test"));
    Assert.assertTrue(evt instanceof Switch);
    Assert.assertTrue(evt.equals(sw));
    Assert.assertTrue(evt == sw);

    Switch sw1 = (Switch)evt;

    Assert.assertTrue(sw1.getValue().equals("on"));
    Assert.assertTrue(sw1.getOriginalState() == Switch.State.ON);
    
  }


  /**
   * Test implementation behavior when controller configuration for finding/reading
   * rules is incorrect.
   */
  @Test public void testWhenNoRuleDir()
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/no-rule-dir/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    try
    {
      sre.start();

      Assert.fail("Should not get here...");
    }

    catch (InitializationException e)
    {
      // Expected -- controller configuration is incorrect if no rule dir is present
    }

  }


  /**
   * Test a mixed deployment of multiple rules, some from drools rule language,
   * others from CSV decision table.
   *
   * @throws Exception  if test fails
   */
  @Test public void testMixedDRLCSVDeployment() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/mixed/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    sre.start();

    // should not get modified...

    Switch sw = new Switch(1, "test", "on", Switch.State.ON);
    Event evt = sre.push(sw);

    Assert.assertTrue(evt.getSource().equals("test"));
    Assert.assertTrue(evt.getSourceID() == 1);
    Assert.assertTrue(evt.getValue().equals("on"));
    Assert.assertTrue(evt.serialize().equals("on"));
    Assert.assertTrue(evt instanceof Switch);
    Assert.assertTrue(evt.equals(sw));
    Assert.assertTrue(evt == sw);

    Switch swevt = (Switch)evt;

    Assert.assertTrue(swevt.getOriginalState().equals(Switch.State.ON));


    // Should get its source modified by EventMod.drl...

    Level level = new Level(123, "test level mod", 30);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod"));
    Assert.assertTrue(evt.getSourceID() == 321);
    Assert.assertTrue(evt.getValue().equals(30));
    Assert.assertTrue(evt.serialize().equals("30"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    // the next three should get modified by DTableTest.csv...

    level = new Level(555, "test level mod 555", 101);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod 555"));
    Assert.assertTrue(evt.getSourceID() == 5555);
    Assert.assertTrue(evt.getValue().equals(100));
    Assert.assertTrue(evt.serialize().equals("100"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);



    level = new Level(666, "test level mod 666", 1);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod 666"));
    Assert.assertTrue(evt.getSourceID() == 6666);
    Assert.assertTrue(evt.getValue().equals(1));
    Assert.assertTrue(evt.serialize().equals("1"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    level = new Level(777, "test level mod 777", 10);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod 777"));
    Assert.assertTrue(evt.getSourceID() == 7777);
    Assert.assertTrue(evt.getValue().equals(10));
    Assert.assertTrue(evt.serialize().equals("10"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);
  }


  /**
   * Test deployment behavior when some of the rule definitions have
   * errors or can't be deployed. Make sure the correct ones still
   * operate.
   *
   * @throws Exception    if test fails
   */
  @Test public void testMixedDeploymentWithErrors() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/mixed-with-errors/").toString();

    config.setResourcePath(newResourcePath);

    StatelessRuleEngine sre = new StatelessRuleEngine();

    sre.start();

    // should not get modified...

    Switch sw = new Switch(1, "test", "on", Switch.State.ON);
    Event evt = sre.push(sw);

    Assert.assertTrue(evt.getSource().equals("test"));
    Assert.assertTrue(evt.getSourceID() == 1);
    Assert.assertTrue(evt.getValue().equals("on"));
    Assert.assertTrue(evt.serialize().equals("on"));
    Assert.assertTrue(evt instanceof Switch);
    Assert.assertTrue(evt.equals(sw));
    Assert.assertTrue(evt == sw);

    Switch swevt = (Switch)evt;

    Assert.assertTrue(swevt.getOriginalState().equals(Switch.State.ON));


    // Should get its source modified by EventMod.drl...

    Level level = new Level(123, "test level mod", 30);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod"));
    Assert.assertTrue(evt.getSourceID() == 321);
    Assert.assertTrue(evt.getValue().equals(30));
    Assert.assertTrue(evt.serialize().equals("30"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    // the next three should get modified by DTableTest.csv...

    level = new Level(555, "test level mod 555", 101);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod 555"));
    Assert.assertTrue(evt.getSourceID() == 5555);
    Assert.assertTrue(evt.getValue().equals(100));
    Assert.assertTrue(evt.serialize().equals("100"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);



    level = new Level(666, "test level mod 666", 1);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod 666"));
    Assert.assertTrue(evt.getSourceID() == 6666);
    Assert.assertTrue(evt.getValue().equals(1));
    Assert.assertTrue(evt.serialize().equals("1"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    level = new Level(777, "test level mod 777", 10);

    evt = sre.push(level);

    Assert.assertTrue(evt.getSource().equals("test level mod 777"));
    Assert.assertTrue(evt.getSourceID() == 7777);
    Assert.assertTrue(evt.getValue().equals(10));
    Assert.assertTrue(evt.serialize().equals("10"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);
  }

}

