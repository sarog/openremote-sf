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
package org.openremote.controller.statuscache;

import java.util.List;
import java.util.ArrayList;

import org.junit.Test;
import org.openremote.controller.protocol.Event;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.event.Switch;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.LevelSensor;

import junit.framework.Assert;

/**
 * Unit tests for {@link StatusCache} class.
 *
 * @author @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StatusCacheTest
{


  /**
   * Simple run-through test of queryStatus() method.
   */
  @Test public void testQueryStatus()
  {
    StatusCache cache = new StatusCache();

    Switch switchEvent = new Switch(1, "test", "foo", Switch.State.ON);

    cache.update(switchEvent);

    Assert.assertTrue(cache.queryStatus(1).equals("foo"));
  }

  /**
   * Test response on a unknown sensor id.
   */
  @Test public void testQueryStatusNonExistingStatus()
  {
    StatusCache cache = new StatusCache();

    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(0)));
  }



  /**
   * Test registration on two different types of sensors -- level and range
   */
  @Test public void testRegisterSensor()
  {
    StatusCache cache = new StatusCache();

    DummyCommand cmd1 = new DummyCommand("0");

    Sensor s1 = new RangeSensor("test1", 1, cache, cmd1, -10, 10);

    cache.registerSensor(s1);

    Assert.assertTrue(cache.getSensor(1).equals(s1));
    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(1)));

    s1.start();

    waitForUpdate();

    Assert.assertTrue(cache.queryStatus(1).equals("0"));


    DummyCommand cmd2 = new DummyCommand("10");

    Sensor s2 = new LevelSensor("test2", 2, cache, cmd2);

    cache.registerSensor(s2);

    Assert.assertTrue(cache.getSensor(2).equals(s2));
    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(2)));


    s2.start();

    waitForUpdate();

    Assert.assertTrue(cache.queryStatus(2).equals("10"));
  }


  @Test public void testDuplicateRegistration()
  {
    Assert.fail("Not Yet Implemented -- see ORCJAVA-204 (http://jira.openremote.org/browse/ORCJAVA-204)");
  }

  @Test public void testNullRegistration()
  {
    Assert.fail("Not Yet Implemented -- see ORCJAVA-204 (http://jira.openremote.org/browse/ORCJAVA-204)");
  }



  /**
   * Basic sensor update test
   */
  @Test public void testUpdate()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    TestProcessor tp = new TestProcessor();

    processors.add(tp);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    DummyCommand cmd = new DummyCommand("on");

    Sensor s = new SwitchSensor("test-sensor", 2, cache, cmd);

    cache.registerSensor(s);

    Assert.assertTrue(Sensor.isUnknownSensorValue(cache.queryStatus(2)));

    s.start();

    waitForUpdate();

    Assert.assertTrue(cache.queryStatus(2).equals("on"));
  }


  /**
   * Test that sensor events are pushed through event processor chain
   */
  @Test public void testEventProcessorPassThrough()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    ValueProcessor vp1 = new ValueProcessor("acme");
    ValueProcessor vp2 = new ValueProcessor("acme");

    processors.add(vp1);
    processors.add(vp2);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Event evt = new Switch(3, "test", "acme", Switch.State.ON);

    cache.update(evt);

    Assert.assertTrue(cache.queryStatus(3).equals("acme"));
  }

  /**
   * Test event replace within event processor chain.
   */
  @Test public void testEventProcessorEventReplace()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    ValueProcessor vp1 = new ValueProcessor("acme");
    OnReplacer rep = new OnReplacer();

    processors.add(vp1);
    processors.add(rep);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Event evt = new Switch(4, "test4", "acme", Switch.State.ON);

    cache.update(evt);

    Assert.assertTrue(
        "Expected 'on', got '" + cache.queryStatus(4) + "'.",
        cache.queryStatus(4).equals("on")
    );
  }


  /**
   * Test update of an event that does not have an associated sensor bound to the same id.
   *
   * NOTE : These semantics may be not useful as long as we operate on IDs on REST interfaces
   *        rather than logical names/properties. However, codifying this behavior in unit
   *        tests for now for possible future use-cases.
   *                                                                        [JPL]
   */
  @Test public void testUpdateUndefinedSensorID()
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain epc = new EventProcessorChain();

    List<EventProcessor> processors = new ArrayList<EventProcessor>();
    TestProcessor tp = new TestProcessor();

    processors.add(tp);

    epc.setEventProcessors(processors);

    StatusCache cache = new StatusCache(cst, epc);

    Event evt = new Switch(1, "test", "foo", Switch.State.ON);

    cache.update(evt);

    Assert.assertTrue(cache.queryStatus(1).equals("foo"));
  }


  // Helper Methods -------------------------------------------------------------------------------

  private void waitForUpdate()
  {
    try
    {
      Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);
    }

    catch (InterruptedException e)
    {
      Assert.fail(e.getMessage());
    }
  }


  // Nested Classes -------------------------------------------------------------------------------


  private static class DummyCommand extends ReadCommand
  {
    private String value;

    DummyCommand(String value)
    {
      this.value = value;
    }

    public String read(Sensor s)
    {
      return value;
    }
  }


  private static class TestProcessor extends EventProcessor
  {
    @Override public Event push(Event evt)
    {
      return evt;
    }

    @Override public String getName()
    {
      return "Test Processor";
    }
  }

  private static class OnReplacer extends EventProcessor
  {
    @Override public Event push(Event evt)
    {
      return new Switch(evt.getSourceID(), evt.getSource(), "on", Switch.State.ON);
    }

    @Override public String getName()
    {
      return "OnReplacer";
    }
  }

  private static class ValueProcessor extends EventProcessor
  {
    private String eventValue;

    ValueProcessor(String val)
    {
      eventValue = val;
    }

    @Override public Event push(Event evt)
    {
      Assert.assertTrue(evt.getValue().equals(eventValue));
      return evt;
    }

    @Override public String getName()
    {
      return "ValueProcessor";
    }
  }




