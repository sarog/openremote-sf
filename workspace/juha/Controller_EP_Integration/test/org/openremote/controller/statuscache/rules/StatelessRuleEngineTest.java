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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.deployer.Version20CommandBuilder;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.statuscache.EventContext;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.statuscache.EventProcessor;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.model.event.Level;
import org.openremote.controller.model.event.Range;
import org.openremote.controller.model.Command;
import org.openremote.controller.protocol.Event;
import org.jdom.Element;

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

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();
    EventGrab grab = new EventGrab();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);
    processors.add(grab);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Switch sw = new Switch(555, "test555", "on", Switch.State.ON);

    cache.update(sw);
    Event evt = grab.event;

    Assert.assertTrue(evt.getSourceID() == 5555);

    sw = new Switch(666, "test666", "on", Switch.State.ON);

    cache.update(sw);
    evt = grab.event;

    Assert.assertTrue(evt.getSourceID() == 6666);

    sw = new Switch(777, "test777", "on", Switch.State.ON);

    cache.update(sw);
    evt = grab.event;

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

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();
    EventGrab grab = new EventGrab();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);
    processors.add(grab);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);


    Switch sw = new Switch(123, "test", "on", Switch.State.ON);

    cache.update(sw);
    Event evt = grab.event;

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

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();
    EventGrab grab = new EventGrab();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);
    processors.add(grab);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);


    // should not get modified...

    Switch sw = new Switch(1, "test", "on", Switch.State.ON);

    cache.update(sw);
    Event evt = grab.event;

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

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod"));
    Assert.assertTrue(evt.getSourceID() == 321);
    Assert.assertTrue(evt.getValue().equals(30));
    Assert.assertTrue(evt.serialize().equals("30"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    // the next three should get modified by DTableTest.csv...

    level = new Level(555, "test level mod 555", 101);

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod 555"));
    Assert.assertTrue(evt.getSourceID() == 5555);
    Assert.assertTrue(evt.getValue().equals(100));
    Assert.assertTrue(evt.serialize().equals("100"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);



    level = new Level(666, "test level mod 666", 1);

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod 666"));
    Assert.assertTrue(evt.getSourceID() == 6666);
    Assert.assertTrue(evt.getValue().equals(1));
    Assert.assertTrue(evt.serialize().equals("1"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    level = new Level(777, "test level mod 777", 10);

    cache.update(level);
    evt = grab.event;

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

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();
    EventGrab grab = new EventGrab();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);
    processors.add(grab);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);


    // should not get modified...

    Switch sw = new Switch(1, "test", "on", Switch.State.ON);

    cache.update(sw);
    Event evt = grab.event;

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

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod"));
    Assert.assertTrue(evt.getSourceID() == 321);
    Assert.assertTrue(evt.getValue().equals(30));
    Assert.assertTrue(evt.serialize().equals("30"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    // the next three should get modified by DTableTest.csv...

    level = new Level(555, "test level mod 555", 101);

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod 555"));
    Assert.assertTrue(evt.getSourceID() == 5555);
    Assert.assertTrue(evt.getValue().equals(100));
    Assert.assertTrue(evt.serialize().equals("100"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);



    level = new Level(666, "test level mod 666", 1);

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod 666"));
    Assert.assertTrue(evt.getSourceID() == 6666);
    Assert.assertTrue(evt.getValue().equals(1));
    Assert.assertTrue(evt.serialize().equals("1"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);


    level = new Level(777, "test level mod 777", 10);

    cache.update(level);
    evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("test level mod 777"));
    Assert.assertTrue(evt.getSourceID() == 7777);
    Assert.assertTrue(evt.getValue().equals(10));
    Assert.assertTrue(evt.serialize().equals("10"));
    Assert.assertTrue(evt instanceof Level);
    Assert.assertTrue(evt.equals(level));
    Assert.assertTrue(evt == level);
  }


  /**
   * Test rule condition based on two existing sensor events.
   *
   * @throws Exception    if test fails
   */
  @Test public void testRuleConditionOnExistingSensorValues() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/sensors/").toString();

    config.setResourcePath(newResourcePath);

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();
    EventGrab grab = new EventGrab();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);
    processors.add(grab);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);


    Switch sw1 = new Switch(999, "my event", "on", Switch.State.ON);
    Switch sw2 = new Switch(1000, "test sensor 1", "off", Switch.State.OFF);

    cache.update(sw2);
    cache.update(sw1);

    Event evt = grab.event;

    Assert.assertTrue(evt.getSource().equals("my event"));
    Assert.assertTrue(evt.getSourceID() == 9999);
    Assert.assertTrue(evt.getValue().equals("on"));
    Assert.assertTrue(evt.serialize().equals("on"));
    Assert.assertTrue(evt instanceof Switch);
    Assert.assertTrue(evt.equals(sw1));
    Assert.assertTrue(evt == sw1);

    Switch swevt = (Switch)evt;

    Assert.assertTrue(swevt.getOriginalState().equals(Switch.State.ON));
  }



  /**
   * Test rule execution that require three separate switch events (sensors) to be
   * in 'on' state.
   *
   * @throws Exception    if test fails
   */
  @Test public void testThreeWaySwitchOn() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/sensor-three-switches/").toString();

    config.setResourcePath(newResourcePath);

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();
    EventGrab grab = new EventGrab();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);
    processors.add(grab);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);


    // All off...

    Switch sw1 = new Switch(1, "sensor 1", "off", Switch.State.OFF);
    Switch sw2 = new Switch(2, "sensor 2", "off", Switch.State.OFF);
    Switch sw3 = new Switch(3, "sensor 3", "off", Switch.State.OFF);


    // Update sensor 1 'off'...

    cache.update(sw1);

    Event evt = grab.event;

    Assert.assertTrue(
       "Expected 'sensor 1', got '" + evt.getSource() + "'",
       evt.getSource().equals("sensor 1")
    );

    String currentState = cache.queryStatus(1);

    Assert.assertTrue(currentState.equals("off"));


    // Update sensor 2 'off'...

    cache.update(sw2);

    evt = grab.event;

    Assert.assertTrue(
       "Expected 'sensor 2', got '" + evt.getSource() + "'",
       evt.getSource().equals("sensor 2")
    );

    currentState = cache.queryStatus(2);

    Assert.assertTrue(currentState.equals("off"));


    // Update sensor 3 'off'....

    cache.update(sw3);

    evt = grab.event;

    Assert.assertTrue(
       "Expected 'sensor 3', got '" + evt.getSource() + "'",
       evt.getSource().equals("sensor 3")
    );

    currentState = cache.queryStatus(3);

    Assert.assertTrue(currentState.equals("off"));



    // Update sensor 1 'on'....


    sw1 = new Switch(1, "sensor 1", "on", Switch.State.ON);

    cache.update(sw1);


    evt = grab.event;

    Assert.assertTrue(
       "Expected 'sensor 1', got '" + evt.getSource() + "'",
       evt.getSource().equals("sensor 1")
    );

    currentState = cache.queryStatus(1);

    Assert.assertTrue(currentState.equals("on"));


    currentState = cache.queryStatus(2);

    Assert.assertTrue(currentState.equals("off"));


    currentState = cache.queryStatus(3);

    Assert.assertTrue(currentState.equals("off"));



    // Update sensor 2 'on'....

    sw2 = new Switch(2, "sensor 2", "on", Switch.State.ON);

    cache.update(sw2);


    evt = grab.event;

    Assert.assertTrue(
       "Expected 'sensor 2', got '" + evt.getSource() + "'",
       evt.getSource().equals("sensor 2")
    );

    currentState = cache.queryStatus(1);

    Assert.assertTrue(currentState.equals("on"));


    currentState = cache.queryStatus(2);

    Assert.assertTrue(currentState.equals("on"));


    currentState = cache.queryStatus(3);

    Assert.assertTrue(currentState.equals("off"));



    // Update sensor 3 'on'...  this is where the rule should trigger.

    sw3 = new Switch(3, "sensor 3", "on", Switch.State.ON);
    
    cache.update(sw3);

    evt = grab.event;

    Assert.assertTrue(
        "Expected 'Complete', got '" + evt.getSource() + "'",
        evt.getSource().equals("Complete")
    );


    currentState = cache.queryStatus(1);

    Assert.assertTrue(currentState.equals("on"));


    currentState = cache.queryStatus(2);

    Assert.assertTrue(currentState.equals("on"));


    currentState = cache.queryStatus(3);

    Assert.assertTrue(currentState.equals("on"));


  }





  /**
   * Test command execution based on sensor event condition.
   *
   * @throws Exception    if test fails
   */
  @Test public void testCommandExecution() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/command-execution/").toString();

    config.setResourcePath(newResourcePath);

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Map<String, CommandBuilder> builders = new HashMap<String, CommandBuilder>();
    builders.put("tester", new TesterCommandBuilder());

    CommandFactory cf = new CommandFactory(builders);
    
    Element cmdElement = new Element("command");
    cmdElement.setAttribute("id", "1");
    cmdElement.setAttribute("protocol", "tester");

    Element nameProp = new Element("property");
    nameProp.setAttribute("name", "name");
    nameProp.setAttribute("value", "My Command");

    Element callbackProp = new Element("property");
    callbackProp.setAttribute("name", "callback");
    callbackProp.setAttribute("value", "cb-4232");

    Set<Element> content = new HashSet<Element>();
    content.add(nameProp);
    content.add(callbackProp);

    cmdElement.addContent(content);

    Version20CommandBuilder commandBuilder = new Version20CommandBuilder(cf);
    Command cmd = commandBuilder.build(cmdElement);


    Set<Command> commands = new HashSet<Command>();
    commands.add(cmd);

    cache.initializeEventContext(commands);


    Switch sw1 = new Switch(1, "test sensor", "on", Switch.State.ON);

    cache.update(sw1);


    Assert.assertTrue(TesterCommandBuilder.callbacks.contains("cb-4232"));

  }


  /**
   * Test parameterized command execution based on sensor event condition.
   *
   * @throws Exception    if test fails
   */
  @Test public void testParameterizedCommandExecution() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/param-command-execution/").toString();

    config.setResourcePath(newResourcePath);

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Map<String, CommandBuilder> builders = new HashMap<String, CommandBuilder>();

    Tester2CommandBuilder targetBuilder = new Tester2CommandBuilder();
    builders.put("tester2", targetBuilder);

    CommandFactory cf = new CommandFactory(builders);

    Element cmdElement = new Element("command");
    cmdElement.setAttribute("id", "10");
    cmdElement.setAttribute("protocol", "tester2");

    Element nameProp = new Element("property");
    nameProp.setAttribute("name", "name");
    nameProp.setAttribute("value", "My Command");

    Set<Element> content = new HashSet<Element>();
    content.add(nameProp);

    cmdElement.addContent(content);

    Version20CommandBuilder commandBuilder = new Version20CommandBuilder(cf);
    Command cmd = commandBuilder.build(cmdElement);


    Set<Command> commands = new HashSet<Command>();
    commands.add(cmd);

    cache.initializeEventContext(commands);


    Switch sw1 = new Switch(100, "test sensor", "on", Switch.State.ON);

    cache.update(sw1);


    Assert.assertTrue(targetBuilder.complete);
  }



  /**
   * Tests rules using event subtypes (range) and a three-way rule definition that
   * executes with different command parameters depending on whether the range value
   * is at min boundary, max boundary or between boundaries.
   * 
   * @throws Exception    if test fails
   */
  @Test public void testSpecializedEventAPI() throws Exception
  {
    String newResourcePath = AllTests.getAbsoluteFixturePath()
        .resolve("statuscache/rules/range-limits/").toString();

    config.setResourcePath(newResourcePath);

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    StatelessRuleEngine sre = new StatelessRuleEngine();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    processors.add(sre);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Map<String, CommandBuilder> builders = new HashMap<String, CommandBuilder>();

    Tester3CommandBuilder targetBuilder = new Tester3CommandBuilder();
    builders.put("tester3", targetBuilder);

    CommandFactory cf = new CommandFactory(builders);

    Element cmdElement = new Element("command");
    cmdElement.setAttribute("id", "99");
    cmdElement.setAttribute("protocol", "tester3");

    Element nameProp = new Element("property");
    nameProp.setAttribute("name", "name");
    nameProp.setAttribute("value", "My Command");

    Set<Element> content = new HashSet<Element>();
    content.add(nameProp);

    cmdElement.addContent(content);

    Version20CommandBuilder commandBuilder = new Version20CommandBuilder(cf);
    Command cmd = commandBuilder.build(cmdElement);


    Set<Command> commands = new HashSet<Command>();
    commands.add(cmd);

    cache.initializeEventContext(commands);


    for (int rangeValue = 0; rangeValue < 10; rangeValue++)
    {
      Range range = new Range(11, "temperature", rangeValue, -10, 10);

      cache.update(range);

      Assert.assertTrue(
          "Expected '" + rangeValue + "', got " + targetBuilder.currentValue,
          targetBuilder.currentValue.equals(Integer.toString(rangeValue)));
    }


    {
      Range range = new Range(11, "temperature", 10, -10, 10);

      cache.update(range);

      Assert.assertTrue(
          "Expected 'hit the max', got " + targetBuilder.currentValue,
          targetBuilder.currentValue.equals("hit the max"));
    }

    for (int rangeValue = 9; rangeValue > -10; rangeValue--)
    {
      Range range = new Range(11, "temperature", rangeValue, -10, 10);

      cache.update(range);

      Assert.assertTrue(
          "Expected '" + rangeValue + "', got " + targetBuilder.currentValue,
          targetBuilder.currentValue.equals(Integer.toString(rangeValue)));
    }

    Range range = new Range(11, "temperature", -10, -10, 10);

    cache.update(range);

    Assert.assertTrue(
        "Expected 'hit the min', got " + targetBuilder.currentValue,
        targetBuilder.currentValue.equals("hit the min"));


  }






  // Nested Classes -------------------------------------------------------------------------------

  private static class TesterCommandBuilder implements CommandBuilder, ExecutableCommand
  {

    static Set<String> callbacks = new HashSet<String>();

    String callbackId;


    @Override public org.openremote.controller.command.Command build(Element element)
    {
      List<Element> elements = element.getChildren("property");

      for (Element property : elements)
      {
        if (property.getAttribute("name").getValue().equals("callback"))
        {
          callbackId = property.getAttribute("value").getValue();
        }
      }
      return this;
    }

    @Override public void send()
    {
      callbacks.add(callbackId);
    }
  }

  private static class Tester2CommandBuilder implements CommandBuilder
  {
    boolean complete = false;

    @Override public org.openremote.controller.command.Command build(Element element)
    {
      Assert.assertTrue(element.getAttribute("protocol").getValue().equals("tester2"));
      Assert.assertTrue(element.getAttribute("id").getValue().equals("10"));

      Assert.assertTrue(element.getAttribute(
          org.openremote.controller.command.Command.DYNAMIC_VALUE_ATTR_NAME).getValue().equals("5")
      );

      complete = true;

      return null;
    }
  }



  private static class Tester3CommandBuilder implements CommandBuilder
  {
    String currentValue = "";

    @Override public org.openremote.controller.command.Command build(Element element)
    {
      Assert.assertTrue(element.getAttribute("protocol").getValue().equals("tester3"));
      Assert.assertTrue(element.getAttribute("id").getValue().equals("99"));

      currentValue = element.getAttribute(
          org.openremote.controller.command.Command.DYNAMIC_VALUE_ATTR_NAME).getValue();

      return null;
    }
  }


  private static class EventGrab extends EventProcessor
  {

    Event event;


    @Override public void push(EventContext ctx)
    {
      this.event = ctx.getEvent();
    }

    @Override public String getName()
    {
      return "Event Grab";
    }
  }

}