///**
// *
// * This class is mainly used to test the <b>SkipStateTrack</b>.<br /><br />
// *
// * There is a <b>ods file</b> named <b>SkipStateTrackTest.ods</b> in current directory.<br />
// * The file <b>SkipStateTrackTest.ods</b> contains several situations of skip-state tracking.<br />
// * So, the following methods depend on these situations in SkipStateTrackTest.ods descriped.<br /><br />
// *
// * <b>NOTE: Start tomcat firstly.</b>
// *
// */
//
//   private Logger logger = Logger.getLogger(this.getClass().getName());
//
//   /**
//    * <b>Situation1:</b><br />
//    *
//    *  Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    *  not timeout while observing and Getting the changed status at last.
//    */
//   @Test
//   public void testCase1() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1001");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
//
//   /**
//    * <b>Situation2:</b><br />
//    *
//    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
//    * client gets 503 error at last.<br /><br />
//    *
//    * <b>Second Polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
//    * gets the changed status with <b>the value of column STATUS_CHANGED_IDS in TIME_OUT_TABLE</b> from<br />
//    * CachedStatus table(currently it's simulated).<br /><br />
//    *
//    * <b>NOTE:</b> This situation must work with method <b>simulateSkipStateTrackTestCase2</b> which was called<br />
//    * while <b>InitCachedStatusDBListener</b> starting.
//    */
//   @Test
//   public void testCase2() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1002");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
//
//   /**
//    * <b>Situation3:</b><br /><br />
//    *
//    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
//    * client gets 503 error at last.<br /><br />
//    *
//    * <b>Second polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
//    * but the statuses which previous polling request care about didn't change.<br />
//    * So, current polling request observes the change of statuses and gets the changed status at last.<br /><br />
//    *
//    * <b>NOTE:</b> This situation must work with method <b>simulateSkipStateTrackTestCase3</b> which was called<br />
//    * while <b>InitCachedStatusDBListener</b> starting.
//    */
//   @Test
//   public void testCase3() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1003");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
//
//   /**
//    * <b>Situation4:</b><br /><br />
//    *
//    * <b>First polling request:</b> Not found time out record in TIME_OUT_TABLE during polling operation,<br />
//    * timeout while observing, this time out request will record into TIME_OUT_TABLE,<br />
//    * client gets 503 error at last.<br /><br />
//    *
//    * <b>Second polling request:</b> Found previous time out record in TIME_OUT_TABLE,<br />
//    * but the statuses which previous polling request care about didn't change.<br />
//    * So, current polling request observes the change of statuses but timeout,<br />
//    * client gets 503 error at last.<br /><br />
//    */
//   //@Test
//   public void testCase4() throws Exception {
//      WebConversation wc = new WebConversation();
//      WebRequest pollingGetMethodRequest = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + AllTests.WEBAPP_PORT + "/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1004");
//      try {
//         WebResponse wr = wc.getResponse(pollingGetMethodRequest);
//         logger.info("The result was : \n" + wr.getText());
//      } catch (HttpException e) {
//         if (e.getResponseCode() == 504) {
//            logger.info("Polling request was timeout.");
//         }
//      }
//   }
}
